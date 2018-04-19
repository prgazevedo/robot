package com.company.comms;

public class CommsProperties {

    /** Cmd messages separators */
    public static final String CMD_SEPARATOR=",";
    public static final String CMD_TERMINATOR=";";
    /** Seconds to block while waiting  */
    public static final int DEFAULT_SLEEP = 1;
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
        Look, // 3-Command to scan
        // Setup connection test
        // Order is PC->Arduino(6-AskUs) Arduino->PC(4-AreYou) PC->Arduino(5-Ack) Arduino->PC(7-YouAre)
        AreYouReady              , // 4-Command to ask if other side is ready: RPI -> Arduino: "AreYouReady" will cause Arduino -> RPI: "Acknowledge
        Acknowledge              , // 5-Command to acknowledge that cmd was received
        // Acknowledge test
        AskUsIfReady             , // 6-Command to ask other side to ask if ready Arduino -> RPI: "AskUsIfReady" will cause RPI -> Arduino: "YouAreReady"
        AckYouAreReady              , // 7-Command to acknowledge that other is ready
        Error,                      // 8-Error
        AckMove              , // 9-Command to move
        AckRotate    , // 10-Command to rotate
        AckLook, // 11-Command to scan
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
        if (buffersize >= SERIAL_DATA_MAX_SIZE) return true;
        else return false;

    }
}
