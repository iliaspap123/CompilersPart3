import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;


public class ll_visitor extends GJDepthFirst<String[], Map> {

  int temps_vars;
  int numIfs;
  String currentClass;



  /**
   * f0 -> "public"
   * f1 -> Type()
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( FormalParameterList() )?
   * f5 -> ")"
   * f6 -> "{"
   * f7 -> ( VarDeclaration() )*
   * f8 -> ( Statement() )*
   * f9 -> "return"
   * f10 -> Expression()
   * f11 -> ";"
   * f12 -> "}"
   */
  public String[] visit(MethodDeclaration n, Map argu) throws Exception {
     String[] type = n.f1.accept(this, argu);
     String[] meth = n.f2.accept(this, argu);
     System.out.println("define "+type[1]+" @"+currentClass+"."+meth[1]+"(i8* %this) {");
     n.f4.accept(this, argu);
     n.f7.accept(this, argu);
     n.f8.accept(this, argu);
     n.f10.accept(this, argu);
     System.out.println("}");

     return null;
  }



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
     // System.out.println("IF");

     String expr[] = n.f2.accept(this, argu);

     String type = null;
     String value = null;
     if(expr!=null ) { // && expr.startsWith("i32 ")
       type = expr[0];
       value = expr[1];
     }

     System.out.println("\tbr i1 "+value+", label %if"+String.valueOf(numIfs)+", label %else"+String.valueOf(numIfs));
     System.out.println("if"+String.valueOf(numIfs)+":");

     n.f4.accept(this, argu);
     System.out.println("\n\tbr label %end"+String.valueOf(numIfs));
     System.out.println("else"+String.valueOf(numIfs)+":");
     //
     n.f6.accept(this, argu);
     System.out.println("\n\tbr label %end"+String.valueOf(numIfs));

     System.out.println("\nend"+String.valueOf(numIfs)+":");
     numIfs++;
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
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(PlusExpression n, Map argu) throws Exception {
    String[] pr1 = n.f0.accept(this, argu);
    String value1 = null;
    if(pr1 != null) {
      value1 = pr1[1];
    }
    String[] pr2 = n.f2.accept(this, argu);
    String value2 = null;
    if(pr2 != null) {
      value2 = pr2[1];
    }
    String[] res= new String[2];
    res[0] = "i32";
    res[1] = "%_"+String.valueOf(temps_vars);
    System.out.println("\t"+res[1]+" = add i32 "+value1+", "+value2);
    temps_vars++;
    return res;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(MinusExpression n, Map argu) throws Exception {
    String[] pr1 = n.f0.accept(this, argu);
    String value1 = null;
    if(pr1 != null) {
      value1 = pr1[1];
    }
    String[] pr2 = n.f2.accept(this, argu);
    String value2 = null;
    if(pr2 != null) {
      value2 = pr2[1];
    }
    String[] res= new String[2];
    res[0] = "i32";
    res[1] = "%_"+String.valueOf(temps_vars);
    System.out.println("\t"+res[1]+" = sub i32 "+value1+", "+value2);
    temps_vars++;
    return res;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(TimesExpression n, Map argu) throws Exception {
     String[] pr1 = n.f0.accept(this, argu);
     String value1 = null;
     if(pr1 != null) {
       value1 = pr1[1];
     }
     String[] pr2 = n.f2.accept(this, argu);
     String value2 = null;
     if(pr2 != null) {
       value2 = pr2[1];
     }
     String[] res= new String[2];
     res[0] = "i32";
     res[1] = "%_"+String.valueOf(temps_vars);
     System.out.println("\t"+res[1]+" = mul i32 "+value1+", "+value2);
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
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  public String[] visit(ArrayType n, Map argu) throws Exception {
     String[] res= new String[2];
     res[0] = "int[]";
     res[1] = "i8*";
     return res;
  }

  /**
   * f0 -> "boolean"
   */
  public String[] visit(BooleanType n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = "i1";
    return res;
  }

  /**
   * f0 -> "int"
   */
  public String[] visit(IntegerType n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = "i32";
    return res;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public String[] visit(ClassDeclaration n, Map argu) throws Exception {
     String[] className = n.f1.accept(this, argu);
     currentClass = className[1];

     ClassForm M = (ClassForm) argu.get(currentClass);
     // M.printAll(currentClass);
     System.out.println("@."+className[1]+"_vtable = global ["+M.MethodInfo.size()+" x i8*] [");
     for(String meth : M.Methods.keySet() ) {
       MethodForm methF = M.Methods.get(meth);
       String type = "i8*";
       if(methF.Type.equals("int")) {
         type = "i32";
       }
       else if(methF.Type.equals("boolean")) {
         type = "i1";
       }
       System.out.print("\t\t\t\ti8* bitcast ("+type+"(i8*");
       for(String keys : methF.Arguments.keySet()) {
         String arg_type = methF.Arguments.get(keys);
         if(arg_type.equals("int")) {
           arg_type = "i32";
         }
         else if(arg_type.equals("boolean")) {
           arg_type = "i1";
         }
         else {
           arg_type = "i8*";
         }
         System.out.print(","+arg_type);
       }
       System.out.println(")* @"+currentClass+"."+meth+" to i8*)");
     }

     System.out.println("\t\t\t\t]");
     // n.f3.accept(this, argu);
      n.f4.accept(this, argu);
     return null;
  }


}
