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

/*
#define FW 16736925
#define BK 16754775
#define LE 16720605
#define RI 16761405
#define ST 16712445
#define UNKNOWN_F 5316027
#define UNKNOWN_B 2747854299
#define UNKNOWN_L 1386468383
#define UNKNOWN_R 553536955
#define UNKNOWN_S 3622325019
*/

#define LOW_SPEED 100
#define ROT_MIN_SPEED 180
#define MAX_SPEED 250
#define SPEED_STEP 10
#define WORK_UNIT  50
#define DELAY_UNIT  50


/*
typedef enum {
    MOVE_FWD,
    MOVE_BWD,
    MOVE_LWD,
    MOVE_RWD,
    MOVE_STP,
    NONE,
    lookleft,
    lookright,
    lookfront,
    testdistance,
    }all_codes ;
*/

#include <avr/pgmspace.h>

const PROGMEM  typedef enum {
    MOVE_FORWARD,
    MOVE_BACKWARD,
    MOVE_LEFTWARD,
    MOVE_RIGHTWARD,
    MOVE_STOP,
    }move_codes ;
   
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
uint8_t RECV_PIN = 12;
uint8_t EN1_PIN=11;
uint8_t EN2_PIN=7;
uint8_t EN3_PIN=8;
uint8_t EN4_PIN=9;
uint8_t ENA_PIN=5;
uint8_t ENB_PIN=6;

///////////////////////////////////////
// Global variables
uint8_t current_speed=LOW_SPEED;
uint8_t current_direction; 

///////////////////////////////////////
// create servo object to control servo
Servo myservo; 
int Echo = A4;
int Trig = A5;
///////////////////////////////////////
// create IR object to control IR Receiver
/*
IRrecv irrecv(RECV_PIN);
decode_results results;
unsigned long val;
*/
///////////////////////////////////////
// Attach a new CmdMessenger object to the default Serial port
char field_separator   = ',';
char command_separator = ';';
CmdMessenger cmdMessenger = CmdMessenger(Serial, field_separator, command_separator);
// This is the list of recognized commands.
// In order to receive, attach a callback function to these events:
// Return the command list: 0;
// Move: 1, <direction:FWD(0)/BWD(1)>,<speed:0-150>,<time: in ms>;
// Rotate: 2, <direction:LEFT(0)/RIGHT(1)>,<speed:0-150>,<time: in ms>;
// Scan: 3, <angle:0-180>;
// Ping Arduino - AreYouReady:4; Command to ask if other side is ready
// Arduino Requests to AskUsIfReady: 5;           
// Arduino Acknowledges is ready: 6;             
const PROGMEM enum {
  kCommandList         , // Command to request list of available commands
  kMove              , // Command to move
  kRotate    , // Command to rotate
  kScan              , // Command to scan
  // Setup connection test
    kAreYouReady              , // Command to ask if other side is ready: RPI -> Arduino: "AreYouReady" will cause Arduino -> RPI: "Acknowledge
    kAcknowledge              , // Command to acknowledge that cmd was received
    // Acknowledge test
    kAskUsIfReady             , // Command to ask other side to ask if ready Arduino -> RPI: "AskUsIfReady" will cause RPI -> Arduino: "YouAreReady"
    kYouAreReady              , // Command to acknowledge that other is ready
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
  //cmdMessenger.attach(kAreYouReady, OnArduinoReady);
  //cmdMessenger.attach(kAskUsIfReady, OnAskUsIfReady);
  
  
}


// Show available commands
void ShowCommands()
{
  writeToSerial("Available commands");
  writeToSerial(" 0; returns this command list");
  writeToSerial(" 1,<direction:FWD/BWD>,<speed:0-150>,<time: in ms>;");
  writeToSerial(" 2,<direction:LEFT/RIGHT>,<speed:0-150>,<time: in ms>;");
  writeToSerial(" 3,<angle:0-180>;");
  writeToSerial(" 4,Ping Arduino");
  writeToSerial(" 5,Ack Ping");
  writeToSerial(" 6,Ping from Arduino to verify readiness of counterpart");
  writeToSerial(" 7,Ack from Arduino counterpart");
}

// ------------------  C A L L B A C K S -----------------------

// Called when a received command has no attached function
void OnUnknownCommand()
{
  writeToSerialAndFlush("This command is unknown!");
  ShowCommands();
}
// Callback function that shows a list of commands
void OnCommandList()
{
  ShowCommands();
}

void OnMove()
{
  
    writeToSerial("onMove()");
    bool bdir = cmdMessenger.readBoolArg();
    writeToSerial("Move direction is: "+String(bdir));
    int speed = cmdMessenger.readInt16Arg();
    writeToSerial("Move speed is: "+String(speed));
    int time = cmdMessenger.readInt16Arg();
    writeToSerial("Move time is: "+String(speed));
    
}

void OnRotate()
{
  
    writeToSerial("onRotate()");
   bool bdir = cmdMessenger.readBoolArg();
    writeToSerial("Rotate direction is: "+String(bdir));
   int speed = cmdMessenger.readInt16Arg();
    writeToSerial("Rotate speed is: "+String(speed));
    int time = cmdMessenger.readInt16Arg();
    writeToSerial("Rotate time is: "+String(speed));
    
}

void OnScan()
{
  
    writeToSerial("onScan()");
   int speed = cmdMessenger.readInt16Arg();
    writeToSerial("Rotate angle is: "+String(speed));

}

void OnArduinoReady()
{
  // In response to ping. We just send a throw-away Acknowledgment to say "i'm ready"
  //cmdMessenger.sendCmd(kAcknowledge,"Arduino ready");
}

void OnAskUsIfReady()
{
  // The other side asks us to send kAreYouReady command, wait for
  //acknowledge
   //int isAck = cmdMessenger.sendCmd(kAreYouReady, "Asking PC if ready", true, kAcknowledge,1000 );
  // Now we send back whether or not we got an acknowledgments
  //cmdMessenger.sendCmd(kYouAreReady,isAck?1:0);
}

// ------------------  OLD CMD and Serial CODE  -----------------------
/*
all_codes hashit (String inString) {
    if (inString == "w") return MOVE_FWD;
    if (inString == "x") return MOVE_BWD;
    if (inString == "a") return MOVE_LWD;
    if (inString == "d") return MOVE_RWD;
    if (inString == "s") return MOVE_STP;
    if (inString == "j") return lookleft;
    if (inString == "l") return lookright;
    if (inString == "k") return lookfront;
    if (inString == "t") return testdistance;
    else return NONE;
}
*/
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


// ------------------  Move Code -----------------------

boolean updateDirectionSpeed(move_codes newdir)
{
    if(newdir==current_direction) 
    {
        
        if(current_speed<MAX_SPEED) 
        {
          writeToSerial("Update of speed from: "+printint(current_speed)+" to: "+printint(current_speed+SPEED_STEP));
          current_speed=current_speed+SPEED_STEP;
          return true;
        }
        else
        {
           writeToSerial(F("Achieved MAX_SPEED"));
           current_speed=MAX_SPEED;
           return false;
        }
        
    }
    else 
    {

      if(newdir==MOVE_FORWARD || newdir==MOVE_BACKWARD || newdir==MOVE_STOP)
      {
        writeToSerial("Update of direction; new speed: "+ printint(LOW_SPEED));      
        current_speed=LOW_SPEED;
      }
      else if(newdir==MOVE_RIGHTWARD || newdir==MOVE_LEFTWARD )
      {
        writeToSerial("Update of direction; new speed: "+ printint(ROT_MIN_SPEED));
        current_speed=ROT_MIN_SPEED;
        
      }
      current_direction=newdir;
      return false;
    }

}


void controlled_move(int speed, int time)
{
  while(time>0){
     analogWrite(ENA_PIN,speed);
     analogWrite(ENB_PIN,speed);
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
  writeToSerialAndFlush("moveForward, for: "+String(time)+" ms");
  controlled_move(speed, time);
}


void moveBackward(int speed, int time )
{

  digitalWrite(EN1_PIN,LOW);
  digitalWrite(EN2_PIN,HIGH);
  digitalWrite(EN3_PIN,HIGH);
  digitalWrite(EN4_PIN,LOW);
  writeToSerialAndFlush("moveBackward, for: "+String(time)+" ms");
  controlled_move(speed, time);
}
void moveLeft(int speed, int time )
{

  digitalWrite(EN1_PIN,HIGH);
  digitalWrite(EN2_PIN,LOW);
  digitalWrite(EN3_PIN,HIGH);
  digitalWrite(EN4_PIN,LOW);
  writeToSerialAndFlush("moveLeft, for: "+String(time)+" ms");
  controlled_move(speed, time);
}
void moveRight(int speed, int time )
{

  digitalWrite(EN1_PIN,LOW);
  digitalWrite(EN2_PIN,HIGH);
  digitalWrite(EN3_PIN,LOW);
  digitalWrite(EN4_PIN,HIGH);
  writeToSerialAndFlush("moveRight, for: "+String(time)+" ms");
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
    writeToSerialAndFlush("testDistance");
    Distance_test();
}

void setupUSServo()
{
  writeToSerialAndFlush("setupUSServo begin");
   myservo.attach(3);// attach servo on pin 3 to servo object
  pinMode(Echo, INPUT);
  pinMode(Trig, OUTPUT);
  writeToSerialAndFlush("setupUSServo ended");

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
  writeToSerialAndFlush("testServo begin");
    for(int i=10; i<=180; i=i+30){
      servoLook(i);
      delay(500);
    }
    servoLook(90);
   delay(500);
   writeToSerialAndFlush("testServo begin");
}

void setupEngines()
{
  writeToSerialAndFlush("setupEngines begin");
  pinMode(EN1_PIN,OUTPUT);
  pinMode(EN2_PIN,OUTPUT);
  pinMode(EN3_PIN,OUTPUT);
  pinMode(EN4_PIN,OUTPUT);
  pinMode(ENA_PIN,OUTPUT);
  pinMode(ENB_PIN,OUTPUT);
  writeToSerialAndFlush("setupEngines ended");
}

void testEngines()
{
  int SLOW_MOVE=1000;
  writeToSerialAndFlush("testEngines begin");
  //test engines
  moveForward(LOW_SPEED,SLOW_MOVE);
  delay(100);
  moveBackward(LOW_SPEED,SLOW_MOVE);
  delay(100);
  moveLeft(LOW_SPEED,SLOW_MOVE);
  delay(100);
  moveRight(LOW_SPEED,SLOW_MOVE);
  delay(100);
  moveStop();
   delay(100);
   writeToSerialAndFlush("testEngines ended");
}

/*
void setupIR()
{
    writeToSerialAndFlush("setupIR started");
    irrecv.enableIRIn();
}
*/


// ------------------  Read Serial and IR Code -----------------------
/*
void readIR() {
      if (irrecv.decode(&results)){
      val = results.value;
      writeToSerialAndFlush(printint(val));
      irrecv.resume();
      switch(val){
        case FW:
        case UNKNOWN_F: 
        {
            updateDirectionSpeed(MOVE_FORWARD);
            moveForward(current_speed,SLOW_MOVE);
        }
        break;
        case BK:
        case UNKNOWN_B:
        {
            updateDirectionSpeed(MOVE_BACKWARD);
            moveBackward(current_speed,SLOW_MOVE);
        }
        break;
        case LE:
        case UNKNOWN_L:
        {
            updateDirectionSpeed(MOVE_LEFTWARD);
            moveLeft(current_speed,SLOW_MOVE);
        }
        break;
        case RI:
        case UNKNOWN_R:
        {
            updateDirectionSpeed(MOVE_RIGHTWARD);
            moveRight(current_speed,SLOW_MOVE);
        }
        break;
        case ST:
        case UNKNOWN_S: moveStop(); break;
        default:break;
      }
    }
}


void readInputSerial(){
      String s = Serial.readString(); // Read from serial
      if(LOOPDEBUG) writeToSerialAndFlush("readInputSerial:"+s);
      switch(hashit(s) ){
          case MOVE_FWD:
            {
                updateDirectionSpeed(MOVE_FORWARD);
                moveForward(current_speed,SLOW_MOVE);
            }
              break;
          case MOVE_BWD:
            {
                updateDirectionSpeed(MOVE_BACKWARD);
                moveBackward(current_speed,SLOW_MOVE);
            }
              break;
          case MOVE_LWD:
            {
                updateDirectionSpeed(MOVE_LEFTWARD);
                moveLeft(current_speed,SLOW_MOVE);
            }
              break;
          case MOVE_RWD:
            {
                updateDirectionSpeed(MOVE_RIGHTWARD);
                moveRight(current_speed,SLOW_MOVE);
            }
              break;
          case MOVE_STOP:
              moveStop();
              break;
          case lookleft:
              servoLook(180);
              break;
          case lookright:
              servoLook(10);
              break;
          case lookfront:
              servoLook(90);
              break;
           case testdistance:
              testDistance();
              break;
          case NONE:
          default:
              if(LOOPDEBUG) writeToSerialAndFlush("readInputSerial: Cannot interpret command!");
              break;
      }
}
*/


// ------------------ M A I N ( ) ----------------------

void setup() {

  
  Serial.begin(115200);
  writeToSerialAndFlush("setup: Serial opened");
  writeToSerialAndFlush("setup begin");
  //setupIR();
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
  writeToSerialAndFlush("setup ended");


  writeToSerialAndFlush("New com setup ");

  // Attach my application's user-defined callback methods
   cmdMessenger.printLfCr();
  attachCommandCallbacks();


}

void loop() {


    if (Serial.available() > 0)
    {
       //old ReadSerial
       //readInputSerial();
       // Process incoming serial data, and perform callbacks
        cmdMessenger.feedinSerialData();
    }
    delay(100); // delay in between reads for stability
    //readIR();
    delay(100); // delay in between reads for stability
}


