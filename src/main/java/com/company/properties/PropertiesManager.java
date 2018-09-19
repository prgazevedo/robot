package com.company.properties;

import com.company.MainRobot;
import com.company.manager.Manager;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager extends Manager {

    private String m_linuxPortName;
    private String m_OSXPortName;


    private String m_WorkingDir;
    private String m_LogsDir;
    private String m_LogsImageDir;
    private java.util.Properties m_Prop;

    @Override
    public void initialize() {

        super.initialize();
        FileInputStream fileInputStream = readFile("./robot_properties.xml");
        readPropertiesFile(fileInputStream);
        readProperties();
    }

    @Override
    public void writeLog(Level messageLevel, String message) { m_mainRobot.writeLog(messageLevel,this.getClass().toString()+":"+message); }

    public PropertiesManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
    }

    private void readPropertiesFile(FileInputStream fileInputStream) {
        m_Prop = new Properties();
        try {
            m_Prop.loadFromXML(fileInputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileInputStream readFile(String filename){
        try {
            File file = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            writeLog(Level.INFO, "readFile at working Directory = " + m_WorkingDir);
            writeLog(Level.INFO, "readFile file name = " + file.getCanonicalPath());
            return fileInputStream;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getImageFileToWrite(){
        // Write image to a png file
        String completeFilePath = FileUtils.getNameOflastFileModified(m_WorkingDir+m_LogsDir);
        String extractedFilePath = FileUtils.removeExtension(completeFilePath);
        m_LogsImageDir = m_WorkingDir+m_LogsDir+extractedFilePath+"/";
        try {
            FileUtils.createOrRetrieveDir(m_LogsImageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filepath = m_LogsImageDir+"graph_image_"+System.currentTimeMillis()+".png";
        File outputfile = new File(filepath);
        return outputfile;
    }




    private void readProperties()
    {
        try {

            m_WorkingDir = m_Prop.getProperty("WorkingDir");
            m_LogsDir = m_Prop.getProperty("LogsDir");
            m_linuxPortName = m_Prop.getProperty("LinuxPortName");
            m_OSXPortName = m_Prop.getProperty("OSXPortName");
            writeLog(Level.INFO,"OSXPortName:"+m_OSXPortName);
            writeLog(Level.INFO,"linuxPortName:"+m_linuxPortName);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
