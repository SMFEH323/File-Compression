import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The main program.
 * 
 * @author Sayf Elhawary
 */
public class HuffMain {

	public static void main ( String[] args ) {
		for ( ; true ; ) {
			Scanner scan = new Scanner(System.in);
			try {
				System.out.println("(1) compress file");
				System.out.println("(2) uncompress file");
				System.out.println("(3) compare files");
				System.out.println("(4) quit");
				System.out.print("> ");
				int choice = scan.nextInt();
				scan.nextLine();
				if ( choice == 1 ) {
					System.out.println();
					System.out.print("file to compress: ");
					String infile = scan.nextLine();
					System.out.print("file to save in: ");
					String outfile = scan.nextLine();
					System.out.println();
					if (!Huff.compress(infile,outfile)) {
						System.out.println("Unable to compress file due to not resulting in savings.");
						System.out.println();
					}
				} else if ( choice == 2 ) {
					System.out.println();
					System.out.print("file to uncompress: ");
					String infile = scan.nextLine();
					System.out.print("file to save in: ");
					String outfile = scan.nextLine();
					System.out.println();
					Huff.uncompress(infile,outfile);
				} else if ( choice == 3 ) {
					System.out.println();
					System.out.print("file #1: ");
					String file1 = scan.nextLine();
					System.out.print("file #2: ");
					String file2 = scan.nextLine();
					System.out.println();
					Huff.compare(file1,file2);
				} else if ( choice == 4 ) {
					scan.close();
					break;
				} else {
					System.out.println("Please input a number related to the operations");
					System.out.println();
				}
			} catch ( FileNotFoundException e ) {
				System.out
				    .println("File not found. Please input a correct directory or path.");
				System.out.println();
			} catch ( IOException e ) {
				System.out.println("IO error.");
				System.out.println();
			} catch ( InputMismatchException e ) {
				System.out.println("Please try again.");
				System.out.println();
			}
		}

	}

}
