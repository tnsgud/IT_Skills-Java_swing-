package 잡동사니;

import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		var str = "ㅎㅇ\n";
		str = str.replaceAll("\\^", "dd");
		System.out.println(str);
	}
}
