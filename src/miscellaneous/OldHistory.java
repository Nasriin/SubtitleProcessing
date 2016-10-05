package miscellaneous;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.ANNIEConstants;

import java.util.Iterator;
import java.util.List;

public class OldHistory{
	private Document doc;
	public static String unknownFeatureLable = "UnKnown";

	public OldHistory(Document doc) {
		this.doc = doc;
	}

	public void make(){
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


				Iterator itHighlightToken = highlightTokens.iterator();
				Annotation currAnnot;

				while(itHighlightToken.hasNext()) {
					currAnnot = (Annotation) itHighlightToken.next();
					FeatureMap features_currAnnot = currAnnot.getFeatures();
					features_currAnnot.put(unknownFeatureLable, ""+true);
					
				}//while

			}

		}//while
		
		AnnotationSet annotationSet = doc.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE);
		Iterator itToken = annotationSet.iterator();
		Annotation currAnnot;
		while(itToken.hasNext()){
			currAnnot = (Annotation) itToken.next();
			FeatureMap tokenFeatures = currAnnot.getFeatures();
			Object unKnown = tokenFeatures.get(unknownFeatureLable);
			if(unKnown == null){
				
				tokenFeatures.put(unknownFeatureLable, ""+false);
			}//if
		}//while
		
		
	}//update
}//history
