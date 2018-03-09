#include <IRremote.h>
#include <stdlib.h>


///SERVO
#include <Servo.h> //servo library
Servo myservo; // create servo object to control servo
int Echo = A4;  
int Trig = A5; 
////

#define F 16736925
#define B 16754775
#define L 16720605
#define R 16761405
#define S 16712445
#define UNKNOWN_F 5316027
#define UNKNOWN_B 2747854299
#define UNKNOWN_L 1386468383
#define UNKNOWN_R 553536955
#define UNKNOWN_S 3622325019

#define LOW_SPEED 100
#define WORK_UNIT  50
#define DELAY_UNIT  50
#define SLOW_MOVE  1000

int RECV_PIN = 12;
int in1=6;
int in2=7;
int in3=8;
int in4=9;
int ENA=5;
int ENB=11;
int ABS=115;


IRrecv irrecv(RECV_PIN);
decode_results results;
unsigned long val;

enum string_code {
    forward,
    back,
    left,
    right,
    stopp, 
    none,
    lookleft,
    lookright,
    lookfront};

string_code hashit (String inString) {
    if (inString == "w") return forward;
    if (inString == "x") return back;
    if (inString == "a") return left;
    if (inString == "d") return right; 
    if (inString == "s") return stopp; 
    if (inString == "j") return lookleft;
    if (inString == "k") return lookright; 
    if (inString == "l") return lookfront; 
    else return none;
}




void controlled_move(int speed, int time)
{
  while(time>0){
  analogWrite(ENA,speed);
  analogWrite(ENB,speed);
  delay( WORK_UNIT);
  time-=WORK_UNIT;
  digitalWrite(ENA,LOW);
  digitalWrite(ENB,LOW);
  delay( DELAY_UNIT);
  time-=DELAY_UNIT;
 }
}

void _mForward(int speed, int time )
{ 

  digitalWrite(in1,HIGH);//digital output
  digitalWrite(in2,LOW);
  digitalWrite(in3,LOW);
  digitalWrite(in4,HIGH);
  Serial.println("[Arduino]: go forward!");
  controlled_move(speed, time);
}



void _mBack(int speed, int time )
{

  digitalWrite(in1,LOW);
  digitalWrite(in2,HIGH);
  digitalWrite(in3,HIGH);
  digitalWrite(in4,LOW);
  Serial.println("[Arduino]: go back!");
  controlled_move(speed, time);
}
void _mLeft(int speed, int time )
{

  digitalWrite(in1,HIGH);
  digitalWrite(in2,LOW);
  digitalWrite(in3,HIGH);
  digitalWrite(in4,LOW); 
  Serial.println("[Arduino]: go left!");
  controlled_move(speed, time);
}
void _mRight(int speed, int time )
{

  digitalWrite(in1,LOW);
  digitalWrite(in2,HIGH);
  digitalWrite(in3,LOW);
  digitalWrite(in4,HIGH);
  Serial.println("[Arduino]: go right!");
  controlled_move(speed, time);
}
void _mStop()
{
  digitalWrite(ENA,LOW);
  digitalWrite(ENB,LOW);
  Serial.println("[Arduino]: STOP!");  
}



void printFloat(float val)
{
  char buff[10];
  dtostrf(val, 4, 6, buff);
  Serial.print("[Arduino]: Distance_test: ");
  Serial.println(buff);   
}
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
  printFloat(Fdistance);    
  return (int)Fdistance;
}  

void setupUSServo()
{
  Serial.println("[Arduino]: Setup servo");
   myservo.attach(3);// attach servo on pin 3 to servo object  
  pinMode(Echo, INPUT);    
  pinMode(Trig, OUTPUT);  
}


bool checkAngle(int angle)
{
  if(angle<=180 || angle>=0) return true;
  else return false;
}

void servoLook(int angle)
{
  if(checkAngle(angle)) myservo.write(angle);
  else Serial.println("[Arduino]: Servo cannot move there: "+angle);
}

void testServo()
{
    Serial.println("[Arduino]: Test servo");

    delay(100);
    
    for(int i=0; i<=180; i=i+30){
      servoLook(i);
      delay(400);
    }
    servoLook(90);
    
   delay(100);
}

void setupEngines()
{
  Serial.println("[Arduino]: Setup engines");
  pinMode(in1,OUTPUT);
  pinMode(in2,OUTPUT);
  pinMode(in3,OUTPUT);
  pinMode(in4,OUTPUT);
  pinMode(ENA,OUTPUT);
  pinMode(ENB,OUTPUT);
}

void testEngines()
{
  Serial.println("[Arduino]: Test engines");
  //test engines
  _mForward(LOW_SPEED,SLOW_MOVE);
  delay(500);
  _mBack(LOW_SPEED,SLOW_MOVE);
  delay(500);
  _mLeft(LOW_SPEED,SLOW_MOVE);
  delay(500);
  _mRight(LOW_SPEED,SLOW_MOVE);
  delay(500);
  _mStop();
}

void setup() {
  Serial.begin(115200);
  Serial.println("[Arduino]: Hello from Robot");
  Serial.println("[Arduino]: Reading from serial");
  Serial.println("[Arduino]: Setup Servo and Engines");
  setupUSServo();
  //testServo();
  setupEngines();
  //testEngines();
  //irrecv.enableIRIn();  
  Serial.println("[Arduino]: Finish Setup");
  
 
}


void readIR() {
      if (irrecv.decode(&results)){ 
      val = results.value;
      Serial.println(val);
      irrecv.resume();
      switch(val){
        case F: 
        case UNKNOWN_F: _mForward(LOW_SPEED,SLOW_MOVE);break;
        case B: 
        case UNKNOWN_B: _mBack(LOW_SPEED,SLOW_MOVE); break;
        case L: 
        case UNKNOWN_L: _mLeft(LOW_SPEED,SLOW_MOVE); break;
        case R: 
        case UNKNOWN_R: _mRight(LOW_SPEED,SLOW_MOVE);break;
        case S: 
        case UNKNOWN_S: _mStop(); break;
        default:break;
      }
    }
}


void loop() {
    
    String s = Serial.readString(); // Read from serial
    Serial.println(s);
    switch(hashit(s) ){
        case forward:  _mForward(LOW_SPEED,SLOW_MOVE); ;break;
        case back: _mBack(LOW_SPEED,SLOW_MOVE); break;
        case left: _mLeft(LOW_SPEED,SLOW_MOVE); break;
        case right: _mRight(LOW_SPEED,SLOW_MOVE);break;
        case stopp: _mStop(); break;
        case none: break;
        case lookleft: servoLook(0); break;
        case lookright:servoLook(180); break;
        case lookfront:servoLook(90); break;
        default:Serial.println("Cannot interpret command!");break;
    }

     delay(100); // delay in between reads for stability    
     
}

