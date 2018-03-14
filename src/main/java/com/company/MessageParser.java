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

public class MessageParser {

    private String payload;
    private boolean isTxMessage = false;
    private Long timestamp;

    public SerialMessage getMessage( byte[] Data) throws Exception {
        update( new String(Data));
        SerialMessage message = new SerialMessage();
        message.setPayload(payload);
        message.setTimestamp(timestamp);
        message.setTxMessage(isTxMessage);
        return message;
    }


    public byte[] getData(SerialMessage message) throws Exception {
        update(message);
        return getSerialString().getBytes();
    }

    String getSerialString() {
        StringBuffer message = new StringBuffer();
        message.append(payload).append("\n");
        return message.toString();
    }

    void update(SerialMessage message) throws Exception {

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
