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
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ir.irproject.extra_credit.POJO.Querys;
import com.ir.irproject.extra_credit.resource.Constants;
import com.ir.irproject.extra_credit.resource.RetrievalModel;
import com.ir.irproject.extra_credit.retrieval_module.SearchEngine;

/**
 *
 * @author vaibhav
 */
public class Task3 {

	public void execute() throws FileNotFoundException, IOException, Exception {
		int option = -1;
		

			System.out.println("Insert 1 to implement Task 3-A " + System.lineSeparator() + "Or 2 to implement Task 3-B"
					+ System.lineSeparator() + "3 to exit");

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			option = Integer.parseInt(input.readLine());

			
			
			
			
			if (option == 1) {
				BufferedReader read;
				BufferedWriter wr;
				// Get all files from the corpus:
				File folder = new File(Constants.RAW_CORPUS);
				File[] listOfFiles = folder.listFiles();

				// Get the stoplist
				Set<String> stopList = new HashSet<String>();
				read = new BufferedReader(new FileReader(Constants.stopList));
				String s = read.readLine();
				while (s != null) {
					stopList.add(s);
					s = read.readLine();
				}
				read.close();
				System.out.println("Stoplist generated");

				// Tokenize the corpus
				for (File file : listOfFiles) {
					Document doc = Jsoup.parse(file, "UTF-8");
					Tokenize t = new Tokenize();
					String fileNametxt = file.getName().replace(".html", ".txt");
					wr = new BufferedWriter(new FileWriter(Constants.TEXT_CORPUS + File.separator + fileNametxt));
					wr.write(t.tokenize(doc.body().text() + " " + file.getName().replace(".html", ""), 4, stopList));
					wr.flush();
					wr.close();
				}
				System.out.println("Document processing done");

				// Process the queries
				BufferedReader readqueries = new BufferedReader(new FileReader(Constants.RAW_QUERIES));
				StringBuilder str = new StringBuilder();
				s = readqueries.readLine();
				Tokenize t = new Tokenize();
				while (s != null) {
					str = str.append(s + System.lineSeparator());
					s = readqueries.readLine();
				}
				readqueries.close();
				
				s = str.toString().replaceAll("[\\n\\r]+", " ");
				String s1[] = s.split("</DOC>");
				wr = new BufferedWriter(new FileWriter(Constants.QUERY_PATH + ".txt"));
				for (int i = 0; i < s1.length; ++i) {
					// tokenize the queries
					s1[i] = t.tokenize(s1[i].replaceAll(".*</DOCNO>", "").trim(), 4, stopList);
					if(i != s1.length-2)
					wr.write(s1[i].trim()+System.lineSeparator());
		            else{
		            wr.write(s1[i].trim());
		            }
					
				}
				wr.flush();
				wr.close();
				System.out.println("Query Processing done");

				// Index
				new Indexing().generate();
				System.out.println("indexing done");

				// scoring:

				new TfIdf().generate();
				System.out.println("TFIDF done");
								
				wr = new BufferedWriter(new FileWriter(Constants.QUERY_PATH + ".txt"));
				for (int i = 0; i < s1.length; ++i) {
					// tokenize the queries
					s1[i] = t.tokenize(s1[i].replaceAll(".*</DOCNO>", "").trim(), 3, stopList);
					if(i != s1.length-2)
					wr.write(s1[i].trim()+System.lineSeparator());
		            else{
		            wr.write(s1[i].trim());
		            }
					
				}
				
				wr.flush();
				
				for (File file : listOfFiles) {
					Document doc = Jsoup.parse(file, "UTF-8");
					t = new Tokenize();
					String fileNametxt = file.getName().replace(".html", ".txt");
					wr = new BufferedWriter(new FileWriter(Constants.TEXT_CORPUS + File.separator + fileNametxt));
					wr.write(t.tokenize(doc.body().text() + " " + file.getName().replace(".html", ""), 3, stopList));
					wr.flush();				
				}
								
				
				
				wr.close();
				
				new Lucene().generate();
				System.out.println("Lucene done");

				
				/**
				 * @author Divyavijay Sahay
				 */
				// Querys containing all the query present in the file at the
				// location
				// specified by the Query_path constant.
				FileUtils.cleanDirectory(new File(Constants.BM25_Index)); 
				Querys querys = new Querys(Constants.QUERY_PATH);

				runSearchEngine(querys, RetrievalModel.BM25);
				System.out.println("BM25 done");
				
				System.exit(0);
								
			}

			else if (option == 2) {
				FileUtils.cleanDirectory(new File(Constants.BM25_Index)); 
				// Index
				BufferedReader read;
				// Get all files from the corpus:
				File folder = new File(Constants.RAW_CORPUS);
				File[] listOfFiles = folder.listFiles();

				// Get the stoplist
				Set<String> stopList = new HashSet<String>();
				read = new BufferedReader(new FileReader(Constants.stopList));
				String s = read.readLine();
				while (s != null) {
					stopList.add(s);
					s = read.readLine();
				}
				System.out.println("Stoplist generated");
				String temp = Constants.TEXT_CORPUS;
				String temp2 = Constants.QUERY_PATH;
				Constants.TEXT_CORPUS = "input" + File.separator + "StemmedCorpus";
				Constants.QUERY_PATH = "input" + File.separator + "Query"+ File.separator + "StemmedQueries";
				System.out.println("Document processing done");

				
				FileUtils.cleanDirectory(new File(Constants.BM25_FOLDER));
				FileUtils.cleanDirectory(new File(Constants.LUCENE_FOLDER));
				FileUtils.cleanDirectory(new File(Constants.TFIDF_FOLDER)); 
				// Index
				new Indexing().generate();
				System.out.println("indexing done");

				// scoring:

				new TfIdf(false).generate();
				System.out.println("TFIDF done");
				new Lucene(false).generate();
				System.out.println("Lucene done");

				/**
				 * @author Divyavijay Sahay
				 */
				// Querys containing all the query present in the file at the
				// location
				// specified by the Query_path constant.
				FileUtils.cleanDirectory(new File(Constants.BM25_Index)); 
				Querys querys = new Querys(Constants.QUERY_PATH);

				runSearchEngine(querys, RetrievalModel.BM25);
				System.out.println("BM25 done");
				
				
				System.out.println("************************"+System.lineSeparator());				
				Constants.TEXT_CORPUS = temp;
				Constants.QUERY_PATH = temp2;
				new Indexing().generate();
				read.close();
				
				System.exit(0);
			}
		
	}

	private static void runSearchEngine(Querys querys, RetrievalModel model) {
		SearchEngine searchEngine = new SearchEngine(model);
		searchEngine.setDisplayResults(false);
		searchEngine.setPerformEvaluation(true);
		searchEngine.search(querys);

	}

}
