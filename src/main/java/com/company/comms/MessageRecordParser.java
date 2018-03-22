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


package com.company.comms;

import com.company.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageRecordParser {

    private String m_recordPayload;
    private boolean isTxMessage = false;
    private Long timestamp;
    private MessagePayload m_messagePayload;
    private final static Logger logger =  LogManager.getLogger(MessageRecordQueue.class);

    public SerialMessageRecord getMessage(byte[] Data) throws Exception {
        update( new String(Data));
        SerialMessageRecord message = new SerialMessageRecord();
        message.setPayload(m_recordPayload);
        message.setTimestamp(timestamp);
        message.setTxMessage(isTxMessage);
        return message;
    }


    public MessagePayload getMessagePayload(SerialMessageRecord message) throws Exception {
        MessagePayload messagePayload = null;
        try {
            update(message);
            messagePayload =  processMessagePayload(m_recordPayload);
            return m_messagePayload;
        }
        catch(Exception e){
            logger.error("getMessagePayload - Exception:"+e.toString()+" messagePayload is:"+messagePayload);
        }
        return null;
    }


    public byte[] getData(SerialMessageRecord message) throws Exception {
        update(message);
        return getSerialString().getBytes();
    }

    String getSerialString() {
        StringBuffer message = new StringBuffer();
        message.append(m_recordPayload).append("\n");
        return message.toString();
    }

    public MessagePayload getMessagePayload(){
        return m_messagePayload;
    }

    private boolean isMessagePayloadACommand()
    {
        if(m_recordPayload !=null)
        {
            //If the m_recordPayload is terminated it probably is a Cmd
            if(m_recordPayload.substring(m_recordPayload.length() - 1).equals(";"))
            {

                //if it does not start with "[Arduino]" it is a command
                if(!m_recordPayload.contains("[Arduino]"))
                {
                    return true;
                }
                else return false;
            }
            else return false;
        }
        else return false;

    }

    private MessagePayload processMessagePayload(String messageRecordPayload){
        m_messagePayload = null;
        if(isMessagePayloadACommand()) {
            try {
                MessagePayload.MessagePayloadBuilder MPB = new MessagePayload.MessagePayloadBuilder();

                m_messagePayload = MPB.build(messageRecordPayload);
                if (m_messagePayload == null) {
                    logger.error("processMessagePayload could not build the messagePayload from the MessageRecord" + messageRecordPayload);
                }
                if (m_messagePayload.getM_cmd_type().equals(ApplicationProperties.cmds.None)) {
                    logger.error("processMessagePayload could not process the cmd type of messageRecord: " + messageRecordPayload);
                    logger.error("processMessagePayload found messagePayload was: " + m_messagePayload.getM_cmd_type().toString());
                }
            }
            catch(Exception e){
                logger.error("processMessagePayload - Exception: "+e.toString());
            }
            return m_messagePayload;
        }
        else return null;



    }

    void update(SerialMessageRecord message) throws Exception {

        m_recordPayload = message.getPayload();
        isTxMessage = message.getTxMessage();
        timestamp = message.getTimestamp();

    }

    void update(String rawData) throws Exception {
        timestamp = System.currentTimeMillis();
        isTxMessage = false;
        m_recordPayload = rawData;

    }

}
