import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Does the actual file compression/uncompression.
 * 
 * @author Sayf Elhawary
 */
public class Huff {

	/**
	 * Writes a compressed version of the file infile. to the file outfile.
	 * 
	 * @param infile
	 *          The desired file that will be compressed.
	 * @param outfile
	 *          The desired file that will be written as the compressed version of
	 *          the file infile.
	 * @throws IOException
	 */
	public static boolean compress ( String infile, String outfile )
	    throws IOException {
		boolean compressionDone = false;
		InputStreamReader in = new FileReader(infile);
		BitOutputStream bitOut = new BitOutputStream(new FileOutputStream(outfile));
		BitInputStream bitIn = new BitInputStream(new FileInputStream(infile));
		Map<Character,Integer> map = new HashMap<Character,Integer>();
		Map<Character,BitString> encodings;
		if ( !bitIn.readBits(HuffConstants.MAGIC_NUMBER.length())
		    .equals(HuffConstants.MAGIC_NUMBER) ) {
			bitIn.close();
			// Stores the characters and the number of times they were repeated in to
			// a map.
			for ( int inByte = in.read() ; inByte != -1 ; inByte = in.read() ) {
				if ( map.containsKey((char) inByte) ) {
					int count = map.get((char) inByte);
					count++;
					map.put((char) inByte,count);
				} else {
					map.put((char) inByte,1);
				}
			}
			in.close();
			PriorityQueue<WeightedCodingTree> trees =
			    new PriorityQueue<WeightedCodingTree>(new WeightedCodingTreeComparator());
			// Creates WeightedCodingTrees using the map keys and values and adds them
			// to a
			// priorityqueue.
			trees.add(new WeightedCodingTree(1,HuffConstants.PSEUDO_EOF));
			for ( char c : map.keySet() ) {
				trees.add(new WeightedCodingTree(map.get(c),c));
			}
			// Builds a coding tree using the Huffman coding algorithm and writes the
			// compressed file.
			for ( ; trees.size() > 1 ; ) {
				trees.add(new WeightedCodingTree(trees.poll(),trees.poll()));
			}
			in = new FileReader(infile);
			assert trees.size() == 1 : "The tree size should be equal to one";
			WeightedCodingTree finalTree = trees.poll();
			CodingTree currentTree = finalTree.getCodingTree();
			encodings = currentTree.getEncodings();
			//if (checkSavings(map, encodings)) {
				compressionDone = true;
				bitOut.writeBits(HuffConstants.MAGIC_NUMBER);
			currentTree.write(bitOut);
			for ( int inByte = in.read() ; inByte != -1 ; inByte = in.read() ) {
				bitOut.writeBits(encodings.get((char) inByte));
			}
			bitOut.writeBits(encodings.get(HuffConstants.PSEUDO_EOF));
			bitOut.flush();
			bitOut.close();
			//}
			in.close();
		} else {
			System.out
			    .println("The selected file is already compressed. Please choose another one.");
			System.out.println();
		}
		return compressionDone;
	}

	/**
	 * Writes an uncompressed version of the file infile to the file outfile.
	 * 
	 * @param infile
	 *          The desired file that will be uncompressed.
	 * @param outfile
	 *          The desired file that will be written as the uncompressed version
	 *          of the file infile.
	 * @throws IOException
	 */
	public static void uncompress ( String infile, String outfile )
	    throws IOException {
		BitInputStream bitIn = new BitInputStream(new FileInputStream(infile));
		FileWriter out = new FileWriter(outfile);
		BitString magicNumber = bitIn.readBits(HuffConstants.MAGIC_NUMBER.length());
		if ( magicNumber.equals(HuffConstants.MAGIC_NUMBER) ) {
			CodingTree currentTree = new CodingTree(bitIn);
			for ( char charRead = currentTree
			    .nextChar(bitIn) ; charRead != HuffConstants.PSEUDO_EOF ; charRead =
			        currentTree.nextChar(bitIn) ) {
				out.write(charRead);
			}
			out.flush();
		} else {
			System.out
			    .println("The selected file is already uncompressed. Please choose another one.");
			System.out.println();
		}
		bitIn.close();
		out.close();
	}

	/**
	 * Does a bitwise comparison, printing out whether the files are the same or
	 * differ.
	 * 
	 * @param file1
	 *          The first file.
	 * @param file2
	 *          The second file.
	 * @throws IOException
	 */
	public static void compare ( String file1, String file2 ) throws IOException {
		FileInputStream in1 = new FileInputStream(file1);
		FileInputStream in2 = new FileInputStream(file2);
		int totalBytes = 0;
		boolean same = true;
		int in1Byte = in1.read();
		int in2Byte = in2.read();
		for ( ; in1Byte != -1 && in2Byte != -1 ; in1Byte = in1.read(), in2Byte =
		    in2.read() ) {
			if ( in1Byte != in2Byte ) {
				System.out.println("file mismatch, byte " + totalBytes + ": " + in1Byte
				    + " " + in2Byte);
				same = false;
				break;
			}
			totalBytes++;
		}
		in1.close();
		in2.close();
		if ( (in1Byte != -1 && in2Byte == -1)
		    || (in1Byte == -1 && in2Byte != -1) ) {
			System.out.println("file length mismatch");
			same = false;
		}

		if ( same ) {
			System.out.println("files are the same");
			System.out.println();
		} else {
			System.out.println("files are different");
			System.out.println();
		}

	}
	
	private static boolean checkSavings(Map<Character, Integer> map, Map<Character, BitString> encodings) {
		String str1 = "";
		String str2 = (HuffConstants.MAGIC_NUMBER).toString();
		for (char c : map.keySet()) {
			for (int i = 0; i < map.get(c); i++) {
				str1 += new BitString(c).toString();
				str2 += encodings.get(c).toString();
			}
		}
		str2 += (HuffConstants.PSEUDO_EOF).toString();
		if (str1.compareTo(str2) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
