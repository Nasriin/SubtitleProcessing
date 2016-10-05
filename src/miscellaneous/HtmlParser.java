package miscellaneous;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.GateConstants;
import gate.Utils;

public class HtmlParser {
	private Document doc = null;
	AnnotationSet docAnnots = null;

	public HtmlParser(Document doc) {
		this.doc = doc;
		docAnnots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
	}

	private ArrayList<String> getInformation(String lable){
		AnnotationSet annotSet = docAnnots.get(lable);
		if(!annotSet.isEmpty() || annotSet == null)
			System.out.println("Parser could not find set of annotaions with this lable: " + lable);
		ArrayList<String> infoList = new ArrayList<String>();
		List<Annotation> annotSetOrder = Utils.inDocumentOrder(annotSet);
		Iterator<Annotation> annotIterator = annotSetOrder.iterator();
		while(annotIterator.hasNext()){
			Annotation annot = annotIterator.next();
			String info = Utils.stringFor(doc, annot);
			if(!info.isEmpty())
				infoList.add(info);
		}

		return infoList;
	}
	public ArrayList<String> getShortDefinition(){
		return getInformation("h2");
	}
	public ArrayList<String> getDefinition(){
		return getInformation("ftdef");
	}
	public ArrayList<String> getexample(){
		return getInformation("ftexa");
	}

	public String getHeadWord() {
		String headWord = null;
		AnnotationSet words = docAnnots.get("h1");
		Iterator<Annotation> headWords = words.iterator();
		while(headWords.hasNext()){
			Annotation headWordAnnot = (Annotation)headWords.next();
			String word = Utils.stringFor(doc, headWordAnnot);
			if(!word.isEmpty() && headWord == null)
				headWord = word;
			else if (!word.isEmpty())
				System.err.println("There are multiple head words");
		}
		return headWord;
	}

	public String getPos(){
		String pos = null;
		AnnotationSet spanAnnots = docAnnots.get("span");
		Iterator<Annotation> spanIters = spanAnnots.iterator();
		while(spanIters.hasNext()){
			Annotation spanAnnot = spanIters.next();
			FeatureMap spanFeatures = spanAnnot.getFeatures();
			if(!spanFeatures.isEmpty() && spanFeatures.get("class") != null){
				String classFeature = spanFeatures.get("class").toString();
				if(classFeature.equalsIgnoreCase("pos")){
					pos = Utils.stringFor(doc, spanAnnot);
				}
			}

		}

		return pos;
	}


}
