package miscellaneous;

import java.util.List;

public interface Dictionary {
	public List<Meaning> getMeaning(String word, String pos);
}
