package com.company.comms;

import com.company.ApplicationProperties;
import com.company.MainRobot;
import com.company.Manager;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommsManager extends Manager {

    /** The the serial port we shall use */
    private  SerialPort m_comPort=null;
    /** Handle back to Robot*/
    private MainRobot m_mainRobot = null;
    /** The Message queue that holds serial messages*/
    private  MessageRecordQueue m_queue=null;
    /** List of serial ports */
    private List<SerialPort> m_serialPortlist=null;
    /** Map of serial ports and names */
    private HashMap<String, SerialPort> m_portMap=null;
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

    public  SerialPort getM_comPort() { return m_comPort; }
    public  MessageRecordQueue getM_queue() { return m_queue; }


    @Override
    public void initialize() {
        String portId = null;
        m_serialPortlist = Arrays.asList(SerialPort.getCommPorts());

        for (SerialPort serialP : m_serialPortlist) {
            m_portMap.put(serialP.getSystemPortName(), serialP);
            writeLog(Level.INFO," Ports in Map: "+serialP.getSystemPortName() );
        }

        try {
            String portname = ApplicationProperties.PORT_NAME;
            writeLog(Level.INFO," Will open: " + portname);
            openPort( portname );
            setPortDefaultParams(m_comPort);


            //add event listeners
            addEventListeners(m_comPort);

        } catch (Exception e) {
            writeLog(Level.ERROR, e.toString());
        }
    }

    public CommsManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_queue = new MessageRecordQueue(String.valueOf(this.toString()));
        m_portMap = new HashMap<String, SerialPort>();
        m_serialPortlist = new ArrayList<SerialPort>();
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

    public boolean isComPortOpen(){
        if(m_comPort!=null) {
            if (m_comPort.isOpen()) {
                return true;
            }
        }
        return false;
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



    private void setPortDefaultParams(SerialPort comPort)
    {

        writeLog(Level.INFO," setPortDefaultParams called");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        // set port parameters
        comPort.setBaudRate(CommsProperties.DATA_RATE);
        comPort.setNumDataBits(CommsProperties.DATABITS);
        comPort.setNumStopBits(CommsProperties.STOPBITS);
        comPort.setParity(CommsProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        writeLog(Level.INFO," addEventListeners called");
        SeriaListener listener = new SeriaListener(this);
        listener.initialize(comPort,m_queue);
        comPort.addDataListener(listener);

    }



}
