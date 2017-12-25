package com.ir.irproject.phase1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.core.SimpleAnalyzer;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds files into this index
 * based on the input of the user.
 */
public class TextFileIndexer {
  private static SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_40);
  private IndexWriter writer;
  private ArrayList<File> queue = new ArrayList<File>();
  private static String filepath  ;  
  private static String path  ;
  public static void main(String[] args) throws Exception {
    System.out.println("Enter the path of the project");
    
    String indexLocation = null;
    BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));
    path = br.readLine();
    String s= path+"\\index";
    filepath = path+"\\tokens\\";
    TextFileIndexer indexer = null;
    try {
      indexLocation = s;
      indexer = new TextFileIndexer(s);
    } catch (Exception ex) {
      System.out.println("Cannot create index..." + ex.getMessage());
      System.exit(-1);
    }

    File folder = new File(filepath);
    File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
      indexer.indexFileOrDirectory(filepath+listOfFiles[i].getName());
    }
    }

    //===================================================
    //after adding, we always have to call the
    //closeIndex, otherwise the index is not created    
    //===================================================
    indexer.closeIndex();

    //=========================================================
    // Now search
    //=========================================================
    BufferedReader read = new BufferedReader(new FileReader("queries.txt"));
    int count =0;
    s= read.readLine();
    while(s!=null){
        count++;
    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
    IndexSearcher searcher = new IndexSearcher(reader);
    TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
    BufferedWriter wr =new BufferedWriter (new FileWriter(path+"\\Results1\\"+s.replaceAll("[^a-zA-Z ]", "")+".txt"));
        try{
        Query q = new QueryParser(Version.LUCENE_40, "contents", analyzer).parse(s);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
          int docId = hits[i].doc;
          Document d = searcher.doc(docId);
          //System.out.println(s);
          String[] s1 = d.get("path").split(Pattern.quote("\\"));
          wr.write(count+" Q0 "+ s1[s1.length-1].replace(".txt", "")+" "+ (i+1) + " "+ hits[i].score + " Index->Case_Folded__Punctuation_handled__Rank->Lucene"+System.lineSeparator() );
          
        }
        
        }
        catch(Exception e){
            
        }
        wr.flush();
        wr.close();
    System.out.println(s);
    s=read.readLine();
    reader.close();
    }
      System.out.println("Check the folder results 1");
  }

  /**
   * Constructor
   * @param indexDir the name of the folder in which the index should be created
   * @throws java.io.IOException when exception creating index.
   */
  TextFileIndexer(String indexDir) throws IOException {
    // the boolean true parameter means to create a new index everytime, 
    // potentially overwriting any existing files there.
    FSDirectory dir = FSDirectory.open(new File(indexDir));


    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

    writer = new IndexWriter(dir, config);
  }

  /**
   * Indexes a file or directory
   * @param fileName the name of a text file or a folder we wish to add to the index
   * @throws java.io.IOException when exception
   */
  public void indexFileOrDirectory(String fileName) throws IOException {
    //===================================================
    //gets the list of files in a folder (if user has submitted
    //the name of a folder) or gets a single file name (is user
    //has submitted only the file name) 
    //===================================================
    addFiles(new File(fileName));
    
    int originalNumDocs = writer.numDocs();
    for (File f : queue) {
      FileReader fr = null;
      try {
        Document doc = new Document();

        //===================================================
        // add contents of file
        //===================================================
        fr = new FileReader(f);
        doc.add(new TextField("contents", fr));
        doc.add(new StringField("path", f.getPath(), Field.Store.YES));
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));

        writer.addDocument(doc);
  //      System.out.println("Added: " + f);
      } catch (Exception e) {
        System.out.println("Could not add: " + f);
      } finally {
        fr.close();
      }
    }
    
    int newNumDocs = writer.numDocs();
  //  System.out.println("");
  //  System.out.println("************************");
  //  System.out.println((newNumDocs - originalNumDocs) + " documents added.");
  //  System.out.println("************************");

    queue.clear();
  }

  private void addFiles(File file) {

    if (!file.exists()) {
      System.out.println(file + " does not exist.");
    }
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        addFiles(f);
      }
    } else {
      String filename = file.getName().toLowerCase();
      //===================================================
      // Only index text files
      //===================================================
      if (filename.endsWith(".htm") || filename.endsWith(".html") || 
              filename.endsWith(".xml") || filename.endsWith(".txt")) {
        queue.add(file);
      } else {
        System.out.println("Skipped " + filename);
      }
    }
  }

  /**
   * Close the index.
   * @throws java.io.IOException when exception closing
   */
  public void closeIndex() throws IOException {
    writer.close();
  }
}