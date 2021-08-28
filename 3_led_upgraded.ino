#include <SoftwareSerial.h>
SoftwareSerial Bluetooth(10, 9); // RX, TX
int LED1 = 12;
int LED2 = 13;
int LED3 = 8;
int ILED1 = 5;
int ILED2 = 6;
int ILED3 = 7;


int Data; // the data received

void setup() {
  Bluetooth.begin(9600);
  Serial.begin(9600);
  Serial.println("Waiting for command...");
  Bluetooth.println("Ready to recieve data");
  pinMode(LED1,OUTPUT);
  pinMode(LED2,OUTPUT);
  pinMode(LED3,OUTPUT);
  pinMode(ILED1,OUTPUT);
  pinMode(ILED2,OUTPUT);
  pinMode(ILED3,OUTPUT);
 }

void loop() {
  if (Bluetooth.available()){ //wait for data received
    Data=Bluetooth.read();
    Serial.println(Data);
    if(Data== 0){  
      digitalWrite(LED1,0);
      Serial.println(Data);
      Bluetooth.println("0 recieved");
    }
    else if(Data== 1){
       digitalWrite(LED1,1); 
       Serial.println(Data);
       Bluetooth.println("1 recieved");
    }
     else if(Data== 2){
       digitalWrite(LED2,0); 
       Serial.println(Data);
       Bluetooth.println("2 recieved");
    }
     else if(Data== 3){
       digitalWrite(LED2,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 4){
       digitalWrite(LED3,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data==5 ){
       digitalWrite(LED3,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 15){
       digitalWrite(ILED1,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 16){
       digitalWrite(ILED1,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 17){
       digitalWrite(ILED2,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 18){
       digitalWrite(ILED2,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 19){
       digitalWrite(ILED3,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
     else if(Data== 20){
       digitalWrite(ILED3,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 21){        ///kill main
       digitalWrite(LED1,0);
       digitalWrite(LED2,0);
       digitalWrite(LED3,0);
       digitalWrite(ILED1,0);
       digitalWrite(ILED2,0);
       digitalWrite(ILED3,0); 
       Serial.println(Data);
     
    }
    else if(Data== 22){     ////indicator 1
       digitalWrite(ILED3,0);
       digitalWrite(ILED1,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 23){         ////indicator 2 
       digitalWrite(ILED1,0);
       digitalWrite(ILED2,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 24){     /////////indicator3
       digitalWrite(ILED2,0);
       digitalWrite(ILED3,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 25){    ///light_on indicator1 off
       digitalWrite(ILED1,0);
       digitalWrite(LED1,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 26){     ///fan_on indicator2 off
       digitalWrite(ILED2,0);
       digitalWrite(LED2,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 27){    ///tv_on indicator 3 off
       digitalWrite(ILED3,0);
       digitalWrite(LED3,1); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 28){  ///light_off indicator1 off 
       digitalWrite(ILED1,0);
       digitalWrite(LED1,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 29){
       digitalWrite(ILED2,0); ///fan_off indicator2 off
       digitalWrite(LED2,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 30){
       digitalWrite(ILED3,0); ///tv off indicator3 off
       digitalWrite(LED3,0); 
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 31){          ////all indicator off
       digitalWrite(ILED1,0);
       digitalWrite(ILED2,0); 
       digitalWrite(ILED3,0);
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    else if(Data== 32){          ////all indicator on
       digitalWrite(ILED1,1);
       digitalWrite(ILED2,1); 
       digitalWrite(ILED3,1);
       Serial.println(Data);
       Bluetooth.println("3 recieved");
    }
    
     else{;}
  }
delay(100);
}
