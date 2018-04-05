package com.company;


import com.company.movement.Movement;
import com.company.movement.MovementManager;
import com.company.navigation.GraphManager;
import com.company.navigation.GraphProperties;
import com.company.navigation.GraphViewer;
import com.company.navigation.NavigationManager;

public class RobotProxy implements Movement{


    private static GraphManager m_mp;
    private static GraphViewer m_gv;
    private static NavigationManager m_NavigationManager;
    private  MainRobot m_mainRobot;

    public static MovementManager getM_MovementManager() {
        return m_MovementManager;
    }

    private static MovementManager m_MovementManager;

    public static NavigationManager getM_NavigationManager() {
        return m_NavigationManager;
    }



    public void move(boolean fwd_direction, int speed, int time) {

    }


    public void rotate(boolean west_direction, int speed, int time) {

    }

    public RobotProxy(MainRobot mainRobot) {
        m_mainRobot = new MainRobot();

        m_mp  = new GraphManager(GraphProperties.N_VERTEXES);
        m_gv = new GraphViewer("Graph", m_mp );
        m_NavigationManager = new NavigationManager(m_mp);
        m_MovementManager = new MovementManager(this);
    }


    public static void main(String[] args) {

        m_NavigationManager.runMockNavigator();
        m_gv.viewGraph();
    }


    public void checkComms() {


        /*
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        m_writeThread.pingArduinoAskUsIfReady();
        TimeUnit.SECONDS.sleep(1);
        */
    }
}
