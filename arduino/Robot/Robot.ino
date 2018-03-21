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


//#include <IRremote.h>
#include <Servo.h> //servo library
#include <CmdMessenger.h>  //CmdMessenger library
#include <avr/pgmspace.h>


const PROGMEM uint8_t LOW_SPEED = 100;
const PROGMEM uint8_t ROT_MIN_SPEED = 180;
const PROGMEM uint8_t MAX_SPEED = 250;
const PROGMEM uint8_t WORK_UNIT = 50;
const PROGMEM uint8_t DELAY_UNIT = 50;


/* 
 *  NOTE: Had to physically change EN1_PIN and ENB_PIN and in code 
 *  (were EN1_PIN=6 and ENB_PIN=11 now EN1_PIN=11 and ENB_PIN=6)
 *  When PIN11 uses the analogWrite function, it will use the timer2; 
 * However, IR library uses the timer2 interrupt.  
 * So if both are used, it can cause timed confusion and errors. 
 * PIN6 uses timer0, so if PIN11 and PIN6 swap, they will not use the same timer. 
 * Info from:
 * https://forum.arduino.cc/index.php?topic=460099.0
 * Comments in https://www.youtube.com/watch?v=nVwfC9fVlVs
 */

///////////////////////////////////////
//Pin assignments 
const PROGMEM uint8_t RECV_PIN = 12;
const PROGMEM uint8_t EN1_PIN=11;
const PROGMEM uint8_t EN2_PIN=7;
const PROGMEM uint8_t EN3_PIN=8;
const PROGMEM uint8_t EN4_PIN=9;
const PROGMEM uint8_t ENA_PIN=5;
const PROGMEM uint8_t ENB_PIN=6;


const PROGMEM  typedef enum {
    MOVE_FORWARD,
    MOVE_BACKWARD,
    MOVE_LEFTWARD,
    MOVE_RIGHTWARD,
    MOVE_STOP,
    }move_codes ;
   
///////////////////////////////////////
// create servo object to control servo
Servo myservo; 
const PROGMEM uint8_t Echo = A4;
const PROGMEM uint8_t Trig = A5;

///////////////////////////////////////
// Attach a new CmdMessenger object to the default Serial port
const PROGMEM char field_separator   = ',';
const PROGMEM char command_separator = ';';
CmdMessenger cmdMessenger = CmdMessenger(Serial, field_separator, command_separator);
// This is the list of recognized commands.
// In order to receive, attach a callback function to these events:
// Return the command list: 0;
// Move: 1, <direction:FWD(0)/BWD(1)>,<speed:0-150>,<time: in ms>;
// Rotate: 2, <direction:LEFT(0)/RIGHT(1)>,<speed:0-150>,<time: in ms>;
// Scan: 3, <angle:0-180>;
// Ping Arduino - AreYouReady:4; Command to ask if we are ready
// Ack Arduino - Acknowledge:5; Ack from Arduino
// Arduino Requests to AskUsIfReady: 6;           
// Arduino Acknowledges is ready: 7;             
const PROGMEM enum {
  kCommandList         , // 0-Command to request list of available commands
  kMove              , // 1-Command to move
  kRotate    ,        // 2-Command to rotate
  kScan              , // 3-Command to scan
  // Setup connection test
    kAreYouReady              , // 4-Command to ask if other side is ready: RPI -> Arduino: "AreYouReady" will cause Arduino -> RPI: "Acknowledge
    kAcknowledge              , // 5-Command to acknowledge that cmd was received
    // Acknowledge test
    kAskUsIfReady             , // 6-Command to ask other side to ask if ready Arduino -> RPI: "AskUsIfReady" will cause RPI -> Arduino: "YouAreReady"
    kYouAreReady              , // 7-Command to acknowledge that other is ready
    kError,                     // 8-Error
} cmds;

// Callbacks define on which received commands we take action
void attachCommandCallbacks()
{
  // Attach callback methods
  
  cmdMessenger.attach(OnUnknownCommand);
  cmdMessenger.attach(kCommandList, OnCommandList);
  cmdMessenger.attach(kMove, OnMove);
  cmdMessenger.attach(kRotate, OnRotate);
  cmdMessenger.attach(kScan, OnScan);
  cmdMessenger.attach(kAreYouReady, OnArduinoReady);
  cmdMessenger.attach(kAskUsIfReady, OnAskUsIfReady);
  
}


// Show available commands
void ShowCommands()
{
  writeToSerial(F("Available commands"));
  writeToSerial(F(" 0; returns this command list "));
  writeToSerial(F(" 1,<direction:FWD/BWD>,<speed:0-150>,<time: in ms>; "));
  writeToSerial(F(" 2,<direction:LEFT/RIGHT>,<speed:0-150>,<time: in ms>; "));
  writeToSerial(F(" 3,<angle:0-180>; "));
  writeToSerial(F(" 4;Ping Arduino"));
  writeToSerial(F(" 5;Ack Ping"));
  writeToSerial(F(" 6;Ping from Arduino to verify readiness of counterpart"));
  writeToSerial(F(" 7;Ack from Arduino counterpart"));
}

// ------------------  C A L L B A C K S -----------------------

// Called when a received command has no attached function
void OnUnknownCommand()
{
  writeToSerialAndFlush("OnUnknownCommand");
// Default response for unknown commands and corrupt messages
  cmdMessenger.sendCmd(kError,"Unknown command");
  cmdMessenger.sendCmdStart(kYouAreReady);  
  cmdMessenger.sendCmdArg("Command without attached callback");    
  cmdMessenger.sendCmdArg(cmdMessenger.commandID());    
  cmdMessenger.sendCmdEnd();
}

void OnArduinoReady()
{
  // In response to ping. We just send a throw-away Acknowledgment to say "i'm ready"
  writeToSerialAndFlush(F("OnArduinoReady"));
  cmdMessenger.sendCmd(kAcknowledge,"Arduino ready");
}

void OnAskUsIfReady()
{
  // The other side asks us to send kAreYouReady command, wait for acknowledge
  writeToSerialAndFlush(F("OnAskUsIfReady"));
   int isAck = cmdMessenger.sendCmd(kAreYouReady, "Asking PC if ready", true, kAcknowledge,1000 );
  // Now we send back whether or not we got an acknowledgments
  cmdMessenger.sendCmd(kYouAreReady,isAck?1:0);
}

// Callback function that shows a list of commands
void OnCommandList()
{
  writeToSerialAndFlush(F("OnCommandList called"));
  ShowCommands();
}

void OnMove()
{
  
    writeToSerial(F("onMove()"));
    bool bdir = cmdMessenger.readBoolArg();
    //writeToSerial("Move direction is: "+String(bdir));
    int speed = cmdMessenger.readInt16Arg();
    //writeToSerial("Move speed is: "+String(speed));
    int time = cmdMessenger.readInt16Arg();
    //writeToSerial("Move time is: "+String(speed));
    if(bdir==true)
    {
      //Move FWD
      moveForward(speed,time);
    }
    else
    {
      //Move BWD
      moveBackward(speed,time);
      
    }
    //send ack
    OnArduinoReady();
    
}

void OnRotate()
{
  
    writeToSerial(F("onRotate()"));
   bool bdir = cmdMessenger.readBoolArg();
    //writeToSerial("Rotate direction is: "+String(bdir));
   int speed = cmdMessenger.readInt16Arg();
    //writeToSerial("Rotate speed is: "+String(speed));
    int time = cmdMessenger.readInt16Arg();
    //writeToSerial("Rotate time is: "+String(speed));
    if(bdir==true)
    {
      //Move LEFT
      moveLeftward(speed,time);
    }
    else
    {
      //Move BWD
      moveRightward(speed,time);
      
    }
    //send ack
    OnArduinoReady();
    
}

void OnScan()
{
  
    writeToSerial(F("onScan()"));
   int angle = cmdMessenger.readInt16Arg();
    writeToSerial("Rotate angle is: "+String(angle));
    servoLook(angle);
    testDistance();
    //send ack
    OnArduinoReady();
    

}



// ------------------  OLD CMD and Serial CODE  -----------------------

String printint(int val)
{
  return String(val);
}

String printFloat(float val)
{
  char buff[10];
  dtostrf(val, 4, 6, buff);
  String str((char*)buff);
  return str;

}

void writeToSerial(String inString){
  Serial.print(F("[Arduino]: "));
  Serial.println(inString);
}


void writeToSerialAndFlush(String inString){
  writeToSerial(inString);
  Serial.flush();
}




void controlled_move(int speed, int time)
{
  //Check speed limit
  int current_speed=LOW_SPEED;
  if(speed>LOW_SPEED && speed<MAX_SPEED)
  {
    current_speed=speed;
  }
  while(time>0){
     analogWrite(ENA_PIN,current_speed);
     analogWrite(ENB_PIN,current_speed);
     delay( WORK_UNIT);
     time-=WORK_UNIT;
     digitalWrite(ENA_PIN,LOW);
     digitalWrite(ENB_PIN,LOW);
     delay( DELAY_UNIT);
     time-=DELAY_UNIT;
 }
}


void moveForward(int speed, int time )
{
  digitalWrite(EN1_PIN,HIGH);
  digitalWrite(EN2_PIN,LOW);
  digitalWrite(EN3_PIN,LOW);
  digitalWrite(EN4_PIN,HIGH);
  writeToSerialAndFlush("moveForward, for speed: "+String(speed)+" <100-255>, time: "+String(time)+" ms");
  controlled_move(speed, time);
}


void moveBackward(int speed, int time )
{

  digitalWrite(EN1_PIN,LOW);
  digitalWrite(EN2_PIN,HIGH);
  digitalWrite(EN3_PIN,HIGH);
  digitalWrite(EN4_PIN,LOW);
  writeToSerialAndFlush("moveBackward, for speed: "+String(speed)+" <100-255>, time: "+String(time)+" ms");
  controlled_move(speed, time);
}
void moveLeftward(int speed, int time )
{

  digitalWrite(EN1_PIN,HIGH);
  digitalWrite(EN2_PIN,LOW);
  digitalWrite(EN3_PIN,HIGH);
  digitalWrite(EN4_PIN,LOW);
  writeToSerialAndFlush("moveLeftward, for speed: "+String(speed)+" <100-255>, time: "+String(time)+" ms");
  controlled_move(speed, time);
}
void moveRightward(int speed, int time )
{

  digitalWrite(EN1_PIN,LOW);
  digitalWrite(EN2_PIN,HIGH);
  digitalWrite(EN3_PIN,LOW);
  digitalWrite(EN4_PIN,HIGH);
  writeToSerialAndFlush("moveRightward, for speed: "+String(speed)+" <100-255>, time: "+String(time)+" ms");
  controlled_move(speed, time);
}
void moveStop()
{

  digitalWrite(EN1_PIN,LOW);
  digitalWrite(EN2_PIN,LOW);
  digitalWrite(EN3_PIN,LOW);
  digitalWrite(EN4_PIN,LOW);
  controlled_move(LOW_SPEED, 100);
  writeToSerialAndFlush("moveStop");
}

// ------------------  UltraSonic Code -----------------------

 /*Ultrasonic distance measurement Sub function*/
int Distance_test()
{

  digitalWrite(Trig, LOW);
  delayMicroseconds(2);
  digitalWrite(Trig, HIGH);
  delayMicroseconds(20);
  digitalWrite(Trig, LOW);
  float Fdistance = pulseIn(Echo, HIGH);
  Fdistance= Fdistance/58;
  writeToSerialAndFlush("Distance_test for: "+printFloat(Fdistance)+" distance");
  return (int)Fdistance;
}

void testDistance()
{
    writeToSerialAndFlush(F("testDistance"));
    Distance_test();
}

void setupUSServo()
{
  writeToSerialAndFlush(F("setupUSServo begin"));
   myservo.attach(3);// attach servo on pin 3 to servo object
  pinMode(Echo, INPUT);
  pinMode(Trig, OUTPUT);
  writeToSerialAndFlush(F("setupUSServo ended"));

}


bool checkAngle(int angle)
{
  if(angle<=180 && angle>=10) return true;
  else return false;
}

void servoLook(int angle)
{
  writeToSerialAndFlush("servoLook: "+String(angle));
  if(checkAngle(angle)) myservo.write(angle);
  else writeToSerialAndFlush("servoLook: cannot move there: "+String(angle));
}


// ------------------  Test and Setup Code -----------------------

void testServo()
{
  writeToSerialAndFlush(F("testServo begin"));
    for(int i=10; i<=180; i=i+30){
      servoLook(i);
      delay(500);
    }
    servoLook(90);
   delay(500);
   writeToSerialAndFlush(F("testServo begin"));
}

void setupEngines()
{
  //writeToSerialAndFlush(F("setupEngines begin"));
  pinMode(EN1_PIN,OUTPUT);
  pinMode(EN2_PIN,OUTPUT);
  pinMode(EN3_PIN,OUTPUT);
  pinMode(EN4_PIN,OUTPUT);
  pinMode(ENA_PIN,OUTPUT);
  pinMode(ENB_PIN,OUTPUT);
  //writeToSerialAndFlush(F("setupEngines ended"));
}

void testEngines()
{
  int SHORT_MOVE=500;
  //writeToSerialAndFlush(F("testEngines begin"));
  //test engines
  moveForward(LOW_SPEED,SHORT_MOVE);
  delay(100);
  moveBackward(LOW_SPEED,SHORT_MOVE);
  delay(100);
  moveLeftward(LOW_SPEED,SHORT_MOVE);
  delay(100);
  moveRightward(LOW_SPEED,SHORT_MOVE);
  delay(100);
  moveStop();
   delay(100);
   //writeToSerialAndFlush(F("testEngines ended"));
}




// ------------------ M A I N ( ) ----------------------

void setup() {

  
  Serial.begin(115200);
  writeToSerialAndFlush(F("setup: Serial opened"));
  //writeToSerialAndFlush(F("setup begin"));
   // Attach my application's user-defined callback methods
  cmdMessenger.printLfCr();
  attachCommandCallbacks();
  delay(500);
  setupUSServo();
  delay(500);
  testServo();
  delay(500);
  setupEngines();
  delay(500);
  testEngines();
  delay(500);
  testDistance();
  ShowCommands();
  writeToSerialAndFlush("setup ended");

}

void loop() {

    if (Serial.available() > 0)
    {

       // Process incoming serial data, and perform callbacks
        cmdMessenger.feedinSerialData();
    }
    delay(100); // delay in between reads for stability

}


