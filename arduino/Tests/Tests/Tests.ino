/*
* Tests for Arduino in bench
* Desc: Java sends Data as string to the serial,
* when Arduino reads "ON" turns the led on
* when Arduino reads "OFF" turns the led off
*/
#include <IRremote.h>

int RECV_PIN = 12;
IRrecv irrecv(RECV_PIN);
decode_results results;
const int ledPin = 13;


void setup() {
 pinMode(ledPin, OUTPUT); // setup led's pin as output
 Serial.begin(9600); //initialize serial comm at 9600 bits per sec
 irrecv.enableIRIn(); // Start the receiver
}

void loop() 
{
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
}
