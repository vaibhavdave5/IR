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
 * @author Vaibhav
 */
public class CustomRetrieval {
	// Start time of the execution
	private static long startTime = System.currentTimeMillis();
	// Maximum count of results to be generated for a query.
	private static final int maxSearchResultCount = 1000;

	// Maximum count of results to be saved in the output file for a query.
	private static final int noOfResults = 100;

	// Maximum count of results to be displayed for a query.
	private static final int displayResultsCount = 5;

	public void execute() throws IOException, Exception {

		BufferedReader read;
		BufferedWriter wr;
		// Get all files from the corpus:
		

		// Get the stoplist
		Set<String> stopList = new HashSet<String>();
		read = new BufferedReader(new FileReader(Constants.stopList));
		String s = read.readLine();
		while (s != null) {
			stopList.add(s);
			s = read.readLine();
		}
		System.out.println("Stoplist generated");

		File folder = new File(Constants.RAW_CORPUS);
		File[] listOfFiles = folder.listFiles();
		// Tokenize the corpus
		for (File file : listOfFiles) {
			Document doc = Jsoup.parse(file, "UTF-8");
			Tokenize t = new Tokenize();
			String fileNametxt = file.getName().replace(".html", ".txt");
			wr = new BufferedWriter(new FileWriter(Constants.TEXT_CORPUS + File.separator + fileNametxt));
			wr.write(doc.body().text());
			wr.flush();
		}
		System.out.println("Document processing done");

		// Process the queries
        BufferedReader readqueries = new BufferedReader(new FileReader(Constants.RAW_QUERIES));
        StringBuilder str = new StringBuilder();
        s = readqueries.readLine();
        Tokenize t =new Tokenize();
        while(s!=null){
            str = str.append(s+System.lineSeparator());
            s=readqueries.readLine();            
        }
        s = str.toString().replaceAll("[\\n\\r]+", " ");
        String s1[] = s.split("</DOC>");
        wr = new BufferedWriter(new FileWriter(Constants.QUERY_PATH+".txt"));
        for(int i =0; i < s1.length-1; ++i){
            // tokenize the queries
            s1[i] =t.tokenize(s1[i].replaceAll(".*</DOCNO>", "").trim(), 3, stopList);
            if(i != s1.length-2)
            wr.write(s1[i].trim()+System.lineSeparator());
            else{
            wr.write(s1[i].trim());
            }
        }
        wr.flush();
        System.out.println("Query Processing done");        
 
		displayTime();

		/**
		 * @author Divyavijay Sahay
		 */
		//Clear the index for generating new index.
		FileUtils.cleanDirectory(new File(Constants.BM25_Index)); 
		// Querys containing all the query present in the file at the location
		// specified by the Query_path constant.
		Querys querys = new Querys(Constants.QUERY_PATH);
		
		System.out.println("\nTFIDF started.");
		runSearchEngine(querys, RetrievalModel.BM25);
		System.out.println("BM25 done");
		
		
		// Tokenize the corpus
		for (File file : listOfFiles) {
			Document doc = Jsoup.parse(file, "UTF-8");
			t = new Tokenize();
			String fileNametxt = file.getName().replace(".html", ".txt");
			wr = new BufferedWriter(new FileWriter(Constants.TEXT_CORPUS + File.separator + fileNametxt));
			wr.write(t.tokenize(doc.body().text(), 3, stopList));
			wr.flush();
		}
		
//		// Index
//		new Indexing().generate();
				
		System.out.println("TfIDf started");
		new TfIdf().generate();
		System.out.println("TFIDF done");
		displayTime();

		System.out.println("Lucene started");
		new Lucene().generate();
		System.out.println("Lucene done");
		displayTime();
		
		System.out.println("Smoothered Query Model started");
		new SmQuery().generate();
		System.out.println("Smoothered Query Model done");
		displayTime();
		read.close();
		readqueries.close();
		

		
	}

	private static void runSearchEngine(Querys querys, RetrievalModel model) {
		SearchEngine searchEngine = new SearchEngine(model);
		searchEngine.setDisplayResults(false);
		searchEngine.setPerformEvaluation(true);
		searchEngine.setShowSnippet(true);
		searchEngine.search(querys);

		displayTime();
	}

	/**
	 * Display the execution time till present.
	 * 
	 * @author Divyavijay Sahay
	 */
	private static void displayTime() {
		System.out.println();
		System.out.println("Execution time: " + ((long) System.currentTimeMillis() - startTime) / 1000f + " sec");
	}
}
