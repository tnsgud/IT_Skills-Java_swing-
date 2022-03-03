package 잡동사니;

import java.util.ArrayList;

public class Login extends BaseFrame {
	ArrayList<String> arr = new ArrayList<String>();
	
	public Login() {
		System.out.println("this message Login constructor ");
		
		ui();
	}
	
	@Override
	void ui() {
		System.out.println(arr.hashCode());
		System.out.println("this message ui method");
	}

	
	public static void main(String[] args) {
		new Login();
	}
}
