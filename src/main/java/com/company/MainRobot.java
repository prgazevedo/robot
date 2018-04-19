/*
 * Copyright 2018 Pedro Azevedo (prgazevedo@gmail.com)
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.company;


import com.company.WorkingThreads.ThreadManager;

import com.company.comms.CommsManager;
import com.company.events.EventCaller;
import com.company.manager.Manager;
import com.company.manager.ManagerFactory;
import com.company.movement.ActionManager;
import com.company.graph.GraphManager;
import com.company.graph.GraphViewer;
import com.company.navigation.NavigationManager;
import com.company.navigation.PathManager;
import com.company.state.StateManager;
import org.apache.logging.log4j.Level;

/**
 * Responsible to initialize the Managers
 */
public class MainRobot extends Manager {



    public ManagerFactory getMF() {
        return m_managerFactory;
    }

    private ManagerFactory m_managerFactory;

    public ThreadManager getM_ThreadManager() { return (ThreadManager)getMF().getThreadManager(); }
    public GraphViewer getM_GraphViewer() { return (GraphViewer)getMF().getGraphViewer(); }
    public  NavigationManager getM_NavigationManager() { return (NavigationManager) getMF().getNavigationManager(); }
    public ActionManager getM_ActionManager() { return (ActionManager) getMF().getActionManager(); }
    public CommsManager getM_CommsManager() { return (CommsManager)getMF().getCommsManager(); }
    public PathManager getM_PathManager() { return (PathManager)getMF().getPathManager(); }
    public GraphManager getM_GraphManager() { return (GraphManager)getMF().getGraphManager(); }
    public EventCaller getM_EventCaller() { return (EventCaller)getMF().getEventCaller(); }
    public StateManager getM_StateManager() { return (StateManager)getMF().getStateManager(); }


    public MainRobot() {
        m_managerFactory = new ManagerFactory(this);
        m_managerFactory.initialize();
    }




    public String getName(){
        return ApplicationProperties.APPLICATION_NAME;
    }

    public static void main(String[] args) throws Exception {
        MainRobot robot = new MainRobot();
        robot.writeLog(Level.INFO, "Robot main start");
        robot.initialize();
        robot.writeLog(Level.INFO, "Robot main initialize");
        robot.getM_ActionManager().testIfArduinoReady();
        robot.writeLog(Level.INFO, "Robot Test if Arduino Ready");
        while(!robot.getM_StateManager().isM_bIsArduinoReady())
        {
            robot.writeLog(Level.INFO, "Robot waiting for Arduino to be ready...(sleep 1 sec)");
            robot.getM_ThreadManager().sleep();
        }
        robot.getM_ActionManager().testRobot();
        //robot.getM_NavigationManager().runMockNavigator();
        robot.getM_GraphViewer().viewGraph();


    }

}