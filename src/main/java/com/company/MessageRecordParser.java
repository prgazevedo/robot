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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.*;

public class MessageRecordParser {

    private String payload;
    private boolean isTxMessage = false;
    private Long timestamp;
    private MessagePayload m_messagePayload;
    private final static Logger logger =  LogManager.getLogger(MessageRecordQueue.class);

    public SerialMessageRecord getMessage(byte[] Data) throws Exception {
        update( new String(Data));
        SerialMessageRecord message = new SerialMessageRecord();
        message.setPayload(payload);
        message.setTimestamp(timestamp);
        message.setTxMessage(isTxMessage);
        return message;
    }


    public MessagePayload getMessagePayload(SerialMessageRecord message) throws Exception {
        update(message);
        return processMessagePayload();
    }


    public byte[] getData(SerialMessageRecord message) throws Exception {
        update(message);
        return getSerialString().getBytes();
    }

    String getSerialString() {
        StringBuffer message = new StringBuffer();
        message.append(payload).append("\n");
        return message.toString();
    }

    public MessagePayload getMessagePayload(){
        return m_messagePayload;
    }

    private MessagePayload processMessagePayload(){
        MessagePayload.MessagePayloadBuilder MPB = new MessagePayload.MessagePayloadBuilder();
        List<String> cmdlist = new ArrayList<String>(Arrays.asList(payload.split(" , ")));
        for (String s:cmdlist)
        {
            //only 1 argument
            if(cmdlist.size()==1)
            {
                if(s.substring(s.length() - 1).equals(";"))
                {
                    //Is terminated
                    String cmd= s.substring(0,s.length() - 1);
                    MPB.cmd(cmd);
                    m_messagePayload=MPB.build();
                }
                else
                {
                    //Is not terminated
                    logger.error("Message was not terminated - payload:"+payload+" string:"+s);
                }
            }
            //Other payloads do not interest us for now
            else break;
        }
        return m_messagePayload;



    }

    void update(SerialMessageRecord message) throws Exception {

        payload = message.getPayload();
        isTxMessage = message.getTxMessage();
        timestamp = message.getTimestamp();

    }

    void update(String rawData) throws Exception {
        timestamp = System.currentTimeMillis();
        isTxMessage = false;
        payload = rawData;

    }

}
