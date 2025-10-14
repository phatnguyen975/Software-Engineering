// https://codeforces.com/problemset/problem/8/A

import java.util.Scanner;

public class Problem08 {
    public static boolean isPossibleDirection(String flagsColors, String firstMemory, String secondMemory) {
        int firstMemoryIndex = flagsColors.indexOf(firstMemory);
        int secondMemoryIndex = flagsColors.lastIndexOf(secondMemory);

        if (firstMemoryIndex == -1 || secondMemoryIndex == -1) {
            return false;
        }

        return firstMemoryIndex + firstMemory.length() <= secondMemoryIndex;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String flagsColors = sc.nextLine();
        String firstMemory = sc.nextLine();
        String secondMemory = sc.nextLine();

        boolean forward = isPossibleDirection(flagsColors, firstMemory, secondMemory);
        String reversedFlagsColors = new StringBuilder(flagsColors).reverse().toString();
        boolean backward = isPossibleDirection(reversedFlagsColors, firstMemory, secondMemory);

        if (forward && backward) {
            System.out.println("both");
        } else if (forward) {
            System.out.println("forward");
        } else if (backward) {
            System.out.println("backward");
        } else {
            System.out.println("fantasy");
        }

        sc.close();
    }
}
