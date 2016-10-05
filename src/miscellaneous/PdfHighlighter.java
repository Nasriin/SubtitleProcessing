package miscellaneous;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleResource;
import gate.util.GateException;
import gate.util.InvalidOffsetException;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.PDFTextStripperByArea;

@CreoleResource
public class PdfHighlighter extends AbstractLanguageAnalyser{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute() throws ExecutionException {
		Document doc = getDocument();
		try {
			File file = new File(doc.getSourceUrl().toURI());
			boolean isPdf = file.getName().endsWith(".pdf");
			if(!isPdf)
				throw new ExecutionException("Input file should be in Pdf format!");
			
			List<String> highlightsList = extractHighlights(file);
			addHighlightAnnot(highlightsList, doc);
			
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InvalidOffsetException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) throws GateException {
		Gate.init();
		Gate.getCreoleRegister().registerComponent(PdfHighlighter.class);
		
		System.out.println("Class of new PR named PdfHighlighter was built");
	}
	
	public List<String> extractHighlights(File file) {
		List<String> allHighlights = new LinkedList<String>();
		try {
			PDDocument pddDocument = PDDocument.load(file);
			List allPages = pddDocument.getDocumentCatalog().getAllPages();
			System.out.println("Number of PDFPages: " + allPages.size());
			for (int i = 0; i < allPages.size(); i++) {
				int pageNum = i + 1;
				PDPage page = (PDPage) allPages.get(i);
				List<PDAnnotation> la = page.getAnnotations();
				if (la.size() < 1) {
					continue;
				}
				System.out.println("\nProcess Page " + pageNum + "...");
				System.out.println("Total annotations = " + la.size());
				for (PDAnnotation pdfAnnot: la){
					if(!pdfAnnot.getSubtype().equalsIgnoreCase("Highlight"))
						continue;
						
					PDFTextStripperByArea stripper = new PDFTextStripperByArea();
					stripper.setSortByPosition(true);

					PDRectangle rect = pdfAnnot.getRectangle();
					float x = rect.getLowerLeftX() - 1;
					float y = rect.getUpperRightY() - 1;
					float width = rect.getWidth() + 2;
					float height = rect.getHeight() + rect.getHeight() / 4;
					int rotation = page.findRotation();
					if (rotation == 0) {
						PDRectangle pageSize = page.findMediaBox();
						y = pageSize.getHeight() - y;
					}

					Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
					stripper.addRegion(Integer.toString(0), awtRect);
					stripper.extractRegions(page);

//					System.out.println("Getting text from region = " + awtRect + "\n");
					String textForRegion = stripper.getTextForRegion(Integer.toString(0));
					allHighlights.add(textForRegion);
//					System.out.println(textForRegion);
//					System.out.println("Getting text from comment = " + pdfAnnot.getContents());
				}
			}
			pddDocument.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("\nLIST OF HIGHLIGHTS CREATED");
		return allHighlights;
	}
	
	public void addHighlightAnnot(List<String> highLightsList, Document doc) throws InvalidOffsetException {

		int startIndex = 0;
		System.out.println("Adding Highlight Annotations...");

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



}
