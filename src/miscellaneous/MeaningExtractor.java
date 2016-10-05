package miscellaneous;

import gate.Document;

import java.io.File;
import java.io.IOException;

public interface MeaningExtractor {

	void setDocument(Document doc);

	void setDictionary(Dictionary dic);

	void extract(File outputFile) throws IOException;

}
