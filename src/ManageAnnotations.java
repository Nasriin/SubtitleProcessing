import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ManageAnnotations {

	private List<String> highLightsList;
	Corpus corpus;
	public ManageAnnotations(List<String> highlightsList, Corpus corpus) throws ResourceInstantiationException {
		this.highLightsList = highlightsList;
		this.corpus = corpus;
		//		this.newCorpus = Factory.newCorpus("Harry with highlight");
	}
	public void addHighlightAnnot() throws InvalidOffsetException {

		Iterator iterDoc = corpus.iterator();
		int startIndex = 0;
		System.out.println("Adding Highlight Annotations...");

		//		while(iterDoc.hasNext()){
		Document doc = (Document) iterDoc.next();
		String docContent = doc.getContent().toString();
		AnnotationSet annotations = doc.getAnnotations();
		Iterator<String> iterHighlight = highLightsList.iterator();
		int cntUnMatched = 0;
		int prevIndx = 0;
		String prevWord = "";

		while(iterHighlight.hasNext()){
			String highlight = (String) iterHighlight.next().trim();
			startIndex = docContent.indexOf(highlight, startIndex);
			FeatureMap features = Factory.newFeatureMap();

			if(startIndex == -1){
				System.err.println("..Not found: "+ highlight.trim() + " Prev Word: "+ prevWord);
				System.err.println(docContent.substring(prevIndx, prevIndx+1000));
				cntUnMatched++;
				continue;
			}
			prevWord = highlight;
			prevIndx = startIndex;
			long startIdx = (long)startIndex;
			long endIdx = (long)(startIndex+highlight.length());
			annotations.add(startIdx, endIdx, "Highlight", features);	
		}

		System.out.println("HighLight Annotations Added! Matched: "+ doc.getAnnotations().get("Highlight").size() + ", UnMatched: " + cntUnMatched);
		//		}

	}

	public List<String> extractHighlightedTokens() {
		Iterator iter = corpus.iterator();
		List<String> words = new LinkedList<String>();

		while(iter.hasNext()) {
			Document doc = (Document) iter.next();
			AnnotationSet defaultAnnotSet = doc.getAnnotations();

			AnnotationSet sentences2 = defaultAnnotSet.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
			System.out.println(sentences2.size());
			List<Annotation> sentences = Utils.inDocumentOrder(sentences2);
			
			Iterator sentIt = sentences.iterator();
			while(sentIt.hasNext()){
				Annotation sentence = (Annotation) sentIt.next();
				Long startOffset_sent = sentence.getStartNode().getOffset();
				Long endOffset_sent = sentence.getEndNode().getOffset();


				AnnotationSet highlights = defaultAnnotSet.get("Highlight", startOffset_sent, endOffset_sent);

				Iterator hiIterate = highlights.iterator();
				while(hiIterate.hasNext()){
					Annotation highlightAnnot = (Annotation) hiIterate.next();
					Long startOffset = highlightAnnot.getStartNode().getOffset();
					Long endOffset = highlightAnnot.getEndNode().getOffset();
					AnnotationSet highlightTokens2 = defaultAnnotSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, startOffset, endOffset);

					List<Annotation> highlightTokens = Utils.inDocumentOrder(highlightTokens2);


					int expLength = highlightTokens.size();
					StringBuilder sb = new StringBuilder();
					Iterator it = highlightTokens.iterator();
					Annotation currAnnot;

					while(it.hasNext()) {
						currAnnot = (Annotation) it.next();
						FeatureMap features_currAnnot = currAnnot.getFeatures();
						
						String root = features_currAnnot.get("root").toString();
						String word = features_currAnnot.get(ANNIEConstants.TOKEN_STRING_FEATURE_NAME).toString();;
						//					words.add(root);
						String pos = features_currAnnot.get(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME).toString();
						//					System.out.println("Word: "+ word + " POS: " + pos);
						if(pos.length() < 2){
							//						System.out.println(pos);
							continue;
						}
						sb.append(root + " ");
					}//end while
					String sent = Utils.stringFor(doc, sentence);
					sent = sent.replace('\n', ' ');
					String line = sb.toString() + "\t.....\t" + sent;
					words.add(line);
				}//end while
			}

		} // for each doc
		return words;
	}
}
