************************************************************
HOW TO RUN THE PROGRAM
************************************************************

1) extract the project source code at any location in your
computer.

2) Open Eclipse.

3) Go to : File -> Import

4) Go to : Maven -> Existing Maven Project and Click Next

5) Click on "Browse" and select the extracted project source 
code.

6) Select the "pom" check box, under projects section and 
click Finish.

7) Make sure the maven project is build if not done by
the eclipse on its on.
*in case build is required. Follow these 2 steps:
--Right click on pom.xml
--select maven build (2nd option)

8) Add all the html/raw documents in the corpus directory located at:
{project folder}/input/DataSource

9) Add the raw query strings in the the query file, located at:
{project folder}/input/Query/raw_queries.txt

10) Open the App.java file under "run" package (ie. IRProject\src\main\java\run)

11) Execute the program.

------------Extra Credit--------------
12) For executing Extra Credit - Proximity score model
Open the App.java under package "com.ir.irproject.extra_credit"

13) For exeucting:
No Stopping run : Change the value of boolean variable "runWithStopping" to "false"
With Stopping run : Change the value of boolean variable "runWithStopping" to "true"

14) Check the results for each run:
Evaluation - check the folder (output/Evaluation/PROXIMITY_SCORE_analysis.txt)
Scores for each query - check the folder (output/Proximity_Score)
Snippet for each query - check the folder (output/Snippet/PROXIMITY_SCORE)
--------------------------------------

Note: in App.java under package "com.ir.irproject.extra_credit"

Opting for option 1 does the following:
Implements the four baseline runs of phase1 task1 
    Results can be found in the output folder namely BM25 SMQ Lucene TFIDF and the results are in the format <docID> <score>
Implements evaluation on the four baseline runs
    Results can be found in the output folder -> Evaluation
    Check the BM25_Analysis , SMQ_Analysis , TFIDF_Analysis , LUCENE_analysis.
Implements Snippet generation on BM25 Results
    Snippets can be found in output -> Snippet -> BM25 folder

Opting for option 2 does the following:
Implements the one run of phase1 task2 
    Results can be found in the output folder namely PRF_Lucene_Results in the format <docID> <score>
    output folder also contains the expanded queries in the file namely PRFExpandedQueries.txt
    The frequent words from top 30 document of each query can be found in the folder output -> PRF_Frequent_Terms
    Evaluation of the PRF can be found in the output ->Evaluation folder -> PRF_Analysis.txt

Opting for option 3 does the following:
    Opting for option 1 in 3 will perform Lucene , BM25 and TFIDF models on stopped corpus.
            Results can be found in the output folder namely BM25 Lucene TFIDF and the results are in the format <docID> <score>
        Implements evaluation on the three runs
            Results can be found in the output folder -> Evaluation
            Check the BM25_Analysis.txt , TFIDF_Analysis.txt , LUCENE_analysis.txt


    Opting for option 2 in 3 will perform Lucene , BM25 and TFIDF models on stemmed corpus.
            Results can be found in the output folder namely BM25 Lucene TFIDF and the results are in the format <docID> <score>
        Discussion between the three results is reported in the project report.
        
