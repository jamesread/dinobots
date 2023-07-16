#include <ESP8266WiFi.h>
#include <string>

const char* SSID = "teratan-lan";
const char* PSK = "qwertyuiop";

#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_MS_PWMServoDriver.h"

Adafruit_MotorShield AFMS;
Adafruit_DCMotor *motor1;
Adafruit_DCMotor *motor2;

WiFiServer server(80);

void setup() {
  Serial.begin(9600);
  Serial.println("Startup\n");
  two();
}

void two() {
  WiFi.setHostname("raptor");
  WiFi.setAutoReconnect(true); 
  WiFi.setSleepMode(WIFI_NONE_SLEEP);
  WiFi.mode(WIFI_STA);    
  WiFi.disconnect();
  WiFi.begin(SSID, PSK);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.printf("%d\n", WiFi.status());
  } 

  Serial.println(WiFi.localIP());

  server.begin(); 

  AFMS = Adafruit_MotorShield(); 
  AFMS.begin();

  motor1 = AFMS.getMotor(3);
  motor2 = AFMS.getMotor(4);
  //sserial.begin(9600);
  
  motorSpeed(200);
}

void motorSpeed(int speed) {
  motor1->setSpeed(speed);
  motor2->setSpeed(speed); 
}

void motorsForward() {
  motorSpeed(200);  
  motor1->run(FORWARD);
  motor2->run(BACKWARD); 
}

void motorsBackward() {
  motorSpeed(200);
  motor1->run(BACKWARD);
  motor2->run(FORWARD);  
}

void turn(int leftDirection, int rightDirection) {
  motor1->setSpeed(120);
  motor1->run(leftDirection);
  
  motor2->setSpeed(120);
  motor2->run(rightDirection);
}

void turnLeft() {
  Serial.println("Left");
  turn(FORWARD, FORWARD); 
}

void turnRight() {
  turn(BACKWARD, BACKWARD); 
}

using std::string;
 
void loop() {
  WiFiClient client = server.accept();

  if (client) {
    while (client.connected()) {
      if (client.available()) {
        String cmd = client.readStringUntil('\n');

        handleCommand(cmd);
      }
    }
  }
} 

void handleCommand(String cmd) {   
  Serial.printf("handleCommand %s\n", cmd); 

  if (cmd == "f") {
    return motorsForward();
  }

  if (cmd == "b") {
    return motorsBackward();
  }

  if (cmd == "l") {
    return turnLeft();
  }

  if (cmd == "r") {
    return turnRight();
  }
  
  motorSpeed(0);
}
