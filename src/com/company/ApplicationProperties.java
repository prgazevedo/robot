package com.company;


import java.util.Properties;

//import org.apache.commons.lang.builder.ToStringBuilder;

public class ApplicationProperties {

    /** Application name */
    public static final String APPLICATION_NAME = "MyController.org";
    /** Milliseconds to block while waiting for port open */
    public static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    public static final int DATA_RATE = 115200;
    public static final int DATABITS = 8;
    public static final int PARITY = 0;
    //Acceptable values are NO_PARITY, EVEN_PARITY, ODD_PARITY, MARK_PARITY, and SPACE_PARITY.
    public static final int STOPBITS = 1;
    // Message splitter char for serial message
    public static final byte MESSAGE_SPLITTER = '\n';

}


