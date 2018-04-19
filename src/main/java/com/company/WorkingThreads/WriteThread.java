package com.company.WorkingThreads;

import com.company.comms.*;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;


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

public class WriteThread extends Thread {


    /** The the serial port we shall use */
    private static SerialPort m_comPort=null;
    /** The output stream to the port */
    private static OutputStream m_outputStream=null;
    /** The input stream to the port */
    private static InputStreamReader m_inputStream=null;
    private static MessageRecordQueue m_queue=null;
    private static Logger log =  LogManager.getLogger(SerialListener.class);
    private boolean m_shouldRun = false;
    private static MessageRecordParser m_parser=null;
    private MessagePayload.MessagePayloadBuilder m_MPB;
    public boolean isM_shouldRun() {
        return m_shouldRun;
    }
    public void setM_shouldRun(boolean m_shouldRun) {
        this.m_shouldRun = m_shouldRun;
    }
    private ThreadManager m_threadManager;

    public WriteThread(String name) {
        super(name);
    }

    public WriteThread(ThreadManager threadManager){
        super("writeThread");
        m_threadManager = threadManager;
        m_queue=m_threadManager.getM_queue();
        m_comPort=threadManager.getM_comPort();
        m_shouldRun=true;
        m_parser= new MessageRecordParser();
        m_outputStream = m_comPort.getOutputStream();
        //Create Payload Builder
        m_MPB = new MessagePayload.MessagePayloadBuilder();
    }


    /**
     * WriteStream
     */
    private static synchronized void writeStream(String cmd) {
        if (m_comPort != null) {
            log.info("Writing to serial:"+cmd);
            try {
                m_outputStream.write(cmd.getBytes()); // Write to serial
                //m_outputStream.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static synchronized void writeinQueue(byte[] bytes){//Send Message
        SerialMessageRecord message = null;
        try{
         message = m_parser.getMessage(bytes);
         message.setTxMessage(true);
         message.setTimestamp(System.currentTimeMillis());
        } catch (Exception e){
            log.error("writeinQueue - Exception: "+ e);
        }
        if (message != null)
        {
            m_queue.add(message);
        }
    }

    /**
     * ReadStream
     */
    public static synchronized void readStream() {

       log.info(" readStream called \n");
        if (m_comPort != null) {
            try (Scanner scanner = new Scanner(m_inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    log.info(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String readFromConsole(){
        Scanner in = new Scanner(System.in);
        String cmd = "";
        System.out.println("Enter command: or EXIT to exit:");
        cmd = in.next();
        return cmd;

    }

    public void requestCmdList() {
        m_MPB.setM_cmd_type(CommsProperties.cmds.CommandList);
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());
    }


    public void AckWeAreReady() {
        m_MPB.setM_cmd_type(CommsProperties.cmds.Acknowledge);
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());
    }

    public void pingArduinoIsReady()
    {
        m_MPB.setM_cmd_type(CommsProperties.cmds.AreYouReady);
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());
    }

    public void pingArduinoAskUsIfReady()
    {
        m_MPB.setM_cmd_type(CommsProperties.cmds.AskUsIfReady);
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());
    }





    public void move(boolean fwd_direction,int speed, int time )
    {
        m_MPB.setM_cmd_type(CommsProperties.cmds.Move);
        if(fwd_direction) m_MPB.addArg("1");
        else m_MPB.addArg("0");
        m_MPB.addArg(String.valueOf(speed));
        m_MPB.addArg(String.valueOf(time));
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());

    }

    public void rotate(boolean west_direction,int speed, int time )
    {
        m_MPB.setM_cmd_type(CommsProperties.cmds.Rotate);
        if(west_direction) m_MPB.addArg("1"); //LEFT
        else  m_MPB.addArg("0"); //RIGHT
        m_MPB.addArg(String.valueOf(speed));
        m_MPB.addArg(String.valueOf(time));
        MessagePayload payload= m_MPB.build();
        sendMessage(payload.toSerial());
    }

    public void look(int degrees )
    {
        m_MPB.setM_cmd_type(CommsProperties.cmds.Look);
        m_MPB.addArg(String.valueOf(degrees));
        MessagePayload payload = m_MPB.build();
        sendMessage(payload.toSerial());
    }


    private void sendMessage(String cmd)
    {
        writeStream(cmd);
        writeinQueue(cmd.getBytes());
    }


    public void run() {
        try {
            Thread.currentThread().setName("writeThread");
            log.info(" New Write thread launched!");
            String cmd = "";
            while (!(cmd=readFromConsole()).equals("EXIT"))
            {
                if(cmd.endsWith(";"))
                {
                    log.info("Cmd: "+cmd+" - is well terminated (;). Sending to serial");
                    sendMessage(cmd);
                }
                else
                {
                    System.out.println("Cmd: "+cmd+"- is not well terminated. Add (;) at end");
                }

            }

        } catch (Exception e) {
            log.error("Error writing to Serial");
            throw(e);
        }
        m_comPort.closePort();
        log.info(" Exiting! CLOSE COM");
    }



}
