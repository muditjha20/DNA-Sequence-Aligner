import java.io.*;
import java.util.Scanner;

public class Main {

    public static int getLetterIndex(char letter) {
        if (letter == 'A')
            return 0;
        if (letter == 'C')
            return 1;
        if (letter == 'G')
            return 2;
        if (letter == 'T')
            return 3;
        return 0;
    }

    public static String readSequenceByNumber(String fileName, int targetNumber) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        String seq = "";
        String header = "sequence:" + targetNumber + ":";

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith(header)) {
                for (int i = header.length(); i < line.length(); i++) {
                    char ch = line.charAt(i);
                    if (ch == 'X') {
                        reader.close();
                        return seq;
                    }
                    if (Character.isLetter(ch)) seq += ch;
                }

                // read the next lines
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    for (int i = 0; i < line.length(); i++) {
                        char ch = line.charAt(i);
                        if (ch == 'X') {
                            reader.close();
                            return seq;
                        }
                        if (Character.isLetter(ch)) seq += ch;
                    }
                }
            }
        }
        reader.close();
        return seq;
    }

    public static void main(String[] args) throws Exception {

        // Read comparison.conf
        BufferedReader config = new BufferedReader(new FileReader("comparison.conf"));
        String line;
        String sequenceFile = "";
        String weightFile = "";
        String penaltyFile = "";
        String outputFile = "";

        int seq1Number = 1, seq2Number = 2;
        boolean writeToFile = false;

        while ((line = config.readLine()) != null) {
            line = line.trim();
            if (line.equals("") || line.startsWith("#"))
                continue;

            if (line.startsWith("sequenceInputFile"))
                sequenceFile = line.split("=")[1].trim();

            if (line.startsWith("weightMatrixFile"))
                weightFile = line.split("=")[1].trim();

            if (line.startsWith("gapPenaltyFile"))
                penaltyFile = line.split("=")[1].trim();

            if (line.startsWith("sequenceA"))
                seq1Number = Integer.parseInt(line.split("=")[1].trim());

            if (line.startsWith("sequenceB"))
                seq2Number = Integer.parseInt(line.split("=")[1].trim());

            if (line.startsWith("outputFile"))
                outputFile = line.split("=")[1].trim();

            if (line.startsWith("writeToFile"))
                writeToFile = line.split("=")[1].trim().equalsIgnoreCase("true");
        }
        config.close();

        // Read correct sequences using numbers
        String seq1 = readSequenceByNumber(sequenceFile, seq1Number);
        String seq2 = readSequenceByNumber(sequenceFile, seq2Number);
        if (seq1.equals("") || seq2.equals("")) {
            System.out.println("Could not find both sequences.");
            return;
        }

        // Read weight matrix
        double[][] weight = new double[4][4];
        BufferedReader weightReader = new BufferedReader(new FileReader(weightFile));
        int row = 0;
        while (row < 4) {
            line = weightReader.readLine();
            if (line == null)
                break;
            line = line.trim();
            if (line.equals(""))
                continue;
            String[] nums = line.split("\\s+"); // get all numbers
            for (int col = 0; col < 4; col++) {
                weight[row][col] = Double.parseDouble(nums[col]); // populate the matrix
            }
            row++;
        }
        weightReader.close();

        // Read gap penalties
        double[] gap = new double[4];
        Scanner scanner = new Scanner(new File(penaltyFile));
        for (int i = 0; i < 4; i++) {
            gap[i] = scanner.nextDouble();
        }
        scanner.close();

        // Set up scoring table
        int rows = seq1.length() + 1;
        int cols = seq2.length() + 1;
        double[][] score = new double[rows][cols];
        char[][] direction = new char[rows][cols];

        for (int j = 1; j < cols; j++) {
            char letter = seq2.charAt(j - 1);
            int index = getLetterIndex(letter);
            score[0][j] = score[0][j - 1] + gap[index];
            direction[0][j] = 'L';
        }

        for (int i = 1; i < rows; i++) {
            char letter = seq1.charAt(i - 1);
            int index = getLetterIndex(letter);
            score[i][0] = score[i - 1][0] + gap[index];
            direction[i][0] = 'U';
        }

        // Fill table
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                char a = seq1.charAt(i - 1);
                char b = seq2.charAt(j - 1);
                int idxA = getLetterIndex(a);
                int idxB = getLetterIndex(b);

                double match = score[i - 1][j - 1] + weight[idxA][idxB];
                double gapA = score[i - 1][j] + gap[idxA];   // gap in seq2
                double gapB = score[i][j - 1] + gap[idxB];   // gap in seq1

                if (match > gapA && match > gapB) {
                    score[i][j] = match;
                    direction[i][j] = 'D';
                } else if (gapB >= gapA) {
                    score[i][j] = gapB;
                    direction[i][j] = 'L';
                } else {
                    score[i][j] = gapA;
                    direction[i][j] = 'U';
                }
            }
        }

        // Backtrack
        String aligned1 = "", aligned2 = "";
        int i = rows - 1, j = cols - 1;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && direction[i][j] == 'D') { // match/mismatch
                aligned1 = seq1.charAt(i - 1) + aligned1;
                aligned2 = seq2.charAt(j - 1) + aligned2;
                i--;
                j--;
            } else if (i > 0 && direction[i][j] == 'U') { // gap in seq2
                aligned1 = seq1.charAt(i - 1) + aligned1;
                aligned2 = "-" + aligned2;
                i--;
            } else { // gap in seq1
                aligned1 = "-" + aligned1;
                aligned2 = seq2.charAt(j - 1) + aligned2;
                j--;
            }
        }

        // Output
        PrintStream out;
        if (writeToFile) {
            out = new PrintStream(outputFile);
        } else {
            out = System.out;
        }
        out.println("Score: " + score[rows - 1][cols - 1]);
        out.println("First sequence: " + aligned1);
        out.println("Second sequence: " + aligned2);
        if (out != System.out)
            out.close();
    }
}