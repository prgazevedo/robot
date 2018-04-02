package com.company.movement;

import com.company.WriteThread;
import com.company.navigation.Direction;
import com.company.navigation.MainNavigation;

import java.util.concurrent.TimeUnit;

public class MovementManager extends Thread{

    /** Write Thread */
    private static WriteThread m_writeThread=null;
    private static MainNavigation m_navigationThread=null;

    public MovementManager(WriteThread writeThread, MainNavigation navigationThread) {
        m_writeThread=writeThread;
        m_navigationThread=navigationThread;
    }


    public void init() throws Exception{
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        //Test write to Arduino
        m_writeThread.moveForward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveBackward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveForward(100,1000);
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.moveBackward(100,1000);
    }

    private void move(Direction direction){

    }
}
