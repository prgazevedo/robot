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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageRecordQueue {
    private String nameQueue;
    private BlockingQueue<SerialMessageRecord> m_queue;
    private final static Logger logger =  LogManager.getLogger(MessageRecordQueue.class);

    public MessageRecordQueue(BlockingQueue<SerialMessageRecord> queue) {
        this.m_queue = queue;
    }

    public MessageRecordQueue(String name) {
        nameQueue=name;
        m_queue = new ArrayBlockingQueue<SerialMessageRecord>(1000);
    }


    public synchronized void add(SerialMessageRecord message) {
        m_queue.add(message);

        logger.debug("Added[Queue:{}, QueueSize:{}, Message:{}]", nameQueue, m_queue.size(), message);
    }


    public synchronized SerialMessageRecord take() {
        if (!m_queue.isEmpty()) {
            SerialMessageRecord message = m_queue.remove();

            logger.debug("Removed[Queue:{}, QueueSize:{}, Message:{}]", nameQueue, m_queue.size(), message);
            return message;
        } else {
            logger.warn("There is no message in the queue, returning null");
            return null;
        }
    }

    public void logContents(){
        if(m_queue!=null) {
            logger.trace("Queue size is {}", m_queue.size());
            logger.trace("Queue data is {}", m_queue.toString());
        }

    }

    public void auditAllContents(){
        if(m_queue!=null) {
            logger.debug("auditAllContents, take all messages from message queue, queue size is {}", m_queue.size());
            while(!m_queue.isEmpty()){
               try{
                   logger.info("Queue data is {}",m_queue.take().toString());
               } catch (Exception e){
                   logger.error("auditAllContents - Exception Error taking from Message queue:"+e);
               }
            }
        }
    }

    private SerialMessageRecord getLastMessageRecord()
    {
        SerialMessageRecord messageRecord = null;
        if(m_queue!=null) {
            if(m_queue.size()>0) logger.trace("getLastMessageRecord, take last message from message queue size is {}", m_queue.size());
            if(!m_queue.isEmpty()){
                logger.trace("Queue data is not empty");
                try{

                    messageRecord = m_queue.take();
                    logger.trace("getLastMessageRecord Queue data was {}",messageRecord.toString());
                } catch (Exception e){
                    logger.error("getLastMessageRecord - Exception - Error taking from Message queue:"+e);
                }
                if(messageRecord==null)
                {
                    logger.error("getLastMessageRecord - Error taking from Message queue");
                }
            }
        }

        return messageRecord;
    }

    public SerialMessageRecord auditLastMessage(){
        SerialMessageRecord messageRecord=getLastMessageRecord();
        if(messageRecord!=null)
        {
            logger.trace("SerialMessageRecord in Queue is {}",messageRecord.toString());

        }
        if(messageRecord==null)
        {
            logger.error("auditLastMessage SerialMessageRecord in Queue is null.");

        }
        return messageRecord;
    }

    public synchronized boolean isEmpty() {
        return m_queue.isEmpty();
    }


    public synchronized void clear() {
        m_queue.clear();
    }



}
