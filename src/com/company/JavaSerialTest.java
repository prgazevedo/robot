package com.company;


        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import com.fazecast.jSerialComm.*;
        import java.util.*;


public class JavaSerialTest  {
    private static SerialPort m_comPort;
    private static SerialPort m_comPort2; //for OSX
    /** The output stream to the port */
    private static OutputStream m_outputStream;
    /** The input stream to the port */
    private static InputStreamReader m_inputStream;
    //List of serial ports
    private List<SerialPort> SerialPortlist=null;
    HashMap<String, SerialPort> m_portMap=null;


    //PortName
    private static final String portName1 = "cu.usbmodem1441";

    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
           // "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/tty.usbmodem1411", //Mac OS X
            "/dev/tty.usbmodem1431",
            "cu.usbmodem1441", //call-out
            "tty.usbmodem1441", //call-in
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };



private void openPort( String portID)
{
    SerialPort sPort= null;
    System.out.println("[Raspberry]: openPort: " + portID);
    if(m_portMap.containsKey(portID)){
        System.out.println("[Raspberry]: openPort: Found port in portMap: "+portID);
         sPort = m_portMap.get(portID);
        // Try to open port, terminate execution if not possible
        if (sPort.openPort()) {
            m_comPort=sPort;
            System.out.println("[Raspberry]: "+m_comPort.getSystemPortName() + " successfully opened.");
            return;

        } else {
            System.out.println(portID + "[Raspberry]: failed to open.");
            return;
        }
    }
    else
    {
        System.out.println("[Raspberry]: openPort: Could not find port in portMap: "+portID);
    }


}

    private void setPortDefaultParams(SerialPort comPort)
    {

        System.out.println("[Raspberry]: setPortDefaultParams called");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        // set port parameters
        comPort.setBaudRate(ApplicationProperties.DATA_RATE);
        comPort.setNumDataBits(ApplicationProperties.DATABITS);
        comPort.setNumStopBits(ApplicationProperties.STOPBITS);
        comPort.setParity(ApplicationProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        System.out.println("[Raspberry]: addEventListeners called");
        SeriaListener listener = new SeriaListener();
        comPort.addDataListener(listener);
    }


    public void initialize() {

        String portId = null;
        SerialPortlist = Arrays.asList(SerialPort.getCommPorts());
        //Create Map
        m_portMap = new HashMap<String, SerialPort>();
        for (SerialPort serialP : SerialPortlist) {
            m_portMap.put(serialP.getSystemPortName(), serialP);
            System.out.println("[Raspberry]: Ports in Map: "+serialP.getSystemPortName() );
        }

        try {

            System.out.println("[Raspberry]: Will open: " + portName1);
            openPort( portName1 );
            setPortDefaultParams(m_comPort);
            addEventListeners(m_comPort);

            // open the streams
            m_inputStream = new InputStreamReader(m_comPort.getInputStream());
            m_outputStream = m_comPort.getOutputStream();


        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (m_comPort != null) {
            m_comPort.removeDataListener();
            m_comPort.closePort();
        }
    }






    /**
     * WriteStream
     */
    public static synchronized void writeStream(String cmd) {
        if (m_comPort != null) {
            System.out.println("[Raspberry]:Writing to serial:"+cmd);
            try {
                m_outputStream.write(cmd.getBytes()); // Write to serial
                //m_outputStream.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * ReadStream
     */
    public static synchronized void readStream() {

        System.out.println("[Raspberry]: readStream called \n");
        if (m_comPort != null) {
            try (Scanner scanner = new Scanner(m_inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) throws Exception {
        JavaSerialTest main = new JavaSerialTest();
        main.initialize();



        Scanner in = new Scanner(System.in);
        if(m_comPort!=null){
            if(m_comPort.isOpen()){
                System.out.println("[Raspberry]: Port: "+ m_comPort.getSystemPortName()+" is Open");
                Thread writeThread = new Thread() {
                    @Override public void run() {
                        try {
                            System.out.println("[Raspberry]: New Write thread launched!");
                            String cmd = "";
                            while (!cmd.equals("EXIT")) {
                                System.out.println("[Raspberry]: Enter command:");
                                cmd = in.next();
                                if (cmd.equals("w") || cmd.equals("a")|| cmd.equals("d") || cmd.equals("s") || cmd.equals("x") || cmd.equals("j") || cmd.equals("k") || cmd.equals("l")  )
                                {

                                    writeStream(cmd);
                                }
                            }

                        } catch (Exception e) {
                            System.out.println("[Raspberry]: Error writing to Serial");
                        }
                        m_comPort.closePort();
                        System.out.println("[Raspberry]: Exiting! CLOSE COM");
                    }
                };
                writeThread.start();
                System.out.println("[Raspberry]: writeThread Started");
            }

        }

        Thread readThread=new Thread() {
            public void run() {
                System.out.println("[Raspberry]: New Read thread launched!");
                if(m_inputStream!=null) {
                    readStream();
                }
                else{
                    System.out.println("[Raspberry]: m_inputStream is null");
                }
            }
        };
        readThread.start();

        System.out.println("[Raspberry]: readThread Started");

    }

}