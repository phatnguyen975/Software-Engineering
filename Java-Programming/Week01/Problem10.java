// https://codeforces.com/problemset/problem/10/A

import java.util.Scanner;

public class Problem10 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int P1 = sc.nextInt();
        int P2 = sc.nextInt();
        int P3 = sc.nextInt();
        int T1 = sc.nextInt();
        int T2 = sc.nextInt();

        int[] l = new int[n];
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            l[i] = sc.nextInt();
            r[i] = sc.nextInt();
        }

        int totalPower = 0;

        for (int i = 0; i < n; i++) {
            totalPower += (r[i] - l[i]) * P1;

            if (i < n - 1) {
                int idleTime = l[i + 1] - r[i];

                if (idleTime <= T1) {
                    totalPower += idleTime * P1;
                } else if (idleTime <= T1 + T2) {
                    totalPower += T1 * P1 + (idleTime - T1) * P2;
                } else {
                    totalPower += T1 * P1 + T2 * P2 + (idleTime - T1 - T2) * P3;
                }
            }
        }

        System.out.println(totalPower);

        sc.close();
    }
}
