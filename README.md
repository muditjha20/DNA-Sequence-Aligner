# DNA Sequence Aligner

A Java program that performs **global alignment** of two DNA sequences, with support for custom match/mismatch scores and per-nucleotide gap penalties. Input is fully configurable through external files.

---

## 🧠 Algorithm Used

This program implements the **Needleman-Wunsch algorithm**, a dynamic programming approach for **global alignment** of biological sequences. It ensures the optimal alignment across the **entire length** of both DNA sequences, introducing gaps where necessary.

---

## 🚀 Features

- Implements **Needleman-Wunsch** for global sequence alignment
- Supports any two user-selected sequences
- Custom 4×4 scoring matrix for A, C, G, T
- Independent gap penalties per nucleotide
- Outputs alignment to terminal or file
- Handles multiline sequences with `X` terminators

---

## ⚙️ Configuration File: `comparison.conf`

This file controls all input/output behavior:

```
sequenceInputFile = sequence.input
sequenceA         = 4                     # 1-based index of first sequence
sequenceB         = 5                     # 1-based index of second sequence
weightMatrixFile  = matrix.input
gapPenaltyFile    = penalty.input
writeToFile       = true                  # true = output to file
outputFile        = sequenceComparison.out
```

---

## 🧬 Input File Formats

### `sequence.input`
```
sequence:4:ACCGTAC
AGGT
X
sequence:5:ACGTA
CG
X
```
- Sequences are prefixed by `sequence:<number>:`
- Lines following the header are joined into one sequence
- The sequence ends at `X` on its own line

---

### `matrix.input`
4x4 matrix of scoring values (space-separated):

```
1    -0.1 -0.3 -0.2
-0.1 1    -0.2 -0.4
-0.3 -0.2 1    -0.1
-0.2 -0.4 -0.1 1
```

### `penalty.input`
Single line of gap penalties:

```
-0.55 -0.55 -0.55 -0.55
```

---

## ▶️ How to Run

> Requires Java 8+

### Compile:
```bash
javac Main.java
```

### Run:
```bash
java Main
```

If `writeToFile = true`, the result will be saved to `outputFile`. Otherwise, output prints to the terminal.

---

## 📤 Sample Output

```
Score: 2.35
First sequence: ACCGT-AC
Second sequence: A-CGTAG-
```

---

## 📄 License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## 👤 Author

Mudit Mayank Jha
