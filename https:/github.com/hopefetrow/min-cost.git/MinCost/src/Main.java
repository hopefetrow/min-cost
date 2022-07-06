import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

/**
 * Date 06/23/2021
 * 
 * @author Hope Fetrow
 *
 *         Given two strings X and Y, find how many minimum edits(update, delete
 *         or add) is needed to convert string X into string Y and the cost of
 *         the conversion by producing a min cost matrix.
 * 
 *         The min cost matrix is used to determine the decision sequence for
 *         the minimum cost conversion.
 * 
 */
public class Main {
	// costs (unless overwritten by user)
	static int ins = 1;
	static int del = 1;
	static int ch = 2;
	// string X and Y
	static char[] str1;
	static char[] str2;
	// min cost matrix
	static int M[][];
	// for edit sequence
	static ArrayList<String> edits;
	// pointer for reading from file
	static int p;

	/**
	 * Creates a minimum cost matrix for converting string X into string Y. Returns
	 * the minimum cost of edits.
	 */
	public static int minCostMatrix() {
		M = new int[str1.length + 1][str2.length + 1];
		int n = str1.length;
		int m = str2.length;

		for (int i = 1; i <= n; i++) // initialize column 1 to cost of deleting
			M[i][0] = M[i - 1][0] + del;

		for (int j = 1; j <= m; j++) // initialize row 1 to cost of inserting
			M[0][j] = M[0][j - 1] + ins;

		for (int i = 1; i <= n; i++) { // visit every row
			for (int j = 1; j <= m; j++) { // and every column
				// CASE 1: both strings empty
				if (i == 0 && j == 0)
					// case 1 solution: initialize to 0
					M[i][j] = 0;
				// CASE 2: string X has an element but Y is empty
				if (j == 0 && i >= 0)
					// case 2 solution: delete from X
					M[i][0] = M[i - 1][0] + del;
				// CASE 3: X is empty but Y has element
				if (j > 0 && i == 0)
					// case 3 solution: insert value
					M[0][j] = M[0][j - 1] + ins;
				// CASE 4: neither subset is empty
				if (i != 0 && j != 0) {
					// subcase 1: characters are the same
					if (str1[i - 1] == str2[j - 1])
						// subcase 1 solution: store value of diagonal at current location
						M[i][j] = M[i - 1][j - 1];
					// subcase 2: characters are not the same
					else
						// subcase 2 solution: find min of 3 edit options for current location
						M[i][j] = Math.min(M[i - 1][j - 1] + ch, Math.min(M[i - 1][j] + del, M[i][j - 1] + ins));
				}
			}
		}

		return M[str1.length][str2.length];

	}

	/**
	 * Prints the actual edit sequence by back tracing from bottom right corner of
	 * matrix (the min cost value).
	 */
	public static void minCostSequence() {
		int i = M.length - 1;
		int j = M[0].length - 1;
		edits = new ArrayList<String>();

		while (i >= 0 && j >= 0) { // not at far left or top
			// CASE 1: can't go left or up any further
			if (i == 0 || j == 0) {
				// subcase 1: at top of matrix but not far left
				if (i == 0 && j != 0)
					// solution: sequence of inserts
					while (j > 0) {
						edits.add((" - insert " + str1[j - 1]));
						j--;
					}
				// subcase 2: at far left but not top
				if (j == 0 && i != 0)
					// solution: sequence of deletes
					while (i > 0) {
						edits.add((" - delete " + str1[i - 1]));
						i--;
					}
				break;
			} else {
				if (str1[i - 1] == str2[j - 1]) { // values are the same, copied from diagonal
					i = i - 1;
					j = j - 1;
				} else if (M[i][j] == M[i - 1][j - 1] + ch) { // element in X changed for Y
					edits.add((" - change " + str1[i - 1] + " to " + str2[j - 1]).toString());
					j = j - 1;
					i = i - 1;
				} else if (M[i][j] == M[i - 1][j] + del) { // deleted element in X
					edits.add((" - delete " + str1[i - 1]).toString());
					i = i - 1;
				} else if (M[i][j] == M[i][j - 1] + ins) { // inserted an element in Y
					edits.add((" - insert " + str2[j - 1]).toString());
					j = j - 1;
				} else {
					throw new IllegalArgumentException("Error from input data");
				}
			}
		}
	}

	/**
	 * Prints entire cost matrix
	 */
	public static void printMatrix() {
		System.out.println("\n\nMin cost matrix: \n");
		{
			System.out.print("    ");
			for (char c : str2)
				System.out.printf("%3s", c);
			System.out.println();
			for (int i = 0; i < M.length; i++) {
				if (i != 0)
					System.out.print(str1[i - 1]);
				else
					System.out.print(" ");
				for (int j = 0; j < M[i].length; j++) {
					System.out.format("%3d", M[i][j]);
				}
				System.out.println();
			}
		}
	}

	/**
	 * Writes cost matrix to a .txt file
	 */
	public static void writeMatrix(FileWriter fr) {

		try {
			fr.write("\nMin cost matrix: \n\n");
			// Loop through all rows
			for (int[] row : M) {

				// converting each row as string
				// and then writing
				fr.write(Arrays.toString(row) + "\n");
				fr.write("");
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Prints the edit sequence to transform X into Y
	 */
	public static void printSequence() {
		System.out.println("\nSequence of edits to convert X into Y: ");
		int index = edits.size() - 1;
		for (String s : edits) {
			System.out.println(edits.get(index));
			index--;
		}
	}

	/**
	 * Writes cost matrix to a .txt file
	 */
	public static void writeSequence(FileWriter fr) {
		try {

			// print the sequence
			int index = edits.size() - 1;
			fr.write("Sequence of edits to convert X into Y: \n\n");
			for (String s : edits) {
				fr.write(edits.get(index).toString() + "\n");
				index--;
			}
			fr.close();
		} catch (IOException e) {
			System.out.println("Error writing to file. ");
			e.printStackTrace();
		}

	}

	/**
	 * Gets user input and calls the method for min cost matrix and decision
	 * sequence and prints min cost of edits to convert string X to string Y. If
	 * both strings are under length 100, it prints matrix and decision sequence. If
	 * either string's length
	 * 
	 */
	public static void main(String args[]) {
		// one line from file
		String st;
		// for reading from file
		File read = null;
		// offer options for file to read from
		FileWriter swriter = null;
		FileWriter mwriter = null;
		int minCost = 0, n = 1;
		String x = "", y = "";

		Scanner input = new Scanner(System.in);

		try {
			/**
			 * Get user input
			 */
			while (n > 0) {
				System.out.print("\n\nEnter option: " + "\n  [1 - to enter two strings and cost amounts]"
						+ "\n  [2 - to choose from existing files with costs = I:1, D:1, C:2]"
						+ "\n  [0 - to exit] \n");
				while (!input.hasNextInt()) {
					System.out.println("That's not an option!");
					input.next();
				}
				n = input.nextInt();

				switch (n) {

				/**
				 * User wants to enter costs and strings
				 */
				case 1:
					try {
						System.out.print("\n\n[Enter cost for insert (an integer < 10)]: ");
						ins = input.nextInt();
						System.out.print("\n[Enter cost for delete (an integer < 10)]: ");
						del = input.nextInt();
						System.out.println("\n[Enter cost for change (an integer < 10)]: ");
						ch = input.nextInt();
					} catch (Exception e) {
						System.out.println("Invalid input");
						break;
					}
					while (x.compareTo("0") != 0 && y.compareTo("0") != 0) { // user input is an int greater than 0
						System.out.println("\n[Enter string X (starting string)]: ");
						x = input.next();
						str1 = x.toCharArray();
						System.out.println("\n[Enter string Y (end string)]: ");
						y = input.next();
						str2 = y.toCharArray();

						// call method to get min cost edit
						minCost = minCostMatrix();
						// call method to get edit sequence
						minCostSequence();

						System.out.println("\n\n  X = " + x);
						System.out.println("  Y = " + y);
						System.out.println("\n  length of X: n=" + str1.length);
						System.out.println("  length of Y: m=" + str2.length);
						System.out.println("\n  Costs: \n   I: " + ins + "\n   D: " + del + " \n   C: " + ch);
						System.out.println("\nMin cost to convert string X into string Y: " + minCost);
						if (str1.length < 100 && str2.length < 100) {
							printMatrix();
							printSequence();
						} else {
							swriter = new FileWriter("output-sequence.txt");
							mwriter = new FileWriter("output-matrix.txt");
							writeMatrix(mwriter);
							writeSequence(swriter);
						}
					}
					break;
				/**
				 * User wants to read from file
				 */
				case 2:
					while (n >= 0) {
						System.out.print("\n\nEnter option: \n" + "  [1 - random dna string of length 8]\n"
								+ "  [2 - random dna string of length 10]\n"
								+ "  [3 - random dna string of length 600]\n"
								+ "  [4 - random dna string of length 1000]\n"
								+ "  [5 - homologous protein string of length 60]\n" + "  [0 - to exit]\n");
						n = input.nextInt();
						swriter = new FileWriter("output-sequence.txt");
						mwriter = new FileWriter("output-matrix.txt");

						switch (n) {
						case 1: // read file with random DNA string of length 8
							read = new File("rand-DNA-8.txt");
							p = 1;
							break;
						case 2: // read file with random DNA string of length 10
							read = new File("rand-DNA-10.txt");
							p = 1;
							break;
						case 3: // read file with random DNA string of length 600
							read = new File("rand-DNA-600.txt");
							p = 1;
							break;
						case 4: // read file with random DNA string of length 1000
							read = new File("rand-DNA-1000.txt");
							p = 1;
							break;
						case 5: // read file with random DNA string of length 1000
							read = new File("homologous-protein.txt");
							p = 0;
							break;
						}
						// read from file
						BufferedReader br = new BufferedReader(new FileReader(read));
						while ((st = br.readLine()) != null) {
							// for determining if it is string X or Y
							switch (p) {
							case 0:
								p++;
								continue;
							case 1:
								str1 = st.toCharArray();
								System.out.println("\n\nX = " + st);
								p++;
								continue;
							case 2:
								str2 = st.toCharArray();
								System.out.println("Y = " + st);
								p = 1;
								// call method to get min cost edit
								minCost = minCostMatrix();
								// call method to get edit sequence
								minCostSequence();
								// print cost values
								System.out.println("\nCosts: \n  I: " + ins + "\n  D: " + del + " \n  C: " + ch);
								// only print matrix and sequence for sizes less than 25
								if (str1.length < 100 && str2.length < 100) {
									printMatrix();
									printSequence();
									writeMatrix(mwriter);
									writeSequence(swriter);
								} else {
									// write to file for sizes > 25
									writeMatrix(mwriter);
									writeSequence(swriter);
								}
								System.out.printf("\n%s%d\n", "Min cost to convert string X into Y: \n  ", minCost);
								continue;
							}
						}
						br.close();
					}
				default:
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured.");

		}
	}
}
