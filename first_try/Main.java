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
        System.err.println(fileWrite);
  	    fis = new FileInputStream(arg);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileWrite));
        writer.write("declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\ndefine void @print_int(i32 %i) {\n%_str = bitcast [4 x i8]* @_cint to i8*\ncall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\nret void\n}\ndefine void @throw_oob() {\n  \n%_str = bitcast [15 x i8]* @_cOOB to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}");
        writer.write("write krrrraa");
        writer.close();
  	    MiniJavaParser parser = new MiniJavaParser(fis);
  	    System.err.println("Program parsed successfully: "+arg);
 	      TableVisitor eval = new TableVisitor();
        Goal root = parser.Goal();
        root.accept(eval, null);

        System.out.println("declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\ndefine void @print_int(i32 %i) {\n%_str = bitcast [4 x i8]* @_cint to i8*\ncall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\nret void\n}\ndefine void @throw_oob() {\n  \n%_str = bitcast [15 x i8]* @_cOOB to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}");
        // // semantic analysis //
        // check c = new check();
        // root.accept(c, eval.ClassTypes);
        //
        // System.err.println("Program is semantically correct");


        // for all classes print offsets //
        // for(String keys : eval.ClassTypes.keySet()) {
        //   ClassForm M = eval.ClassTypes.get(keys);
        //
        //   M.printAll(keys);
        // }

        ll_visitor c = new ll_visitor();
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
