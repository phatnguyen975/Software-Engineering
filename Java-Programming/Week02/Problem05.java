// https://codeforces.com/problemset/problem/5/B

import java.util.*;

public class Problem05 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        List<String> lines = new ArrayList<>();
        int maxLength = 0;

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            lines.add(line);
            maxLength = Math.max(maxLength, line.length());
        }

        String border = "*".repeat(maxLength + 2);
        System.out.println(border);

        int oddPaddingTurn = 0;

        for (String line : lines) {
            int totalPadding = maxLength - line.length();
            int leftPadding, rightPadding;

            if (totalPadding % 2 == 0) {
                leftPadding = totalPadding / 2;
                rightPadding = leftPadding;
            } else {
                leftPadding = oddPaddingTurn % 2 == 0 ? totalPadding / 2 : totalPadding / 2 + 1;
                rightPadding = totalPadding - leftPadding;
                oddPaddingTurn++;
            }

            String leftSpaces = " ".repeat(leftPadding);
            String rightSpaces = " ".repeat(rightPadding);
            System.out.printf("*%s%s%s*\n", leftSpaces, line, rightSpaces);
        }

        System.out.println(border);

        sc.close();
    }
}
