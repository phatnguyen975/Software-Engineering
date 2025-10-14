// https://codeforces.com/problemset/problem/1/A

import java.util.Scanner;

public class Problem01 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int m = sc.nextInt();
        int a = sc.nextInt();

        int x = Math.ceilDiv(n, a);
        int y = Math.ceilDiv(m, a);
        long result = (long) x * y;

        System.out.println(result);

        sc.close();
    }
}
