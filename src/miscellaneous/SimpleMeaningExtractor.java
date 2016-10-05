package miscellaneous;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.ANNIEConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

public class SimpleMeaningExtractor implements MeaningExtractor {

	private Document doc;
	private LongmanDictionary dic;
	@Override
	public void setDocument(Document doc) {
		this.doc = doc;
	}

	@Override
	public void setDictionary(Dictionary dic) {
		this.dic = (LongmanDictionary) dic;
	}

	@Override
	public void extract(File outputFile) throws IOException {
		FeatureMap unknownWordFeature = Factory.newFeatureMap();
		unknownWordFeature.put(SrtApplication.TOKEN_KNOWN_FEATURE_NAME, new Boolean(false));
		
		AnnotationSet unknownWordsAnnot = doc.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, unknownWordFeature);
		for (Annotation unknownWord: unknownWordsAnnot){
			String root = unknownWord.getFeatures().get("root").toString();
			String pos = unknownWord.getFeatures().get(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME).toString();
			
			dic.getMeaning(root, pos);
		}
	}
//	@Override
	public void extract2(File outputFile) throws IOException {

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));

		AnnotationSet defaultAnnotSet = doc.getAnnotations();
		AnnotationSet sentences2 = defaultAnnotSet.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
		System.out.println(sentences2.size());
		List<Annotation> sentences = Utils.inDocumentOrder(sentences2);

		Iterator sentIt = sentences.iterator();
		while(sentIt.hasNext()){
			Annotation sentence = (Annotation) sentIt.next();
			Long startOffset_sent = sentence.getStartNode().getOffset();
			Long endOffset_sent = sentence.getEndNode().getOffset();

			AnnotationSet sentTokens = defaultAnnotSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE, startOffset_sent, endOffset_sent);
			Iterator tokenIt = sentTokens.iterator();
			while(tokenIt.hasNext()){
				Annotation token = (Annotation) tokenIt.next();
				FeatureMap tokenFeatures = token.getFeatures();
				String unKnown = (String) tokenFeatures.get(OldHistory.unknownFeatureLable);
				if(unKnown.equalsIgnoreCase("true")){
					String root = tokenFeatures.get("root").toString();
					String pos = tokenFeatures.get(ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME).toString();
					if(pos.length() < 2)
						continue;

					StringBuilder sb = new StringBuilder();
					sb.append(root + " ");
					String sent = Utils.stringFor(doc, sentence);
					sent = sent.replace('\n', ' ');
					String line = sb.toString() + "\t.....\t" + sent + "\n";
					bw.write(line);
				}//end if

			}//end while

		}//end while
		bw.close();

	}

}
