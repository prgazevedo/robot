package com.company;


//////////
//This is a builder pattern implementation to build immutable MessagePayload objects:
// This is the list of recognized commands.
// In order to receive, attach a callback function to these events:
// Return the command list: 0;
// Move: 1, <direction:FWD(0)/BWD(1)>,<speed:0-150>,<time: in ms>;
// Rotate: 2, <direction:LEFT(0)/RIGHT(1)>,<speed:0-150>,<time: in ms>;
// Scan: 3, <angle:0-180>;
// Ping Arduino - AreYouReady:4; Command to ask if we are ready
// Ack Arduino - Acknowledge:5; Ack from Arduino
// Arduino Requests to AskUsIfReady: 6;
// Arduino Acknowledges is ready: 7;

public final class MessagePayload {


    public static enum cmds{
                CommandList         , // Command to request list of available commands
                Move              , // Command to move
                Rotate    , // Command to rotate
                Scan              , // Command to scan
                // Setup connection test
                AreYouReady              , // Command to ask if other side is ready: RPI -> Arduino: "AreYouReady" will cause Arduino -> RPI: "Acknowledge
                Acknowledge              , // Command to acknowledge that cmd was received
                // Acknowledge test
                AskUsIfReady             , // Command to ask other side to ask if ready Arduino -> RPI: "AskUsIfReady" will cause RPI -> Arduino: "YouAreReady"
                YouAreReady              , // Command to acknowledge that other is ready
    } ;
    private final String separator=",";
    private final String terminator=";";


    private final cmds m_cmd_type;
    private final String m_arg1;
    private final String m_arg2;
    private final String m_arg3;

    public MessagePayload(MessagePayloadBuilder mpb) {

        this.m_cmd_type = mpb.getM_cmd_type();
        this.m_arg1 = mpb.getM_arg1();
        this.m_arg2 = mpb.getM_arg2();
        this.m_arg3 = mpb.getM_arg3();
    }



    public static class MessagePayloadBuilder {


        private cmds m_cmd_type=null;
        private String m_arg1=null;
        private String m_arg2=null;
        private String m_arg3=null;

        public MessagePayloadBuilder() {
        }

        public cmds getM_cmd_type() {
            return m_cmd_type;
        }

        public String getM_arg1() {
            return m_arg1;
        }

        public String getM_arg2() {
            return m_arg2;
        }

        public String getM_arg3() {
            return m_arg3;
        }


        public MessagePayloadBuilder cmd(cmds cmd_type) {
            this.m_cmd_type = cmd_type;
            return this;
        }

        public MessagePayloadBuilder Args(String arg1, String arg2, String arg3) {
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            this.m_arg3 = arg3;
            return this;
        }

        public MessagePayloadBuilder Args(String arg1, String arg2) {
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            return this;
        }

        public MessagePayloadBuilder Args(String arg1) {
            this.m_arg1 = arg1;
            return this;
        }

        public MessagePayload build() {
            return new MessagePayload(this);
        }
    }

    public cmds getM_cmd_type() {
        return m_cmd_type;
    }

    public String getM_arg1() {
        return m_arg1;
    }

    public String getM_arg2() {
        return m_arg2;
    }

    public String getM_arg3() {
        return m_arg3;
    }

    @Override
    public String toString() {
        if( m_cmd_type ==null) return "Error building payload";
        else if(m_arg1==null) return (m_cmd_type.toString()+terminator);
        else if(m_arg1!=null && m_arg2==null) return (m_cmd_type.toString()+separator+m_arg1+terminator);
        else if(m_arg2!=null && m_arg3==null) return (m_cmd_type.toString()+separator+m_arg1+separator+m_arg2+terminator);
        else if(m_arg3!=null) return (m_cmd_type.ordinal()+separator+m_arg1+separator+m_arg2+separator+m_arg3+terminator);
        else {
            return "Error building payload";
        }
    }
}
