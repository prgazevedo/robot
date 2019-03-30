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


import com.company.comms.CommsManager;
import com.company.events.EventCaller;
import com.company.graph.GraphManager;
import com.company.graph.GraphViewer;
import com.company.manager.Manager;
import com.company.manager.ManagerFactory;
import com.company.movement.ActionManager;
import com.company.navigation.NavigationManager;
import com.company.navigation.PathManager;
import com.company.properties.PropertiesManager;
import com.company.state.StateManager;
import com.company.workingThreads.ThreadManager;
import org.apache.logging.log4j.Level;

/**
 * Responsible to initialize the Managers
 */
public class MainRobot extends Manager {


    //private static MainRobot m_MainRobot;
    public ManagerFactory getMF() {
        return m_managerFactory;
    }

    private ManagerFactory m_managerFactory;

    public ThreadManager getM_ThreadManager() { return (ThreadManager)getMF().getThreadManager(); }
    public  NavigationManager getM_NavigationManager() { return (NavigationManager) getMF().getNavigationManager(); }
    public ActionManager getM_ActionManager() { return (ActionManager) getMF().getActionManager(); }
    public CommsManager getM_CommsManager() { return (CommsManager)getMF().getCommsManager(); }
    public PathManager getM_PathManager() { return (PathManager)getMF().getPathManager(); }
    public GraphManager getM_GraphManager() { return (GraphManager)getMF().getGraphManager(); }
    public EventCaller getM_EventCaller() { return (EventCaller)getMF().getEventCaller(); }
    public StateManager getM_StateManager() { return (StateManager)getMF().getStateManager(); }
    public PropertiesManager getM_PropertiesManager() { return (PropertiesManager)getMF().getPropertiesManager(); }

    public MainRobot() {
        m_managerFactory = new ManagerFactory(this);
        m_managerFactory.initialize();

    }

    @Override
    public void initialize()  {
        super.initialize();

        m_mainRobot.writeLog(Level.INFO, "Robot main initialize");
        try {
            m_mainRobot.getM_ActionManager().waitIfArduinoReady();
            m_mainRobot.writeLog(Level.INFO, "Robot Test if Arduino Ready completed");
            m_mainRobot.getM_ActionManager().testRobot();
            m_mainRobot.writeLog(Level.INFO, "Robot Test Robot Functions completed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getName(){
        return ApplicationProperties.APPLICATION_NAME;
    }

    public static void main(String[] args) throws Exception {
        m_mainRobot = new MainRobot();
        m_mainRobot.initialize();
        m_mainRobot.writeLog(Level.INFO, "Robot main start");
        //robot.getM_NavigationManager().runNavigator();
        while(m_mainRobot.getM_NavigationManager().runStepwiseMockNavigator()){
            m_mainRobot.getM_GraphManager().updateViewGraph();
            m_mainRobot.getM_GraphManager().updateSaveGraph();
        }

    }

    public void shutDown(int status){
        m_mainRobot.writeLog(Level.INFO,"shutDown has been called: Robot main code will now exit with code: "+status);
        System.exit(0);
    }


}