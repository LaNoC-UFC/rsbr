package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return factorial(n) / (factorial(n - k) * factorial(k));
    }

    private static int factorial(int n) {
        return n == 0 ? 1 : n * factorial(n - 1);
    }

    private static void checkBinomial(int n, int k) {
        if (n < k) {
            throw new RuntimeException("Number Is Too Large Exception, n < k!");
        } else if (n < 0) {
            throw new RuntimeException("Not Positive Exception!");
        }
    }
}
