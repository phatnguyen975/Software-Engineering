// https://codeforces.com/problemset/problem/5/A

import java.util.Scanner;

public class Problem05 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int activeUsers = 0;
        int totalTraffic = 0;

        while (sc.hasNextLine()) {
            String command = sc.nextLine();

            if (command.startsWith("+")) {
                activeUsers++;
            } else if (command.startsWith("-")) {
                activeUsers--;
            } else {
                String message = command.substring(command.indexOf(":") + 1);
                totalTraffic += message.length() * activeUsers;
            }
        }

        System.out.println(totalTraffic);

        sc.close();
    }
}
