package dictionary;


import java.io.File;
import java.io.IOException;
import java.util.List;

import miscellaneous.Dictionary;
import miscellaneous.Meaning;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneDictionary implements Dictionary {
	private Directory  idxDir = null;

	public LuceneDictionary(String idxPath) throws IOException {
		idxDir = FSDirectory.open(new File(idxPath));
	}
	public List<Meaning> getMeaning(String word, String pos){

		IndexReader ireader;
		try {
			ireader = IndexReader.open(idxDir);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			// Parse a simple query that searches for "text":
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
			QueryParser parser = new QueryParser(Version.LUCENE_35, LuceneDictionaryMaker.word, analyzer);
			Query query = parser.parse(word);
			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
