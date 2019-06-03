import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;



class Main {
    public static void main (String [] args){
	if(args.length < 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
	}
	FileInputStream fis = null;
  for(String arg: args) { // for all arguments
  	try{
        String extensionRemoved = arg.split(".java")[0];
        String fileWrite = extensionRemoved+".ll";
  	    fis = new FileInputStream(arg);
  	    MiniJavaParser parser = new MiniJavaParser(fis);
  	    System.err.println("Program parsed successfully: "+arg);
 	      TableVisitor eval = new TableVisitor();
        Goal root = parser.Goal();
        root.accept(eval, null);

        // // for all classes print offsets //
        // for(String keys : eval.ClassTypes.keySet()) {
        //   ClassForm M = eval.ClassTypes.get(keys);
        //
        //   M.printAll(keys);
        // }

        ll_visitor c = new ll_visitor();
        c.fileName = fileWrite;
        root.accept(c, eval.ClassTypes);
  	}
  	catch(ParseException ex){
  	    System.err.println(ex.getMessage());
  	}
  	catch(FileNotFoundException ex){
  	    System.err.println(ex.getMessage());
  	}
    catch(Exception ex){
      System.err.println("error");
      System.err.println(ex.getMessage());
    }
  	finally{
  	    try{
  		if(fis != null) fis.close();
  	    }
  	    catch(IOException ex){
  		System.err.println(ex.getMessage());
  	    }
  	}
  }
    }
}
