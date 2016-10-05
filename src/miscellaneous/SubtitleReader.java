package miscellaneous;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.corpora.MimeType;
import gate.corpora.TextualDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.util.DocumentFormatException;
import gate.util.GateException;

import java.io.File;
import java.net.MalformedURLException;

//@CreoleResource (isPrivate=true, autoinstances=[])
public class SubtitleReader extends TextualDocumentFormat {
	private static final long serialVersionUID = 1L;

	@Override
	public Resource init() throws ResourceInstantiationException {
		// Register XML mime type 
		MimeType mime = new MimeType("text","srt"); 
		// Register the class handler for this mime type 
		mimeString2ClassHandlerMap.put(mime.getType()+ "/" + mime.getSubtype(), 
				this); 
		// Register the mime type with mine string 
		mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), 
				mime); 
		// Register file suffixes for this mime type 
		suffixes2mimeTypeMap.put("srt",mime); 
		// Register magic numbers for this mime type 
//		magic2mimeTypeMap.put("<?xml",mime); 
		// Set the mimeType for this language resource 
		setMimeType(mime); 
		return this; 
	}
	
	@Override
	public void unpackMarkup(Document doc) throws DocumentFormatException {
		String strFile = doc.getContent().toString();
		String[] lines = strFile.split("\n");
		int i = 0;
		StringBuilder sb = new StringBuilder();
		while(i < lines.length){
			
			for (i = i + 2; lines[i].trim().length() != 0; ++i){
				sb.append(lines[i]);
			}
			++i;
		}
		
		doc.setContent(new DocumentContentImpl(sb.toString()));
	}
	
	
	public static void main(String[] args) throws GateException, MalformedURLException {
		Gate.init();
		Gate.getCreoleRegister().registerDirectories(new File(".").toURI().toURL());
		File srtFile = new File("/Users/nasrin/Downloads/Desperate/desperate.housewives.s01e01.dvdrip.xvid-wat.srt");
		
		Document doc = Factory.newDocument(srtFile.toURI().toURL());
		System.out.println("NewFormatReader.main()" + doc.getContent().toString());
		
	}
}
