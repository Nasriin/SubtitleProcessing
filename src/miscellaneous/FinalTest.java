package miscellaneous;

import gate.Document;
import gate.Gate;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

public class FinalTest {
	
	private Dictionary dic = null;

	@Test
	public void test1() throws IOException, GateException {
//		File txtFile = new File("/Users/nasrin/Documents/LearnGate/Harry Potter and the Deathly Hallows copy.pdf");
//		File stateFile = new File("state.obj");
		File outputFile = new File("newWords.txt");
		File srtFile = new File("desperate.housewives.s01e03.dvdrip.xvid-wat.srt");
		
		Gate.init();
		Gate.getCreoleRegister().registerDirectories(new File(".").toURI().toURL());

//		NLPApplication pdfApp = new PDFApplication();
//		Document doc = pdfApp.process(txtFile); //NLP application = PRs, Feature Unknown add to token and set its value based on history
//		History history = new History(doc);
//		history.make();
//		pdfApp.save(stateFile);
//		pdfApp.load(stateFile);
		

		User user = new User("Nasrin");
		History history = user.getHistory();
		SrtApplication srtApp = new SrtApplication();
		Document doc = srtApp.process(srtFile);
		srtApp.setHistory(history);
		TreeMap<String, Set<String>> newWords = srtApp.getNewWords();
		user.setNewWords(newWords);
		user.print(outputFile);

		MeaningExtractor extractor = new SimpleMeaningExtractor();
//		extractor.setDocument(doc);
//		extractor.setDictionary(dic);
//		extractor.extract(outputFile);
	}
	
}
