#define PIN_M_FL 11
#define PIN_M_FR 12
#define PIN_M_BL 10
#define PIN_M_BR 13
#define PIN_BT_TX 8
#define PIN_BT_RX 9

#include <SoftwareSerial.h>

SoftwareSerial sserial(PIN_BT_RX, PIN_BT_TX);

void setup()
{
  pinMode(PIN_M_FL, OUTPUT);
  pinMode(PIN_M_FR, OUTPUT);
  pinMode(PIN_M_BL, OUTPUT);
  pinMode(PIN_M_BR, OUTPUT);

  sserial.begin(9600);
}

void pulsePin(int pin, int count) {
  for (int i = 0; i < count; i++){ 
    digitalWrite(pin, HIGH);
    delay(500);
    digitalWrite(pin, LOW);
  }
}

void monitorSerial() {
  if (sserial.available()) {
    char sdata = sserial.read();
     
    switch(sdata) {
    case 0x0: 
      digitalWrite(PIN_M_FL, LOW);
      digitalWrite(PIN_M_FR, LOW);
      digitalWrite(PIN_M_BL, LOW);
      digitalWrite(PIN_M_BR, LOW);       
      break;
    case 0x1:
      digitalWrite(PIN_M_FL, HIGH);
      break;
    case 0x2:
      digitalWrite(PIN_M_FR, HIGH);
      break;
    case 0x3:
      digitalWrite(PIN_M_BL, HIGH);
      break;
    case 0x4:
      digitalWrite(PIN_M_BR, HIGH);
      break;
    case 0x5: // fwd
      digitalWrite(PIN_M_FL, HIGH);
      digitalWrite(PIN_M_FR, HIGH);
      digitalWrite(PIN_M_BL, HIGH);
      digitalWrite(PIN_M_BR, HIGH);       
      break;
    case 0x6: // l
      digitalWrite(PIN_M_FL, HIGH);
      digitalWrite(PIN_M_FR, LOW);
      digitalWrite(PIN_M_BL, HIGH);
      digitalWrite(PIN_M_BR, LOW);       
      break;       
    case 0x7: // r
      digitalWrite(PIN_M_FL, LOW);
      digitalWrite(PIN_M_FR, HIGH);
      digitalWrite(PIN_M_BL, LOW);
      digitalWrite(PIN_M_BR, HIGH);       
      break;    
    }   
  }
  
  delay(100);    
}

void fakePwm() {
  int FL = 2;
  int FR = 2;
  int BL = 2;
  int BR = 2; 
  
  for (int i = 0; i < 10; i++) {
    if (FL > 2) { digitalWrite(PIN_M_FL, HIGH); }
    if (FR > 2) { digitalWrite(PIN_M_FR, HIGH); }
    if (BL > 2) { digitalWrite(PIN_M_BL, HIGH); }
    if (BR > 2) { digitalWrite(PIN_M_BR, HIGH); }
    delayMicroseconds(1000); 
    if (FL > 1) { digitalWrite(PIN_M_FL, HIGH); }
    if (FR > 1) { digitalWrite(PIN_M_FR, HIGH); }
    if (BL > 1) { digitalWrite(PIN_M_BL, HIGH); }
    if (BR > 1) { digitalWrite(PIN_M_BR, HIGH); }
    delayMicroseconds(2000); 
    digitalWrite(PIN_M_FL, LOW); 
    digitalWrite(PIN_M_FR, LOW);
    digitalWrite(PIN_M_BL, LOW);
    digitalWrite(PIN_M_BR, LOW);    
    delayMicroseconds(1000); 
  } 
}

void loop() {
  monitorSerial();
}
