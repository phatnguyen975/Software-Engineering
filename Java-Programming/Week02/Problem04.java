// https://codeforces.com/problemset/problem/4/B

import java.util.Scanner;

public class Problem04 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int d = sc.nextInt();
        int sumTime = sc.nextInt();

        int[] minTimes = new int[d];
        int[] maxTimes = new int[d];

        int totalMinTime = 0;
        int totalMaxTime = 0;

        for (int i = 0; i < d; i++) {
            minTimes[i] = sc.nextInt();
            maxTimes[i] = sc.nextInt();
            totalMinTime += minTimes[i];
            totalMaxTime += maxTimes[i];
        }

        if (sumTime < totalMinTime || sumTime > totalMaxTime) {
            System.out.println("NO");
        } else {
            System.out.println("YES");

            int remainingTime = sumTime - totalMinTime;
            int[] schedule = new int[d];

            for (int i = 0; i < d; i++) {
                int timeToAdd = Math.min(remainingTime, maxTimes[i] - minTimes[i]);
                schedule[i] = minTimes[i] + timeToAdd;
                remainingTime -= timeToAdd;
            }

            for (int i = 0; i < d; i++) {
                System.out.print(schedule[i] + " ");
            }
        }

        sc.close();
    }
}
