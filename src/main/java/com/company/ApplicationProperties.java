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
import org.apache.logging.log4j.Level;


public class ApplicationProperties {

    /** Application name */
    public static final String APPLICATION_NAME = "Robot";
    public static final String CMD_SEPARATOR=",";
    public static final String CMD_TERMINATOR=";";
    /** Application debug level */
    public static final  org.apache.logging.log4j.Level LOG_LEVEL = Level.INFO ;
    /** Milliseconds to block while waiting for port open */
    public static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    public static final int DATA_RATE = 115200;
    /** Data bits to be sent: We shall use 8-N-1: one start bit, the eight data bits, and the one stop bit */
    public static final int DATABITS = 8;
    /** Parity: Acceptable values are NO_PARITY, EVEN_PARITY, ODD_PARITY, MARK_PARITY, and SPACE_PARITY. */
    public static final int PARITY = 0;
    /** Stop bits to be sent */
    public static final int STOPBITS = 1;
    /** Maximum size of message */
    public static final int SERIAL_DATA_MAX_SIZE = 1000;
    /** Acceptable Message splitter chars for serial message */
    private static final byte MESSAGE_SPLITTERS[] = {
            '\n',
            '\r'
    };


    public enum cmds{
        CommandList         , // 0-Command to request list of available commands
        Move              , // 1-Command to move
        Rotate    , // 2-Command to rotate
        Scan              , // 3-Command to scan
        // Setup connection test
        AreYouReady              , // 4-Command to ask if other side is ready: RPI -> Arduino: "AreYouReady" will cause Arduino -> RPI: "Acknowledge
        Acknowledge              , // 5-Command to acknowledge that cmd was received
        // Acknowledge test
        AskUsIfReady             , // 6-Command to ask other side to ask if ready Arduino -> RPI: "AskUsIfReady" will cause RPI -> Arduino: "YouAreReady"
        YouAreReady              , // 7-Command to acknowledge that other is ready
        Error,                      // 8-Error
        None, //No comand was identified
    } ;




    public static int getSerialDataMaxSize() {
        return SERIAL_DATA_MAX_SIZE;
    }


    /** Returns true if byte is Message splitter in serial message */
    public static final boolean isMessageSplitter(byte b){
        for(int i=0; i<MESSAGE_SPLITTERS.length;i++)
        {
            if(b==MESSAGE_SPLITTERS[i]) return true;
        }
        return false;
    }
    public static final boolean isMessageOversize(int buffersize) {
        if (buffersize >= ApplicationProperties.SERIAL_DATA_MAX_SIZE) return true;
        else return false;

    }



}


