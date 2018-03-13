package com.company;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

     final class SeriaListener implements SerialPortPacketListener
    {

        //private static final Logger log = LogManager.getRootLogger();
        private final static Logger log =  LogManager.getLogger(SeriaListener.class);
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

        @Override
        public int getPacketSize() { return 80; }

        @Override
        public void serialEvent(SerialPortEvent event)
        {

            try{
                byte[] newData = event.getReceivedData();
                //log.info("[Raspberry]: serialEvent called");
                if(newData.length>0) readRawData(newData);


            }
            catch (Exception rEx)
            {
                log.error(rEx.toString());
            }
        }

        private void readRawData(byte[] buffer){
            //log.info("[Raspberry]: data received. Size: "+buffer.length+ "\n");
            String prefix="[Raspberry]: readRawData from serial: ";
            String output="";
            try {

                for (int i = 0; i < buffer.length; ++i) {
                    //    System.out.print((char)buffer[i]);

                    if ( ApplicationProperties.isMessageSplitter(buffer[i])  )
                    {
                        //If there is content in the output log it
                        if(!output.equals("")) log.info(prefix+output);
                        output="";

                    }
                    else{
                        output += (char) buffer[i];
                    }


                }
               //log.info(output);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }



    }


