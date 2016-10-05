package miscellaneous;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.GateConstants;
import gate.Utils;
import gate.creole.ResourceInstantiationException;
import gate.util.ExtensionFileFilter;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LongmanDictionary implements Dictionary {
	
	public void makeDic(String dicFolder, String idxPath) throws ResourceInstantiationException, IOException{
		//initiate for Indexing in lucene
		Directory idxDir = FSDirectory.open(new File(idxPath));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		iwc.setRAMBufferSizeMB(256.0);
		IndexWriter writer = new IndexWriter(idxDir, iwc);

		
		//give files of folder to Gate one by one
		Corpus corpus = Factory.newCorpus("DictionaryCorpus");
		ExtensionFileFilter filter = new ExtensionFileFilter("Html Files", "html");
		URL url = (new File(dicFolder)).toURI().toURL();
		corpus.populate(url, filter, null, false);
		
		Iterator<Document> iterDoc = corpus.iterator();
		while(iterDoc.hasNext()){
			Document doc = iterDoc.next();
			HtmlParser parser = new HtmlParser(doc);
			
			String headWord = parser.getHeadWord(); 
			ArrayList<String> shortDefList = parser.getShortDefinition();
			ArrayList<String> definitionList = parser.getDefinition();
			ArrayList<String> exampleList = parser.getexample();
			String pos = parser.getPos();
			
			//now I have to index annotation in the doc in the lucene
			org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
			if(headWord != null && shortDefList.get(0) != null && definitionList.get(0) != null && exampleList.get(0) != null){
				docLucene.add(new Field("word", headWord, Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field("shortDef", shortDefList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field("longDef", definitionList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field("example", exampleList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field("pos", pos, Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(docLucene);
			}
		}
		writer.close();
	
		
	}
	
	@Override
	public List<Meaning> getMeaning(String word, String pos) {
		
		
		
		return null;
	}
	
	public void dicInit(){
		
	}
	
	public static void main(String[] args) throws IOException, GateException {
		Gate.init();
		LongmanDictionary longmanDic = new LongmanDictionary();
		String filesPath = "/Users/nasrin/Documents/LearnGate/dictionary_test";
		String indexPath = "/Users/nasrin/Documents/LearnGate/eclipse/HarryNewWords/ldoceIndexed";
//		longmanDic.indexDictionaryFiles(filesPath, indexPath);
		longmanDic.makeDic(filesPath, indexPath);
		System.out.println("LongmanDictionary.main()");
	}

}
