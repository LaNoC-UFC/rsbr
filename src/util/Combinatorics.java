package util;

import java.math.BigInteger;
import java.util.*;

public class Combinatorics {

    private Combinatorics() {
    }

    public static <T> List<Set<T>> allCombinationsOf(Set<T> elements) {
        List<Set<T>> result = new ArrayList<>();
        for (BigInteger combinationIndex = BigInteger.ONE; combinationIndex.compareTo(numberOfCombinations(elements.size())) < 0 ; combinationIndex = combinationIndex.add(BigInteger.ONE)) {
            result.add(combinationOf(elements, combinationIndex));
        }
        return result;
    }

    static BigInteger binomialCoefficient(int n, int k) {
        checkBinomial(n, k);
        k = Math.min(k, n - k);
        long initialN = n - k + 1;
        return binomialReverseProduct(initialN, k + 1);
    }

    static <T> Set<T> combinationOf(Set<T> elements, BigInteger combinationIndex) {
        List<T> objects = new ArrayList<>(elements);
        Set<T> result = new HashSet<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(BigInteger.valueOf(objects.size())) < 0; i = i.add(BigInteger.ONE)) {
            if (pertainsToCombination(i, combinationIndex)) {
                result.add(objects.get(i.intValue()));
            }
        }
        return result;
    }

    private static BigInteger numberOfCombinations(int numberOfElements) {
        return BigInteger.valueOf(2).pow(numberOfElements);
    }

    private static boolean pertainsToCombination(BigInteger subjectIndex, BigInteger combinationIndex) {
        BigInteger two = BigInteger.valueOf(2);
        return combinationIndex.divide(two.pow(subjectIndex.intValue())).remainder(two).equals(BigInteger.ONE);
    }

    private static BigInteger binomialReverseProduct(long n, long maxK) {
        BigInteger result = BigInteger.ONE;
        for (long k = 1; k < maxK; k++) {
            result = result.multiply(BigInteger.valueOf(n)).divide(BigInteger.valueOf(k));
            n++;
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
