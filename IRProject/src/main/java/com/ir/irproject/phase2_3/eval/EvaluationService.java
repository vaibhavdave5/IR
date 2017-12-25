package com.ir.irproject.phase2_3.eval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.ir.irproject.extra_credit.resource.Constants;
import com.ir.irproject.extra_credit.resource.RetrievalModel;
import com.ir.irproject.extra_credit.tools.TextFile;

/**
 * Represents a single evaluation service
 * 
 * @author Nicholas Carugati
 *
 */
public class EvaluationService {

	/**
	 * The list of mean precision ranks collected
	 */
	private ArrayList<Double> precisionRanks;

	/**
	 * The list of reciprocoal ranks collected
	 */
	private ArrayList<Double> reciprocalRanks;

	/**
	 * The relevancy list of the corpus
	 */
	private RelevancyList list;

	/**
	 * Creates a new evaluation service instance
	 */
	public EvaluationService() {
		try {
			this.list = new RelevancyList();
			this.precisionRanks = new ArrayList<Double>();
			this.reciprocalRanks = new ArrayList<Double>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Performs an analysis on evaluation
	 * 
	 * @param model
	 * @throws IOException
	 *             if there are any file issues
	 */
	public void analyze(RetrievalModel model) throws IOException {
		File[] files = TextFile.getFiles(model);

		LinkedHashMap<String, Evaluator> data = new LinkedHashMap<String, Evaluator>();
		ArrayList<Integer> sortedFiles = new ArrayList<Integer>();
		for (File f : files) {
			int id = Integer.parseInt(f.getName().split(Constants.EXTENSION_TXT)[0]);
			sortedFiles.add(id);
		}
		Collections.sort(sortedFiles);
		for (int id : sortedFiles) {
			String sid = "" + id;
			if (list.contains(id)) {
				Evaluator eval = new Evaluator(list, sid, model);
				eval.calculatePrecisions();
				eval.calculateRecalls();
				precisionRanks.add(eval.calculateAP());
				reciprocalRanks.add(eval.calculateRR());
				data.put(sid, eval);
			}
		}
		saveAnalysis(data, model);
	}

	/**
	 * Saves the search engine analysis
	 * 
	 * @param data
	 *            the evaluation data
	 * @param model
	 *            the model for which evaluation is running
	 */
	private void saveAnalysis(LinkedHashMap<String, Evaluator> data, RetrievalModel model) {
		StringBuilder sb = new StringBuilder();
		sb.append("MAP: " + calculateMAP()).append("\n");
		sb.append("MRR: " + calculateMRR()).append("\n\n");
		for (String id : data.keySet()) {
			Evaluator ev = data.get(id);
			double[] precPair = new double[2];
			sb.append("Query ID: " + id + "\n");
			sb.append(addSeparator());
			precPair[0] = ev.precisionAt(5);
			precPair[1] = ev.precisionAt(20);
			sb.append("P@5 = " + precPair[0]).append("\n");
			sb.append("P@20 = " + precPair[1]).append("\n");
			sb.append("Precision: " + Arrays.toString(ev.getPrecision())).append("\n");
			sb.append("Recall: " + Arrays.toString(ev.getRecall())).append("\n");
			sb.append(addSeparator());
			sb.append("\n");
		}
		String path = Constants.EVALUATION_RESULTS + File.separator + "analysis";
		TextFile t = new TextFile(model + "_analysis", Constants.EVALUATION_RESULTS, Constants.EXTENSION_TXT);
		t.saveFile(path, sb.toString());

		System.out.println("Evaluation generated for " + model + ".");
	}

	/**
	 * A utility method to add separators
	 * 
	 * @return a visual file separator
	 */
	private static String addSeparator() {
		char[] line = new char[64];
		Arrays.fill(line, '-');
		return new String(line).concat("\n");
	}

	/**
	 * Calculates the mean reciprocal rank for the evaluation session
	 * 
	 * @return the mean reciprocal rank of all the executed queries
	 */
	public double calculateMRR() {
		double sum = 0;
		for (double d : reciprocalRanks) {
			sum += d;
		}
		return sum / reciprocalRanks.size();
	}

	/**
	 * Calculates the mean average precision for the evaluation session
	 * 
	 * @return the mean average precision of all the executed queries
	 */
	public double calculateMAP() {
		double sum = 0;
		for (double d : precisionRanks) {
			sum += d;
		}
		return sum / precisionRanks.size();
	}

}