#include <SoftwareSerial.h>
#define PIN_BT_RX 2
#define PIN_BT_TX 13

SoftwareSerial sserial(PIN_BT_RX, PIN_BT_TX);

#include <AFMotor.h>
#define HBR_MOT_L 3
#define HBR_MOT_R 4

AF_DCMotor motor1(HBR_MOT_L, MOTOR12_64KHZ); 
AF_DCMotor motor2(HBR_MOT_R, MOTOR12_64KHZ); 

void setup() {
  sserial.begin(9600);
  
  motorSpeed(200);
}

void motorSpeed(int speed) {
  motor1.setSpeed(speed);
  motor2.setSpeed(speed); 
}

void motorsForward() {
  motorSpeed(200);  
  motor1.run(FORWARD);
  motor2.run(BACKWARD); 
}

void motorsBackward() {
  motorSpeed(200);
  motor1.run(BACKWARD);
  motor2.run(FORWARD);  
}

void turn(int leftDirection, int rightDirection) {
  motor1.setSpeed(120);
  motor1.run(leftDirection);
  
  motor2.setSpeed(120);
  motor2.run(rightDirection);
}

void turnLeft() {
  turn(FORWARD, FORWARD); 
}

void turnRight() {
  turn(BACKWARD, BACKWARD); 
}

void loop() {
  if (sserial.available()) { 
    char sdata = sserial.read();
   
    switch(sdata) {
      case 0x1:
        motorsForward();
        break;
      case 0x2:
        motorsBackward();
        break;
      case 0x3:
        turnLeft();
        break;
      case 0x4:
        turnRight();
        break;
      default:
        motorSpeed(0);
    } 
  }
  
  delay(200); 
}
