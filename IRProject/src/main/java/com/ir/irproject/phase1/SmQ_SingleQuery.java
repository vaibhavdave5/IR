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
public class SmQ_SingleQuery {

	static String[] docIDs;
	static long D = 0;
	static boolean found = false;
	static float lambda = 0.35f;
	private static Map<String, String> indexes = null;

	public RankedDocuments generate(String query) throws FileNotFoundException, IOException {
		RankedDocuments smq_results = new RankedDocuments();

		// Getting some numbers ready:
		indexes = getIndexes();
		long C = getC();
		// Read the queries:
		int queryId = 0;

		queryId++;

		String qTerms[] = query.split(" ");
		Map<String, Float> scores = initialize();
		for (String qterm : qTerms) {
			int cq = getCq(qterm);

			for (String docId : docIDs) {
				int fq = getFqd(docId, qterm);
				float score = (1 - lambda) * (fq / D) + lambda * cq / C;
				scores.put(docId, score + scores.get(docId));
			}

		}

		Map<String, Float> map2 = sortByValue(scores);
		for (Map.Entry<String, Float> entry : map2.entrySet()) {

			smq_results.put(entry.getKey(), entry.getValue());

		}

		return smq_results;

	}

	public static int getFqd(String file, String queryTerm) throws FileNotFoundException, IOException {
		BufferedReader read = new BufferedReader(
				new FileReader(Constants.TEXT_CORPUS + File.separator + file + ".txt"));
		String s = read.readLine();
		String s1[] = s.split(" ");
		D = s1.length;
		int count = 0;
		for (String str : s1) {
			if (str.equals(queryTerm)) {
				count++;
			}
		}
		read.close();
		return count;
	
	}

	public static HashMap<String, Float> initialize() {
		HashMap<String, Float> map = new HashMap<String, Float>();
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

	public static int getCq(String queryTerm) throws FileNotFoundException, IOException {

		int count = 0;
		if (indexes.containsKey(queryTerm)) {
			String s = indexes.get(queryTerm);
			String[] posting = s.split("\t");

			for (int i = 2; i < posting.length; i = i + 2) {
				count = count + Integer.parseInt(posting[i]);
			}
			docIDs = new String[(posting.length - 1) / 2];
			int dci = 0;
			for (int i = 1; i <= posting.length - 2; i = i + 2) {
				docIDs[dci++] = posting[i];
			}

		}
		return count;
	}

	public static long getC() throws FileNotFoundException, IOException {
		BufferedReader read = new BufferedReader(new FileReader(Constants.Index_Location));
		long count = 0;
		String s = read.readLine();
		while (s != null) {
			count++;
			s = read.readLine();
		}
		read.close();
		return count;
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
