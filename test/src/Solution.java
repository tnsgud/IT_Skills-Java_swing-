import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Solution {

	public String[] solution(String[] strings, int n) {
		var arr = new ArrayList<Character>();
		var result = new ArrayList<String>();

		for (int i = 0; i < strings.length; i++) {
			arr.add(strings[i].charAt(n));
		}

		Collections.sort(arr);
		Arrays.sort(strings);


		for (int i = 0; i < arr.size(); i++) {
			for (int j = 0; j < strings.length; j++) {
				if (strings[j].charAt(n) == arr.get(i) && !result.contains(strings[j])) {
					result.add(strings[j]);
				}
			}
		}
	
		return result.toArray(String[]::new);
	}

	public Solution() {
		System.out.println(String.join(",", solution("sun,bed,car".split(","), 1)));
		// System.out.println(String.join(",", solution("sun,bed,car".split(","), 1)));
	}

	public static void main(String[] args) {
		new Solution();
	}
}
