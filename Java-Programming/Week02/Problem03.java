// https://codeforces.com/problemset/problem/3/B

import java.util.*;

public class Problem03 {
    static class Vehicle {
        int type;
        int capacity;
        int index;

        Vehicle(int type, int capacity, int index) {
            this.type = type;
            this.capacity = capacity;
            this.index = index;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int v = sc.nextInt();

        List<Vehicle> kayaks = new ArrayList<>();
        List<Vehicle> catamarans = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            int t = sc.nextInt();
            int p = sc.nextInt();
            if (t == 1) {
                kayaks.add(new Vehicle(t, p, i));
            } else {
                catamarans.add(new Vehicle(t, p, i));
            }
        }

        kayaks.sort((a, b) -> b.capacity - a.capacity);
        catamarans.sort((a, b) -> b.capacity - a.capacity);

        long[] kayakPrefixSum = new long[kayaks.size() + 1];
        for (int i = 0; i < kayaks.size(); i++) {
            kayakPrefixSum[i + 1] = kayakPrefixSum[i] + kayaks.get(i).capacity;
        }

        long[] catamaranPrefixSum = new long[catamarans.size() + 1];
        for (int i = 0; i < catamarans.size(); i++) {
            catamaranPrefixSum[i + 1] = catamaranPrefixSum[i] + catamarans.get(i).capacity;
        }

        long maxCapacity = 0;
        int bestKayakCount = 0;
        int bestCatamaranCount = 0;

        for (int i = 0; i <= catamarans.size(); i++) {
            int currentVolume = i * 2;

            if (currentVolume > v) {
                break;
            }

            int remainingVolume = v - currentVolume;
            int kayakCount = Math.min(remainingVolume, kayaks.size());
            long currentCapacity = catamaranPrefixSum[i] + kayakPrefixSum[kayakCount];

            if (currentCapacity > maxCapacity) {
                maxCapacity = currentCapacity;
                bestCatamaranCount = i;
                bestKayakCount = kayakCount;
            }
        }

        System.out.println(maxCapacity);

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < bestKayakCount; i++) {
            result.add(kayaks.get(i).index);
        }

        for (int i = 0; i < bestCatamaranCount; i++) {
            result.add(catamarans.get(i).index);
        }

        for (int i = 0; i < result.size(); i++) {
            System.out.print(result.get(i));
            if (i < result.size() - 1) {
                System.out.print(" ");
            }
        }

        sc.close();
    }
}
