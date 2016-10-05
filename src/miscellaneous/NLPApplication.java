package miscellaneous;

import gate.Document;

import java.io.File;

/**
 * Given a text file (pdf, srt, ...) and put nlp annotation on it. it also should detect unknown word and annotates them.
 * @author nasrin
 *
 */
public interface NLPApplication {

	Document process(File filePath);

	void save(File stateFile);

	void load(File stateFile);

}
