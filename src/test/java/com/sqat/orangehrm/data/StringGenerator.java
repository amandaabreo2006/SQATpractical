package com.sqat.orangehrm.data;

import java.util.concurrent.ThreadLocalRandom;

/** Builds test strings of an exact length, as required by the BVA test cases. */
public final class StringGenerator {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    /** Repeating block that satisfies every character-class rule of the password policy. */
    private static final String PASSWORD_BLOCK = "Qz7#Lm9$Wx4!Rt2%Vb6&Np3*";

    private StringGenerator() {
    }

    /** Name-like string: capital first letter, then lowercase letters. Length 0 returns "". */
    public static String name(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("T");
        while (sb.length() < length) {
            sb.append(LETTERS.charAt(sb.length() % LETTERS.length()));
        }
        return sb.toString();
    }

    /** Random lowercase username (random so the server's "Already exists" check never interferes). */
    public static String username(int length) {
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(r.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    /** Password of exact length containing upper, lower, digit and special chars (for length >= 4). */
    public static String password(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(PASSWORD_BLOCK.charAt(sb.length() % PASSWORD_BLOCK.length()));
        }
        return sb.toString();
    }

    /** Short unique suffix for data created on the shared public demo. */
    public static String uniqueSuffix() {
        return Long.toString(System.currentTimeMillis(), 36);
    }
}
