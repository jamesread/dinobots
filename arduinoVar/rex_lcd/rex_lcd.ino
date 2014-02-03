#include <LiquidCrystal.h>

#define COLS 16
#define ROWS 2

LiquidCrystal lcd(8, 13, 9, 4, 5, 6, 7);

void setup()
{
  lcd.clear(); 
  lcd.begin(COLS, ROWS);
  lcd.setCursor(0,0); 
}

void loop()
{
  lcd.setCursor(0,0); 
  lcd.print("REX: Ready!"); 
   
  lcd.setCursor(0, 1);
  lcd.print("  hi langeeks! ");
  
  delay(2000);
  
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("caek!");
  
  delay(500);
    
  lcd.clear();
}



