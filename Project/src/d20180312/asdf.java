package d20180312;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
 
public class asdf {
 
 public static void main(String[] args) {
 
  try {
   Enumeration e = CommPortIdentifier.getPortIdentifiers();
   
   System.out.println("Enumeration get()............... "+ e.hasMoreElements());
 
   while (e.hasMoreElements()) {
    CommPortIdentifier first = (CommPortIdentifier) e.nextElement();
    System.out.println("COM name : " + first.getName());
   }
    
  } catch (Exception e) {
   e.printStackTrace();
  }
 
 }
 
}