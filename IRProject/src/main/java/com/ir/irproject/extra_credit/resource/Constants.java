package com.ir.irproject.extra_credit.resource;

import java.io.File;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class Constants {

	// Document constants
	public static final String DOCUMENT_CONTENT = "content";
	public static final String DOCUMENT_NAME = "name";

	// path constants
	public static String INDEXER_OUTPUT_PATH = "output" + File.separator + "Indexer";
	public static String INDEXER_DATA_PATH = "input" + File.separator + "Corpus";
	public static String Data_Source = "input" + File.separator + "DataSource";
	public static String INDEXER_TEST_DATA_PATH = "input" + File.separator + "test_Corpus";
	public static String QUERY_PATH = "input" + File.separator + "Query" + File.separator + "queries";
	public static String TEST_QUERY_PATH = "input" + File.separator + "Query" + File.separator + "test_queries";
	public static String RESULTS_DIRECTORY = "output" + File.separator + "Results";
	public static String WIKIPEDIA_CORPUS_PARSED = "input" + File.separator + "W-Corpus" + File.separator + "PARSED";
	public static String WIKIPEDIA_CORPUS_RAW = "input" + File.separator + "W-Corpus" + File.separator + "RAW";
	public static String SNIPPET_RESULTS = "output" + File.separator + "Snippet";
	public static String STEMMED_CORPUS_PATH = "input" + File.separator + "Corpus";
	public static String EVALUATION_RESULTS = "output" + File.separator + "Evaluation";
	public static String BM25_Index = "Index";

	// Lucene constants
	public static final Version VERSION = Version.LUCENE_47;
	public static final SimpleAnalyzer ANALYZER = new SimpleAnalyzer(VERSION);
	public static final IndexWriterConfig CONFIG = new IndexWriterConfig(Constants.VERSION, Constants.ANALYZER);
	public static final QueryParser QUERY_PARSER = new QueryParser(VERSION, DOCUMENT_CONTENT, ANALYZER);

	// File constants
	public static final String EXTENSION_TXT = ".txt";

	// Vaibhav
	public static final String BM25_FOLDER = "output" + File.separator + "BM25";

	public static final String SMQ_FOLDER = "output" + File.separator + "SMQ";

	public static final String TFIDF_FOLDER = "output" + File.separator + "TFIDF";

	public static String TEXT_CORPUS = "input" + File.separator + "TokenizedCorpus";

	public static final String PRF_FOLDER = "output" + File.separator + "PRF";
	
	public static final String PROXIMITY_SCORE_FOLDER = "output" + File.separator + "Proximity_Score";
	
	public static final String RAW_CORPUS = "input" + File.separator + "DataSource";

	public static final String RAW_QUERIES = "input" + File.separator + "Query" + File.separator + "raw_queries.txt";

	public static final String Index_Location = "input" + File.separator + "index.txt";

	public static final String stopList = "input" + File.separator + "common_words";

	public static final String Lucene_Index = "input" + File.separator + "lucene_index";

	public static final String Lucene_Results = "output" + File.separator + "Lucene";

	public static final String PRF_Output = "output" + File.separator + "PRF_Frequent_Terms";
	public static final String LUCENE_FOLDER = "output" + File.separator + "Lucene";

	public static final String RELEVANCY_FOLDER = "input" + File.separator + "Relevancy" + File.separator + "cacm.rel";

	public static final String RELEVANCY_RESULTS = "output" + File.separator + "Relevancy";
	public static final String Stemmed_CORPUS = "input" + File.separator + "StemmedCorpus";

}
