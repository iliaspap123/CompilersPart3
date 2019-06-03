import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;


public class ll_visitor extends GJDepthFirst<String[], Map> {

// oi visitors epistrefoun enan pinaka apo string me 2 theseis
// sthn thesh 0 krataw ton typo kai sthn 1 ton kataxwrith-timh
// san orisma pairnoun to vtable mazi me ta offsets

  int temps_vars;
  int numIfs;
  int numLoops;
  String currentClass;
  String currentMeth;
  ArrayList<String> temp_args;
  BufferedWriter writer;
  String fileName;

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
     temps_vars = 0;
     // numIfs = 0;
     // numLoops = 0;
     String[] type = n.f1.accept(this, argu);
     String[] meth = n.f2.accept(this, argu);
     currentMeth = meth[1];
     if(!type[1].equals("i1") &&!type[1].equals("i32") && !type[1].equals("i8*")) {
       type[1] = "i8*";
     }
     String str_def = "define "+type[1]+" @"+currentClass+"."+meth[1]+"(i8* %this";
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
     emit(str_def+") {");
     emit(str_decl);
     n.f4.accept(this, argu);
     n.f7.accept(this, argu);
     n.f8.accept(this, argu);
     String[] ret = n.f10.accept(this, argu);
     String temp_type = ret[0];
     if(!ret[0].equals("i1") && !ret[0].equals("i32") && !ret[0].equals("i8*")) {
       temp_type = "i8*";
     }
     emit("\tret "+temp_type+" "+ret[1]);
     emit("}");
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
     emit("\t%"+ident[1]+" = alloca "+type[1]);
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
       if((type=check_in_meth(pr[1],methF))!=null) {
         if(type.equals("int")) {
           pr[0] = "i32";
         }
         else if(type.equals("boolean")) {
           pr[0] = "i1";
         }
         else {
           pr[0]="i8*";
         }
         emit("\t%_"+temps_vars+" = load "+pr[0]+", "+pr[0]+"* %"+pr[1]);
         pr[1] = "%_"+temps_vars;
         temps_vars++;
         if(pr[0].equals("i8*")) {
           pr[0] = type;
         }
       }
       else {
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
         emit("\t%_"+temps_vars+" = getelementptr i8, i8* %this, i32 "+offset[1]);
         temps_vars++;
         emit("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+pr[0]+"*");
         temps_vars++;
         emit("\t%_"+temps_vars+" = load "+pr[0]+", "+pr[0]+"* %_"+(temps_vars-1));
         temps_vars++;
         if(pr[0].equals("i8*")) {
           pr[0] = offset[0];
         }
         pr[1] = "%_"+(temps_vars-1);

       }

     }
     return pr;
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public String[] visit(ArrayAllocationExpression n, Map argu) throws Exception {
     String[] expr = n.f3.accept(this, argu);

     emit("\t%_"+temps_vars+" = icmp slt i32 "+expr[1]+", 0");
     emit("\tbr i1 %_"+temps_vars+", label %arr_alloc"+numIfs+", label %arr_alloc"+(numIfs+1));
     temps_vars++;
     emit("arr_alloc"+numIfs+":");
     emit("\tcall void @throw_oob()");
     emit("\tbr label %arr_alloc"+(numIfs+1));
     emit("arr_alloc"+(numIfs+1)+":");

     emit("\t%_"+temps_vars+" = add i32 "+expr[1]+", 1");
     temps_vars++;
     emit("\t%_"+temps_vars+" = call i8* @calloc(i32 4,i32 %_"+(temps_vars-1)+")");
     temps_vars++;
     emit("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to i32*");
     emit("\tstore i32 "+expr[1]+", i32* %_"+temps_vars);
     String[] res= new String[2];
     res[0] = "i32*";
     res[1] = "%_"+temps_vars;
     temps_vars++;
     numIfs += 2;


     return res;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  public String[] visit(ArrayAssignmentStatement n, Map argu) throws Exception {
     String[] Ident_arr = n.f0.accept(this, argu);

     ClassForm classF = (ClassForm) argu.get(currentClass);
     MethodForm methF = classF.Methods.get(currentMeth);
     if(check_in_meth(Ident_arr[1],methF)==null) {
       String[] offset = check_var(Ident_arr[1],currentClass,argu);
       offset[0] = "i32*";
       emit("\t%_"+temps_vars+" = getelementptr i8, i8* %this, i32 "+offset[1]);
       temps_vars++;
       emit("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+offset[0]+"*");
       temps_vars++;

       Ident_arr[1] = "%_"+(temps_vars-1);
     }

     String[] expr1 = n.f2.accept(this, argu);
     String[] expr2 = n.f5.accept(this, argu);
     String oob1 = "oob"+numIfs;
     String oob2 = "oob"+(numIfs+1);
     String oob3 = "oob"+(numIfs+2);
     numIfs += 3;
     String arr = "%_"+temps_vars;
     emit("\t%_"+temps_vars+" = load i32*, i32** "+Ident_arr[1]);
     temps_vars++;
     emit("\t%_"+temps_vars+" = load i32, i32 *%_"+(temps_vars-1));
     temps_vars++;
     emit("\t%_"+temps_vars+" = icmp ult i32 "+expr1[1]+", %_"+(temps_vars-1));
     emit("\tbr i1 %_"+temps_vars+", label %"+oob1+", label %"+oob2);
     temps_vars++;
     emit(oob1+":");
     //
     emit("\t%_"+temps_vars+" = add i32 "+expr1[1]+", 1");
     temps_vars++;
     emit("\t%_"+temps_vars+" = getelementptr i32, i32* "+arr+", i32 %_"+(temps_vars-1));
     emit("\tstore i32 "+expr2[1]+", i32* %_"+temps_vars);
     emit("\tbr label %"+(oob3));
     emit(oob2+":");
     emit("\tcall void @throw_oob()");
     emit("\tbr label %"+oob3);
     emit(oob3+":");
     temps_vars++;
     return null;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public String[] visit(ArrayLookup n, Map argu) throws Exception {
     String[] pr1 = n.f0.accept(this, argu);
     String[] pr2 = n.f2.accept(this, argu);
     String oob1 = "oob"+numIfs;
     String oob2 = "oob"+(numIfs+1);
     String oob3 = "oob"+(numIfs+2);
     numIfs+=3;
     emit("\t%_"+temps_vars+" = load i32, i32 *"+pr1[1]);
     temps_vars++;
     emit("\t%_"+temps_vars+" = icmp ult i32 "+pr2[1]+", %_"+(temps_vars-1));
     emit("\tbr i1 %_"+temps_vars+", label %"+oob1+", label %"+oob2);
     temps_vars++;
     emit(oob1+":");

     emit("%_"+temps_vars+" = add i32 "+pr2[1]+", 1");
     temps_vars++;
     emit("%_"+temps_vars+" = getelementptr i32, i32* "+pr1[1]+", i32 %_"+(temps_vars-1));
     temps_vars++;
     emit("%_"+temps_vars +" = load i32, i32* %_"+(temps_vars-1));
     emit("\tbr label %"+oob3);
     emit(oob2+":");
     emit("\tcall void @throw_oob()");
     emit("\tbr label %"+oob3);
     emit(oob3+":");
     String[] res= new String[2];
     res[0] = "i32";
     res[1] = "%_"+temps_vars;
     temps_vars++;
     return res;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public String[] visit(ArrayLength n, Map argu) throws Exception {
     String[] pr = n.f0.accept(this, argu);
     emit("\t%_"+temps_vars+" = load i32, i32 *"+pr[1]);
     String[] res= new String[2];
     res[0] = "i32";
     res[1] = "%_"+temps_vars;
     temps_vars++;
     return res;
  }


  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public String[] visit(AllocationExpression n, Map argu) throws Exception {
     String[] type = n.f1.accept(this, argu);
     type[0] = type[1];
     ClassForm classF = (ClassForm) argu.get(type[0]);
     int size = (classF.offset_var+8);
     // int num_meth = classF.Methods.size();
     // while(classF.Isimpliments != null) {
     //   classF = (ClassForm)argu.get(classF.Isimpliments);
     //   num_meth+=classF.MethodInfo.size();
     // }
     String temptype = type[0];
     LinkedHashMap<String,String> vtable = new LinkedHashMap();
     fix_vtable(vtable,temptype,argu);
     classF = (ClassForm) argu.get(type[0]);
     emit("\t%_"+temps_vars+" = call i8* @calloc(i32 1, i32 "+ size+")");
     type[1] = "%_"+temps_vars;
     temps_vars++;
     emit("\t%_"+temps_vars+" = bitcast i8* "+type[1]+" to i8***");
     temps_vars++;
     emit("\t%_"+temps_vars+" = getelementptr ["+vtable.size()+" x i8*], ["+vtable.size()+" x i8*]* @."+type[0]+"_vtable, i32 0, i32 0");
     emit("\tstore i8** "+"%_"+temps_vars+", i8*** %_"+(temps_vars-1));
     temps_vars++;
     //System.out.println("eftasa");
     return type;
  }

  int count_size(Map ClassTypes,String className) throws Exception{
    ClassForm classF = (ClassForm) ClassTypes.get(className);
    emit("size "+(classF.offset_var+8));
    return 0;
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
   //System.out.println(expr[0]);
   ClassForm classF = (ClassForm) argu.get(currentClass);
   MethodForm methF = classF.Methods.get(currentMeth);
   if(check_in_meth(Ident_arr[1],methF)==null) {
     //System.out.println("ready");

     String[] offset = check_var(Ident_arr[1],currentClass,argu);
     if(offset[0].equals("int")) {
       Ident_arr[0] = "i32";
     }
     else if(offset[0].equals("int[]")) {
       Ident_arr[0] = "i32*";
     }
     else if(offset[0].equals("boolean")) {
       Ident_arr[0] = "i1";
     }
     else {
       Ident_arr[0]="i8*";
     }
     emit("\t%_"+temps_vars+" = getelementptr i8, i8* %this, i32 "+offset[1]);
     temps_vars++;
     emit("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+Ident_arr[0]+"*");
     temps_vars++;

     Ident_arr[1] = "_"+(temps_vars-1);

   }
   //System.out.println("kai edw");

   String type = null;
   String value = null;
   if(expr !=null) {
     type = expr[0];
     value = expr[1];
   }
   if(!type.equals("i1") && !type.equals("i32") && !type.equals("i32*") && !type.equals("i8*")) {
     type = "i8*";
   }
   emit("\tstore "+ type + " "+ value +", "+type+"* %"+Ident_arr[1]);
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

     String expr[] = n.f2.accept(this, argu);

     String type = null;
     String value = null;
     if(expr!=null ) { // && expr.startsWith("i32 ")
       type = expr[0];
       value = expr[1];
     }
     String labelIf = "if"+numIfs;
     String labelElse = "else"+numIfs;
     String labelEnd = "end"+numIfs;
     numIfs++;
     emit("\tbr i1 "+value+", label %"+labelIf+", label %"+labelElse);
     emit(labelIf+":");

     n.f4.accept(this, argu);
     emit("\n\tbr label %"+labelEnd);
     emit(labelElse+":");
     //
     n.f6.accept(this, argu);
     emit("\n\tbr label %"+labelEnd);

     emit("\n"+labelEnd+":");

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

     String loopInit = "loopInit"+numLoops;
     String loopStart = "loopStart"+numLoops;
     String loopEnd = "loopEnd"+numLoops;
     numLoops++;
     emit("\tbr label %"+loopInit);
     emit(loopInit+":");
     String expr[] = n.f2.accept(this, argu);

     String type = null;
     String value = null;
     if(expr!=null ) { // && expr.startsWith("i32 ")
       type = expr[0];
       value = expr[1];
     }
     emit("\tbr i1 "+value+", label %"+loopStart+", label %"+loopEnd);
     emit(loopStart+":");

     n.f4.accept(this, argu);
     emit("\tbr label %"+loopInit);
     emit(loopEnd+":");

     return null;
  }


  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public String[] visit(CompareExpression n, Map argu) throws Exception {

     String[] pr1 = n.f0.accept(this, argu);

     String value1 = pr1[1];
     if(pr1[0].equals("Identifier") ) {
       value1 = "%_"+String.valueOf(temps_vars);
       emit("\t"+value1+" = load i32, i32* %"+pr1[1]);
       temps_vars++;
     }

     String[] pr2 = n.f2.accept(this, argu);

     String value2 = pr2[1];
     if(pr2[0].equals("Identifier") ) {
       value2 = "%_"+String.valueOf(temps_vars);
       emit("\t"+value2+" = load i32, i32* %"+pr2[1]);
       temps_vars++;
     }

     String[] res= new String[2];
     res[0] = "i1";
     res[1] = "%_"+String.valueOf(temps_vars);
     emit("\t"+res[1]+" = icmp slt i32 "+value1+", "+value2);
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
    emit("\t"+res[1]+" = add i32 "+value1+", "+value2);
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
    emit("\t"+res[1]+" = sub i32 "+value1+", "+value2);
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
     emit("\t"+res[1]+" = mul i32 "+value1+", "+value2);
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
     // emit(pr[0]+" pr "+pr[1]);
     String[] meth = n.f2.accept(this, argu);
     int offset = check_meth(pr[0]+"."+meth[1],pr[0],argu)/8;
     // System.out.println(offset);
     emit("\t; "+pr[0]+"."+meth[1]+": "+offset);
     // emit(pr[0]+" "+pr[1]+" "+" "+offset);
     emit("\t%_"+temps_vars+" = bitcast i8* "+pr[1]+" to i8***");
     temps_vars++;
     emit("\t%_"+temps_vars+" = load i8**, i8*** %_"+(temps_vars-1));
     temps_vars++;
     emit("\t%_"+temps_vars+" = getelementptr i8*, i8** %_"+(temps_vars-1)+", i32 "+offset);
     temps_vars++;
     emit("\t%_"+temps_vars+" = load i8*, i8** %_"+(temps_vars-1));
     temps_vars++;
     emit("\t%_"+temps_vars+" = bitcast i8* %_"+(temps_vars-1)+" to "+ret_args(meth[1],pr[0],argu));
     String call = "%_"+temps_vars;
     temps_vars++;

     temp_args = new ArrayList();
     n.f4.accept(this, argu);

     String type_meth = get_meth_type(meth[1],pr[0],argu);
     String line_call;
     if(!type_meth.equals("i1") && !type_meth.equals("i32") && !type_meth.equals("i32*")) {
        line_call = "\t%_"+temps_vars+" = call i8* "+call+"(i8* "+pr[1];
     }
     else {
       line_call = "\t%_"+temps_vars+" = call "+type_meth+" "+call+"(i8* "+pr[1];
     }
     for(String x : temp_args) {
       line_call += ", "+x;
     }
     line_call += ")";
     emit(line_call);

     temp_args.clear();
     String[] res= new String[2];
     res[0] = type_meth;
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
     if(!expr[0].equals("i1") &&!expr[0].equals("i32") && !expr[0].equals("i8*")) {
       expr[0] = "i8*";
     }
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
     if(!expr[0].equals("i1") &&!expr[0].equals("i32") && !expr[0].equals("i8*")) {
       expr[0] = "i8*";
     }
     temp_args.add(expr[0]+" "+expr[1]);
     return null;
  }

  /**
   * f0 -> "!"
   * f1 -> Clause()
   */
  public String[] visit(NotExpression n, Map argu) throws Exception {
     String[] ret = n.f1.accept(this, argu);
     emit("%_"+temps_vars+" = xor i1 1, "+ret[1]);
     ret[1] = "%_"+temps_vars;
     temps_vars++;
     return ret;
  }

  /**
   * f0 -> Clause()
   * f1 -> "&&"
   * f2 -> Clause()
   */
  public String[] visit(AndExpression n, Map argu) throws Exception {
     String[] res1 = n.f0.accept(this, argu);

     emit("\tbr label %andStart"+numIfs);
     emit("\tandStart"+numIfs+":");
     emit("\tbr i1 "+res1[1]+", label %andclause"+numIfs+", label %andclause"+(numIfs+2));

     emit("andclause"+numIfs+":");

     String[] res2 = n.f2.accept(this, argu);
     emit("\tbr label %andclause"+(numIfs+1));

     emit("andclause"+(numIfs+1)+":");
     emit("\tbr label %andclause"+(numIfs+2));

     emit("andclause"+(numIfs+2)+":");

     emit("%_"+temps_vars+" = phi i1 [ 0,%andStart"+numIfs+"], [ "+res2[1]+", %andclause"+(numIfs+1)+"]");
     res2[1] = "%_"+temps_vars;
     temps_vars++;
     return res2;
  }


   /**
    * f0 -> "emit"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String[] visit(PrintStatement n, Map argu) throws Exception {

      String[] expr = n.f2.accept(this, argu);
      emit("\tcall void (i32) @print_int(i32 "+expr[1]+")");
      return null;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String[] visit(Type n, Map argu) throws Exception {
      String[] type = n.f0.accept(this, argu);
      if(!type[1].equals("i1") && !type[1].equals("i32") && !type[1].equals("i8*")) {
        type[1] = "i8*";
      }
      return type;
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
    res[0] = "i1";
    res[1] = "1";
    return res;
  }

  /**
   * f0 -> "false"
   */
  public String[] visit(FalseLiteral n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = "i1";
    res[1] = "0";
    return res;
  }

  /**
   * f0 -> "this"
   */
  public String[] visit(ThisExpression n, Map argu) throws Exception {
    String[] res= new String[2];
    res[0] = n.f0.toString();
    res[1] = "%"+n.f0.toString();
    return res;
  }

  void fix_vtable(LinkedHashMap table,String className,Map ClassTypes){
    ClassForm classF = (ClassForm)ClassTypes.get(className);
    if(classF.Isimpliments == null) {
      for(String keys : classF.Methods.keySet()) {
        table.put(keys,className);
      }
      return;
    }
    fix_vtable(table,classF.Isimpliments,ClassTypes);
    for(String keys : classF.Methods.keySet()) {
      table.put(keys,className);
    }
    return;
  }

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public String[] visit(Goal n, Map argu) throws Exception {
     writer = new BufferedWriter(new FileWriter(fileName));
     emit("declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\ndefine void @print_int(i32 %i) {\n%_str = bitcast [4 x i8]* @_cint to i8*\ncall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\nret void\n}\ndefine void @throw_oob() {\n  \n%_str = bitcast [15 x i8]* @_cOOB to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}");

     HashMap<String,ClassForm> hash = (HashMap<String,ClassForm>) argu;
     for(String className : hash.keySet() ) {
      ClassForm M = hash.get(className);
      if(M.Methods.containsKey("main")) {
        continue;
      }
      // int count_meth = M.MethodInfo.size();
      // while(M.Isimpliments != null) {
      //   M = hash.get(M.Isimpliments);
      //   count_meth+=M.MethodInfo.size();
      // }
      M = hash.get(className);
      LinkedHashMap<String,String> vtable = new LinkedHashMap();
      fix_vtable(vtable,className,argu);
      emit("@."+className+"_vtable = global ["+vtable.size()+" x i8*] [");
      boolean first = true;
      // System.out.println(vtable);
      for(String method : vtable.keySet()) {
        String curClass = vtable.get(method);
        M = hash.get(curClass);

          MethodForm methF = M.Methods.get(method);
          String type = "i8*";
          if(methF.Type.equals("int")) {
            type = "i32";
          }
          else if(methF.Type.equals("boolean")) {
            type = "i1";
          }
          if(first) {
            first = false;
          }
          else {
            emit(",");
          }
          emit("\t\t\t\ti8* bitcast ("+type+"(i8*");
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
            emit(","+arg_type);
          }
          emit(")* @"+curClass+"."+method+" to i8*)");
      }

      emit("\t\t\t\t]");
     }
     n.f0.accept(this, argu);
     n.f1.accept(this, argu);
     writer.close();
     return null;
  }

  void emit(String code) throws Exception{
    writer.write(code+"\n");
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
     n.f4.accept(this, argu);
     return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */
  public String[] visit(ClassExtendsDeclaration n, Map argu) throws Exception {
     String[] className = n.f1.accept(this, argu);
     currentClass = className[1];
     n.f3.accept(this, argu);
     n.f6.accept(this, argu);
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
    if(classF.ClassVarInfo.containsKey(var)) { //it definied in currentClass
      res[0] = classF.ClassVars.get(clean_var);
      res[1] = String.valueOf(classF.ClassVarInfo.get(var)+8);
      return res;
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) { //check in all super classes
      classF = (ClassForm) ClassTypes.get(superClass);

      if(classF.ClassVars.get(clean_var) != null) {
        res[0] = classF.ClassVars.get(clean_var);
        res[1] = String.valueOf(classF.ClassVarInfo.get(superClass+"."+clean_var)+8);
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

  String get_meth_type(String classMeth,String className,Map ClassTypes) {
    ClassForm M = (ClassForm) ClassTypes.get(className);
    while(!M.Methods.containsKey(classMeth)) {
      M = (ClassForm) ClassTypes.get(M.Isimpliments);
    }
    MethodForm methF = M.Methods.get(classMeth);
    if(methF.Type.equals("int")) {
      return "i32";
    }
    else if(methF.Type.equals("boolean")) {
      return "i1";
    }
    return methF.Type;
    }

  String ret_args(String classMeth,String className,Map ClassTypes) {
    ClassForm M = (ClassForm) ClassTypes.get(className);
    while(!M.Methods.containsKey(classMeth)) {
      M = (ClassForm) ClassTypes.get(M.Isimpliments);
    }
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
     currentClass = className[1];
     currentMeth = "main";
     emit("@."+className[1]+"_vtable = global [0 x i8*] []");

     emit("define i32 @main() {");
     n.f14.accept(this, argu);
     n.f15.accept(this, argu);

     emit("\tret i32 0");
     emit("}");
     return null;
    }


}
