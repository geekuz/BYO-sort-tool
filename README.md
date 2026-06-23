# Build Your Own `sort` Tool

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)

A Java implementation of the Unix `sort` command, built for the
[Coding Challenges "Build Your Own sort tool"](https://codingchallenges.fyi/challenges/challenge-sort)
challenge. It reads lines from a file (or standard input), sorts them lexicographically, and
can drop duplicates — but the real point of the exercise is that **the sorting is done by hand**,
with four classic algorithms plus a hash-based random sort that you can switch between.

## Quick start

```bash
mvn -q test                 # compile + run the 40-test suite
mvn -q compile              # just compile to target/classes

# run it
java -cp target/classes org.example.Main words.txt
cat words.txt | java -cp target/classes org.example.Main
mvn -q exec:java -Dexec.args="-u words.txt"
```

## Usage

```
ccsort [OPTIONS] [FILE...]

  -u, --unique            drop duplicate lines (keep one of each)
  -R, --random-sort       random, hash-based permutation; equal keys stay together
      --algorithm NAME     merge | quick | heap | radix   (default: merge)
      --algorithm=NAME     same, equals form
  -                       read standard input (may be mixed with files)
```

No files ⇒ read standard input. Unknown flags and bad algorithm names exit non-zero with a
message on stderr.

## Getting the test data

The challenge uses a Project Gutenberg book, reduced to one word per line:

```bash
curl -fsSL https://www.gutenberg.org/cache/epub/132/pg132.txt -o test.txt
tr -s '[[:punct:][:space:]]' '\n' < test.txt | sed '/^[0-9]/d' > words.txt
```

That yields ~57,600 words. The canonical smoke test:

```bash
java -cp target/classes org.example.Main words.txt | uniq | head -n5
# A
# ACTUAL
# AGREE
# AGREEMENT
# AND
```

(`test.txt` and `words.txt` are git-ignored; a tiny `src/test/resources/words-sample.txt`
is committed for the integration test.)

## How it works

The flow is deliberately small and linear (`SortTool`):

```
read lines  ->  pick a Sorter  ->  sort  ->  (optional) drop adjacent duplicates  ->  write
```

- `cli/ArgumentParser` validates the command line into an immutable `cli/SortOptions`.
- `sort/Sorter` is the strategy interface (`String[] sort(String[] input)`); every
  implementation returns a **new** array and never mutates its input.
- `-u` works by dropping *adjacent* equal lines. This is correct because every sort here —
  including the random one — places equal keys next to each other.

### The algorithms

| Algorithm | Idea | Time | Space | Stable? |
|-----------|------|------|-------|---------|
| **Merge** (default) | Split in half, sort halves, merge | O(n log n) always | O(n) | Yes |
| **Quick** | Partition around a median-of-three pivot, recurse | O(n log n) avg, O(n²) worst | O(log n) | No |
| **Heap** | Build a max-heap, repeatedly extract the max | O(n log n) always | O(1) | No |
| **Radix** (MSD) | Bucket by character position, recurse per bucket | ~O(n·k) | O(n) | Yes |
| **Random** | Sort by `hash(salt + line)`; ties by value | O(n log n) | O(n) | n/a |

Notes:

- **Merge sort is the default** because it is stable and has no bad case — predictable behaviour
  that pairs naturally with `-u`.
- **Quick sort** uses a median-of-three pivot and an insertion-sort cutoff so the near-sorted /
  reverse-sorted shapes common in word lists don't trigger the O(n²) worst case. It also recurses
  into the smaller partition and loops on the larger one to bound stack depth to O(log n).
- **Radix sort** never compares whole strings; it distributes by the character at position `d`
  and recurses on `d+1`. A string that *ends* at position `d` sorts before any string that still
  has a character there, which is exactly why `"A" < "AB"`. Buckets are kept in a sparse
  `TreeMap` so memory tracks the data rather than a fixed 65,536-slot alphabet.
- **Random sort** mirrors GNU `sort -R`: one random salt per run, then order by the salted
  SHA-256 of each line. Identical lines hash identically, so duplicates group together; a fixed
  seed (used in tests) makes the result reproducible.

All five orderings agree with the system `sort` (under `LC_ALL=C`, i.e. byte/code-unit order) on
the full dataset — the comparison sorts produce identical output, and the random sort dedupes to
the same unique-line count.

### Indicative performance

Best-of-two wall-clock on the ~57,600-line `words.txt` (includes JVM startup, so treat as
relative, not absolute):

| Algorithm | Time |
|-----------|------|
| merge  | 0.20s |
| heap   | 0.20s |
| radix  | 0.26s |
| quick  | 0.35s |
| random | 0.46s |

Random sort is slowest because it computes a SHA-256 per line; radix is competitive with the
comparison sorts thanks to short distinguishing prefixes; quick carries some constant-factor
overhead from partitioning here.

## Project layout

```
src/main/java/org/example/
├── Main.java                 # CLI entry point (arg parsing, exit codes)
├── SortTool.java             # read -> sort -> dedup -> write
├── cli/
│   ├── Algorithm.java        # enum + flag parsing
│   ├── SortOptions.java      # immutable parsed options (record)
│   └── ArgumentParser.java   # String[] args -> SortOptions
└── sort/
    ├── Sorter.java           # strategy interface
    ├── MergeSort.java
    ├── QuickSort.java
    ├── HeapSort.java
    ├── RadixSort.java
    └── RandomSort.java
src/test/java/org/example/    # JUnit 5 + AssertJ (40 tests)
src/test/resources/words-sample.txt
```

## Testing

`mvn test` runs 40 tests. The key one is `SorterContractTest`, which is **parameterized over
every comparison sorter** and checks each against `Arrays.sort` as the oracle (large random
data plus empty/single/duplicate/sorted/reverse edge cases). `RandomSortTest` verifies the
permutation, grouping, and reproducibility properties; `SortToolTest` covers `-u`, file/stdin
reading, the missing-file error, and the challenge's `A, ACTUAL, AGREE, AGREEMENT, AND` output.

## Notes / scope

Implements exactly the challenge's flags (`-u`, `-R`/`--random-sort`, algorithm selection).
Ordering follows Java `String.compareTo` (UTF-16 code units), matching the challenge's expected
ASCII output. Other GNU flags (`-r`, `-n`, `-k`, locale collation) are out of scope.
