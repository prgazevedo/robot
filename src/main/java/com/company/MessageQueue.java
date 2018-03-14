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
    BlockingQueue<SerialMessage> queue;
    private final static Logger logger =  LogManager.getLogger(MessageQueue.class);

    public MessageQueue(BlockingQueue<SerialMessage> queue) {
        this.queue = queue;
    }

    public MessageQueue(String name) {
        nameQueue=name;
        queue = new ArrayBlockingQueue<SerialMessage>(1000);
    }


    public synchronized void add(SerialMessage message) {
        queue.add(message);

        logger.debug("Added[Queue:{}, QueueSize:{}, Message:{}]", nameQueue, queue.size(), message);
    }


    public synchronized SerialMessage take() {
        if (!queue.isEmpty()) {
            SerialMessage message = queue.remove();

            logger.debug("Removed[Queue:{}, QueueSize:{}, Message:{}]", nameQueue, queue.size(), message);
            return message;
        } else {
            logger.warn("There is no message in the queue, returning null");
            return null;
        }
    }

    public String dumptoString(){
        return queue.toString();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }


    public synchronized void clear() {
        queue.clear();
    }



}
