import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;



public class TableVisitor extends GJDepthFirst<String, Map> {


  public HashMap<String,ClassForm> ClassTypes = new HashMap(); //symbol table
  String currentClass;

  /**
  * f0 -> "class"
  * f1 -> Identifier()
  * f2 -> "{"
  * f3 -> ( VarDeclaration() )*
  * f4 -> ( MethodDeclaration() )*
  * f5 -> "}"
  */
  public String visit(ClassDeclaration n, Map argu) throws Exception {

    String className = n.f1.accept(this, argu);
    currentClass = className;

    if(ClassTypes.containsKey(className)) {
      String Message = "double decleration of class "+className;
      throw new MyException(className,null,Message);
    }

    ClassForm elem = new ClassForm();
    ClassTypes.put(className,elem);

    if(n.f3.present()) {
      n.f3.accept(this, elem.ClassVars); // insert ClassVars to map
      for(String key : elem.ClassVars.keySet()) { // add offsets
        LinkedHashMap<String,String> tmpClassF = elem.ClassVars;
        elem.MyAddVar(className+"."+key,tmpClassF.get(key));
      }
    }

    if(n.f4.present()) {
      n.f4.accept(this, elem.Methods); // insert methods to map
      for(String key : elem.Methods.keySet()) { // add methods offset
        LinkedHashMap<String,MethodForm> tmpMeth = elem.Methods;
        MethodForm methF = tmpMeth.get(key);
        elem.MyAddMethod(className+"."+key,methF.Type);
      }
    }
    return className;
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
  public String visit(ClassExtendsDeclaration n, Map argu) throws Exception {

    String className = n.f1.accept(this, argu);
    currentClass = className;

    if(ClassTypes.containsKey(className)) {
      String Message = "double decleration of class "+className;
      throw new MyException(className,null,Message);
    }

    ClassForm elem = new ClassForm();
    ClassTypes.put(className,elem);

    elem.Isimpliments = n.f3.accept(this, argu);
    if(!ClassTypes.containsKey(elem.Isimpliments) || className.equals(elem.Isimpliments)) {
      String Message = "extended class has not declared yet";
      throw new MyException(className,null,Message);
    }

    ClassForm super_elem = ClassTypes.get(elem.Isimpliments);

    elem.offset_var = super_elem.offset_var; // take superClass offsets
    elem.offset_meth = super_elem.offset_meth;
    if(n.f5.present()) {
      n.f5.accept(this, elem.ClassVars);
      for(String key : elem.ClassVars.keySet()) {
        LinkedHashMap<String,String> tmpClassF = elem.ClassVars;
        elem.MyAddVar(className+"."+key,tmpClassF.get(key));
      }
    }
    if(n.f6.present()) {
      n.f6.accept(this, elem.Methods);
      for(String key : elem.Methods.keySet()) {
        LinkedHashMap<String,MethodForm> tmpMeth = elem.Methods;
        MethodForm methF = tmpMeth.get(key);
        if(!super_elem.Methods.containsKey(key)) { // add method offset if method doesn't exist
          elem.MyAddMethod(className+"."+key,methF.Type);
        }
      }
    }

    return className;
  }


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
  public String visit(MethodDeclaration n, Map argu) throws Exception {

    MethodForm meth = new MethodForm();

    meth.Type = n.f1.accept(this, null);
    String funct = n.f2.accept(this, null); // function name

    if(argu.containsKey(funct)) {
      String Message = "Method "+funct + " is already declared";
      throw new MyException(currentClass,null,Message);
    }

    n.f4.accept(this, meth.Arguments);

    if(n.f7.present()) {
      n.f7.accept(this, meth.Vars);
    }
    argu.put(funct,meth);
    return funct;
  }


  /**
  * f0 -> Type()
  * f1 -> Identifier()
  */
  public String visit(FormalParameter n, Map argu) throws Exception {
    String type = n.f0.accept(this, argu);
    String ident = n.f1.accept(this, argu);
    if(argu.containsKey(ident)) {
      String Message = "Variable "+ident + " is already declared";
      throw new MyException(currentClass,null,Message);
    }
    argu.put(ident,type);
    return type+ident;
  }


  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public String visit(VarDeclaration n, Map argu) throws Exception {
     String Type = n.f0.accept(this, argu);
     String Ident = n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     if(argu.containsKey(Ident)) {
       String Message = "Variable "+Ident + " is already declared";
       throw new MyException(currentClass,null,Message);
     }
     argu.put(Ident,Type);

     return Type + " " + Ident;
  }


  /**
  * f0 -> <IDENTIFIER>
  */
  public String visit(Identifier n, Map argu) throws Exception {
    return n.f0.toString();
  }

  /**
   * f0 -> "boolean"
   */
  public String visit(BooleanType n, Map argu) throws Exception {
     return n.f0.toString();
  }


  /**
  * f0 -> "int"
  */
  public String visit(IntegerType n, Map argu) throws Exception {
    return n.f0.toString();
  }

  /**
  * f0 -> "int"
  * f1 -> "["
  * f2 -> "]"
  */
  public String visit(ArrayType n, Map argu) throws Exception {
    return n.f0.toString()+n.f1.toString()+n.f2.toString();
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
  public String visit(MainClass n, Map argu) throws Exception {
     String className = n.f1.accept(this, argu);
     currentClass = className;

     ClassForm elem = new ClassForm();
     ClassTypes.put(className,elem);

     MethodForm main_method = new MethodForm();


     elem.Methods.put("main",main_method);

     main_method.Type = "void";
     main_method.Arguments.put(n.f11.accept(this, argu),"String[]");

     n.f14.accept(this, main_method.Vars);

     return className;
  }
}
