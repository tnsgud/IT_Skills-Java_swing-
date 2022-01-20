package aa;

import java.util.Iterator;

public class Main {
	public static void main(String[] args) {
		int arr[][] = new int[15][15];
		int result[][] = new int[15][15];
		int po[] = { 8 - 1, 4 - 1, 8 - 1, 10 - 1 };

		// 1차: 소리 확산
		for (int r = 0; r < po.length / 2; r++) {
			for (int i = 4; 1 <= i; i--) {
				for (int j = po[r * 2] - i; j <= po[r * 2] + i; j++) {
					for (int k = po[r * 2 + 1] - i; k <= po[r * 2 + 1] + i; k++) {
						if (0 <= j && j < 15 && k >= 0 && k < 15 && !(j == po[r * 2] && k == po[r * 2 + 1])) {
							arr[k][j]++;
							result[k][j]++;
						}
					}
				}
			}
		}

		// 2차: 에코처리
		// 상
		for (int r = 0; r < po.length / 2; r++) {
			if (po[r * 2 + 1] < 5) {
				if (arr[0][po[r * 2]] > 1) {
					int x = arr[0][po[r * 2]];
					for (int i = x - 1; i >= 0; i--) {
						for (int j = 0; j < 15; j++) {
							if (result[i][j] + (arr[0][j] - i) > 0 && !(j == po[r * 2] && i == po[r * 2 + 1]))
								result[i][j] += (arr[0][j] - i);
						}
					}
				}
			}
		}
//		//하
		for (int r = 0; r < po.length / 2; r++) {
			if (11 <= po[r * 2 + 1]) {
				if (arr[14][po[r * 2]] > 1) {
					int x = arr[14][po[r * 2]];
					for (int i = x - 1; i >= 0; i--) {
						for (int j = 0; j < 15; j++) {
							if (result[14 - i][j] + (arr[14][j] - i) > 0 && !(j == po[r * 2] && i == po[r * 2 + 1]))
								result[14 - i][j] += (arr[14][j] - i);
						}
					}
				}
			}
		}

		// 좌
		for (int r = 0; r < po.length / 2; r++) {
			if (po[r * 2] < 5) {
				if (arr[po[r * 2 + 1]][0] > 1) {
					int x = arr[po[r * 2 + 1]][0];
					for (int i = 0; i < 15; i++) {
						for (int j = x - 1; j >= 0; j--) {
							if (result[i][j] + (arr[i][0] - j) > 0 && !(j == po[r * 2] && i == po[r * 2 + 1]))
								result[i][j] += (arr[i][0] - j);
						}
					}
				}
			}
		}
////		
////		//우
		for (int r = 0; r < po.length / 2; r++) {
			if (11 <= po[r * 2]) {
				if (arr[po[r * 2 + 1]][14] > 1) {
					int x = arr[po[r * 2 + 1]][14];
					for (int i = 0; i < 15; i++) {
						for (int j = x - 1; j >= 0; j--) {
							if (result[i][14 - j] + (arr[i][14] - j) > 0 && !(j == po[r * 2] && i == po[r * 2 + 1]))
								result[i][14 - j] += (arr[i][14] - j);
						}
					}
				}
			}
		}

		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				sum += result[i][j];
				System.out.print(result[i][j] + " ");
			}
			System.out.println("");
		}

		System.out.println(sum);
	}
}
