package com.company.workingThreads;

import com.company.comms.*;
import com.company.events.EventNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MonitorThread extends Thread {

    ThreadManager m_threadManager;
    MessageRecordQueue m_queue;
    MessageRecordParser m_parser;
    WriteThread m_writeThread;
    EventNotifier m_caller;

    private static Logger log;
    private boolean m_shouldRun = false;





    public MonitorThread(String name) {
        super(name);
    }

    public MonitorThread(ThreadManager threadManager) {
        super("monitorThread");
        m_threadManager = threadManager;
        m_queue= threadManager.getM_queue();
        m_shouldRun=true;
        m_parser= new MessageRecordParser();
        log =  LogManager.getLogger(MonitorThread.class);
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
            log.error("processMessage - Exception: Serial Message Record:"+smr.toString());
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
                m_cmd_type.equals(CommsProperties.cmds.Acknowledge) ||
                        m_cmd_type.equals(CommsProperties.cmds.AckLook) )
        {

            log.info("Process called doWork() for: "+ m_cmd_type.toString());
            m_caller.doCallback(m_cmd_type, m_args);

        }
    }
}
