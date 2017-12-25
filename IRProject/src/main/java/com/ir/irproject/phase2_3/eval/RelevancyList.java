package com.ir.irproject.phase2_3.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ir.irproject.extra_credit.resource.Constants;
import com.ir.irproject.extra_credit.tools.TextFile;

/**
 * Represents the relevancy list of the corpus for designated queries
 * @author Nicholas Carugati
 *
 */
public class RelevancyList {

	/**
	 * Gets the relevance list for a specific query 
	 * which stores specific relevancy values
	 */
	private Map<Integer, HashSet<Integer>> relevancies;
	
	/**
	 * Creates a new instance of the relevancy list
	 * @throws IOException if any file issues occur
	 */
	public RelevancyList() throws IOException {
		relevancies = new HashMap<Integer, HashSet<Integer>>();
		File file = TextFile.getFile(Constants.RELEVANCY_FOLDER, ".txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = br.readLine()) != null) {
			String[] tuple = line.split(" ");
			if(tuple.length != 4) {
				br.close();
				return;
			}
			tuple[2] = tuple[2].substring(5, tuple[2].length());
			int qId = Integer.parseInt(tuple[0]);
			int docId = Integer.parseInt(tuple[2]);
			if(relevancies.get(qId) != null) {
				relevancies.get(qId).add(docId);
			} else {
				HashSet<Integer> vals = new HashSet<Integer>();
				vals.add(docId);
				relevancies.put(qId, vals);
			}
		}
		br.close();
	}

	/**
	 * Retrieves the relevancy information about the corpus
	 * @return the relevancy list
	 */
	public Map<Integer, HashSet<Integer>> getRelevancies() {
		return relevancies;
	}

	/**
	 * Returns true if this relevancy list contains the given key.
	 * @param key : the key to be checked
	 */
	public boolean contains(int key) {
		return this.relevancies.containsKey(key);
	}

}
