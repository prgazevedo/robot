package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Manager implements IManager{

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(MainRobot.class);
    @Override
    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Raspberry]:"+message); }

    @Override
    public void initialize(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), ApplicationProperties.LOG_LEVEL);

    }
}
