package com.company;

public interface IManager {

    public void initialize();
    public void writeLog(org.apache.logging.log4j.Level messageLevel, String message);
}
