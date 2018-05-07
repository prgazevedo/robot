package com.company.manager;

public interface IManager {

    public void initialize();
    public void managerExit(int status);
    public void writeLog(org.apache.logging.log4j.Level messageLevel, String message);
}
