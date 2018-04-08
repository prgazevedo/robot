package com.company.WorkingThreads;

import com.company.comms.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorThread extends Thread {

    ThreadManager m_threadManager;
    MessageRecordQueue m_queue;
    MessageRecordParser m_parser;
    WriteThread m_writeThread;
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

    public MonitorThread(ThreadManager threadManager) {
        super("monitorThread");
        m_threadManager = threadManager;
        m_queue= threadManager.getM_queue();
        m_shouldRun=true;
        m_parser= new MessageRecordParser();
    }

    private void processMessage(){
        SerialMessageRecord smr = m_queue.auditLastMessage();
        try {
            String recordPayload = m_parser.getRecordPayload(smr);
            MessagePayload messagePayload = MessagePayload.convertRecordtoMessagePayload(recordPayload);
            if(messagePayload!=null) {
                log.info("Message Payload Cmd detected. Is Tx?:"+smr.getTxMessage()+" Payload is: " + messagePayload.toString());
                handleMessage(messagePayload);
            }
            else{
                log.info("No Message Payload Cmd detected. SerialMessageRecord was: " + smr.toString());
            }
        }
        catch(Exception e)
        {
            log.error("processMessage - Exception:"+e.toString());
        }
    }

    private void handleMessage(MessagePayload messagePayload)
    {
        m_threadManager.Process(messagePayload.getM_Args(),messagePayload.getM_cmd_type());
    }

    public void run() {

        log.info(" New monitorThread launched!");
        while(m_shouldRun) {
            if (m_queue != null) {
                if(!m_queue.isEmpty())
                {
                    processMessage();
                }
            } else {
                log.warn("[Raspberry]: m_inputStream is null");
            }
        }
        log.info("monitorThread will exit.");
    }

    public void setWriteThread(WriteThread writeThread) {
        m_writeThread=writeThread;
    }
}