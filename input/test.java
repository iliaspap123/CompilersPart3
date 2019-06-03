class Test{
    public static void main(String[] a){
	B a;
	a = new B();
	System.out.println(new B().foo());
    }
}


class A{
	int x;
	public int faa1(){
		System.out.println(2);
		return 3;
	}
	public int foo(){
		System.out.println(2);
		return 3;
	}
	public int faa2(){
		System.out.println(2);
		return 3;
	}
}
class B extends A{

	public int foo(){
		System.out.println(1);
		//System.out.println(this.foo());
		return 4;
	}

}