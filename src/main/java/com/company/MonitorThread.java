package com.company;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorThread extends Thread {

    MessageQueue m_queue=null;
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

    public MonitorThread(MessageQueue queue) {
        super("monitorThread");
        m_queue=queue;
        m_shouldRun=true;
    }



    public void run() {

        log.info(" New monitorThread launched!");
        while(m_shouldRun) {
            if (m_queue != null) {
                m_queue.auditLastMessage();
            } else {
                //log.warn("[Raspberry]: m_inputStream is null");
            }
        }
        log.info("monitorThread will exit.");
    }
}
