package com.pccw.nowplayer.utils;

import java.math.BigInteger;
import java.util.Random;

public class MathUtil {
    private static Random random = new Random();


    public static int randomIndexWithWeights(int[] weights) {
        if (weights == null || weights.length == 0) return -1;

        // get sum of weights
        int n = weights.length;
        int total = 0;
        for (int i = 0; i < n; i++) {
            total += weights[i];
        }

        // random
        int r = ((int) (Math.random() * total)) + 1;

        // pick
        int x = 0;
        for (int i = 0; i < n; i++) {
            x += weights[i];
            if (x >= r) return i;
        }
        return 0;
    }


    public static int randomInt() {
        return random.nextInt();
    }


    public static int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }


    public static String randomKey() {
        String key = new BigInteger(64, random).toString(Character.MAX_RADIX);
        return key;
    }


    public static long randomLong(long min, long max) {
        long realMin = Math.min(min, max);
        long realMax = Math.max(min, max);
        long range = realMax - realMin + 1;
        long rnd = random.nextLong();
        long ret = Math.abs(rnd % range) + min;
        return ret;
    }
}
