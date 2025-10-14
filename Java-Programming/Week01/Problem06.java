// https://codeforces.com/problemset/problem/6/A

import java.util.Arrays;
import java.util.Scanner;

public class Problem06 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int[] sticks = new int[4];
        for (int i = 0; i < 4; i++) {
            sticks[i] = sc.nextInt();
        }

        Arrays.sort(sticks);

        boolean triangle = false;
        boolean segment = false;

        for (int i = 0; i < 2; i++) {
            int a = sticks[i], b = sticks[i + 1], c = sticks[i + 2];

            if (a + b > c) {
                triangle = true;
                break;
            } else if (a + b == c) {
                segment = true;
            }
        }

        if (triangle) {
            System.out.println("TRIANGLE");
        } else if (segment) {
            System.out.println("SEGMENT");
        } else {
            System.out.println("IMPOSSIBLE");
        }

        sc.close();
    }
}
