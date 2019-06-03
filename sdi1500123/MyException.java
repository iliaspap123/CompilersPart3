
public class MyException extends Exception {
  String ClassName;
  String MethodName;
  String Message;

  public MyException(String myClass, String myMeth, String myMess) {
    ClassName = myClass;
    MethodName = myMeth;
    Message = myMess;
  }

  public String getMessage() {
    String Myerror = null;
    if(MethodName == null) {
      Myerror = "In class: " + ClassName + " with message: " + Message;
    }
    else {
      Myerror = "In class: " + ClassName + " In Method: " + MethodName + " with message: " + Message;
    }
    return Myerror;
  }


}
