package com.company.WorkingThreads;


import com.company.MainRobot;
import com.company.WorkingThreads.MonitorThread;
import com.company.WorkingThreads.WriteThread;
import com.company.comms.CommsProperties;
import com.company.movement.Movement;
import com.company.movement.MovementManager;
import org.apache.logging.log4j.Level;


import java.util.concurrent.TimeUnit;

public class RobotProxy implements Movement{


    private static WriteThread m_writeThread=null;
    private static MonitorThread m_monitorThread=null;
    private MainRobot m_mainRobot;

    public  WriteThread getM_writeThread() { return m_writeThread; }
    public  MonitorThread getM_monitorThread() { return m_monitorThread; }

    private void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ m_mainRobot.writeLog(messageLevel,this.getClass().toString()+":"+message); }

    public RobotProxy(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        createWriteThread();
        createMonitorThread();

    }


    public void initialize(){
        startWriteThread();
        startMonitorThread();
    }


    public void move(boolean fwd_direction, int speed, int time) {
        m_writeThread.move(fwd_direction,speed,time);
        sleep();
    }


    public void rotate(boolean west_direction, int speed, int time) {
        m_writeThread.rotate(west_direction,speed,time);
        sleep();

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
            m_writeThread = new WriteThread(m_mainRobot.getM_CommsManager().getM_queue(),m_mainRobot.getM_CommsManager().getM_comPort());
        }
        else writeLog(Level.ERROR,"createWriteThread: COM port is not open - could not create");

    }

    private void createMonitorThread() {
        if(m_mainRobot.getM_CommsManager().isComPortOpen()){
            m_monitorThread = new MonitorThread(m_mainRobot.getM_CommsManager().getM_queue());
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


}
