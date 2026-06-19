package kwicdeltaj.prod1.kwic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
/*** added by dFileOut
 */
public class SaveFile implements Save {
	public void save(List<StringStorage> list) {
		try {
			FileWriter file = new FileWriter("shifts.html");
			PrintWriter recordFile = new PrintWriter(file);
			recordFile.println("<!DOCTYPE html>");
			recordFile.println("<head>");
			recordFile.println("<meta charset=\"UTF-8\">");
			recordFile.println("<title>KEYWORD IN CONTEXT</title>");
			recordFile.println("</head>");
			recordFile.println("<body>");
			recordFile.println("<center><b>KEYWORD IN CONTEXT</b></center>");
			for(StringStorage n : list) {
				recordFile.println("<center>" + n.getRight() + " <b>" + n.getKeyword() +
					"</b> " + n.getLeft() + "</center>");
				recordFile.println("<br/>");
			}
			recordFile.println("</body>");
			recordFile.println("</html>");
			System.out.println("File generated successfully = shifts.html");
			file.close();
		}
		catch(IOException e) {
			System.err.printf("Error writing file: %s.\n", e.getMessage());
		}
	}
}