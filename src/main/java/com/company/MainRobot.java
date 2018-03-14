/*
 * Copyright 2018 Pedro Azevedo (prgazevedo@gmail.com)
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.company;


import java.io.InputStreamReader;
import java.io.OutputStream;
import com.fazecast.jSerialComm.*;
import java.util.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;



public class MainRobot {

    /** Set the logger with the class name */
    private final static Logger logger =  LogManager.getLogger(MainRobot.class);
    /** The the serial port we shall use */
    private static SerialPort m_comPort=null;
    /** The output stream to the port */
    private static OutputStream m_outputStream=null;
    /** The input stream to the port */
    private static InputStreamReader m_inputStream=null;
    /** List of serial ports */
    private List<SerialPort> m_serialPortlist=null;
     /** Map of serial ports and names */
    private HashMap<String, SerialPort> m_portMap=null;
     /** The Message queue that holds serial messages*/
    private MessageQueue m_queue;

     /** PortName of this machine */
    private static final String portName1 = "cu.usbmodem1441";

    /** port names to use in different machines */
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

    public MainRobot() {
        if (m_queue == null) {
            m_queue = new MessageQueue(String.valueOf(this.toString()));
        }
        //Create Port Map
        if (m_portMap == null) {
            m_portMap = new HashMap<String, SerialPort>();
        }
        //Create Serial Port List
        if (m_serialPortlist == null) {
            m_serialPortlist = new ArrayList<SerialPort>();
        }

    }


    private void openPort( String portID)
{
    SerialPort sPort;
    writeLog(Level.INFO," openPort: " + portID);
    if(m_portMap.containsKey(portID)){
        writeLog(Level.INFO," openPort: Found port in portMap: "+portID);
         sPort = m_portMap.get(portID);
        // Try to open port, terminate execution if not possible
        if (sPort.openPort()) {
            m_comPort=sPort;
            writeLog(Level.INFO," "+m_comPort.getSystemPortName() + " successfully opened.");

        } else {
            writeLog(Level.WARN, "failed to open: " +portID);

        }
    }
    else
    {
        writeLog(Level.WARN, " openPort: Could not find port in portMap: "+portID);
    }


}

private static void writeLog(org.apache.logging.log4j.Level messageLevel,String message){
    logger.log(messageLevel,"[Raspberry]:"+message);
}


    private void setPortDefaultParams(SerialPort comPort)
    {

        writeLog(Level.INFO," setPortDefaultParams called");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 100, 0);
        // set port parameters
        comPort.setBaudRate(ApplicationProperties.DATA_RATE);
        comPort.setNumDataBits(ApplicationProperties.DATABITS);
        comPort.setNumStopBits(ApplicationProperties.STOPBITS);
        comPort.setParity(ApplicationProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        writeLog(Level.INFO," addEventListeners called");
        SeriaListener listener = new SeriaListener(m_queue);
        comPort.addDataListener(listener);
    }


    private void initialize() {

        String portId = null;
        m_serialPortlist = Arrays.asList(SerialPort.getCommPorts());

        for (SerialPort serialP : m_serialPortlist) {
            m_portMap.put(serialP.getSystemPortName(), serialP);
            writeLog(Level.INFO," Ports in Map: "+serialP.getSystemPortName() );
        }

        try {

            writeLog(Level.INFO," Will open: " + portName1);
            openPort( portName1 );
            setPortDefaultParams(m_comPort);


            // open the streams
            m_inputStream = new InputStreamReader(m_comPort.getInputStream());
            m_outputStream = m_comPort.getOutputStream();

            //add event listeners
            addEventListeners(m_comPort);

        } catch (Exception e) {
            writeLog(Level.ERROR, e.toString());
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
    private static synchronized void writeStream(String cmd) {
        if (m_comPort != null) {
            writeLog(Level.INFO,"Writing to serial:"+cmd);
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

        writeLog(Level.INFO," readStream called \n");
        if (m_comPort != null) {
            try (Scanner scanner = new Scanner(m_inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    writeLog(Level.INFO,line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) throws Exception {
        MainRobot main = new MainRobot();
        main.initialize();


        Scanner in = new Scanner(System.in);
        if(m_comPort!=null){
            if(m_comPort.isOpen()){
                writeLog(Level.INFO," Port: "+ m_comPort.getSystemPortName()+" is Open");
                Thread writeThread = new Thread() {
                    @Override public void run() {
                        try {
                            Thread.currentThread().setName("writeThread");
                            writeLog(Level.INFO," New Write thread launched!");
                            String cmd = "";
                            while (!cmd.equals("EXIT")) {
                                writeLog(Level.INFO," Enter command:");
                                cmd = in.next();
                                if (cmd.equals("w") || cmd.equals("a")|| cmd.equals("d") || cmd.equals("s") || cmd.equals("x") || cmd.equals("j") || cmd.equals("k") || cmd.equals("l")  )
                                {

                                    writeStream(cmd);
                                }
                            }

                        } catch (Exception e) {
                            writeLog(Level.ERROR, "Error writing to Serial");
                            throw(e);
                        }
                        m_comPort.closePort();
                        writeLog(Level.INFO," Exiting! CLOSE COM");
                    }
                };
                writeThread.start();
                writeLog(Level.INFO," writeThread Started");
            }

        }
/*
        Thread readThread=new Thread() {
            public void run() {
                Thread.currentThread().setName("readThread");
                writeLog(Level.INFO," New Read thread launched!");
                if(m_inputStream!=null) {
                    readStream();
                }
                else{
                    log.warn("[Raspberry]: m_inputStream is null");
                }
                writeLog(Level.INFO," New Read thread launched!");
            }
        };
        readThread.start();

        writeLog(Level.INFO," readThread Started");
*/
    }

}