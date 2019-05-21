import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;


public class ll_visitor extends GJDepthFirst<String[], Map> {

  int temps_vars;
  int numIfs;

  /**
  * f0 -> Identifier()
  * f1 -> "="
  * f2 -> Expression()
  * f3 -> ";"
  */
  public String[] visit(AssignmentStatement n, Map argu) throws Exception {
   String[] Ident_arr = n.f0.accept(this, argu);
   String[] expr = n.f2.accept(this, argu);

   String type = null;
   String value = null;
   if(expr !=null) {
     type = expr[0];
     value = expr[1];
   }
   System.out.println("\tstore "+ type + " "+ value +", "+type+"* %"+Ident_arr[1]);
   return null;
  }


  /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   */
  public String[] visit(IfStatement n, Map argu) throws Exception {
     System.out.println("IF");

     String expr[] = n.f2.accept(this, argu);

     String type = null;
     String value = null;
     if(expr!=null ) { // && expr.startsWith("i32 ")
       type = expr[0];
       value = expr[1];
     }

     System.out.println("\tbr i1 "+value+", label %if"+String.valueOf(numIfs)+", label %else"+String.valueOf(numIfs));
     System.out.println("if"+String.valueOf(numIfs));

     n.f4.accept(this, argu);
     System.out.println("else"+String.valueOf(numIfs));
     //
     n.f6.accept(this, argu);
     // numIfs++;
     return null;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(CompareExpression n, Map argu) throws Exception {

     String[] pr1 = n.f0.accept(this, argu);
  //    //String type = null;
     String value1 = pr1[1];
     if(pr1[0].equals("Identifier") ) {
       value1 = "%_"+String.valueOf(temps_vars);
       System.out.println("\t"+value1+" = load i32, i32* %"+pr1[1]);
       temps_vars++;
     }
  //    // else {
  //    //   String[] parts = pr1.split(" ");
  //    //   value1 = parts[1];
  //    // }
     String[] pr2 = n.f2.accept(this, argu);
     String value2 = pr2[1];
     if(pr2[0].equals("Identifier") ) {
       value2 = "%_"+String.valueOf(temps_vars);
       System.out.println("\t"+value2+" = load i32, i32* %"+pr2[1]);
       temps_vars++;
     }
  //    // else {
  //    //   String[] parts = pr2.split(" ");
  //    //   value2 = parts[1];
  //    // }
     String[] res= new String[2];
     res[0] = "i1";
     res[1] = "%_"+String.valueOf(temps_vars);
     System.out.println("\t"+res[1]+" = icmp slt i32 "+value1+", "+value2);
     temps_vars++;
     return res;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public String[] visit(Identifier n, Map argu) throws Exception {
     String[] res= new String[2];
     res[0] = "Identifier";
     res[1] = n.f0.toString();
     return res;
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public String[] visit(IntegerLiteral n, Map argu) throws Exception {
     String[] res= new String[2];
     res[0] = "i32";
     res[1] = n.f0.toString();
     return res;
  }

  /**
   * f0 -> IntegerLiteral()
   *       | TrueLiteral()
   *       | FalseLiteral()
   *       | Identifier()
   *       | ThisExpression()
   *       | ArrayAllocationExpression()
   *       | AllocationExpression()
   *       | BracketExpression()
   */
  public String[] visit(PrimaryExpression n, Map argu) throws Exception {
     return n.f0.accept(this, argu);
  }


}
