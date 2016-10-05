import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.security.SecurityException;
import gate.util.GateException;
import gate.util.Out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class Manager {
	
	private static String orgFile = "/Users/nasrin/Documents/Books/HarryPotter/deatly hallows/Harry Potter and the Deathly Hallows copy.pdf";
	private static Manager manager = null;
	private Manager() throws GateException {
		// initialise the GATE library
				Out.prln("Initialising GATE...");
				Gate.init();
				Out.prln("...GATE initialised");
	}
	public static Manager getInstance() throws GateException{
		if(manager == null)
			manager = new Manager();
		return manager;
	}

	public Corpus createCorpus() throws ResourceInstantiationException, MalformedURLException{
		String name = "HarryPotter corpus";
		Corpus corpus = Factory.newCorpus(name);
		//    for(int i = 0; i < args.length; i++) {
		//      URL u = new URL(args[i]);

		String docAdr = "file://"+ orgFile;
		//File f = new File(adr + "Harry Potter and the Deathly Hallows copy.pdf");
		//URL u = f.toURI().toURL();  
		URL u = new URL(docAdr);

		FeatureMap params = Factory.newFeatureMap();
		params.put("sourceUrl", u);
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Out.prln("Creating doc for " + u);
		Document harryDoc = (Document)
				Factory.createResource("gate.corpora.DocumentImpl", params);
		corpus.add(harryDoc);
		//    } // for each of args
		System.out.println(name + " Created");
		return corpus;
		
	}
	private static void printRoot(List<String> words) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("words.txt"), "UTF-8"));
		
		for(String word: words)
			writer.write(word+"\n");
		
		writer.close();
	}

		
	public static void main(String[] args) throws GateException, IOException {
		Manager manager = Manager.getInstance();
		Corpus corpus = manager.createCorpus();
		
		ExtractHighlights extractHighlights = new ExtractHighlights(orgFile);
		List<String> highlightsList = extractHighlights.run();
		
		
		ApplicationGate application = new ApplicationGate(corpus);
		application.runApplication();
		
		ManageAnnotations manageAnnots = new ManageAnnotations(highlightsList, corpus);
		manageAnnots.addHighlightAnnot();
		List<String> words = manageAnnots.extractHighlightedTokens();
		
		manager.save(corpus, "outputs/sds");
		
		printRoot(words);
		
		System.out.println("Manager.main()");
		
		
	}
	
	private static void remove(File fld){
		if (fld.isDirectory()){
			for (File f: fld.listFiles()){
				remove(f);
			}
		}
		
		fld.delete();
	}
	
	private void save(Corpus corpus, String path) throws PersistenceException, UnsupportedOperationException, MalformedURLException, SecurityException {
		File fld = new File(path);
		if (!fld.exists()){
			fld.mkdirs();
		} else {
			remove(fld);
			fld.mkdirs();
		}
			
		DataStore sds = Factory.createDataStore(SerialDataStore.class.getName(), fld.toURI().toURL().toString());
		
		Corpus adoptedCorpus = (Corpus) sds.adopt(corpus, null);
		adoptedCorpus.sync();
	}

}
