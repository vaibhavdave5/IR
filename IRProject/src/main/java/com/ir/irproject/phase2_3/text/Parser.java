package com.ir.irproject.phase2_3.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * Represents the main parsing component for the search engine
 * @author Nicholas Carugati
 *
 */
public class Parser {

	/**
	 * Determines if this parser instance should follow the stemmed corpus
	 */
	boolean stem = false;
	
	/**
	 * The normal corpus directory path
	 */
	private static String CORPUS_DIRECTORY = "corpus";
	
	/**
	 * The stemmed corpus directory path
	 */
	private static String CORPUS_STEM_DIRECTORY = "corpus_stm";
	
	
	/**
	 * Creates a new parser instance
	 * @param stem whether to use the regular corpus or stemmed
	 */
	public Parser(boolean stem) {
		this.stem = stem;
	}
	
	/**
	 * Default instance of the parser
	 */
	public Parser() {
		this(false);
	}
	
	/**
	 * The main routine which generates the corpus
	 * @throws IOException if there are any issues with file operations
	 */
	public void parse() throws IOException {
		if(stem) {
			parseStem();
			return;
		}
		directoryCheck(CORPUS_DIRECTORY);
		String sep = File.separator;
		System.out.println("data" + sep + "cacm.tar" + sep);
		File[] files = new File("data" + sep + "cacm.tar" + sep).listFiles();
		BufferedWriter bw = null;
		for(File file : files) {
			String id = Integer.toString(Integer.parseInt(file.getName().substring(5, 9)));
			File f = new File("corpus" + sep + id + ".txt");
			bw = new BufferedWriter(new FileWriter(f));
			Document d = Jsoup.parse(file, "UTF-8");
			bw.write(handlePunctuation(Jsoup.clean(d.text(), Whitelist.none())).toLowerCase());
			bw.flush();
		}
		bw.close();
	}
	
	/**
	 * Checks if a directory exists. If it doesn't exist it will create a new one
	 * @param path the path of the directory to check
	 */
	private void directoryCheck(String path) {
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdir();
		}
	}
	
	/**
	 * Parses the stemmed corpus
	 * @throws IOException if there are any file operation issues
	 */
	private void parseStem() throws IOException {
		directoryCheck(CORPUS_STEM_DIRECTORY);
		String sep = File.separator;
		BufferedReader br = new BufferedReader(new FileReader("data" + sep + "cacm_stem.txt"));
		String line = null;
		BufferedWriter bw = null;
		File f = null;
		while ((line = br.readLine()) != null) {
	    		if(line.charAt(0) == '#') {
	    			String[] str = line.split(" ");
	    			f = new File("corpus_stm" + sep + str[1] + ".txt");
	    			bw = new BufferedWriter(new FileWriter(f));
	    		} else if(!NumberUtils.isDigits(StringUtils.deleteWhitespace(line))) {
	    			bw.write(handlePunctuation(new StringBuilder(line).append(" ").toString().toLowerCase()));
	    			bw.flush();
	    		}
		}
		br.close();
		bw.close();
	}
	
	/**
	 * Remove all the punctuation from the file except the punctuation occurring
	 * within digits
	 * @param documentText the text to be modified
	 * @return the modified text
	 * @author Divyavijay Sahay
	 */
	private String handlePunctuation(String documentText) {
		boolean keepPunc = false;
		boolean withinCharcter = false;
		StringBuilder builder = new StringBuilder();
		int position = 0;
		for (char ch : documentText.toCharArray()) {
			position++;
			if (Character.isLetter(ch) || Character.isWhitespace(ch)) { // letter and white space
				builder.append(ch);
				keepPunc = false;
				if (Character.isLetter(ch)) {
					withinCharcter = true;
				} else {
					withinCharcter = false;
				}
			} else if (Character.isDigit(ch)) { // digits
				builder.append(ch);
				keepPunc = true;
			} else {
				if (keepPunc) {
					// Keep the punctuation only within digits
					if ((position) != documentText.length()) {
						if (!Character.isWhitespace(documentText.charAt(position))
								&& Character.isDigit(documentText.charAt(position))) {
							builder.append(ch);
						}

					}
				} else {
					// keep the hyphen if within letters
					if (withinCharcter) {
						if (ch == (char) 45) { // Hyphen
							builder.append(ch);
							withinCharcter = false;
						}
					}
				}
			}
		}
		String modifiedContent = builder.toString();
		builder = null;
		return modifiedContent;
	}

}
