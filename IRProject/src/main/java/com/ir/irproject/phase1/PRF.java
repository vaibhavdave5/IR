/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ir.irproject.phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.ir.irproject.extra_credit.resource.*;
import com.ir.irproject.phase2_3.eval.EvaluationService;
import com.ir.irproject.extra_credit.POJO.*;

/**
 *
 * @author Vaibhav
 */
public class PRF {
	
	
	private EvaluationService evaluationService = new EvaluationService();
	
	public Map<Integer, LinkedHashMap<String, Integer>> generateRelevantWords() throws Exception {
		HashMap<Integer, LinkedHashMap<String, Integer>> results = new HashMap<Integer, LinkedHashMap<String, Integer>>();
		// public void doYourWork() throws IOException {
		File folder = new File(Constants.TFIDF_FOLDER);
		File[] listOfFiles = folder.listFiles();
		List<String> wordsToBeIgnored = new ArrayList<String>();
		wordsToBeIgnored.add("cacm");
		wordsToBeIgnored.add("jb");
		wordsToBeIgnored.add("ca");

		// Get the stoplist
		Set<String> stopList = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(Constants.stopList));
		String str = reader.readLine();
		while (str != null) {
			stopList.add(str);
			str = reader.readLine();
		}

		Map<Integer, List<String>> queryMap = new HashMap<Integer, List<String>>();
		BufferedReader queryReader = new BufferedReader(new FileReader(Constants.QUERY_PATH + ".txt"));
		String query = queryReader.readLine();
		int counter = 0;
		while (query != null) {
			ArrayList<String> temp = new ArrayList<String>();
			for (String term : query.split(" ")) {
				temp.add(term);
			}
			queryMap.put(++counter, temp);
			query = queryReader.readLine();
		}

		for (File file : listOfFiles) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			int count = 0;
			String s = fileReader.readLine();
			while (s != null && count < 30) {
				String s1[] = s.split("\t");
				String fileName = s1[0];
				BufferedReader read = new BufferedReader(
						new FileReader(Constants.TEXT_CORPUS + File.separator + fileName + ".txt"));
				String text = read.readLine();
				String[] textarr = text.split(" ");

				for (String term : textarr) {
					if (!StringUtils.isNumeric(term)&&map.containsKey(term) && !stopList.contains(term) && !wordsToBeIgnored.contains(term)
							&& !queryMap.get(Integer.parseInt(file.getName().replace(".txt", ""))).contains(term)) {
						map.put(term, map.get(term) + 1);
					} else {
						map.put(term, 1);
					}
				}
				s = fileReader.readLine();
				count++;
				read.close();
			}
			LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> entry : sortByValue(map).entrySet()) {
				temp.put(entry.getKey(), entry.getValue());
			}
			
			results.put(Integer.parseInt(file.getName().replace(".txt", "")), temp);
			fileReader.close();
		}
		
		queryReader.close();
		reader.close();
		return results;
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

	public RankedDocuments[] generateWithPRF() throws Exception {
		Map<Integer, LinkedHashMap<String, Integer>> map = new PRF().generateRelevantWords();
		RankedDocuments[] d = new RankedDocuments[map.size()];
		BufferedReader read = new BufferedReader(new FileReader(Constants.QUERY_PATH + ".txt"));
		String query;
		int count = 0;
		BufferedWriter wr = new BufferedWriter(new FileWriter("output"+File.separator+"PRFExpandedQueries.txt"));
		for (Map.Entry<Integer, LinkedHashMap<String, Integer>> entry : map.entrySet()) {
			query = read.readLine();

			int stop = 0;
			LinkedHashMap<String, Integer> queryExpansionTerms = entry.getValue();

			for (Map.Entry<String, Integer> ent : queryExpansionTerms.entrySet()) {
				query = query + " " + ent.getKey();
				if (stop++ == 10) {
					break;
				}
			}
			
			wr.write(query+System.lineSeparator());
			d[count] = new RankedDocuments();
			d[count++] = new TfIdf_SingleQuery().generate(query);
		}
		wr.flush();
		wr.close();
		read.close();
		return d;
	}

	public void execute() throws Exception {
		RankedDocuments[] d = new PRF().generateWithPRF();

		for (int i = 0; i < d.length; i++) {
			BufferedWriter wr = new BufferedWriter(
					new FileWriter("output" + File.separator + "PRFTFIDFResults" + File.separator + (i + 1) + ".txt"));
			RankedDocuments inFocus = d[i];
			//System.out.println(d[i].size());
			int j = 1;
			for (Map.Entry<String, Float> entry : inFocus.entrySet()) {
				wr.write(entry.getKey() + "\t" + entry.getValue() + System.lineSeparator());
				j++;
				if(j==101){
					break;
				}
			}
			wr.flush();
			wr.close();
		}
		
		evaluationService.analyze(RetrievalModel.PRF);

	}
}
