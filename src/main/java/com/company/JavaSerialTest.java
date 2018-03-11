package com.company;


import java.io.InputStreamReader;
import java.io.OutputStream;
import com.fazecast.jSerialComm.*;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;



public class JavaSerialTest  {

    //private static final Logger log = LogManager.getRootLogger();
    private final static Logger log =  LogManager.getLogger(JavaSerialTest.class);
    private static SerialPort m_comPort;
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
    log.info("[Raspberry]: openPort: " + portID);
    if(m_portMap.containsKey(portID)){
        log.info("[Raspberry]: openPort: Found port in portMap: "+portID);
         sPort = m_portMap.get(portID);
        // Try to open port, terminate execution if not possible
        if (sPort.openPort()) {
            m_comPort=sPort;
            log.info("[Raspberry]: "+m_comPort.getSystemPortName() + " successfully opened.");
            return;

        } else {
            log.warn("[Raspberry]: failed to open: " +portID);
            return;
        }
    }
    else
    {
        log.warn("[Raspberry]: openPort: Could not find port in portMap: "+portID);
    }


}

    private void setPortDefaultParams(SerialPort comPort)
    {

        log.info("[Raspberry]: setPortDefaultParams called");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 100, 0);
        // set port parameters
        comPort.setBaudRate(ApplicationProperties.DATA_RATE);
        comPort.setNumDataBits(ApplicationProperties.DATABITS);
        comPort.setNumStopBits(ApplicationProperties.STOPBITS);
        comPort.setParity(ApplicationProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        log.info("[Raspberry]: addEventListeners called");
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
            log.info("[Raspberry]: Ports in Map: "+serialP.getSystemPortName() );
        }

        try {

            log.info("[Raspberry]: Will open: " + portName1);
            openPort( portName1 );
            setPortDefaultParams(m_comPort);


            // open the streams
            m_inputStream = new InputStreamReader(m_comPort.getInputStream());
            m_outputStream = m_comPort.getOutputStream();

            //add event listeners
            addEventListeners(m_comPort);

        } catch (Exception e) {
            log.error(e.toString());
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
            log.info("[Raspberry]:Writing to serial:"+cmd);
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

        log.info("[Raspberry]: readStream called \n");
        if (m_comPort != null) {
            try (Scanner scanner = new Scanner(m_inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    log.info(line);
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
                log.info("[Raspberry]: Port: "+ m_comPort.getSystemPortName()+" is Open");
                Thread writeThread = new Thread() {
                    @Override public void run() {
                        try {
                            Thread.currentThread().setName("writeThread");
                            log.info("[Raspberry]: New Write thread launched!");
                            String cmd = "";
                            while (!cmd.equals("EXIT")) {
                                log.info("[Raspberry]: Enter command:");
                                cmd = in.next();
                                if (cmd.equals("w") || cmd.equals("a")|| cmd.equals("d") || cmd.equals("s") || cmd.equals("x") || cmd.equals("j") || cmd.equals("k") || cmd.equals("l")  )
                                {

                                    writeStream(cmd);
                                }
                            }

                        } catch (Exception e) {
                            log.error("[Raspberry]: Error writing to Serial");
                        }
                        m_comPort.closePort();
                        log.info("[Raspberry]: Exiting! CLOSE COM");
                    }
                };
                writeThread.start();
                log.info("[Raspberry]: writeThread Started");
            }

        }
/*
        Thread readThread=new Thread() {
            public void run() {
                Thread.currentThread().setName("readThread");
                log.info("[Raspberry]: New Read thread launched!");
                if(m_inputStream!=null) {
                    readStream();
                }
                else{
                    log.warn("[Raspberry]: m_inputStream is null");
                }
                log.info("[Raspberry]: New Read thread launched!");
            }
        };
        readThread.start();

        log.info("[Raspberry]: readThread Started");
*/
    }

}