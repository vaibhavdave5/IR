package com.ir.irproject.extra_credit.retrieval_module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.ir.irproject.extra_credit.POJO.QueryTermMap;
import com.ir.irproject.extra_credit.POJO.Querys;
import com.ir.irproject.extra_credit.POJO.RankedDocuments;
import com.ir.irproject.extra_credit.resource.Constants;
import com.ir.irproject.extra_credit.resource.RetrievalModel;
import com.ir.irproject.extra_credit.tools.Soundex;
import com.ir.irproject.extra_credit.tools.TextFile;
import com.ir.irproject.phase2_3.eval.EvaluationService;
import com.ir.irproject.phase2_3.text.Snippet;

public class SearchEngine {

	// Represents the retrieval model for this search engine.
	private final RetrievalModel retrievalModel;

	// QueryMap generated from the given querys
	private LinkedHashMap<String, QueryTermMap> queryMap;

	// Indexer for this search engine.
	private Indexer indexer = null;

	// Variable representing the object for BM25 retrieval model
	private BM25 bm25Model;

	// Variable representing the object for ProximityScore retrieval model
	private ProximityScoreModel proximityScoreModel;

	// Ranked documents of the search results.
	private RankedDocuments rankedDocuments;

	// Text file handler for the results of this search engine.
	private TextFile resultFile;

	private boolean displayResults = true;
	private boolean showSnippet = false;

	private EvaluationService evaluationService;
	private boolean performEvaluation = false;
	private boolean performQueryCorrection = false;

	private Snippet snippet;

	public boolean isPerformQueryCorrection() {
		return performQueryCorrection;
	}

	public void setPerformQueryCorrection(boolean performQueryCorrection) {
		this.performQueryCorrection = performQueryCorrection;
	}

	public boolean isPerformEvaluation() {
		return performEvaluation;
	}

	public void setPerformEvaluation(boolean performEvaluation) {
		this.performEvaluation = performEvaluation;
	}

	public boolean isDisplayResults() {
		return displayResults;
	}

	public void setDisplayResults(boolean displayResults) {
		this.displayResults = displayResults;
	}

	public boolean isShowSnippet() {
		return showSnippet;
	}

	public void setShowSnippet(boolean showSnippet) {
		this.showSnippet = showSnippet;
	}

	/**
	 * Getter and setter methods for Indexer.
	 */
	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	/**
	 * Constructor for this Search engine.
	 */
	public SearchEngine(RetrievalModel retrievalModel) {
		this.retrievalModel = retrievalModel;
		this.indexer = new Indexer();
		this.indexer.generate(1, true);
		this.snippet = new Snippet();
		this.evaluationService = new EvaluationService();
	}

	/**
	 * Search the documents and order them according to their BM25 score, for the given querys. Save the result in
	 * Results directory.
	 * 
	 */
	public void search(Querys querys) {

		if (isDisplayResults())
			System.out.println("\n*** Top 5 Search Results ***");

		if (getIndexer().size() != 0) {

			queryMap = querys.generate(getIndexer().getInvertedIndex());
			bm25Model = new BM25(getIndexer());
			proximityScoreModel = new ProximityScoreModel(getIndexer());

			int queryCount = 0;

			Set<String> querySet = queryMap.keySet();
			Iterator<String> iterator = querySet.iterator();

			// for each query
			while (iterator.hasNext()) {
				queryCount++;
				String queryText = iterator.next();
				String correctQuery;

				if (isPerformQueryCorrection()) {
					HashMap<String, ArrayList<String>> map = Soundex
							.generateMap(getIndexer().getInvertedIndex().keySet());
					correctQuery = Soundex.getCorrectQuery(queryText, map);
					queryText = correctQuery;
				}

				if (isDisplayResults()) {
					System.out.println();
					System.out.println("Query : " + queryText);
					System.out.println("-------------------------------");
				}

				if (queryMap.containsKey(queryText)) {
					if (this.retrievalModel.equals(RetrievalModel.BM25))
						rankedDocuments = bm25Model.getRankedDocuments(this.queryMap.get(queryText));
					else if (this.retrievalModel.equals(RetrievalModel.PROXIMITY_SCORE))
						rankedDocuments = proximityScoreModel.getRankedDocuments(queryText, queryMap.get(queryText));

					// Display the top 5 results.
					if (isDisplayResults())
						displayRankedDocuments(rankedDocuments.getTop(5));

					// Save the results.
					if (this.retrievalModel.equals(RetrievalModel.BM25)) {
						resultFile = new TextFile(String.valueOf(queryCount), Constants.BM25_FOLDER,
								Constants.EXTENSION_TXT);
					} else if (this.retrievalModel.equals(RetrievalModel.PROXIMITY_SCORE)) {
						resultFile = new TextFile(String.valueOf(queryCount), Constants.PROXIMITY_SCORE_FOLDER,
								Constants.EXTENSION_TXT);
					}

					resultFile.saveResults(rankedDocuments.getTop(100), this.retrievalModel, queryCount);
					if (isShowSnippet()) {
						snippet.saveWithRank(rankedDocuments.getTop(10), queryText, "Query" + queryCount,
								this.retrievalModel);
					}
					rankedDocuments.clear();
				}

			}

			if (!isDisplayResults())
				System.out.println(this.retrievalModel + " results generated.");

			if (isShowSnippet())
				System.out.println("Snippets generated.");

			if (isPerformEvaluation()) {
				try {
					this.evaluationService.analyze(this.retrievalModel);
				} catch (IOException e) {
					System.out.println("Evaluation failed.");
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("Inverted index not present.");
		}

	}

	/**
	 * Display the details of the ranked documents.
	 */
	private void displayRankedDocuments(RankedDocuments documents) {
		Set<String> keys = documents.keySet();
		Iterator<String> iterator = keys.iterator();
		int count = 0;
		// System.out.println(keys.size());
		while (iterator.hasNext()) {
			count++;
			String docID = iterator.next();
			System.out.println(count + ". " + docID + " - " + documents.get(docID));
		}
	}

}
