package util;

import java.util.*;

public class Combinatorics {

    private Combinatorics() {
    }

    public static <T> List<Set<T>> allCombinationsOf(Set<T> c) {
        List<Set<T>> result = new ArrayList<>();
        List<T> objects = new ArrayList<>(c);
        for (int m = 1; m != 1 << objects.size(); m++) {
            Set<T> aCombination = new HashSet<>();
            for (int i = 0; i != objects.size(); i++) {
                if ((m & (1 << i)) != 0) {
                    aCombination.add(objects.get(i));
                }
            }
            result.add(aCombination);
        }
        return result;
    }

    static long binomialCoefficient(int n, int k) throws RuntimeException {
        checkBinomial(n, k);
        if (n - k > k)
            return integralProduct(n - k + 1, n) / factorial(k);
        return integralProduct(k + 1, n) / factorial(n - k);
    }

    private static long factorial(int n) {
        return (n < 2) ? 1 : integralProduct(2, n);
    }

    private static long integralProduct(int from, int to) {
        long result = 1;
        for (int i = from; i <= to; i++) {
            result *= i;
        }
        return result;
    }

    private static void checkBinomial(int n, int k) {
        if (n < k) {
            throw new RuntimeException("Number Is Too Large Exception, n < k!");
        } else if (n < 0) {
            throw new RuntimeException("Not Positive Exception!");
        }
    }
}
