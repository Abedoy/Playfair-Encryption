/**
* <pre>
* author		Adaias Bedoy
* description: This program uses playfair method to encrypt and decrypt messages from text files. There are 4 text files: msgin, msgout, cipin, cipout.  Messages are in blocks of 4 characters with spaces in between. The final decryption is missing the portion * to remove the X's placed to deal with situations. 
* with duplicate letters next to each other but the message is still readable.
*
*</pre>
*/


import java.awt.Point;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuilder;
import java.io.*;

public class Playfair
{
	/**
	 * <pre>
	 * Description: Global Data for the key and the input which is the text message from file
	 * Pre: None
	 * Post: Initializes some data
	 * </pre>
	 * 
	 */

	// length of array
	private int length = 0;
  
	// table for Playfair cipher
	private String [][] table;
	
	String input = "";
	String keyword = "";
  

  
	//reads automatically message from file
	/**
	 * <pre>
	 * Description: Constructor for playfair cipher. Chose to make it read something immediately since other * *   methods need read data to do anything.
	 * Pre: None.
	 * Post: Gets some message data read and ready to encrypt
	 * </pre>
	 * 
	 */
	private Playfair()
	{
  
		//buffer reader that reads from text file
		System.out.println("Reading msg you want to encrypt from file...");
		try (BufferedReader br = new BufferedReader(new FileReader("msgin.txt")))
		{
 
			String sCurrentLine;
			
			int iteration = 0;
 
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(iteration == 0)
				{
					keyword = sCurrentLine;
					iteration++; // skip first line which is the key, important not to print key!
					continue;
				}
				input += sCurrentLine;
				
				System.out.println(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		
		} 
		//Parsing stuff like lowercase, spaces, and J away for the key
		keyword = keyword.toUpperCase();
		keyword = keyword.replaceAll("[^A-Z]", "");
		keyword = keyword.replaceAll("J", "I"); 
		//System.out.println();
		table = this.cipherTable(keyword);
		//more parsing but this time for the clear text
		input = input.toUpperCase();
		input = input.replaceAll("[^A-Z]", "");
		input = input.replaceAll("J", "I"); 

		System.out.println();
    
		// encodes and then decodes the encoded message
		String output = cipher(input);
		String decryptedOutput = dec(output);
    
		// output the results to user
		//this.printTable(table);
		//this.printResults(output,decryptedOutput);
	}
  

  	/**
	 * <pre>
	 * Description: Creates cipher table used for encryption and decryption algorithms
	 * Pre: Needs a key which comes from first line of clear text file
	 * Post: Creates cipher table by using a 2D array of strings
	 * </pre>
	 * 
	 */
	// creates the cipher table based on some input string (already parsed)
	private String[][] cipherTable(String key)
	{
		String[][] playfairTable = new String[5][5];
		String keyString = key + "ABCDEFGHIKLMNOPQRSTUVWXYZ";
    
		// fill string array with empty string
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				playfairTable[i][j] = "";
			}
		}
		//check string array for repeated letters then fills out table with unused letters
		for(int k = 0; k < keyString.length(); k++)
				{
					boolean repeat = false;
					boolean used = false;
					for(int i = 0; i < 5; i++)
					{
						for(int j = 0; j < 5; j++)
						{
							//fills out first part of the table using the key
							if(playfairTable[i][j].equals("" + keyString.charAt(k)))
							{
								repeat = true;
							}
							//fills out the rest of the table after the key has been successfully added first
							else if(playfairTable[i][j].equals("") && !repeat && !used)
							{
								playfairTable[i][j] = "" + keyString.charAt(k);
								used = true;
							}
						}
					}
				}
		return playfairTable;
  }
  	/**
	 * <pre>
	 * Description: Prepares message for encryption by making letter pairs
	 * Pre: Needs text from file
	 * Post: Puts all the letters in pairs which are ready to be encrypted
	 * </pre>
	 * 
	 */
  // cipher: takes input (all upper-case), encrypts it, and returns output
  private String cipher(String in)
  {
    length = (int) in.length() / 2 + in.length() % 2;
    
    // insert x between double-letter pairs & redefines "length"
	//not actually needed after homework update, but since it's left here since it was already implemented
    for(int i = 0; i < (length - 1); i++)
	{
		if(in.charAt(2 * i) == in.charAt(2 * i + 1))
		{
			in = new StringBuffer(in).insert(2 * i + 1, 'X').toString();
			length = (int) in.length() / 2 + in.length() % 2;
		}
    }
    
    // adds an x to the last pair, if necessary
    String[] pair = new String[length];
    for(int j = 0; j < length ; j++)
	{
		//checks if number of letters in message is odd, if it is odd then add X to make sure all letters are paired
		if(j == (length - 1) && in.length() / 2 == (length - 1))
		{
			in = in + "X";
		}
			pair[j] = in.charAt(2 * j) +""+ in.charAt(2 * j + 1);
    }
    
		// encodes the pairs and returns the output
		String out = "";
		String[] encryptPair = new String[length];
		encryptPair = enc(pair);
		for(int k = 0; k < length; k++)
		{
			out = out + encryptPair[k];
		}
    return out;
  }
  
  	/**
	 * <pre>
	 * Description: Actual encryption that uses the letter pair column and row numbers
	 * Pre: Need letter pairs sorted out to use
	 * Post: Encrypts letter pairs and returns the encryption to cipher text file
	 * </pre>
	 * 
	 */
  // encrypts the pair input with the cipher's specifications
  private String[] enc(String di[])
  {
		String[] encrypt = new String[length];
		for(int i = 0; i < length; i++)
		{
			char a = di[i].charAt(0); //first letter
			char b = di[i].charAt(1); //second letter
			int r1 = (int) getPoint(a).getX(); //row # of first letter in pair
			int r2 = (int) getPoint(b).getX(); //row # of second letter in pair
			int c1 = (int) getPoint(a).getY(); // column # of first letter in pair
			int c2 = (int) getPoint(b).getY(); // column # of second letter in pair
      
			// case 1: letters in pair are of same row, shift columns to right
			if(r1 == r2)
			{
				c1 = (c1 + 1) % 5;
				c2 = (c2 + 1) % 5;
			}
			// case 2: letters in pair are of same column, shift rows down
			else if(c1 == c2)
			{
				r1 = (r1 + 1) % 5;
				r2 = (r2 + 1) % 5;
			}
			// case 3: letters in pair form rectangle, swap first column # with second column #
			else
			{
				int temp = c1;
				c1 = c2;
				c2 = temp;
			}
      
			//performs the table look-up and puts those values into the encoded array
			encrypt[i] = table[r1][c1] + "" + table[r2][c2];
		}
			try 
			{
				String content = "";
 
				File file = new File("cipout.txt");
				// if file doesnt exists, then create it
				if (!file.exists()) 
				{
					file.createNewFile();
				}
 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(keyword);
				bw.newLine();
				for(int i = 0; i < 44; i++)
				{
				bw.write(encrypt[i]);
				bw.write(encrypt[i+1]);
					for(int j = 0; j < 4; j++)
					{
						double modulus = j%4;
						if(modulus == 0)
						{
							bw.write(" ");
						}
					}
					
				}
				
				bw.close();
 
				//System.out.println("Done");
			}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			try 
			{
				String content = "";
 
				File file = new File("cipin.txt");
				// if file doesnt exists, then create it
				if (!file.exists()) 
				{
					file.createNewFile();
				}
 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(keyword);
				bw.newLine();
				for(int i = 0; i < 44; i++)
				{
				bw.write(encrypt[i]);
				bw.write(encrypt[i+1]);
					for(int j = 0; j < 4; j++)
					{
						double modulus = j%4;
						if(modulus == 0)
						{
							bw.write(" ");
							
						}
					}
					
				}
				
				bw.close();
 
				//System.out.println("Done");
			}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				System.out.println("Reading encrypted msg from file...");
		try (BufferedReader br = new BufferedReader(new FileReader("cipout.txt")))
		{
 
			String sCurrentLine;
			
			int iteration = 0;
 
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(iteration == 0)
				{
					keyword = sCurrentLine;
					iteration++; // skip first line which is the key, important not to print key!
					continue;
				}
				input += sCurrentLine;
				
				System.out.println(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		
		} 
	return encrypt;
  }
  
  	/**
	 * <pre>
	 * Description: Decryption which is the reverse of the encryption. Puts clear text in a file
	 * Pre: Need encrypted message to decrypt it
	 * Post: Turns cipher text into clear text and puts it in a file
	 * </pre>
	 * 
	 */
  // decodes the output given from the cipher and decrypt which is the opposite of encrypting method
  private String dec(String out)
  {
	String decrypted = "";
    for(int i = 0; i < out.length() / 2; i++)
	{
		char a = out.charAt(2*i); //first letter
		char b = out.charAt(2*i+1); //second letter
		int r1 = (int) getPoint(a).getX(); //row # of first letter in pair
		int r2 = (int) getPoint(b).getX(); //row # of second letter in pair
		int c1 = (int) getPoint(a).getY(); //column # of first letter in pair
		int c2 = (int) getPoint(b).getY(); //column # of second letter in pair
		
		if(r1 == r2) //case 1: letters in pair are of same row, shift column to left
		{
			c1 = (c1 + 4) % 5;
			c2 = (c2 + 4) % 5;
		}
		else if(c1 == c2) //case 2: letters in pair are of same column, shift rows up
		{
			r1 = (r1 + 4) % 5;
			r2 = (r2 + 4) % 5;
		}
		else //case 3:letters in pair form rectangle swap second column # with first column #
		{
			int temp = c1;
			c1 = c2;
			c2 = temp;
		}
      decrypted = decrypted + table[r1][c1] + table[r2][c2];
	  	

		
    }
	try //this loop writes the decrypted message out in a file and adds spaces to make it easier to read. However there are still X's between double letters
	{
		File file = new File("msgout.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) 
		{
			file.createNewFile();
		}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(keyword);
			bw.newLine();
			String[] strArray = decrypted.split("");
			
			for(int i = 0; i < strArray.length; i++)
			{
				bw.write(strArray[i]);
				
				//bw.write(strArray[i+2]);
				for(int j = 1; j <= 1; j++)
				{
					double modulus = i%4;
					if(modulus == 0)
					{
						bw.write(" ");
						
					}
					
				}
					
			}						
				bw.close();
			}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
	System.out.println("Reading decrypted msg from file...");
		try (BufferedReader br = new BufferedReader(new FileReader("msgout.txt")))
		{
 
			String sCurrentLine;
			
			int iteration = 0;
 
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(iteration == 0)
				{
					keyword = sCurrentLine;
					iteration++; // skip first line which is the key, important not to print key!
					continue;
				}
				input += sCurrentLine;
				
				System.out.println(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		
		} 
    return decrypted;
	
  }
  
  // returns a point containing the row and column of the letter
  	/**
	 * <pre>
	 * Description: Keeps track of position of letters so that the encryption and decryption methods can use them
	 * Pre: Needs cipher table filled with letters
	 * Post: Makes positions available for encryption and decryption methods to use
	 * </pre>
	 * 
	 */
  private Point getPoint(char c)
  {
		Point pt = new Point(0,0);
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				if(c == table[i][j].charAt(0))
				{
					pt = new Point(i,j);
				}
			}
		}
    return pt;
  }
  
	/**
	 * <pre>
	 * Description: Prints table
	 * Pre: Needs key to create the table
	 * Post: Prints cipher table out, but this was mostly used for testing purposes.
	 * </pre>
	 * 
	 */
  	private void printTable(String[][] printedTable)
	{
			System.out.println();
    
			for(int i = 0; i < 5; i++)
			{
				for(int j = 0; j < 5; j++)
				{
					System.out.print(printedTable[i][j]+" ");
				}
				System.out.println();
			}
			System.out.println();
	}

  

  	/**
	 * <pre>
	 * Description: Test methods. Used scanner since it was really simple to use since there were no problems 
	 *  with dealing with complicated parsing and changing array of strings to strings, etc. Prints out some stuff
	 * Pre: Need inputted key and inputted text manually 
	 * Post: Prints cipher table, shows you example of encryption of some inputted text and its decryption.
	 * </pre>
	 * 
	 */
  //test methods
  /*
  //scanner reader to quickly check if the encryption and decryption methods worked without file reader
	// prompts user for the keyword to use for encoding & creates tables
		System.out.println("Please input the keyword for the Playfair cipher.");
		Scanner sc = new Scanner(System.in);
		String keyword = parseString(sc);
		while(keyword.equals(""))
		keyword = parseString(sc);
		System.out.println();
		table = this.cipherTable(keyword);
		
		System.out.println("Please input the message to be encoded");
		System.out.println("using the previously given keyword");
		String input = parseString(sc);
		while(input.equals(""))
		input = parseString(sc);
		System.out.println();
    
		// encodes and then decodes the encoded message
		String output = cipher(input);
		String decryptedOutput = dec(output);
    
		// output the results to user
		this.printTable(table);
		this.printResults(output,decryptedOutput);
	*/
  
  	// parses any input string to remove numbers, punctuation,
	// replaces any J's with I's, and makes string all caps
	/*private String parseString(Scanner s)
	{
		String parse = s.nextLine();
		parse = parse.toUpperCase();
		parse = parse.replaceAll("[^A-Z]", "");
		parse = parse.replace("J", "I");
		return parse;
	}*/
   /* // prints the cipher table out for the user
	private void printTable(String[][] printedTable)
	{
			System.out.println();
    
			for(int i = 0; i < 5; i++)
			{
				for(int j = 0; j < 5; j++)
				{
					System.out.print(printedTable[i][j]+" ");
				}
				System.out.println();
			}
			System.out.println();
	}
	  // prints results (encrypted and decrypted)
  private void printResults(String enc, String dec)
  {
		System.out.println("This is the encoded message:");
		System.out.println(enc);
		System.out.println();
		System.out.println("This is the decoded message:");
		System.out.println(dec);
  }
  */
  
  	// main method to test Playfair method
	public static void main(String[] args)
   {
		Playfair pf = new Playfair();
   }
}