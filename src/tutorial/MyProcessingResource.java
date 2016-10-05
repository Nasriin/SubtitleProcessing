package tutorial;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.SerialAnalyserController;
import gate.creole.metadata.CreoleResource;
import gate.util.GateException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@CreoleResource(comment="it is my first creole", name="gholi" )
public class MyProcessingResource extends AbstractLanguageAnalyser{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute() throws ExecutionException {
		Document doc = getDocument();
		try {
			File file = new File(doc.getSourceUrl().toURI());
			boolean isPdf = file.getName().endsWith(".pdf");
			
			doc.getFeatures().put("PDF", "" + isPdf);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws GateException, MalformedURLException {
		Gate.init();
		Gate.getCreoleRegister().registerComponent(MyProcessingResource.class);
		SerialAnalyserController contorler = (SerialAnalyserController) Factory.createResource(
				"gate.creole.SerialAnalyserController", Factory.newFeatureMap());
		
		contorler.add((ProcessingResource) Factory.createResource("tutorial.MyProcessingResource"));
		
		Document doc = Factory.newDocument(new File("words.txt").toURI().toURL());
		Corpus corpus = Factory.newCorpus("test");
		contorler.setCorpus(corpus);
		contorler.setDocument(doc);
		contorler.execute();
		
		System.out.println(doc);
	}

}
