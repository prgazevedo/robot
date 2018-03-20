package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorThread extends Thread {

    MessageRecordQueue m_queue=null;
    MessageRecordParser m_parser=null;
    private final static Logger log =  LogManager.getLogger(SeriaListener.class);
    private boolean m_shouldRun = false;

    public boolean isM_shouldRun() {
        return m_shouldRun;
    }

    public void setM_shouldRun(boolean m_shouldRun) {
        this.m_shouldRun = m_shouldRun;
    }



    public MonitorThread(String name) {
        super(name);
    }

    public MonitorThread(MessageRecordQueue queue) {
        super("monitorThread");
        m_queue=queue;
        m_shouldRun=true;
        m_parser= new MessageRecordParser();
    }

    private void processMessage(){
        SerialMessageRecord smr = m_queue.auditLastMessage();
        try {
            MessagePayload messagePayload = m_parser.getMessagePayload(smr);
            if(messagePayload!=null) {
                log.info("Message Payload is: " + messagePayload.toString());
            }
            else{
                log.info("No Message Payload Cmd detected. SerialMessageRecord was: " + smr.toString());
            }
        }
        catch(Exception e)
        {
            log.error("Exception:"+e.toString());
        }
    }

    public void run() {

        log.info(" New monitorThread launched!");
        while(m_shouldRun) {
            if (m_queue != null) {
                if(!m_queue.isEmpty()) processMessage();
            } else {
                //log.warn("[Raspberry]: m_inputStream is null");
            }
        }
        log.info("monitorThread will exit.");
    }
}
