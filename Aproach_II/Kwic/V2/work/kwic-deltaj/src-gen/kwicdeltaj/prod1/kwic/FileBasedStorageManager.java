package kwicdeltaj.prod1.kwic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/*** added by dFileIn
 */
public class FileBasedStorageManager implements DataStorageManager {
	List<String> listString = new LinkedList();
	@Override
	public void init() {
		System.out.println("Enter the name of file");
		Scanner read = new Scanner(System.in);
		String nFile = read.nextLine();
		System.out.println("[FILE BASED = " + nFile + "]");
		try {
			FileReader file = new FileReader(nFile);
			BufferedReader readFile = new BufferedReader(file);
			String n = readFile.readLine();
			while(n != null) {
				listString.add(n);
				n = readFile.readLine();
			}
		}
		catch(IOException e) {
			System.err.printf("Error opening file %s.\n", e.getMessage());
		}
	}
	public List<String> getListString() {
		return listString;
	}
	@Override
	public String line(int index) {
		return listString.get(index);
	}
	@Override
	public int length() {
		return listString.size();
	}
}