// https://codeforces.com/problemset/problem/1/B

import java.util.*;

public class Problem01 {
    public static boolean isRXCY(String s) {
        return s.matches("^R(\\d+)C(\\d+)$");
    }

    public static String toExcelFormat(String s) {
        int cIndex = s.indexOf('C');
        int row = Integer.parseInt(s.substring(1, cIndex));
        int col = Integer.parseInt(s.substring(cIndex + 1));

        StringBuilder colLetters = new StringBuilder();

        while (col-- > 0) {
            colLetters.append((char) ('A' + (col % 26)));
            col /= 26;
        }

        colLetters.reverse();
        return colLetters.toString() + row;
    }

    public static String toRXCYFormat(String s) {
        int i = 0;
        while (i < s.length() && Character.isLetter(s.charAt(i))) {
            i++;
        }

        String colLetters = s.substring(0, i);
        int row = Integer.parseInt(s.substring(i));

        int col = 0;
        for (int j = 0; j < colLetters.length(); j++) {
            col = col * 26 + (colLetters.charAt(j) - 'A' + 1);
        }

        return "R" + row + "C" + col;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < n; i++) {
            String s = sc.nextLine();

            if (isRXCY(s)) {
                System.out.println(toExcelFormat(s));
            } else {
                System.out.println(toRXCYFormat(s));
            }
        }

        sc.close();
    }
}
