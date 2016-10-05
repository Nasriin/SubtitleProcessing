import gate.creole.annic.lucene.LuceneAnalyzer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class Test {

	public Test() throws MalformedURLException, ClassNotFoundException {
		File myJar = new File("../library/lucene-4.6.1/core/lucene-core-4.6.1.jar");
		URLClassLoader child = new URLClassLoader (new URL[]{myJar.toURI().toURL()}, this.getClass().getClassLoader());
		
		Class classToLoad = Class.forName ("com.MyClass", true, child);
	}
	public static void main(String[] args) {
	}
}
