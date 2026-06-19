package kwicdeltaj.prod1.kwic;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/*** added by dBase
 */
public class WordShift {
	public List<StringStorage> shiftBegin(List<IndexStorage> list, String word) {
		List<StringStorage> listFinal = new LinkedList();
		for(IndexStorage n : list) {
			StringStorage stringStorage = new StringStorage();
			stringStorage.setKeyword(word);
			String [] wordS = wordSep(n);
			String left = "";
			for(int i = 0;
				i < n.getPosition();
				i ++) {
				left = left.concat(wordS[i] + " ");
			}
			String right = "";
			for(int j = n.getPosition() + 1;
				j < wordS.length;
				j ++) {
				right = right.concat(" " + wordS[j]);
			}
			stringStorage.setLeft(left);
			stringStorage.setRight(right);
			listFinal.add(stringStorage);
		}
		return listFinal;
	}
	public String [] wordSep(IndexStorage storage) {
		String aux = storage.getLine();
		return aux.split(" ");
	}
}