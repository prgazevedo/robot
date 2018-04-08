package com.company.WorkingThreads;


import com.company.IManager;
import com.company.MainRobot;
import com.company.Manager;
import com.company.WorkingThreads.MonitorThread;
import com.company.WorkingThreads.WriteThread;
import com.company.comms.CommsProperties;
import com.company.comms.MessageRecordQueue;
import com.company.movement.IMovement;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.Level;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ThreadManager extends Manager implements IMovement,IManager {




    /** The the serial port we shall use */
    private SerialPort m_comPort;
    MessageRecordQueue m_queue;
    private static WriteThread m_writeThread;
    private static MonitorThread m_monitorThread;
    private MainRobot m_mainRobot;


    public SerialPort getM_comPort() { return m_comPort; }
    public MessageRecordQueue getM_queue() { return m_queue; }
    public  WriteThread getM_writeThread() { return m_writeThread; }
    public  MonitorThread getM_monitorThread() { return m_monitorThread; }


    @Override
    public void initialize(){
        m_queue = m_mainRobot.getM_CommsManager().getM_queue();
        m_comPort = m_mainRobot.getM_CommsManager().getM_comPort();
        startWriteThread();
        startMonitorThread();
    }

    @Override
    public void writeLog(Level messageLevel, String message) { m_mainRobot.writeLog(messageLevel,this.getClass().toString()+":"+message); }



    @Override
    public void look(int degrees) {
        m_writeThread.look(degrees);
        sleep();
    }

    @Override
    public void move(boolean fwd_direction, int speed, int time) {
        m_writeThread.move(fwd_direction,speed,time);
        sleep();
    }

    @Override
    public void rotate(boolean west_direction, int speed, int time) {
        m_writeThread.rotate(west_direction,speed,time);
        sleep();

    }


    public ThreadManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        createWriteThread();
        createMonitorThread();

    }


    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(CommsProperties.DEFAULT_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void createWriteThread() {
        if(m_mainRobot.getM_CommsManager().isComPortOpen()){
            m_writeThread = new WriteThread(this);
        }
        else writeLog(Level.ERROR,"createWriteThread: COM port is not open - could not create");

    }

    private void createMonitorThread() {
        if(m_mainRobot.getM_CommsManager().isComPortOpen()){
            m_monitorThread = new MonitorThread(this);
            m_monitorThread.setWriteThread(m_writeThread);
        }
        else writeLog(Level.ERROR,"createWriteThread: COM port is not open - could not create");

    }






    //TODO: Move into a Comms Manager class
    public void checkComms() {


        m_writeThread.pingArduinoAskUsIfReady();
        sleep();
        m_writeThread.pingArduinoAskUsIfReady();
        sleep();

    }

    private void startWriteThread()
    {

        m_writeThread.start();
        writeLog(Level.INFO," writeThread Started");
    }

    private void startMonitorThread()
    {

        m_monitorThread.start();
        writeLog(Level.INFO," monitorThread Started");
    }


    public void Process(ArrayList<String> m_args, CommsProperties.cmds m_cmd_type) {
        if(m_cmd_type.equals(CommsProperties.cmds.AreYouReady))
        {
            m_writeThread.AckWeAreReady();
        }
        else if(m_cmd_type.equals(CommsProperties.cmds.Acknowledge))
        {
           //TODO
        }
    }
}
