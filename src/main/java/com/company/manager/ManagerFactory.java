package com.company.manager;

import com.company.MainRobot;
import com.company.comms.CommsManager;
import com.company.events.EventCaller;
import com.company.graph.GraphManager;
import com.company.graph.GraphViewer;
import com.company.movement.ActionManager;
import com.company.navigation.NavigationManager;
import com.company.navigation.PathManager;
import com.company.properties.PropertiesManager;
import com.company.state.StateManager;
import com.company.workingThreads.ThreadManager;

public class ManagerFactory extends Manager implements IManager{



    private ThreadManager m_ThreadManager;
    private GraphViewer m_GraphViewer;
    private NavigationManager m_NavigationManager;
    private ActionManager m_ActionManager;
    private CommsManager m_CommsManager;
    private PathManager m_PathManager;
    private GraphManager m_GraphManager;
    private EventCaller m_EventCaller;
    private StateManager m_StateManager;
    private PropertiesManager m_PropertiesManager;




    public ManagerFactory(MainRobot mainRobot) {
        m_mainRobot=mainRobot;
    }

    @Override
    public void initialize() {
        super.initialize();
        getStateManager();
        getPropertiesManager();
        getCommsManager();
        getThreadManager();
        getActionManager();
        getGraphManager();
        getNavigationManager();
        getPathManager();
       // getGraphViewer();
        getEventCaller();


    }




    public IManager getCommsManager(){
        if(m_CommsManager==null) {
            m_CommsManager = new CommsManager(m_mainRobot);
            m_CommsManager.initialize();
        }
        return m_CommsManager;
    }

    public IManager getActionManager(){
        if(m_ActionManager ==null){
            m_ActionManager =  new ActionManager(m_mainRobot);
            m_ActionManager.initialize();
        }

        return m_ActionManager;
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


    public IManager getEventCaller(){
        if(m_EventCaller==null) {
            m_EventCaller = new EventCaller(m_mainRobot);
            m_EventCaller.initialize();
        }
        return m_EventCaller;
    }

    public IManager getStateManager(){
        if(m_StateManager==null) {
            m_StateManager = new StateManager(m_mainRobot);
            m_StateManager.initialize();
        }
        return m_StateManager;
    }

    public IManager getPropertiesManager(){
        if(m_PropertiesManager==null) {
            m_PropertiesManager = new PropertiesManager(m_mainRobot);
            m_PropertiesManager.initialize();
        }
        return m_PropertiesManager;
    }

}
