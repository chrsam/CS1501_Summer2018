/* Chris Mananghaya (cam314)
 * CS 1501: Assignment 1
 * The goal of this assignment is to create a crossword puzzle solver 
 * via a recursive backtracking algorithm that uses pruning. The program accomplishes this by:
 * (1) reading a dictionary of words in from a file to form a MyDictionary object, 
 * (2) reading a crossword board in from a file (specified by the user via argument), and 
 * (3) filling in the board according to specifications set by (2) and print out the result to
 * the terminal. 
 * Three files are provided:
 * (1) DictInterface.java serves as a foundation for the algorithm by declaring the functions
 * add(String s), searchPrefix(StringBuilder s), and seachPrefix(StringBuilder s, int start, int end). 
 * (2) MyDictionary.java does most of the work. It instantiates the functions from DictInterface.java
 * and uses them to conduct a linear search of an ArrayList to determine if a String argument is a 
 * prefix or a word based on the dictionary provided (dict8.txt).
 * (3) DictTest.java tests MyDictionary.java against provided strings and displays the results of each 
 * compared String as output on the terminal. 
 */

import java.io.*;
import java.util.*;
public class Crossword 
{
    // declaring some starting variables
    public GameBoard[][] board; // creates game board 
    public boolean[] saferow;   // is the row empty? (T/F)
    public boolean[] safecol;   // is the column empty? (T/F)
    private int sol, calls;     // declaration of # of solutions and calls

    // Crossword constructor
    public Crossword(int boardLength) 
    {
        // initializing variables 
        board = new GameBoard[boardLength][boardLength];
        saferow = new boolean[boardLength];
        safecol = new boolean[boardLength];

        for(int row = 0; row < boardLength; row++) 
        {
            saferow[row] = true; // all rows are safe
            for(int col = 0; col < boardLength; col++) 
            {
                safecol[col] = true; // all columns are safe
            }
        }

        sol = 0;
        calls = 0;
    }

    // Is the current location safe? We check the row and column.
    // The column does NOT have to be checked since our algorithm proceeds 
    // in a column by column manner. ???????
    public boolean safe(int row, int col) 
    {
        return (saferow[row] && safecol[col]);
    }

    // This recursive method does most of the work to solve the problem. Note
    // that it is called for each column tried in the board, but due to 
    // backtracking, will overall be called many times. Each call is from 
    // the point of view of the current column, col. 
    public void trycol(int col) 
    {
        calls++; // increment # of calls made
        for(int row = 0; row < boardLength; row++) // try all rows if necessary
        {
            // This test is what does the "pruning" of the execution tree --
            // if the location is not safe we do not bother to make a recursive 
            // call from that position, saving overall many thousands of calls.
            if(safe(row, col))
            {
                // If the current position is free from a threat, put a queen
                // there and mark the row and col as unsafe.
                saferow[row] = false;
                safecol[col] = false;

                if(col == boardLength - 1) 
                {
                    sol++;
                } 
                else 
                {
                    trycol(col + 1);
                }

                saferow[row] = true;
                safecol[col] = true; 
            }
        }
        // Once all rows have been tried, the method finishes, and execution
        // backtracks to the previous call. 
    }

    public void run(int boardLength) 
    {
        trycol(boardLength);
        System.out.println("Program Completed: " + sol + " Solutions, " + calls 
            + " Calls, " + (8*calls) + " iterations. ");
    }

    public static void main(String[] args) throws IOException
    {
        String testType = args[0]; // stores name of test file as a String
        String fileType = args[1]; // stores type of file as a String
        Scanner dictScan = new Scanner(new FileInputStream("dict8.txt")); // reads in dictionary file
        Scanner testScan = new Scanner(new FileInputStream(testType)); // reads in test file 

        // 1st command line argument should specify which test file to use 
        // 2nd command line argument should be "DLB" to use a DLB for the DictInterface
		// or any other string to use MyDictionary
        String st;
        String boardSize; 
        String boardChar; 
		StringBuilder sb;
        DictInterface D;

		if (fileType.equals("DLB")) {
            // D = new DLB();
            D = new MyDictionary();
		} else
			D = new MyDictionary();
		
		while (dictScan.hasNext()) // creates Dictonary
		{
			st = dictScan.nextLine();
			D.add(st);
        }
        
        boardSize = testScan.nextLine();
        Crossword game = new Crossword(Integer.parseInt(boardSize));
        StringBuilder[] colStr;
        StringBuilder[] rowStr;
        int[] colStatus;
        int[] rowStatus; 
        run(Integer.parseInt(boardSize));

        for(int i = 0; i < Integer.parseInt(boardSize); i++) 
        {
            boardChar = testScan.nextLine();
            char[] charArray = boardChar.toCharArray();
            for(int k = 0; k < charArray.length; k++)
            {
                if(charArray[k] == '-')
                {
                    rowStatus[k] = 0; 
                } 
                else if(charArray[k] == '+')
                {
                    rowStatus[k] = 1;
                } 
                else 
                {
                    rowStatus[k] = 2;
                }
            }
        }

		String [] tests = {"abc", "abe", "abet", "abx", "ace", "acid", "hives",
						   "iodin", "inval", "zoo", "zool", "zurich"};
		for (int i = 0; i < tests.length; i++)
		{
			sb = new StringBuilder(tests[i]);
			int ans = D.searchPrefix(sb);
			System.out.print(sb + " is ");
			switch (ans)
			{
				case 0: System.out.println("not found");
					break;
				case 1: System.out.println("a prefix");
					break;
				case 2: System.out.println("a word");
					break;
				case 3: System.out.println("a word and prefix");
			}
		}
    }
}