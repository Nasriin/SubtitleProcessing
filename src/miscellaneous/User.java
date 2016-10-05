package miscellaneous;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class User {
	public final static String FLD_NAME = "history";
	private String name;
	private History history;
	private TreeMap<String, Set<String>> newWords = new TreeMap<String, Set<String>>();
	private File stateFile;
	
	public User(String name) {
		this.name = name;
		//read from the folder history and find name's history
		new File(FLD_NAME).mkdirs();
		stateFile = new File(FLD_NAME, name);
		if (stateFile.exists()){
			loadHistory();
		}
		else{
			this.history = new History();
			System.out.println("...new history file with the name of Nasrin created");
		}
		
	}
	private void saveHistory() {
		try {
			
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(stateFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(history);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in "+stateFile);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void loadHistory() {
		System.out.println("...Loading the history");
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(stateFile);
			ObjectInputStream input = new ObjectInputStream(fileInput);
			this.history = (History) input.readObject();
			input.close();
			fileInput.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	public History getHistory() {
		return history;
	}
	public void setNewWords(TreeMap<String, Set<String>> newWords) {
		System.out.println("...Saving new words and update history");
		this.newWords = newWords;		
		history.add(newWords);
		saveHistory();
	}
	public void print(File output) throws IOException{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output)));
		for(Entry<String, Set<String>> node: newWords.entrySet()){
			pw.println(node.getKey() + "\t" + node.getValue());
		}
		pw.close();
		System.out.println("...File of new words created");
	}
	
}
