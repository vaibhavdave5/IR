package com.ir.irproject.phase2_3.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.ir.irproject.extra_credit.POJO.RankedDocuments;
import com.ir.irproject.extra_credit.resource.Constants;
import com.ir.irproject.extra_credit.resource.RetrievalModel;
import com.ir.irproject.extra_credit.tools.TextFile;

/**
 * Represents an instance of a search engine result summary
 * 
 * @author Nicholas Carugati
 */
public class Snippet {

	/**
	 * Symbol constant to denote a elipsis delimiter
	 */
	public static final String ELIPSIS = " ... ";

	/**
	 * The Oxford word threshold constant: A sentence should be at most 20 words
	 */
	public static final int OXFORD_WORD_THRESHOLD = 20;

	/**
	 * The minimal word threshold to accept in a snippet
	 */
	public static final int MIN_SENTENCE_THRESHOLD = 7;

	/**
	 * If a sentence is too short it will expand by a certain amount of words
	 */
	public static final int EXPAND_INCREMENT = 5;

	/**
	 * The window size of this snippet instance
	 */
	private int windowSize;

	/**
	 * The current relevance counter of this given snippet
	 */
	private double relevanceCounter;

	/**
	 * The list of significance factor ranks from the previous snippet creation execution
	 */
	private LinkedHashMap<String[], Double> ranks = new LinkedHashMap<String[], Double>();

	/**
	 * Stores the phrases and their positions within the content
	 */
	private ArrayList<String[]> phrases = new ArrayList<String[]>();

	/**
	 * Creates a new instance of the snippet
	 */
	public Snippet() {
		this.relevanceCounter = 0.0;
		this.windowSize = OXFORD_WORD_THRESHOLD;
	}

	/**
	 * Generates a snippet for a particular document for a particular query
	 * 
	 * @param doc
	 *            the document id
	 * @param query
	 *            the query and its terms
	 * @throws IOException
	 *             if any file operation issues occur
	 */
	public String summary(String doc, String query) throws IOException {
		HashSet<String> terms = new HashSet<String>();
		for (String s : query.split(" ")) {
			terms.add(s);
		}
		String sep = File.separator;
		File file = TextFile.getFile(Constants.INDEXER_DATA_PATH + sep + doc, ".txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String[] content = br.readLine().split(" ");
		br.close();
		String[] phrase = new String[windowSize];
		int winStart = -1;
		int offset = -1;
		for (int i = 0; i < content.length; i++) {
			int delta = winStart - offset;
			boolean sentenceComplete = sentenceEnded(content[i].charAt(content[i].length() - 1));
			boolean finishPoint = i == content.length - 1 || delta == phrase.length || sentenceComplete;
			if (winStart == -1) {
				if (Character.isUpperCase(content[i].charAt(0)) || isRelevant(terms, content[i])) {
					relevanceCounter = 0.0;
					if (isRelevant(terms, content[i])) {
						content[i] = "<hl>" + content[i] + "</hl>";
						++relevanceCounter;
					}
					phrase = new String[windowSize];
					winStart = i;
					offset = i;
					phrase[delta] = content[i];
					continue;
				}
			} else {
				if (finishPoint) {
					if (i == content.length - 1) {
						phrase = Arrays.copyOfRange(phrase, 0, i - winStart);
						phrase[delta] = content[i];
						createSentence(phrase, winStart, i);
						break;
					} else if (delta == phrase.length) {
						createPsuedoSentence(phrase);
					} else if (sentenceComplete) {
						int didx = i - winStart;
						if (didx <= MIN_SENTENCE_THRESHOLD) {
							int dsw = winStart - EXPAND_INCREMENT <= 0 ? 0 : winStart - EXPAND_INCREMENT;
							int dew = i + EXPAND_INCREMENT >= content.length ? content.length - 1
									: i + EXPAND_INCREMENT;
							String[] expand = Arrays.copyOfRange(content, dsw, dew);
							expand = calculateRelevance(terms, expand);
							createSentence(expand, 0, expand.length);
						} else {
							phrase[delta] = content[i];
							createSentence(phrase, winStart, i);
						}
					}
					phrase = new String[windowSize];
					winStart = -1;
					offset = -1;
					continue;
				}
				if (isRelevant(terms, content[i])) {
					content[i] = "<hl>" + content[i] + "</hl>";
					++relevanceCounter;
				}
				if (!content[i].isEmpty()) {
					phrase[delta] = content[i];
					--offset;
				}
			}
		}
		ranks = sorted(ranks);
		Iterator<String[]> it = ranks.keySet().iterator();
		String[] curr = it.next();
		StringBuilder snippet = new StringBuilder();
		String[] prev = curr;
		for (int i = 0; it.hasNext() && i < 5; curr = it.next(), i++) {
			if (ranks.get(curr) <= 0) {
				break;
			}
			String s = String.join(" ", curr);
			if (isContiguous(prev, curr) || snippet.length() == 0) {
				snippet.append(' ').append(s);
			} else {
				snippet.append(ELIPSIS).append(s);
			}
			prev = curr;
		}
		StringBuilder str = new StringBuilder(doc);
		char[] line = new char[64];
		Arrays.fill(line, '-');
		str.append("\n").append(line).append("\n");
		str.append(WordUtils.wrap(snippet.toString().trim(), 64) + "\n\n");
		return str.toString();
	}

	/**
	 * Creates a regular sentence
	 * 
	 * @param phrase
	 *            the phase to compress and add
	 * @param start
	 *            the starting index of the sentence
	 * @param end
	 *            the ending index of the sentence
	 */
	private void createSentence(String[] phrase, int start, int end) {
		double rank = (relevanceCounter * relevanceCounter) / ((double) end - start);
		phrase = Arrays.copyOfRange(phrase, 0, end - start);
		phrases.add(phrase);
		ranks.put(phrase, rank);
	}

	/**
	 * Creates a phrase that may be a sentence but is too long to fit in a snipper
	 * 
	 * @param phrase
	 *            the phrase to compress and add
	 */
	private void createPsuedoSentence(String[] phrase) {
		double rank = (relevanceCounter * relevanceCounter) / ((double) windowSize);
		phrases.add(phrase);
		ranks.put(phrase, rank);
	}

	/**
	 * Calculates the relevance for a single phrase
	 * 
	 * @param relevants
	 *            the relevant terms to search for
	 * @param phrase
	 *            the phrase to search for relevant terms in
	 * @return the phrase with the relevant words highlighted in tags
	 */
	private String[] calculateRelevance(HashSet<String> relevants, String[] phrase) {
		int rel = 0;
		for (int i = 0; i < phrase.length; i++) {
			if (isRelevant(relevants, phrase[i])) {
				phrase[i] = "<hl>" + phrase[i] + "</hl>";
				rel++;
			}
		}
		relevanceCounter = rel;
		return phrase;
	}

	/**
	 * Determines if a sentence ended
	 * 
	 * @param c
	 *            the character of the word
	 * @return if the character is a punctuation character that ends a sentence
	 */
	private boolean sentenceEnded(char c) {
		return c == '.' || c == '!' || c == '?';
	}

	/**
	 * Sorts the significance factor for each sentence
	 * 
	 * @param ranks2
	 *            the rankings from the snippet processor
	 * @return the sorted rankings for each sentence
	 */
	private LinkedHashMap<String[], Double> sorted(LinkedHashMap<String[], Double> ranks2) {
		List<Map.Entry<String[], Double>> indexEntry = new LinkedList<Map.Entry<String[], Double>>(ranks2.entrySet());
		Collections.sort(indexEntry, new Comparator<Map.Entry<String[], Double>>() {
			public int compare(Map.Entry<String[], Double> v2, Map.Entry<String[], Double> v1) {
				return (v1.getValue()).compareTo(v2.getValue());
			}
		});
		LinkedHashMap<String[], Double> sortedTable = new LinkedHashMap<String[], Double>();
		for (Map.Entry<String[], Double> entry : indexEntry) {
			sortedTable.put(entry.getKey(), entry.getValue());
		}
		return sortedTable;
	}

	/**
	 * Determines if a specific word is relevant according to the query
	 * 
	 * @param query
	 *            the query and it terms
	 * @param word
	 *            the word in question
	 * @return if the word matches any of the query terms
	 */
	private boolean isRelevant(HashSet<String> relevants, String word) {
		String w = word.toLowerCase();
		return relevants.contains(w);
	}

	/**
	 * Determines if a phrase is adjacent to another phrase
	 * 
	 * @param p1
	 *            the first phrase
	 * @param p2
	 *            the second phrase
	 * @return if the phrases are next to eachother
	 */
	private boolean isContiguous(String[] p1, String[] p2) {
		int index = phrases.indexOf(p1);
		if (index == -1 || index + 1 == phrases.size()) {
			return false;
		}

		return p2.equals(phrases.get(index + 1));
	}

	/**
	 * Saves a snippet summary in the order of the container
	 * 
	 * @param fname
	 *            the name of the file to save
	 */
	public void saveWithRank(RankedDocuments rd, String query, String fname, RetrievalModel model) {
		StringBuilder sb = new StringBuilder();
		try {
			for (String docs : rd.keySet()) {
				sb.append(summary(docs, query));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String path = Constants.SNIPPET_RESULTS + File.separator + fname;
		TextFile t = new TextFile(fname, Constants.SNIPPET_RESULTS + File.separator + model, Constants.EXTENSION_TXT);
		t.saveFile(path, sb.toString());
	}

}