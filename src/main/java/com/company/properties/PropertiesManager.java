package com.company.properties;

import com.company.MainRobot;
import com.company.manager.Manager;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager extends Manager {

    private String m_linuxPortName;
    private String m_OSXPortName;


    private String m_WorkingDir;

    @Override
    public void initialize() {

        super.initialize();
        readProperties();
        m_WorkingDir = System.getProperty("user.dir");
    }


    public PropertiesManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
    }

    public void readProperties(){
        java.util.Properties prop = new Properties();
        try {
            System.out.println("Working Directory = " + m_WorkingDir);
            File file = new File("./robot_properties.xml");
            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println("To Read the file"+file.getCanonicalPath());
            prop.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_linuxPortName = prop.getProperty("LinuxPortName");
        m_OSXPortName = prop.getProperty("OSXPortName");
        writeLog(Level.INFO,"OSXPortName:"+m_OSXPortName);
        writeLog(Level.INFO,"linuxPortName:"+m_linuxPortName);

    }

    public String getM_WorkingDir() {
        return m_WorkingDir;
    }


    public String getPortName() throws Exception{
        if(OSValidator.isMac()) return m_OSXPortName;
        else if(OSValidator.isUnix()) return m_linuxPortName;
        else {
            throw new Exception("OS could not be detected - portName for retrieved");

        }
    }
}
