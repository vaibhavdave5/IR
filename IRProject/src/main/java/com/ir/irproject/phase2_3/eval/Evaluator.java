package com.ir.irproject.phase2_3.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.ir.irproject.extra_credit.resource.RetrievalModel;
import com.ir.irproject.extra_credit.tools.TextFile;

/**
 * The search engine evaluator service which reports the effectiveness of search results
 * @author Nicholas Carugati
 *
 */
public class Evaluator {

	/**
	 * Represents the query alias
	 */
	private String queryId;
	
	/**
	 * The precision table for the current evaluation
	 */
	private double[] precision;
	
	/**
	 * The recall table for the current evaluation
	 */
	private double[] recall;
	
	/**
	 * The documents assumed in ranked order
	 */
	private LinkedList<Integer> docs;
	
	/**
	 * The relevancy list for the corpus
	 */
	private RelevancyList rl;
	
	/**
	 * Creates a new instance of the evaluator
	 * @param queryId the alias of the query
	 * @throws IOException if file issues occur
	 */
	public Evaluator(RelevancyList rl, String queryId,RetrievalModel model) throws IOException {
		this.rl = rl;
		this.queryId = queryId;
		populateRanks(queryId,model);
		this.precision = new double[docs.size()];
		this.recall = new double[docs.size()];
	}
	
	/**
	 * Populates all of the ranks 
	 * @param queryId the query alias
	 * @param model the model being evaluated
	 * @throws IOException if file issues occur
	 */
	private void populateRanks(String queryId, RetrievalModel model) throws IOException {
		docs = new LinkedList<Integer>();
		// File file = TextFile.getFile(Constants.BM25_FOLDER + File.separator + queryId, ".txt");
		File file = TextFile.getModelQueryFile(model,queryId);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = br.readLine()) != null) {
			String[] pair = line.split("\t");
			if(pair.length != 2) {
				br.close();
				return;
			}
			pair[0] = pair[0].substring(5, pair[0].length());
			int id = Integer.parseInt(pair[0]);
			docs.add(id);
		}
		br.close();
	}
	
	/**
	 * Calculates the reciprocal ratio
	 * @return the reciprocal ratio of a specific query
	 */
	public double calculateRR() {
		int id = Integer.parseInt(queryId);
		Iterator<Integer> iter = docs.iterator();
		if(rl.getRelevancies().get(id) == null) {
			return 0;
		}
		for(int i = 0; i < precision.length; i++) {
			int docId = iter.next();
			if(rl.getRelevancies().get(id).contains(docId)) {
				return (1 / ((double) i + 1));
			}
		}
		return 1 / precision.length;
	}
	
	/**
	 * Calculates the average precision
	 * @return the mean average precision of a specific query
	 */
	public double calculateAP() {
		ArrayList<Double> relPrec = new ArrayList<Double>();
		double encounters = 0;
		int id = Integer.parseInt(queryId);
		Iterator<Integer> iter = docs.iterator();
		if(rl.getRelevancies().get(id) == null) {
			return 0;
		}
		for(int i = 0; i < precision.length; i++) {
			int docId = iter.next();
			if(rl.getRelevancies().get(id).contains(docId)) {
				encounters++;
				double ans = (double) (encounters / (i + 1));
				relPrec.add(ans);
			}
		}
		double sum = 0;
		if(encounters == 0) {
			return sum;
		}
		for(double precs : relPrec) {
			sum += precs;
		}
		return sum / encounters;
	}
	
	/**
	 * Populates the precision table
	 */
	public void calculatePrecisions() {
		double encounters = 0;
		int id = Integer.parseInt(queryId);
		Iterator<Integer> iter = docs.iterator();
		if(rl.getRelevancies().get(id) != null) {
			for(int i = 0; i < precision.length; i++) {
				int docId = iter.next();
				if(rl.getRelevancies().get(id).contains(docId)) {
					encounters++;
				}
				precision[i] = (double) (encounters / ((double) (i + 1)));
			}
		}
	}
	
	/**
	 * Populates the recall table
	 */
	public void calculateRecalls() {
		double encounters = 0;
		int id = Integer.parseInt(queryId);
		if(rl.getRelevancies().get(id) != null) {
			double relDocs = rl.getRelevancies().get(id).size();
			if(relDocs == 0) {
				return;
			}
			Iterator<Integer> iter = docs.iterator();
			for(int i = 0; i < recall.length; i++) {
				int docId = iter.next();
				if(rl.getRelevancies().get(id).contains(docId)) {
					encounters++;
				}
				recall[i] = (double) (encounters / relDocs);
			}
		}
	}
	
	/**
	 * Gets the precision at a specific rank
	 * @param i the rank to retrieve
	 * @return the precision value at rank i
	 */
	public double precisionAt(int i) {
		return precision[i];
	}
	
	/**
	 * Retrieves the precision
	 * @return the precision table
	 */
	public double[] getPrecision() {
		return precision;
	}

	/**
	 * Retrieves the recall
	 * @return the recall table
	 */
	public double[] getRecall() {
		return recall;
	}

}
