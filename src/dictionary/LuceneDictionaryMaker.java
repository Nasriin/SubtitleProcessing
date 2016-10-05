package dictionary;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import miscellaneous.HtmlParser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneDictionaryMaker {
	public static final String word = "WORD";
	public static final String pos = "POS";
	public static final String shortDef = "SHORTDEFINION";
	public static final String definition = "DEFINITION";
	public static final String example = "EXAMPLE";
	
	private IndexWriter writer = null;
	private String dicFolder;
	
	public LuceneDictionaryMaker(String dicFolder, String idxPath) throws CorruptIndexException, LockObtainFailedException, IOException {
		this.dicFolder = dicFolder;
		init(idxPath);
	}
	private void init(String idxPath) throws CorruptIndexException, LockObtainFailedException, IOException {
		//initiate for Indexing in lucene
		Directory idxDir = FSDirectory.open(new File(idxPath));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		iwc.setRAMBufferSizeMB(256.0);
		writer = new IndexWriter(idxDir, iwc);
		
	}
	public void makeDic() throws ResourceInstantiationException, IOException{

		//give files of folder to Gate one by one
//		Corpus corpus = Factory.newCorpus("DictionaryCorpus");
//		ExtensionFileFilter filter = new ExtensionFileFilter("Html Files", "html");
//		URL url = (new File(dicFolder)).toURI().toURL();
//		corpus.populate(url, filter, null, false);
		
		File folder = new File(dicFolder);
		String[] htmlFiles = folder.list();
		
		for(int i = 0; i < htmlFiles.length; i++){
			
			if(!htmlFiles[i].endsWith("html")){
				System.out.println("not html file in folder: " + htmlFiles[i]);
				continue;
			}

			FeatureMap params = Factory.newFeatureMap();
			URL url = new URL("file:" + dicFolder + "/" + htmlFiles[i]);
			System.out.println(url);
			params.put("sourceUrl", url);
			params.put("preserveOriginalContent", new Boolean(true));
			params.put("collectRepositioningInfo", new Boolean(true));
			Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);

//		Iterator<Document> iterDoc = corpus.iterator();
//		while(iterDoc.hasNext()){
//			Document doc = iterDoc.next();
			
			HtmlParser parser = new HtmlParser(doc);

			String headWord = parser.getHeadWord(); 
			ArrayList<String> shortDefList = parser.getShortDefinition();
			ArrayList<String> definitionList = parser.getDefinition();
			ArrayList<String> exampleList = parser.getexample();
			String category = parser.getPos();

			//now I have to index annotation in the doc in the lucene
			org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
			if(headWord != null && shortDefList.get(0) != null && definitionList.get(0) != null && exampleList.get(0) != null){
				docLucene.add(new Field(word, headWord, Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field(shortDef, shortDefList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field(definition, definitionList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field(example, exampleList.get(0), Field.Store.YES, Field.Index.ANALYZED));
				docLucene.add(new Field(pos, category, Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(docLucene);
			}
		}
		writer.close();
	}

	public static void main(String[] args) throws IOException, GateException {
		Gate.init();
		
		String dicFolder = "/Users/nasrin/Documents/LearnGate/dictionary_test";
		String idxPath = "/Users/nasrin/Documents/LearnGate/eclipse/HarryNewWords/ldoceIndexed";
		LuceneDictionaryMaker dicMaker = new LuceneDictionaryMaker(dicFolder, idxPath);
		dicMaker.makeDic();
		
		System.out.println("LuceneDictionaryMaker.main()");
	}
}
