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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagePayload {



    private final ApplicationProperties.cmds m_cmd_type;
    private final ArrayList<String> m_Args;


    public MessagePayload(MessagePayloadBuilder mpb) {

        this.m_cmd_type = mpb.getM_cmd_type();
        this.m_Args = new ArrayList<String>(mpb.getM_Args());

    }

    public ApplicationProperties.cmds getM_cmd_type() {
        return m_cmd_type;
    }

    public String getArg(int i) {
        if(m_Args!=null) {
            if(i<m_Args.size()) {
                return this.m_Args.get(i);
            }
        }
        return null;
    }

    public ArrayList<String> getM_Args(int i) {
        return this.m_Args;
    }


    public String toSerial() {
        String s="";
        if( m_cmd_type ==null) return s;
        else {

            s = String.valueOf(m_cmd_type.ordinal());
            if(m_Args!=null) {
                for (String arg : m_Args) {
                    s += ApplicationProperties.CMD_SEPARATOR;
                    s += arg;
                }
            }
            s += ApplicationProperties.CMD_TERMINATOR;

        }
        return s;
    }


    @Override
    public String toString() {
        String s="";
        if( m_cmd_type ==null) return s;
        else {

            s = m_cmd_type.toString();
            if(m_Args!=null) {
                for (String arg : m_Args) {
                    s += ApplicationProperties.CMD_SEPARATOR;
                    s += arg;
                }
            }
            s += ApplicationProperties.CMD_TERMINATOR;

        }
        return s;
    }
    /** MessagePayloadBuilder ******************************/
    public static class MessagePayloadBuilder {

        private ApplicationProperties.cmds m_cmd_type=null;
        private  ArrayList<String> m_Args=null;

        public ApplicationProperties.cmds getM_cmd_type() {
            return m_cmd_type;
        }

        public void setM_cmd_type(ApplicationProperties.cmds m_cmd_type) {
            this.m_cmd_type = m_cmd_type;
        }

        public void setM_Args(ArrayList<String> m_Args) {
            this.m_Args = m_Args;
        }



        public ArrayList<String> getM_Args() {
            return m_Args;
        }




        public void setcmdType(String cmd) {
            m_cmd_type = convertCmdType(cmd);
        }

        public void addArg(String arg)
        {
            m_Args.add(arg);
        }

        public ApplicationProperties.cmds convertCmdType(String str){

            String cmd = str;//str.substring(0,str.indexOf(","));
            if(cmd!=null)
            {
                if (ApplicationProperties.cmds.valueOf(cmd) != null) {
                    return ApplicationProperties.cmds.valueOf(cmd);
                } else {
                    return ApplicationProperties.cmds.None;
                }
            }
            else
            {
                throw new RuntimeException(String.format("There is no cmd in String (%s)",str));
            }
        }

        public void parseMessagePayLoad(String payload){
            List<String> cmdlist = new ArrayList<String>(Arrays.asList(payload.split(ApplicationProperties.CMD_SEPARATOR)));
            int index=0;
            for (String s : cmdlist) {

                //only 1 argument
                if (cmdlist.size() == 1) {
                    if (s.substring(s.length() - 1).equals(ApplicationProperties.CMD_TERMINATOR)) {
                        //Is terminated
                        String cmd = s.substring(0, s.length() - 1);
                        setcmdType(cmd);

                    } else {
                        //Is not terminated
                        throw new RuntimeException("Message was not terminated - payload:" + payload + " string:" + s);
                    }
                }
                //Multiple arguments
                else
                {
                    //is first
                    if(index==0)
                    {
                        setcmdType(s);
                    }

                    else
                    {
                        //if terminated by ; remove the terminator
                        if(s.substring(s.length() - 1).equals(ApplicationProperties.CMD_TERMINATOR))
                        {
                            s=s.substring(0,s.length() - 1);
                        }
                        m_Args.add(s);
                    }
                }
                index++;

            }

        }


        private void clear(){
            m_cmd_type=null;
            m_Args.clear();
        }


        public MessagePayloadBuilder() {
            m_Args= new ArrayList<String>();
        }


        public MessagePayload build() {
            //Make the copy
            MessagePayload tmp =  new MessagePayload(this);
            this.clear();
            return tmp;
        }

        public MessagePayload build(String payload) {
            parseMessagePayLoad(payload);
            //Make the copy
            MessagePayload tmp =  new MessagePayload(this);
            this.clear();
            return tmp;
        }
    }


}