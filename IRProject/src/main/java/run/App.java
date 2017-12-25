package run;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.ir.irproject.phase1.CustomRetrieval;
import com.ir.irproject.phase1.PRF;
import com.ir.irproject.phase1.Task3;

public class App {

	public static void main(String[] args) throws Exception {

		BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;

		System.out.println("To Execute task 1 enter 1" + System.lineSeparator() + "To Execute task 2 enter 2"
				+ System.lineSeparator() + "To Execute task 3 enter 3" + System.lineSeparator());

		option = Integer.parseInt(read.readLine());

		if (option == 1) {
			CustomRetrieval c = new CustomRetrieval();
			c.execute();

			System.out.println("Check the folder BM25, SMQ TFIDF AND LUCENE IN THE OUTPUT FOLDER FOR RESULTS"+System.lineSeparator()+
								"We've implemented snippets using BM25 : Check folder Output->Snippets->BM25 for results");

			System.out.println("Check the folder Output -> Evaluation for Evaluation of Each of the base runs");
		}

		else if (option == 2) {
			PRF p = new PRF();
			p.execute();

			System.out.println(
					"Check the folder PRF_FREQUENT_TERMS FOLDER TO FIND THE MOST FREQUENT WORDS PER QUERY FROM RELEVANT DOCUMENTS"
							+ System.lineSeparator()
							+ "Check the folder PRFLUCENERESULTS FOLDER TO FIND THE RESULTS AFTER QUERY EXPANSION"
							+ System.lineSeparator()
							+ "Check the file PRFEXPANDEDQUERIES FOLDER TO FIND THE QUERY AFTER QUERY EXPANSION"
							+ System.lineSeparator() + "ALL THE ABOVE FOLDERS CAN BE FOUND IN THE OUTPUT FOLDER");

		}

		else if (option == 3) {

			System.out.println(
					"After the execution is completed \nCheck the folder BM25, TFIDF AND LUCENE IN THE OUTPUT FOLDER FOR RESULTS");
			Task3 p = new Task3();
			p.execute();

		}

		
	}
}
