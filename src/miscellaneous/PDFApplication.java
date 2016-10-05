package miscellaneous;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.Out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PDFApplication implements NLPApplication {
	private SerialAnalyserController myController;
	private Document doc = null;
	

	private Map<String, String> wordPOS;
	@Override
	
	public Document process(File filePath) {
		try {
			
			doc = Factory.newDocument(filePath.toURI().toURL());
			initApplication();
			setDocument(doc);
			execute();

		} catch (GateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return doc;
	}

	@Override
	public void save(File stateFile){

		wordPOS = new TreeMap<String, String>();
		AnnotationSet annotationSet = doc.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE);
		Iterator it = annotationSet.iterator();
		Annotation currAnnot;
		while(it.hasNext()){
			currAnnot = (Annotation) it.next();
			FeatureMap tokenFeatures = currAnnot.getFeatures();
				String pos = tokenFeatures.get(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME).toString();
				String word = tokenFeatures.get("root").toString();
				wordPOS.put(word, pos);
		}
		
		try {
			
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(stateFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(wordPOS);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in "+stateFile);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public void load(File stateFile) {
		TreeMap<String, String> restoreWordPos = null;
		
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(stateFile);
			ObjectInputStream input = new ObjectInputStream(fileInput);
			restoreWordPos = (TreeMap<String, String>) input.readObject();
			input.close();
			fileInput.close();
			for(String key: restoreWordPos.keySet()){
//				System.out.println(key +" " + restoreWordPos.get(key));
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void initApplication() throws GateException, IOException {
		Out.prln("Initialising Application...");
		
		myController = (SerialAnalyserController) Factory.createResource(
				"gate.creole.SerialAnalyserController", Factory.newFeatureMap());
		
		Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome(), "ANNIE").toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome(), "Tools").toURI().toURL());
		Gate.getCreoleRegister().registerComponent(PdfHighlighter.class);
		
		String prNames[] = {"gate.creole.annotdelete.AnnotationDeletePR", 
				 "gate.creole.tokeniser.DefaultTokeniser", 
				 "gate.creole.splitter.SentenceSplitter", "gate.creole.POSTagger",
				"gate.creole.morph.Morph", PdfHighlighter.class.getName()};
		
		myController.setCorpus(Factory.newCorpus("null corpus"));
		for(int i = 0; i < prNames.length ; i++){
			FeatureMap params = Factory.newFeatureMap();
			ProcessingResource pr = (ProcessingResource) Factory.createResource(prNames[i], params);
			myController.add(pr);
			System.out.println("..."+ prNames[i]);
		}
		
		
		Out.prln("...Application loaded");
	} // initAnnie()
	
	public void setDocument(Document doc) throws ResourceInstantiationException, MalformedURLException{
		myController.setDocument(doc);
	}
	public void execute() throws GateException {
		Out.prln("Running APPLICATION...");
		myController.execute();
		Out.prln("...APPLICATION complete");
	} // execute()

	

}
