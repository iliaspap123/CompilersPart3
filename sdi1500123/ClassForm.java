import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

class ClassInfo {
  int offset_var;
  int offset_meth;
  public LinkedHashMap<String,Integer> ClassVarInfo = new LinkedHashMap();
  public LinkedHashMap<String,Integer> MethodInfo = new LinkedHashMap();

  public void MyAddMethod(String name,String type) {
    this.MethodInfo.put(name,offset_meth);
    this.offset_meth += 8;
  }

  public void printAll(String className) {

    System.out.println("-----------Class "+ className+ "-----------");
    System.out.println("--Variables---");
    for(String keys : this.ClassVarInfo.keySet()) {
      System.out.println(keys + ": " + this.ClassVarInfo.get(keys));
    }

    System.out.println("---Methods---");
    for(String keys : this.MethodInfo.keySet()) {
      System.out.println(keys + ": " + this.MethodInfo.get(keys));
    }
    System.out.println("");
  }

  public void MyAddVar(String name,String type) {
    this.ClassVarInfo.put(name,offset_var);
    if(type.equals("int")) {
      this.offset_var += 4;
    }
    else if(type.equals("boolean")) {
      this.offset_var += 1;
    }
    else {
      this.offset_var += 8;
    }
  }
}


public class ClassForm extends ClassInfo{
  String Isimpliments;
  public LinkedHashMap<String,String> ClassVars = new LinkedHashMap();
  public LinkedHashMap<String,MethodForm> Methods = new LinkedHashMap();
}

class MethodForm {
  String Type;
  LinkedHashMap<String,String> Arguments = new LinkedHashMap();
  HashMap<String,String> Vars = new HashMap();
}
