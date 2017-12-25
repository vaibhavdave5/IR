/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ir.irproject.phase1;

/**
 *
 * @author Vaibhav
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ir.irproject.extra_credit.resource.Constants;

/**
 *
 * @author Vaibhav
 */
public class Indexing {
	// public void generate() throws Exception {

	public void generate() throws Exception {

		File folder = new File(Constants.TEXT_CORPUS);
		File[] listOfFiles = folder.listFiles();
		// for each file in the tokens folder
		Map<String, StringBuilder> map = new HashMap<String, StringBuilder>();
		for (File file : listOfFiles) {
			BufferedReader read = new BufferedReader(new FileReader(file));
			String s = read.readLine();
			String[] s1 = s.split(" ");
			// for each token in the file
			for (String term : s1)
				if (map.containsKey(term)) {
					map.get(term).append("\t" + file.getName().replace(".txt", ""));
				} else {

					map.put(term, new StringBuilder().append("\t" + file.getName().replace(".txt", "")));
				}

			read.close();
		}

		// Write the index in a file.
		BufferedWriter wr = new BufferedWriter(new FileWriter(Constants.Index_Location));

		for (Map.Entry<String, StringBuilder> entry : sortByValue(map).entrySet()) {
			wr.write(entry.getKey() + "\t" + printEntryValues(entry.getValue().toString()) + System.lineSeparator());
		}
		wr.flush();
		wr.close();
	}

	public static String printEntryValues(String str) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String arr[] = Arrays.copyOfRange(str.split("\t"), 1, str.split("\t").length);

		for (String s : arr) {
			if (map.containsKey(s)) {
				int x = map.get(s) + 1;
				map.put(s, x);
			} else {
				map.put(s, 1);
			}
		}

		String temp = "";
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			temp = temp + entry.getKey() + "\t" + entry.getValue().toString() + "\t";
		}
		return temp.substring(0, temp.length() - 1);
	}

	// sorting the hashmap by its value
	public static Map<String, StringBuilder> sortByValue(Map<String, StringBuilder> map) {
		List<Map.Entry<String, StringBuilder>> list = new LinkedList<Map.Entry<String, StringBuilder>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, StringBuilder>>() {
			public int compare(Map.Entry<String, StringBuilder> o1, Map.Entry<String, StringBuilder> o2) {
				return o1.getValue().toString().length() > o2.getValue().toString().length() ? -1
						: (o1.getValue().toString().length() < o2.getValue().toString().length() ? 1 : 0);
			}
		});

		Map<String, StringBuilder> result = new LinkedHashMap<String, StringBuilder>();
		for (Map.Entry<String, StringBuilder> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
