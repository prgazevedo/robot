

/*
* Tests for Arduino in bench
* Desc: Java sends Data as string to the serial,
* when Arduino reads "ON" turns the led on
* when Arduino reads "OFF" turns the led off
*/

// *** ConsoleShell ***

// This example shows how to use CmdMessenger as a shell, and communicate with it using the Serial Console
// This example is different from all others:
// - there is no PC counterpart 
// - it will only receive commands, instead of sending commands it will use Serial.Pring
//
// Below is an example of interacting with the sample:
// 
//   Available commands
//   0;                  - This command list
//   1,<led state>;      - Set led. 0 = off, 1 = on
//   2,<led brightness>; - Set led brighness. 0 - 1000
//   3;                  - Show led state
//  
// Command> 3;
//  
//  Led status: on
//  Led brightness: 500
//  
// Command> 2,1000;
//  
//   Led status: on
//   Led brightness: 1000
//  
// Command> 1,0;
//  
//   Led status: off
//   Led brightness: 1000
#include <IRremote.h>

//NEW CODE
#include <CmdMessenger.h>  // CmdMessenger

// PWM timing variables
unsigned long intervalOn        = 0;
unsigned long prevBlinkTime     = 0;
const unsigned long PWMinterval = 1000;

// Blinking led variables 
bool ledState                   = 1;                 // On/Off state of Led
int  ledBrightness              = prevBlinkTime /2 ; // 50 % Brightness 
const int kBlinkLed             = 13;                // Pin of internal Led

// Attach a new CmdMessenger object to the default Serial port
CmdMessenger cmdMessenger = CmdMessenger(Serial);

// This is the list of recognized commands.  
// In order to receive, attach a callback function to these events
enum
{
  kCommandList         , // Command to request list of available commands
  kSetLed              , // Command to request led to be set in specific state  
  kSetLedBrightness    , // Command to request led to be set in to specific brightness  
  kStatus              , // Command to request led status
};

// Callbacks define on which received commands we take action
void attachCommandCallbacks()
{
  // Attach callback methods
  cmdMessenger.attach(OnUnknownCommand);
  cmdMessenger.attach(kCommandList, OnCommandList);
  cmdMessenger.attach(kSetLed, OnSetLed);
  cmdMessenger.attach(kSetLedBrightness, OnSetLedBrightness);
  cmdMessenger.attach(kStatus, OnStatus);
}

// Called when a received command has no attached function
void OnUnknownCommand()
{
  Serial.println("This command is unknown!");
  ShowCommands();
}

// Callback function that shows a list of commands
void OnCommandList()
{
  ShowCommands();
}

// Callback function that sets led on or off
void OnSetLed()
{
  // Read led state argument, expects 0 or 1 and interprets as false or true 
  ledState = cmdMessenger.readBoolArg(); 
  
  Serial.print("Led status: ");
  Serial.println(ledState?"on":"off");
  char* xtramsg1 = cmdMessenger.readStringArg();
  Serial.println("Extra message1: "+String(xtramsg1));
  int val1= cmdMessenger.readInt16Arg();
  Serial.println("Value1: "+String(val1));
   char* xtramsg2 = cmdMessenger.readStringArg();
  Serial.println("Extra message2: "+String(xtramsg2));
   int val2= cmdMessenger.readInt16Arg();
  Serial.println("Value1: "+String(val2));
  Serial.println(ledBrightness);
  //ShowLedState();  
}

// Callback function that sets led on or off
void OnSetLedBrightness()
{
  // Read led brightness argument, expects value between 0 to 255
  ledBrightness = cmdMessenger.readInt16Arg();  
  // Set led brightness
  SetBrightness();  
  // Show Led state
  ShowLedState();  
}

// Callback function that shows led status
void OnStatus()
{
  // Send back status that describes the led state
  ShowLedState();  
}

// Show available commands
void ShowCommands() 
{
  Serial.println("Available commands");
  Serial.println(" 0;                 - This command list");
  Serial.println(" 1,<led state>,<extra message1>,test =0 /1,<extra message2>,test =0 /1;     - Set led. 0 = off, 1 = on");
  Serial.print  (" 2,<led brightness>; - Set led brighness. 0 - "); 
  Serial.println(PWMinterval);
  Serial.println(" 3;                  - Show led state");
}

// Show led state
void ShowLedState() 
{
  Serial.print("Led status: ");
  Serial.println(ledState?"on":"off");
  Serial.print("Led brightness: ");
  Serial.println(ledBrightness);
}

// Set led state
void SetLedState()
{
  if (ledState)  {
    // If led is turned on, go to correct brightness using analog write
    analogWrite(kBlinkLed, ledBrightness);
  } else {
    // If led is turned off, use digital write to disable PWM
    digitalWrite(kBlinkLed, LOW);
  }
}

// Set led brightness
void SetBrightness() 
{
  // clamp value intervalOn on 0 and PWMinterval
  intervalOn  =  max(min(ledBrightness,PWMinterval),0);
}

// Pulse Width Modulation to vary Led intensity
// turn on until intervalOn, then turn off until PWMinterval
bool blinkLed() {
  if (  micros() - prevBlinkTime > PWMinterval ) {
    // Turn led on at end of interval (if led state is on)
    prevBlinkTime = micros();
    digitalWrite(kBlinkLed, ledState?HIGH:LOW);    
  } else if (  micros() - prevBlinkTime > intervalOn ) {
    // Turn led off at  halfway interval    
    digitalWrite(kBlinkLed, LOW);
  } 
}

//END NEW CODE




int RECV_PIN = 12;
IRrecv irrecv(RECV_PIN);
decode_results results;

const int ledPin = 13;


void setup() {
 //pinMode(ledPin, OUTPUT); // setup led's pin as output
 Serial.begin(9600); //initialize serial comm at 9600 bits per sec
 irrecv.enableIRIn(); // Start the receiver

  // Adds newline to every command
  cmdMessenger.printLfCr();   

  // Attach my application's user-defined callback methods
  attachCommandCallbacks();

  // set pin for blink LED
  pinMode(kBlinkLed, OUTPUT);
  
  // Show command list
  ShowCommands();
 
}

void loop() 
{
  /*
 String s = Serial.readString(); // Read from serial
 if(s == "ON"){
    digitalWrite(ledPin, HIGH); // Turn on light
 }
 if(s == "OFF")
 {
    digitalWrite(ledPin, LOW); // Turn of light
 }
 delay(500); // delay in between reads for stability
 if (irrecv.decode(&results))
    {
     Serial.println("Results in HEX");
     Serial.println(results.value, HEX);
     Serial.println("Results in DEC");
     Serial.println(results.value, DEC);
     irrecv.resume(); // Receive the next value
    }
    delay(100);
*/
      // Process incoming serial data, and perform callbacks
  cmdMessenger.feedinSerialData();
  blinkLed();
}
