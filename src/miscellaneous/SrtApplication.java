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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SrtApplication implements NLPApplication{
	public static final Object TOKEN_KNOWN_FEATURE_NAME = "Known";
	private History history;
	private Document doc;
	private SerialAnalyserController myController;


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

	public void initApplication() throws GateException, IOException {
		Out.prln("Initialising Application...");

		myController = (SerialAnalyserController) Factory.createResource(
				"gate.creole.SerialAnalyserController", Factory.newFeatureMap());

		Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome(), "ANNIE").toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome(), "Tools").toURI().toURL());

		String prNames[] = {"gate.creole.annotdelete.AnnotationDeletePR", 
				"gate.creole.tokeniser.DefaultTokeniser", 
				"gate.creole.splitter.SentenceSplitter", "gate.creole.POSTagger",
		"gate.creole.morph.Morph"};

		myController.setCorpus(Factory.newCorpus("null corpus"));
		for(int i = 0; i < prNames.length ; i++){
			FeatureMap params = Factory.newFeatureMap();
			ProcessingResource pr = (ProcessingResource) Factory.createResource(prNames[i], params);
			myController.add(pr);
			System.out.println("..."+ prNames[i]);
		}

		Out.prln("...Application loaded");
	} // initApplication()

	public void setDocument(Document doc) throws ResourceInstantiationException, MalformedURLException{
		myController.setDocument(doc);
	}
	public void execute() throws GateException {
		Out.prln("Running APPLICATION...");
		myController.execute();
		Out.prln("...APPLICATION complete");
	} // execute()

	@Override
	public void save(File stateFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(File stateFile) {
		// TODO Auto-generated method stub

	}
	public void setHistory(History history){
		this.history = history;
	}

	public TreeMap<String, Set<String>> getNewWords() {
		System.out.println("...Getting new words");
		TreeMap<String, Set<String>> wordPOSs = new TreeMap<String, Set<String>>();
		AnnotationSet annotationSet = doc.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE);
		Iterator<Annotation> it = annotationSet.iterator();
		Annotation currAnnot;
		while(it.hasNext()){
			currAnnot = it.next();
			FeatureMap tokenFeatures = currAnnot.getFeatures();
			String pos = tokenFeatures.get(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME).toString();
			String word = tokenFeatures.get("root").toString();
			boolean isNew = history.isNewWord(word, pos);
			if(isNew){
				tokenFeatures.put(TOKEN_KNOWN_FEATURE_NAME, new Boolean(false));
				Set<String> listPos = wordPOSs.get(word);
				if (listPos == null){
					listPos = new TreeSet<String>();
					wordPOSs.put(word, listPos);
				}

				listPos.add(pos);
			}
			else
				tokenFeatures.put(TOKEN_KNOWN_FEATURE_NAME, new Boolean(true));
		}
		return wordPOSs;

	}



}
