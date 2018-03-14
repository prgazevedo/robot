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
