// https://codeforces.com/problemset/problem/12/A

import java.util.Scanner;

public class Problem12 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        char[][] grid = new char[3][3];
        for (int i = 0; i < 3; i++) {
            grid[i] = sc.nextLine().toCharArray();
        }

        boolean isSymmetric = true;

        if (grid[0][0] != grid[2][2]) {
            isSymmetric = false;
        }

        if (grid[0][1] != grid[2][1]) {
            isSymmetric = false;
        }

        if (grid[0][2] != grid[2][0]) {
            isSymmetric = false;
        }

        if (grid[1][0] != grid[1][2]) {
            isSymmetric = false;
        }

        if (isSymmetric) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }

        sc.close();
    }
}
