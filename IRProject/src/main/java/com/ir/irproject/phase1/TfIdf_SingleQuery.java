/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ir.irproject.phase1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ir.irproject.extra_credit.POJO.RankedDocuments;
import com.ir.irproject.extra_credit.resource.Constants;

/**
 *
 * @author Vaibhav
 */
public class TfIdf_SingleQuery {

	static int nk;
	static String[] docIDs;
	static int[] tfs;
	private static Map<String, String> indexes = null;

	public RankedDocuments generate(String query) throws FileNotFoundException, IOException {
		RankedDocuments tfidf_results = new RankedDocuments();
		// Getting some numbers ready:
		int N = getN();
		indexes = getIndexes();
		// Read the queries:

		Map<String, Float> map = new HashMap<String, Float>();
		map = initialize();
		String[] queryTerm = query.split(" ");
		Map<String, Integer> queryTerms = new HashMap<>();

		for (String term : queryTerm) {
			if (queryTerms.containsKey(term)) {
				int temp = queryTerms.get(term) + 1;
				queryTerms.put(term, temp);
			} else {
				queryTerms.put(term, 1);
			}
		}

		// for query term weight (1+ log fr) * log N/nk
		// for document term weight (1+ log fr) * log N/nk
		for (Map.Entry<String, Integer> entry : queryTerms.entrySet()) {
			String term = entry.getKey();
			int qfr = entry.getValue();
			boolean found = getNk(term);
			if (!found) {
				continue; // if the query term is not present in index go to
							// next query term.
			}
			nk = docIDs.length;
			for (int i = 0; i < docIDs.length; i++) {
				float score = (float) (map.get(docIDs[i]) + (1 + Math.log(tfs[i])) * Math.log(N / nk));
//				map.put(docIDs[i], score);
				tfidf_results.put(docIDs[i], score);
			}
		}

//		Map<String, Float> map2 = sortByValue(map);
//	
//		for (Map.Entry<String, Float> entry : map2.entrySet()) {
//			String key = entry.getKey();
//			tfidf_results.put(key, entry.getValue());
//
//		}
		
		tfidf_results = RankedDocuments.sort(tfidf_results);
		
		return tfidf_results;
	}

	public static int getN() {
		File folder = new File(Constants.TEXT_CORPUS);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles.length;
	}

	public static boolean getNk(String term) throws IOException {
		String s = indexes.get(term);
		boolean found = indexes.containsKey(term);
		// Get the index here
		if (found) {
			String[] s1 = s.split("\t");
			docIDs = new String[(s1.length - 1) / 2];
			int dci = 0;
			for (int i = 1; i <= s1.length - 2; i = i + 2) {
				docIDs[dci++] = s1[i];
			}

			tfs = new int[(s1.length - 1) / 2];
			int tfi = 0;
			for (int i = 2; i <= s1.length - 1; i = i + 2) {
				tfs[tfi++] = Integer.parseInt(s1[i]);
			}
			return true;
		}
		return false;
	}

	public static HashMap<String, Float> initialize() {
		HashMap<String, Float> map = new HashMap<>();
		File folder = new File(Constants.TEXT_CORPUS);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			map.put(file.getName().replace(".txt", ""), 0.0f);
		}
		return map;
	}

	// sorting the hashmap by its value
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static HashMap<String, String> getIndexes() throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(Constants.Index_Location));
		HashMap<String, String> map = new HashMap<>();
		String s = read.readLine();
		while (s != null) {
			String s1[] = s.split("\t");
			map.put(s1[0], s);
			s = read.readLine();
		}
		read.close();
		return map;
	}
}
