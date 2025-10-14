// https://codeforces.com/problemset/problem/9/A

import java.util.Scanner;

public class Problem09 {
    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int yakkoPoint = sc.nextInt();
        int wakkoPoint = sc.nextInt();

        int maxPoint = Math.max(yakkoPoint, wakkoPoint);
        int numerator = 6 - maxPoint + 1;
        int denominator = 6;

        int commonDivisor = gcd(numerator, denominator);
        numerator /= commonDivisor;
        denominator /= commonDivisor;

        System.out.println(numerator + "/" + denominator);

        sc.close();
    }
}
