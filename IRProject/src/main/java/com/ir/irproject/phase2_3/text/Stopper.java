package com.ir.irproject.phase2_3.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a stopper component for the search engine
 * @author Nicholas Carugati
 */
public class Stopper {

	/**
	 * The list of all the stop words
	 */
	private Set<String> stopList;
	
	/**
	 * Creates a single stop list instance. A file is required to load the stopper.
	 * @throws IOException if a file is missing or is unable to read a line
	 */
	public Stopper() throws IOException {
		// If you're using windows you're going to need to change the path format
		BufferedReader br = new BufferedReader(new FileReader("./data/common_words.txt"));
		
		stopList = new HashSet<String>();
		String line = null;
	    while ((line = br.readLine()) != null) {
	    		stopList.add(line);
	    }
	    br.close();
	}
	
	/**
	 * Determines if a stop word is in the set
	 * @param s the string to compare to the stop list
	 * @return if the string is in the set
	 */
	public boolean isStopWord(String s) {
		return stopList.contains(s);
	}

}
