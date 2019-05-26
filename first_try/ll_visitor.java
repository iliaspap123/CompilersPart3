import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


public class ll_visitor extends GJDepthFirst<String[], Map> {

  int temps_vars;
  int numIfs;
  int numLoops;
  String currentClass;
  String currentMeth;
  ArrayList<String> temp_args;


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
     currentMeth = meth[1];
     String str_def = "define "+type[1]+" @"+currentClass+"."+meth[1]+"(i8* %this";
     //temp_args = new ArrayList<String[]>();
     ClassForm classF = (ClassForm) argu.get(currentClass);
     MethodForm methF = (MethodForm) classF.Methods.get(meth[1]);
     String str_decl = "";
     for(String key : methF.Arguments.keySet()) {
       String type_arg = methF.Arguments.get(key);
       if(type_arg.equals("int")) {
         type_arg = "i32";
       }
       else if(type_arg.equals("boolean")) {
         type_arg = "i1";
       }
       else {
         type_arg = "i8*";
       }
       str_def += ", "+type_arg+" %."+key;
       str_decl += "\t%"+key+" = alloca "+type_arg+"\n";
       str_decl += "\tstore "+type_arg+" %."+key+", "+type_arg+"* %"+key+"\n";

     }
     System.out.println(str_def+") {");
     System.out.println(str_decl);
     n.f4.accept(this, argu);
     n.f7.accept(this, argu);
     n.f8.accept(this, argu);
     String[] ret = n.f10.accept(this, argu);
     System.out.println("\tret "+ret[0]+" "+ret[1]);
     System.out.println("}");
    // temp_args.clear();
     return null;
  }


  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public String[] visit(VarDeclaration n, Map argu) throws Exception {
     String[] type = n.f0.accept(this, argu);
     String[] ident = n.f1.accept(this, argu);
     // n.f2.accept(this, argu);
     System.out.println("\t%"+ident[1]+" = alloca "+type[1]);
     return type;
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

     String pr[] = n.f0.accept(this, argu);

     if(pr!= null && pr[0].equals("Identifier")) {

       String type;
       ClassForm classF = (ClassForm) argu.get(currentClass);
       MethodForm methF = classF.Methods.get(currentMeth);
       // System.out.println("exit1: "+pr[0]+pr[1]);
       if((type=check_in_meth(pr[1],methF))!=null) {
         //pr[0] = type2;
         if(type.equals("int")) {
           pr[0] = "i32";
         }
         else if(type.equals("boolean")) {
           pr[0] = "i1";
         }
         else {
           pr[0]="i8*";
         }
         System.out.println("\t%_"+temps_vars+" = load "+pr[0]+", "+pr[0]+"* %"+pr[1]);
         pr[1] = "%_"+temps_vars;
         temps_vars++;
         if(pr[0].equals("i8*")) {
           pr[0] = type;
         }
         // System.out.println("exit2,1: "+type);
       }
       else {
         // System.out.println("pr1 = "+pr[0]);
         String[] offset = check_var(pr[1],currentClass,argu);
         if(offset[0].equals("int")) {
           pr[0] = "i32";
         }
         else if(offset[0].equals("int[]")) {
           pr[0] = "i32*";
         }
         else if(offset[0].equals("boolean")) {
           pr[0] = "i1";
         }
         else {
           pr[0]="i8*";
         }
         System.out.println("\t%_"+temps_vars+" = getelementptr i8, i8*"+ offset[0]+", i32 "+offset[1]);
         temps_vars++;
         System.out.println("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+pr[0]+"*");
         temps_vars++;
         System.out.println("\t%_"+temps_vars+" = load "+pr[0]+", "+pr[0]+"* %_"+(temps_vars-1));
         temps_vars++;
         if(pr[0].equals("i8*")) {
           pr[0] = offset[0];
         }
         pr[1] = "%_"+(temps_vars-1);
         // System.out.println("exit2,2: "+offset);

       }

     }
     return pr;
  }

  // /**
  //  * f0 -> "new"
  //  * f1 -> "int"
  //  * f2 -> "["
  //  * f3 -> Expression()
  //  * f4 -> "]"
  //  */
  // public String[] visit(ArrayAllocationExpression n, Map argu) throws Exception {
  //    n.f0.accept(this, argu);
  //    n.f1.accept(this, argu);
  //    n.f2.accept(this, argu);
  //    n.f3.accept(this, argu);
  //    n.f4.accept(this, argu);
  //    return "int[]";
  // }
  //

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public String[] visit(AllocationExpression n, Map argu) throws Exception {
     String[] type = n.f1.accept(this, argu);
     type[0] = type[1];
     System.out.println("\t%_"+temps_vars+" = call i8* @calloc(i32 1, i32 8)");
     type[1] = "_"+temps_vars;
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = bitcast i8* %_0 to i8***");
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = getelementptr [1 x i8*], [1 x i8*]* @.Fac_vtable, i32 0, i32 0");
     System.out.println("\tstore i8** "+"%_"+temps_vars+", i8*** %_"+(temps_vars-1));
     temps_vars++;
     return type;
  }



  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public String[] visit(BracketExpression n, Map argu) throws Exception {
    return n.f1.accept(this, argu);
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
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  public String[] visit(WhileStatement n, Map argu) throws Exception {

     System.out.println("\tbr label %loopInit"+numLoops);
     System.out.println("loopInit"+numLoops+":");
     String expr[] = n.f2.accept(this, argu);

     String type = null;
     String value = null;
     if(expr!=null ) { // && expr.startsWith("i32 ")
       type = expr[0];
       value = expr[1];
     }
     System.out.println("\tbr i1 "+value+", label %loopStart"+numLoops+", label %loopEnd"+numLoops);
     System.out.println("loopStart"+numLoops+":");

     n.f4.accept(this, argu);
     System.out.println("\tbr label %loopInit"+numLoops);
     System.out.println("loopEnd"+numLoops+":");
     numLoops++;

     return null;
  }


  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(CompareExpression n, Map argu) throws Exception {

    // System.out.println("Compare1");

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
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  public String[] visit(MessageSend n, Map argu) throws Exception {
     String[] pr = n.f0.accept(this, argu);

     if(pr[0].equals("this")) {
       pr[0] = currentClass;
     }
     // System.out.println(pr[0]+" pr "+pr[1]);
     String[] meth = n.f2.accept(this, argu);
     int offset = check_meth(pr[0]+"."+meth[1],pr[0],argu);
     System.out.println("\t; "+pr[0]+"."+meth[1]+": "+offset);
     // System.out.println(pr[0]+" "+pr[1]+" "+" "+offset);
     System.out.println("\t%_"+temps_vars+" = bitcast i8* %"+pr[1]+" to i8***");
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = load i8**, i8*** %_"+(temps_vars-1));
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = getelementptr i8*, i8** %_"+(temps_vars-1)+", i32 "+offset);
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = load i8*, i8** %_"+(temps_vars-1));
     temps_vars++;
     System.out.println("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+ret_args(meth[1],pr[0],argu));
     String call = "\t%_"+temps_vars;
     temps_vars++;
     // System.out.println("ret_args: "+ret_args(meth[1],currentClass,argu));

     temp_args = new ArrayList();
     n.f4.accept(this, argu);
     // System.out.println(args);
     // %_11 = call i32 %_10(i8* %this, i32 %_13)
     String line_call = "\t%_"+temps_vars+" = call i32"+call+"(i8* %"+pr[1];
     for(String x : temp_args) {
       line_call += ", "+x;
     }
     line_call += ")";
     System.out.println(line_call);
     // System.out.println("temp_args: "+temp_args);
     temp_args.clear();
     String[] res= new String[2];
     res[0] = "i32";//+++++++++++++++++++++++++
     res[1] = "%_"+temps_vars;
     temps_vars++;
     return res;
  }

  /**
   * f0 -> Expression()
   * f1 -> ExpressionTail()
   */
  public String[] visit(ExpressionList n, Map argu) throws Exception {
     String[] expr = n.f0.accept(this, argu);
     // System.out.println(expr[0]);
     temp_args.add(expr[0]+" "+expr[1]);
     n.f1.accept(this, argu);
     return null;
  }

  /**
   * f0 -> ( ExpressionTerm() )*
   */
  public String[] visit(ExpressionTail n,Map argu) throws Exception {
     return n.f0.accept(this, argu);
  }


  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public String[] visit(ExpressionTerm n, Map argu) throws Exception {
     String[] expr = n.f1.accept(this, argu);
     // System.out.println(expr[1]);
     temp_args.add(expr[0]+" "+expr[1]);
     return null;
  }



   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String[] visit(PrintStatement n, Map argu) throws Exception {

      String[] expr = n.f2.accept(this, argu);
      System.out.println("\tcall void (i32) @print_int(i32 "+expr[1]+")");
      return null;
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
   * f0 -> "true"
   */
  public String[] visit(TrueLiteral n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = "i1";
    return res;
  }

  /**
   * f0 -> "false"
   */
  public String[] visit(FalseLiteral n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = "i1";
    return res;
  }

  /**
   * f0 -> "this"
   */
  public String[] visit(ThisExpression n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = n.f0.toString();
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

  String check_in_meth(String var,MethodForm methF) {
    if(methF.Arguments.containsKey(var)) {
      return methF.Arguments.get(var);
    }
    if(methF.Vars.containsKey(var)) {
      return methF.Vars.get(var);
    }
    return null;
  }

  String[] check_var(String var,String className,Map ClassTypes) {
    /* check if var is definied */
    ClassForm classF =(ClassForm) ClassTypes.get(className);
    String[] res= new String[2];

    String clean_var = var;
    var = currentClass+"."+var;
    // System.out.println(var+" here1"+classF.ClassVarInfo.keySet());
    if(classF.ClassVarInfo.containsKey(var)) { //it definied in currentClass
      // System.out.println(var+" here2 "+classF.ClassVarInfo.get(var));
      res[0] = classF.ClassVars.get(clean_var);
      // System.out.println(res[0]);
      res[1] = String.valueOf(classF.ClassVarInfo.get(var)+8);
      return res;
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) { //check in all super classes
      classF = (ClassForm) ClassTypes.get(superClass);

      if(classF.ClassVars.get(var) != null) {
        res[0] = classF.ClassVars.get(clean_var);
        res[1] = String.valueOf(classF.ClassVarInfo.get(var)+8);
        return res;
      }

      superClass = classF.Isimpliments;
    }
    return null;
  }




  int check_meth(String var,String className,Map ClassTypes) {
    ClassForm classF =(ClassForm) ClassTypes.get(className);

    if(classF.MethodInfo.containsKey(var)) { //it definied in currentClass
      return classF.MethodInfo.get(var);
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) { //check in all super classes
      classF = (ClassForm) ClassTypes.get(superClass);

      if(classF.MethodInfo.get(var) != null) {
        return classF.MethodInfo.get(var);
      }

      superClass = classF.Isimpliments;
    }
    return -1;
  }

  String ret_args(String classMeth,String className,Map ClassTypes) {
    ClassForm M = (ClassForm) ClassTypes.get(className);

    // for(String meth : M.Methods.keySet() ) {
      MethodForm methF = M.Methods.get(classMeth);
      String type = "i8*";
      if(methF.Type.equals("int")) {
        type = "i32";
      }
      else if(methF.Type.equals("boolean")) {
        type = "i1";
      }
      String res = type+" (i8*";
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
        res += (","+arg_type);
      }
      res += ")*";
    // }
    return res;
  }


    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String[] visit(MainClass n, Map argu) throws Exception {
     String[] className = n.f1.accept(this, argu);
     System.out.println("@."+className[1]+"_vtable = global [0 x i8*] []");
     // n.f6.accept(this, argu);
     // n.f11.accept(this, argu);
     System.out.println("define i32 @main() {");
     n.f14.accept(this, argu);
     n.f15.accept(this, argu);
     // n.f16.accept(this, argu);
     // n.f17.accept(this, argu);
     System.out.println("\tret i32 0");
     System.out.println("}");
     return null;
    }


}
