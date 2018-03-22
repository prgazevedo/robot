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

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

final class SeriaListener implements SerialPortPacketListener
{



    private SerialPort m_serialPort=null;
    private MessageRecordParser m_parser=null;
    private MessageRecordQueue m_queue=null;
    StringBuilder rawMessage = new StringBuilder();
    private boolean isNameSet=false;
    //private static final Logger log = LogManager.getRootLogger();
    private final static Logger log =  LogManager.getLogger(SeriaListener.class);

    public SeriaListener(SerialPort serial,MessageRecordQueue queue) {
        m_serialPort = serial;
        m_queue=queue;
        m_parser= new MessageRecordParser();

    }

    public final void initThreadName(){
        Thread.currentThread().setName("SerialListener");
        isNameSet=true;
    }

    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }

    @Override
    public int getPacketSize() { return 80; }

    @Override
    public void serialEvent(SerialPortEvent event)
    {
        if(!isNameSet) initThreadName();
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        try{
            byte[] buffer = new byte[m_serialPort.bytesAvailable()];
            m_serialPort.readBytes(buffer, buffer.length);
            //byte[] newData = event.getReceivedData();
            //log.info("[Raspberry]: serialEvent called");
            if(buffer.length>0) readRawData(buffer);


        }
        catch (Exception rEx)
        {
            log.error("serialEvent - exception:",rEx.toString());
        }
    }

    private void readRawData(byte[] buffer){
        //log.info("[Raspberry]: data received. Size: "+buffer.length+ "\n");
        String prefix="[Raspberry]: readRawData from serial: ";
        String output="";
        try {

            for (byte b : buffer)
            {
                if (ApplicationProperties.isMessageSplitter(b) && rawMessage.length() > 0)
                {
                    String toProcess = rawMessage.toString();
                    log.trace(prefix + "Received a rawMessage:[{}]", toProcess);

                    //Send Message
                    SerialMessageRecord message = m_parser.getMessage( toProcess.getBytes());

                    if (message != null)
                    {
                        m_queue.add(message);
                    }
                    rawMessage.setLength(0);
                }
                else if (!ApplicationProperties.isMessageSplitter(b))
                {
                    log.trace("Received a char:[{}]", ((char) b));
                    rawMessage.append((char) b);
                }
                else if (ApplicationProperties.isMessageOversize(rawMessage.length() ) )
                {
                    log.warn(
                            "Serial receive buffer size reached to MAX level[{} chars], "
                                    + "Now clearing the buffer. Existing data:[{}]",
                            ApplicationProperties.getSerialDataMaxSize(), rawMessage.toString());
                    rawMessage.setLength(0);
                }
                else {
                    log.trace("Received MESSAGE_SPLITTER and current rawMessage length is ZERO! Nothing to do");
                }
            }
           if(!m_queue.isEmpty()) m_queue.logContents();
        }
        catch(Exception e){
            e.printStackTrace();
            log.error("Exception: ",e);
            rawMessage.setLength(0);

        }
    }



}


