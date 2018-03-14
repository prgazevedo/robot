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


public class ApplicationProperties {

    /** Application name */
    public static final String APPLICATION_NAME = "MyController.org";
    /** Milliseconds to block while waiting for port open */
    public static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    public static final int DATA_RATE = 115200;
    public static final int DATABITS = 8;
    public static final int PARITY = 0;
    //Acceptable values are NO_PARITY, EVEN_PARITY, ODD_PARITY, MARK_PARITY, and SPACE_PARITY.
    public static final int STOPBITS = 1;
    // Message splitter char for serial message
    //public static final byte MESSAGE_SPLITTER = '\n';
    private static final byte MESSAGE_SPLITTERS[] = {
            '\n',
            '\r'
    };

    public static final int SERIAL_DATA_MAX_SIZE = 1000;
    // Message splitter char for serial message
    public static final boolean isMessageSplitter(byte buffer){
        for(int i=0; i<MESSAGE_SPLITTERS.length;i++)
        {
            if(buffer==MESSAGE_SPLITTERS[i]) return true;
        }
        return false;
    }



}


