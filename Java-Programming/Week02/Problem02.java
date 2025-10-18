// https://codeforces.com/problemset/problem/2/B

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Problem02 {
    public static int countFactors(int n, int factor) {
        int count = 0;

        while (n > 0 && n % factor == 0) {
            count++;
            n /= factor;
        }

        return count;
    }

    public static int[][] calculateDP(int[][] factors, int n) {
        int[][] dp = new int[n][n];
        dp[0][0] = factors[0][0];

        for (int j = 1; j < n; j++) {
            dp[0][j] = dp[0][j - 1] + factors[0][j];
        }

        for (int i = 1; i < n; i++) {
            dp[i][0] = dp[i - 1][0] + factors[i][0];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + factors[i][j];
            }
        }

        return dp;
    }

    public static String reconstructPath(int[][] dp, int n) {
        StringBuilder path = new StringBuilder();
        int r = n - 1;
        int c = n - 1;

        while (r > 0 || c > 0) {
            if (r == 0) {
                path.append('R');
                c--;
                continue;
            }

            if (c == 0) {
                path.append('D');
                r--;
                continue;
            }

            if (dp[r - 1][c] < dp[r][c - 1]) {
                path.append('D');
                r--;
            } else {
                path.append('R');
                c--;
            }
        }

        return path.reverse().toString();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(reader.readLine());
        int[][] factors2 = new int[n][n];
        int[][] factors5 = new int[n][n];

        boolean hasZero = false;
        int zeroRow = -1;
        int zeroCol = -1;

        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(reader.readLine());
            for (int j = 0; j < n; j++) {
                int num = Integer.parseInt(st.nextToken());

                if (num == 0) {
                    factors2[i][j] = 1;
                    factors5[i][j] = 1;
                    hasZero = true;
                    zeroRow = i;
                    zeroCol = j;
                } else {
                    factors2[i][j] = countFactors(num, 2);
                    factors5[i][j] = countFactors(num, 5);
                }
            }
        }

        int[][] dp2 = calculateDP(factors2, n);
        int[][] dp5 = calculateDP(factors5, n);

        int minZeros2 = dp2[n - 1][n - 1];
        int minZeros5 = dp5[n - 1][n - 1];

        if (hasZero && Math.min(minZeros2, minZeros5) > 1) {
            System.out.println(1);
            StringBuilder path = new StringBuilder();

            for (int i = 0; i < zeroRow; i++) {
                path.append('D');
            }

            for (int j = 0; j < n - 1; j++) {
                path.append('R');
            }

            for (int i = 0; i < n - 1 - zeroRow; i++) {
                path.append('D');
            }

            System.out.println(path.toString());
        } else {
            if (minZeros2 < minZeros5) {
                System.out.println(minZeros2);
                System.out.println(reconstructPath(dp2, n));
            } else {
                System.out.println(minZeros5);
                System.out.println(reconstructPath(dp5, n));
            }
        }
    }
}
