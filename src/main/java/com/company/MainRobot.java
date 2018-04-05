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


import com.company.WorkingThreads.RobotProxy;

import com.company.comms.CommsManager;
import com.company.movement.MovementManager;
import com.company.navigation.GraphManager;
import com.company.navigation.GraphProperties;
import com.company.graphviewer.GraphViewer;
import com.company.navigation.NavigationManager;
import com.company.navigation.PathManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;


public class MainRobot {

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(MainRobot.class);

    private RobotProxy m_RobotProxy =null;
    private GraphViewer m_GraphViewer;
    private NavigationManager m_NavigationManager;
    private MovementManager m_MovementManager;
    private CommsManager m_CommsManager;
    private PathManager m_PathManager;
    private GraphManager m_GraphManager;


    public GraphManager getM_GraphManager() { return m_GraphManager; }
    public  MovementManager getM_MovementManager() { return m_MovementManager; }
    public  NavigationManager getM_NavigationManager() { return m_NavigationManager; }
    public PathManager getM_PathManager() { return m_PathManager; }
    public CommsManager getM_CommsManager() { return m_CommsManager; }
    public RobotProxy getM_Proxy() { return m_RobotProxy; }


    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Raspberry]:"+message); }

    public MainRobot() {


        m_GraphManager = new GraphManager(this);
        m_PathManager = new PathManager(this);
        m_GraphViewer = new GraphViewer(this);
        m_CommsManager = new CommsManager(this);
        m_NavigationManager = new NavigationManager(this);
        m_RobotProxy = new RobotProxy(this);
        m_MovementManager = new MovementManager(this);


    }

    private void initialize(){
        m_GraphManager.initialize();
        m_PathManager.initialize();
        m_CommsManager.initialize();
        m_RobotProxy.initialize();
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), ApplicationProperties.LOG_LEVEL);
        writeLog(Level.INFO, "Robot initialized");
    }


    public String getName(){
        return ApplicationProperties.APPLICATION_NAME;
    }

    public static void main(String[] args) throws Exception {
        MainRobot robot = new MainRobot();
        robot.writeLog(Level.INFO, "Robot main start");
        robot.initialize();
        robot.m_MovementManager.test();
        robot.m_NavigationManager.runMockNavigator();
        robot.m_GraphViewer.viewGraph();


    }

}