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

    public SeriaListener(MessageQueue queue) {
        m_queue=queue;
        m_parser= new MessageParser();
    }

    private MessageParser m_parser=null;
    private MessageQueue m_queue=null;
    StringBuilder rawMessage = new StringBuilder();

    //private static final Logger log = LogManager.getRootLogger();
    private final static Logger log =  LogManager.getLogger(SeriaListener.class);
    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

    @Override
    public int getPacketSize() { return 80; }

    @Override
    public void serialEvent(SerialPortEvent event)
    {

        try{
            byte[] newData = event.getReceivedData();
            //log.info("[Raspberry]: serialEvent called");
            if(newData.length>0) readRawData(newData);


        }
        catch (Exception rEx)
        {
            log.error(rEx.toString());
        }
    }

    private void readRawData(byte[] buffer){
        //log.info("[Raspberry]: data received. Size: "+buffer.length+ "\n");
        String prefix="[Raspberry]: readRawData from serial: ";
        String output="";
        try {

            /* OLD CODE
            for (int i = 0; i < buffer.length; ++i) {
                //    System.out.print((char)buffer[i]);

                if ( ApplicationProperties.isMessageSplitter(buffer[i])  )
                {
                    //If there is content in the output log it
                    if(!output.equals("")) log.info(prefix+output);
                    output="";

                }
                else{
                    output += (char) buffer[i];
                }


            }
            */

            for (byte b : buffer) {
                if (ApplicationProperties.isMessageSplitter(b) && rawMessage.length() > 0) {
                    String toProcess = rawMessage.toString();
                    log.info(prefix + "Received a rawMessage:[{}]", toProcess);

                    //Send Message
                    SerialMessage message = m_parser.getMessage( toProcess.getBytes());
                    if (message != null) {
                        m_queue.add(message);
                    }
                    rawMessage.setLength(0);
                }
                else if (!ApplicationProperties.isMessageSplitter(b)) {
                    //log.trace("Received a char:[{}]", ((char) b));
                    rawMessage.append((char) b);
                } else if (rawMessage.length() >= ApplicationProperties.SERIAL_DATA_MAX_SIZE) {
                    log.warn(
                            "Serial receive buffer size reached to MAX level[{} chars], "
                                    + "Now clearing the buffer. Existing data:[{}]",
                            ApplicationProperties.SERIAL_DATA_MAX_SIZE, rawMessage.toString());
                    rawMessage.setLength(0);
                } else {
                    log.debug("Received MESSAGE_SPLITTER and current rawMessage length is ZERO! Nothing to do");
                }
            }
           m_queue.logContents();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}


