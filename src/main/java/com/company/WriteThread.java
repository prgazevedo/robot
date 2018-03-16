package com.company;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

public class WriteThread extends Thread {


    /** The the serial port we shall use */
    private static SerialPort m_comPort=null;
    /** The output stream to the port */
    private static OutputStream m_outputStream=null;
    /** The input stream to the port */
    private static InputStreamReader m_inputStream=null;
    private static MessageQueue m_queue=null;
    private static Logger log =  LogManager.getLogger(SeriaListener.class);
    private boolean m_shouldRun = false;
    private static MessageParser m_parser=null;

    public boolean isM_shouldRun() {
        return m_shouldRun;
    }

    public void setM_shouldRun(boolean m_shouldRun) {
        this.m_shouldRun = m_shouldRun;
    }



    public WriteThread(String name) {
        super(name);
    }

    public WriteThread(MessageQueue queue, SerialPort serialPort) {
        super("writeThread");
        m_queue=queue;
        m_comPort=serialPort;
        m_shouldRun=true;
        m_parser= new MessageParser();
        m_outputStream = m_comPort.getOutputStream();
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
        SerialMessage message = null;
        try{
         message = m_parser.getMessage(bytes);
         message.setTxMessage(true);
         message.setTimestamp(System.currentTimeMillis());
        } catch (Exception e){
            log.error(e);
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
        System.out.println("Enter command: (wasdx or jkl) EXIT to exit:");
        //while (!cmd.equals("EXIT")) {

            cmd = in.next();
            return cmd;
        //}
    }



    public void run() {
        try {
            Thread.currentThread().setName("writeThread");
            log.info(" New Write thread launched!");
            String cmd = "";
            while (!(cmd=readFromConsole()).equals("EXIT"))
            {

                if (cmd.equals("w") || cmd.equals("a")|| cmd.equals("d") || cmd.equals("s") || cmd.equals("x") || cmd.equals("j") || cmd.equals("k") || cmd.equals("l")  )
                {
                    writeStream(cmd);
                    writeinQueue(cmd.getBytes());
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
