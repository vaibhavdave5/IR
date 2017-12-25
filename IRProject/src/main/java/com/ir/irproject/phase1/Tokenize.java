/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ir.irproject.phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ir.irproject.extra_credit.resource.Constants;

/**
 *
 * @author Vaibhav
 */
public class Tokenize {
	// Option 1 = Punctuation handling
	// Option 2 = case folding
	// Option 3 = Both
	// Option 4 = Stopped;
	// Option 5 = Non Stopped
	// Option 6 = Stemmed , LowerCase , Not Stopped

	public String tokenize(String s, int option, Set<String> set) throws FileNotFoundException, IOException {
		StringBuilder sb = new StringBuilder("");
		String s1[] = s.replaceAll("[\\n\\r\\t]+", " ").split(" ");// Split with spaces
		StringBuilder finalToken = new StringBuilder();
		if (option == 6) {
			set = new HashSet<String>();
		}

		for (int i = 0; i < s1.length; i++) {
			if (s1[i].matches("^[\u0000-\u0080]+$")) { // Removes other languages
				if (!s1[i].contains("http") || !s1[i].contains("https")) // removes all links
				{
					s1[i] = s1[i].replaceAll("\\{.*\\d+.*\\}", ""); // Remove all number in brackets.
					s1[i] = s1[i].replaceAll("\\(.*\\d+.*\\)", "");
					s1[i] = s1[i].replaceAll("\\[.*\\d+.*\\]", "");
					if (s1[i].matches(".*\\d+.*")) { // if number
						s1[i] = s1[i].replaceAll("[^a-zA-Z$.#0-9:]", ""); // remove all punctuations from text

						if (option == 2 || option == 3 || option == 6) {
							if (s1[i].trim().endsWith(".")) {
								s1[i] = s1[i].substring(0, s1[i].length() - 1).trim();
							}
						}
					} else {
						if (option == 2 || option == 3 || option == 6) {
							s1[i] = s1[i].replaceAll("[^a-zA-Z]", ""); // if only text remove all punctuations
						}
					}

					if (option == 5) {
						if (StringUtils.isNumeric(s1[i])) {
							s1[i] = "";
						}
					}

					if (option == 1 || option == 3 || option == 6) {
						s1[i] = s1[i].toLowerCase(); // case folding
					}
					// If string is left or isn't in stoplist
					if (!s1[i].matches(".*\\s+.") && s1[i].toCharArray().length >= 1
							&& !set.contains(s1[i].toLowerCase())) {
						sb = sb.append(s1[i].trim() + " ");
					}

				}
			}
		}
		return sb.toString();
	}

	public void prcoessQuery(String path, int option, Set<String> stopList) throws IOException {

		// Process the queries
		String s;
		BufferedWriter wr;
		try {
			wr = new BufferedWriter(new FileWriter(Constants.QUERY_PATH + ".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader readqueries = new BufferedReader(new FileReader(path));
		StringBuilder str = new StringBuilder();
		s = readqueries.readLine();
		Tokenize t = new Tokenize();
		while (s != null) {
			str = str.append(s + System.lineSeparator());
			s = readqueries.readLine();
		}
		s = str.toString().replaceAll("[\\n\\r]+", " ");
		String s1[] = s.split("</DOC>");
		wr = new BufferedWriter(new FileWriter(Constants.QUERY_PATH + ".txt"));
		for (int i = 0; i < s1.length - 1; ++i) {
			// tokenize the queries
			s1[i] = t.tokenize(s1[i].replaceAll(".*</DOCNO>", "").trim(), option, stopList);
			if (i != s1.length - 2)
				wr.write(s1[i].trim() + System.lineSeparator());
			else {
				wr.write(s1[i].trim());
			}
		}
		wr.flush();
		System.out.println("Query Processing done");

	}

	public void processCorpus(String path, int option, Set<String> stopList) throws FileNotFoundException, IOException {
		String s;
		BufferedWriter wr;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		// Tokenize the corpus
		for (File file : listOfFiles) {
			Document doc = Jsoup.parse(file, "UTF-8");
			// t = new Tokenize();
			String fileNametxt = file.getName().replace(".html", ".txt");
			wr = new BufferedWriter(new FileWriter(Constants.TEXT_CORPUS + File.separator + fileNametxt));
			wr.write(tokenize(doc.body().text(), option, stopList));
			wr.flush();
		}
		System.out.println("Corpus processing done.");
	}

}
