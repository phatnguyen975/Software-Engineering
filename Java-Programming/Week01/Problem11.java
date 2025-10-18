// https://codeforces.com/problemset/problem/11/A

import java.util.Scanner;

public class Problem11 {
    public static long countMinimumMoves(int[] b, int n, int d) {
        long totalMoves = 0;

        for (int i = 1; i < n; i++) {
            if (b[i] <= b[i - 1]) {
                long diff = b[i - 1] - b[i];
                long movesNeeded = (diff / d) + 1;
                totalMoves += movesNeeded;
                b[i] += movesNeeded * d;
            }
        }

        return totalMoves;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int d = sc.nextInt();

        int[] b = new int[n];
        for (int i = 0; i < n; i++) {
            b[i] = sc.nextInt();
        }

        long totalMoves = countMinimumMoves(b, n, d);
        System.out.println(totalMoves);

        sc.close();
    }
}
