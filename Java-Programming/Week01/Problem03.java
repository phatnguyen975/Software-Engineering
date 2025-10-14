// https://codeforces.com/problemset/problem/3/A

import java.util.Scanner;

public class Problem03 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String s = sc.nextLine();
        String t = sc.nextLine();

        int x1 = s.charAt(0) - 'a' + 1;
        int y1 = s.charAt(1) - '0';
        int x2 = t.charAt(0) - 'a' + 1;
        int y2 = t.charAt(1) - '0';

        // Chebyshev distance:
        // https://www.geeksforgeeks.org/machine-learning/chebyshev-distance/
        int minimumMoves = Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
        System.out.println(minimumMoves);

        while (x1 != x2 || y1 != y2) {
            String moveDirection = "";

            if (x2 > x1) {
                moveDirection += "R";
                x1++;
            } else if (x2 < x1) {
                moveDirection += "L";
                x1--;
            }

            if (y2 > y1) {
                moveDirection += "U";
                y1++;
            } else if (y2 < y1) {
                moveDirection += "D";
                y1--;
            }

            System.out.println(moveDirection);
        }

        sc.close();
    }
}
