import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.Out;

import java.io.File;
import java.io.IOException;

public class ApplicationGate  {

	private SerialAnalyserController myController;
	private Corpus corpus = null;

	
	public ApplicationGate(Corpus corpus) {
		this.corpus = corpus;
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
		
		for(int i = 0; i < prNames.length ; i++){
			FeatureMap params = Factory.newFeatureMap();
			ProcessingResource pr = (ProcessingResource) Factory.createResource(prNames[i], params);
			myController.add(pr);
			System.out.println("..."+ prNames[i]);
		}
		
		
		Out.prln("...Application loaded");
	} // initAnnie()
	
	  public void setCorpus(Corpus corpus) {
	    myController.setCorpus(corpus);
	  } // setCorpus


	public void execute() throws GateException {
		Out.prln("Running APPLICATION...");
		myController.execute();
		Out.prln("...APPLICATION complete");
	} // execute()

	public void runApplication() throws GateException, IOException {
		
		initApplication();
		
		// tell the pipeline about the corpus and run it
		setCorpus(corpus);
		execute();

		
	}

	

	/**
	 *
	 */
}