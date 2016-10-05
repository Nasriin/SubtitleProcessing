package miscellaneous;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * history is a data structure which has the the English knowledge(word, pos) of each person
 * @author nasrin
 *
 */
public class History implements Serializable{
	private Map<String, Set<String>> historyWordPos = new TreeMap<String, Set<String>>();
	public History() {
	}
	public void add(TreeMap<String, Set<String>> wordPos) {
		for(Entry<String, Set<String>> node: wordPos.entrySet()){
			String word = node.getKey();
			if(historyWordPos.get(word) == null){
				historyWordPos.put(word, node.getValue());
			}
			else{
				historyWordPos.get(word).addAll(node.getValue());
			}
		}
		
	}
	public boolean isNewWord(String word, String pos){
		Set<String> listPos = historyWordPos.get(word);
		if(listPos == null){
			return true;
		}
		else{
			return !listPos.contains(pos);
		}
	}
}
