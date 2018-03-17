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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageQueue {
    private String nameQueue;
    private BlockingQueue<SerialMessage> m_queue;
    private final static Logger logger =  LogManager.getLogger(MessageQueue.class);

    public MessageQueue(BlockingQueue<SerialMessage> queue) {
        this.m_queue = queue;
    }

    public MessageQueue(String name) {
        nameQueue=name;
        m_queue = new ArrayBlockingQueue<SerialMessage>(1000);
    }


    public synchronized void add(SerialMessage message) {
        m_queue.add(message);

        logger.debug("Added[Queue:{}, QueueSize:{}, Message:{}]", nameQueue, m_queue.size(), message);
    }


    public synchronized SerialMessage take() {
        if (!m_queue.isEmpty()) {
            SerialMessage message = m_queue.remove();

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
                   logger.error("Error taking from Message queue:"+e);
               }
            }
        }
    }

    public void auditLastMessage(){
        if(m_queue!=null) {
            if(m_queue.size()>0) logger.info("auditLastMessage, take last message from message queue size is {}", m_queue.size());
            if(!m_queue.isEmpty()){
                logger.trace("Queue data is not empty");
                try{
                    logger.info("Queue data is {}",m_queue.take().toString());
                } catch (Exception e){
                    logger.error("Error taking from Message queue:"+e);
                }
            }
        }
    }

    public synchronized boolean isEmpty() {
        return m_queue.isEmpty();
    }


    public synchronized void clear() {
        m_queue.clear();
    }



}
