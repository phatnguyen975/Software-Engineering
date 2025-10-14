// https://codeforces.com/problemset/problem/7/A

import java.util.Scanner;

public class Problem07 {
    public static int countBlackStrokes(char[][] board, String direction) {
        int blackStrokes = 0;

        for (int i = 0; i < 8; i++) {
            boolean hasWhiteSquare = false;

            for (int j = 0; j < 8; j++) {
                char squareColor = direction == "horizontal" ? board[i][j] : board[j][i];
                if (squareColor == 'W') {
                    hasWhiteSquare = true;
                    break;
                }
            }

            if (!hasWhiteSquare) {
                blackStrokes++;
            }
        }

        return blackStrokes;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        char[][] board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            board[i] = sc.nextLine().toCharArray();
        }

        int horizontalStrokes = countBlackStrokes(board, "horizontal");
        if (horizontalStrokes == 8) {
            System.out.println(horizontalStrokes);
        } else {
            int verticalStrokes = countBlackStrokes(board, "vertical");
            System.out.println(horizontalStrokes + verticalStrokes);
        }

        sc.close();
    }
}
