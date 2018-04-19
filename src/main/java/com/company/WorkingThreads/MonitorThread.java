package com.company.WorkingThreads;

import com.company.comms.*;
import com.company.events.EventNotifier;
import com.company.events.IEvent;
import org.apache.commons.collections15.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

public class MonitorThread extends Thread {

    ThreadManager m_threadManager;
    MessageRecordQueue m_queue;
    MessageRecordParser m_parser;
    WriteThread m_writeThread;
    EventNotifier m_caller;
    //LinkedMap<IEvent.EVENT,EventNotifier> m_caller;
    private final static Logger log =  LogManager.getLogger(SerialListener.class);
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
        //m_caller = new LinkedMap<IEvent.EVENT,EventNotifier>();
    }

    private void processMessage(){
        SerialMessageRecord smr = m_queue.auditLastMessage();
        try {
            String recordPayload = m_parser.getRecordPayload(smr);
            MessagePayload messagePayload = MessagePayload.convertRecordtoMessagePayload(recordPayload);
            if(messagePayload!=null) {
                if(smr.getTxMessage()){
                    log.info("Message Payload Cmd detected. Is Tx?:"+smr.getTxMessage()+" Payload is: " + messagePayload.toString());
                }
                else
                {
                    log.info("process() called:"+messagePayload.getM_cmd_type().toString());
                    Process(messagePayload.getM_Args(),messagePayload.getM_cmd_type());
                }

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

    public void notifyME(EventNotifier eventNotifier) {
        m_caller=eventNotifier;

    }

    public void Process(ArrayList<String> m_args, CommsProperties.cmds m_cmd_type) {
        if(m_cmd_type.equals(CommsProperties.cmds.AreYouReady))
        {
            m_writeThread.AckWeAreReady();
        }
        else if(m_cmd_type.equals(CommsProperties.cmds.AckMove) ||
                m_cmd_type.equals(CommsProperties.cmds.AckRotate) ||
                m_cmd_type.equals(CommsProperties.cmds.AckRotate) ||
                m_cmd_type.equals(CommsProperties.cmds.Acknowledge))
        {

            log.info("Process called doWork()");
            m_caller.doCallback(m_cmd_type, m_args);

        }
    }
}
