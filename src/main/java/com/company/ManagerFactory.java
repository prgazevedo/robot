package com.company;

import com.company.WorkingThreads.ThreadManager;
import com.company.comms.CommsManager;
import com.company.events.EventCaller;
import com.company.graphviewer.GraphViewer;
import com.company.movement.MovementManager;
import com.company.navigation.GraphManager;
import com.company.navigation.NavigationManager;
import com.company.navigation.PathManager;
import org.apache.logging.log4j.Level;

public class ManagerFactory implements IManager{

    MainRobot m_mainRobot;

    private ThreadManager m_ThreadManager;
    private GraphViewer m_GraphViewer;
    private NavigationManager m_NavigationManager;
    private MovementManager m_MovementManager;
    private CommsManager m_CommsManager;
    private PathManager m_PathManager;
    private GraphManager m_GraphManager;
    private EventCaller m_EventCaller;




    public ManagerFactory(MainRobot mainRobot) {
        m_mainRobot=mainRobot;
    }

    @Override
    public void initialize() {
        getCommsManager();
        getThreadManager();
        getMovementManager();
        getGraphManager();
        getNavigationManager();
        getPathManager();

        getGraphViewer();
        getEventCaller();
    }

    @Override
    public  void writeLog(Level messageLevel, String message){ m_mainRobot.writeLog(messageLevel,message); }


    public IManager getCommsManager(){
        if(m_CommsManager==null) {
            m_CommsManager = new CommsManager(m_mainRobot);
            m_CommsManager.initialize();
        }
        return m_CommsManager;
    }

    public IManager getMovementManager(){
        if(m_MovementManager==null){
            m_MovementManager =  new MovementManager(m_mainRobot);
            m_MovementManager.initialize();
        }

        return m_MovementManager;
    }

    public IManager getGraphManager(){
        if(m_GraphManager==null) {
            m_GraphManager = new GraphManager(m_mainRobot);
            m_GraphManager.initialize();
        }
        return m_GraphManager;
    }

    public IManager getNavigationManager(){
        if(m_NavigationManager==null) {
            m_NavigationManager = new NavigationManager(m_mainRobot);
            m_NavigationManager.initialize();
        }
        return m_NavigationManager;
    }

    public IManager getPathManager(){
        if(m_PathManager==null){
            m_PathManager =  new PathManager(m_mainRobot);
            m_PathManager.initialize();
        }
        return m_PathManager;

    }

    public IManager getThreadManager(){
        if(m_ThreadManager ==null){
            m_ThreadManager =  new ThreadManager(m_mainRobot);
            m_ThreadManager.initialize();
        }
        return m_ThreadManager;

    }

    public IManager getGraphViewer(){
        if(m_GraphViewer==null) {
            m_GraphViewer = new GraphViewer(m_mainRobot);
            m_GraphViewer.initialize();
        }
        return m_GraphViewer;
    }

    public IManager getEventCaller(){
        EventCaller m_EventCaller = null;
        if(m_EventCaller==null) {
            m_EventCaller = new EventCaller(m_mainRobot);
            m_EventCaller.initialize();
        }
        return m_EventCaller;
    }

}
