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


import com.company.comms.MessageRecordQueue;
import com.company.comms.SeriaListener;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class MainRobot {

    /** The logger we shall use */
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
    private static MessageRecordQueue m_queue;
    //private MessagePayload.MessagePayloadBuilder m_MPB;
     /** PortName of this machine */
    private static final String portName1 = "cu.usbmodem1441";
    /** Write Thread */
    private static WriteThread m_writeThread=null;
    /** Read Thread */
    private static MonitorThread m_monitorThread=null;

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

    private static void writeLog(org.apache.logging.log4j.Level messageLevel,String message){
        logger.log(messageLevel,"[Raspberry]:"+message);
    }

    public MainRobot() {
        if (m_queue == null) {
            m_queue = new MessageRecordQueue(String.valueOf(this.toString()));
        }
        //Create Port Map
        if (m_portMap == null) {
            m_portMap = new HashMap<String, SerialPort>();
        }
        //Create Serial Port List
        if (m_serialPortlist == null) {
            m_serialPortlist = new ArrayList<SerialPort>();
        }



        Configurator.setAllLevels(LogManager.getRootLogger().getName(), ApplicationProperties.LOG_LEVEL);
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
        comPort.setBaudRate(ApplicationProperties.DATA_RATE);
        comPort.setNumDataBits(ApplicationProperties.DATABITS);
        comPort.setNumStopBits(ApplicationProperties.STOPBITS);
        comPort.setParity(ApplicationProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        writeLog(Level.INFO," addEventListeners called");
        SeriaListener listener = new SeriaListener(comPort,m_queue);
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





    public static void main(String[] args) throws Exception {
        MainRobot robot = new MainRobot();
        writeLog(Level.INFO, "Robot initialize");
        robot.initialize();


        if(m_comPort!=null){
            if(m_comPort.isOpen()){
                writeLog(Level.INFO," Port: "+ m_comPort.getSystemPortName()+" is Open");
                m_writeThread = new WriteThread(m_queue,m_comPort) {

                };
                m_writeThread.start();
                writeLog(Level.INFO," writeThread Started");
                //Small test

            }

        }

        m_monitorThread=new MonitorThread(robot.m_queue);
        m_monitorThread.setWriteThread(m_writeThread);
        m_monitorThread.start();
        writeLog(Level.INFO," monitorThread Started");
        TimeUnit.SECONDS.sleep(10);
        m_writeThread.requestCmdList();
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        //Test write to Arduino
        m_writeThread.moveForward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveBackward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveForward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveBackward(100,1000);
    }

}