// https://codeforces.com/problemset/problem/2/A

import java.util.*;

public class Problem02 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        String[] names = new String[n];
        int[] scores = new int[n];
        Map<String, Integer> totalScores = new HashMap<>();

        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
            scores[i] = sc.nextInt();
            totalScores.put(names[i], totalScores.getOrDefault(names[i], 0) + scores[i]);
        }

        int maxScore = Collections.max(totalScores.values());

        Set<String> candidates = new HashSet<>();
        for (Map.Entry<String, Integer> entry : totalScores.entrySet()) {
            if (entry.getValue() == maxScore) {
                candidates.add(entry.getKey());
            }
        }

        String winner = "";
        Map<String, Integer> currentScores = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String name = names[i];
            int score = scores[i];
            currentScores.put(name, currentScores.getOrDefault(name, 0) + score);

            if (candidates.contains(name) && currentScores.get(name) >= maxScore) {
                winner = name;
                break;
            }
        }

        System.out.println(winner);

        sc.close();
    }
}
