/*
  This example comes from a short article series in the Linux 
  Gazette by Richard A. Sevenich and Christopher Lopes, titled
  "Compiler Construction Tools". The article series starts at

  http://www.linuxgazette.com/issue39/sevenich.html

  Small changes and updates to newest JFlex+Cup versions 
  by Gerwin Klein
*/

/*
  Commented By: Christopher Lopes
  File Name: Main.java
  To Create: 
  After the scanner, lcalc.flex, and the parser, ycalc.cup, have been created.
  > javac Main.java
  
  To Run: 
  > java Main test.my
  where test.my is an test input file for the calculator.
*/
   
import java.io.*;
import java.util.Arrays;

public class Main {
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      Types t = Types.VALUE;
      Parser p = new Parser(new Lexer(new FileReader(argv[0])));
      System.out.println("Start parsing");
      VarFunctionsContext context = new VarFunctionsContext();
      Object result = p.parse().value;
      System.out.println("Finish parsing");
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      System.out.println("");
      e.printStackTrace();
    }
  }
}


