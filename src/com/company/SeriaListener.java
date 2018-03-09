package com.company;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;


     final class SeriaListener implements SerialPortPacketListener
    {

        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

        @Override
        public int getPacketSize() { return 20; }

        @Override
        public void serialEvent(SerialPortEvent event)
        {
            try{
                byte[] newData = event.getReceivedData();
                System.out.println("[Raspberry]: serialEvent called");
                readRawData(newData);


            }
            catch (Exception rEx)
            {
                System.err.println(rEx.toString());
            }
        }

        private void readRawData(byte[] buffer){
            System.out.println("[Arduino] --> [Raspberry]: data received. Size: "+buffer.length+ "\n");
            for (int i = 0; i < buffer.length; ++i)
                System.out.print((char)buffer[i]);

        }



    }


