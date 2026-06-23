package org.example.sort;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Random;

/**
 * Hash-based random sort, mirroring GNU {@code sort -R} / {@code --random-sort}.
 *
 * <p>The goal is "a random permutation of the input, except that lines with equal keys sort
 * together." We achieve that without a real shuffle: pick a single random salt for this run,
 * then sort each line by {@code hash(salt + line)}. Because identical lines hash to the same
 * value, they always end up adjacent. The salt makes the ordering unpredictable and different
 * from run to run, while remaining a deterministic function of (salt, data).
 *
 * <p>Ties on the hash are broken by the line's own value. This guarantees equal keys stay
 * grouped (so {@code -u} can drop duplicates) and makes the result reproducible for a fixed salt
 * — which is what the seeded constructors give the tests.
 */
public final class RandomSort implements Sorter {

    private static final int SALT_BYTES = 16;

    private final byte[] salt;

    /** Production constructor: a fresh, unpredictable salt each run. */
    public RandomSort() {
        this(new Random());
    }

    /** Deterministic salt from a seed — used by tests for reproducibility. */
    public RandomSort(long seed) {
        this(new Random(seed));
    }

    public RandomSort(Random random) {
        this.salt = new byte[SALT_BYTES];
        random.nextBytes(this.salt);
    }

    @Override
    public String[] sort(String[] input) {
        // Precompute each line's hash once (not on every comparison) for efficiency.
        record Keyed(String hash, String value) {}

        Keyed[] keyed = new Keyed[input.length];
        for (int i = 0; i < input.length; i++) {
            keyed[i] = new Keyed(hash(input[i]), input[i]);
        }

        Arrays.sort(keyed, Comparator
                .comparing(Keyed::hash)
                .thenComparing(Keyed::value));

        String[] out = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            out[i] = keyed[i].value();
        }
        return out;
    }

    /** Salted SHA-256 of the line, hex-encoded so it compares as a stable, opaque key. */
    private String hash(String line) {
        MessageDigest digest = newDigest();
        digest.update(salt);
        digest.update(line.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest.digest());
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is required by every JVM, so this cannot happen in practice.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
