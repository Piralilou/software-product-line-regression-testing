package kwicdeltaj.prod1.kwic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
/*** added by dBase
 */
public class StopWordManager {
	public List<String> stopWord(String url) {
		List<String> list = new LinkedList();
		try {
			FileReader file = new FileReader(url);
			BufferedReader readFile = new BufferedReader(file);
			String n = readFile.readLine();
			while(n != null) {
				list.add(n);
				n = readFile.readLine();
			}
			file.close();
		}
		catch(IOException e) {
			System.err.printf("Error opening: %s.\n", e.getMessage());
		}
		return list;
	}
}