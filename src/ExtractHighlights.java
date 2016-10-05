import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.PDFTextStripperByArea;
public class ExtractHighlights {
	private String orgFile;
	
	public ExtractHighlights(String file) {
		this.orgFile = file;
	}
	
	public List<String> run() {
		List<String> allHighlights = new LinkedList<String>();
		try {
			PDDocument pddDocument = PDDocument.load(new File(orgFile));
			List allPages = pddDocument.getDocumentCatalog().getAllPages();
			System.out.println("Number of PDFPages: " + allPages.size());
			for (int i = 0; i < allPages.size(); i++) {
				int pageNum = i + 1;
				PDPage page = (PDPage) allPages.get(i);
				List<PDAnnotation> la = page.getAnnotations();
				if (la.size() < 1) {
//					System.out.println("Page " + i + " with NO annotations");
					continue;
				}
				System.out.println("\nProcess Page " + pageNum + "...");
				System.out.println("Total annotations = " + la.size());
				for (PDAnnotation pdfAnnot: la){
					// Just get the first annotation for testing
					//				PDAnnotation pdfAnnot = la.get(0);
					if(!pdfAnnot.getSubtype().equalsIgnoreCase("Highlight"))
						continue;
						
//					System.out.println("[ Annot type = " + pdfAnnot.getSubtype()+" ]");
					
//					System.out.println("Modified date = " + pdfAnnot.getModifiedDate());
//					System.out.println("Rectangle = " + pdfAnnot.getRectangle());
					// Sample code taken from Canoo unit test - extractAnnotations
					// See https://svn.canoo.com/trunk/webtest/src/main/java/com/canoo/webtest/plugins/pdftest/htmlunit/pdfbox/PdfBoxPDFPage.java
					// Experimental - Not completely working since rectangle doesn't take font size/spacing into account
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
}