package com.melax.contract.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;


import edu.uth.clamp.config.ConfigUtil;
import edu.uth.clamp.config.ConfigurationException;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.nlp.structure.ClampNameEntity;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.uima.DocProcessor;

public class RunPipelineGeneText {
	

	static final Logger LOGGER = Logger.getLogger( RunPipelineGeneText.class.getName() );
	
	private static void writeIntoTxt(Document doc, File textOut) {
		
		FileWriter textOutfile = null;
		
		try {
			textOutfile = new FileWriter( textOut );
			textOutfile.write( "Start\tEnd\tSemantic\tCUI\tAssertion\tEntity\n" );
			for( ClampNameEntity cne : XmiUtil.selectNE( doc.getJCas(), 0, doc.getJCas().getDocumentText().length() ) ) {
				textOutfile.write( cne.getBegin() + "\t" + cne.getEnd() + "\t" + cne.getSemanticTag() 
						+ "\t" + cne.getUmlsCui() + "\t" + cne.getAssertion()
						+ "\t" + cne.textStr().replace("\t", " ").replace("\n", " ") + "\n" );
			}
			textOutfile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			LOGGER.warning( "write file failed. file=[" + textOut + "], exp=[" + e1 + "]" );
		} finally {
			if( textOut != null ) {
				IOUtils.closeQuietly( textOutfile );
			}
		}
		
	}
	
	
	public static void main(String[] argv) throws ConfigurationException, IOException, DocumentIOException, UIMAException {
		
		
		List<DocProcessor> pipeline = ConfigUtil.importPipelineFromJar( new File ("pipeline/sema4.pipeline.jar"));

		File indir = new File ("data/ori/");
	    for (String str: indir.list()) {
//	    	System.out.println(str);
	    	Document doc = new Document( "data/ori/" + str);
	    	for( DocProcessor proc : pipeline ) {
	    		proc.process( doc );
		    }
	    	
	    	doc.save( "data/ori_out/" + str.replace(".txt", ".xmi") );
	    	  	
	    	writeIntoTxt(doc, new File("data/ori_out/" + str));
	    }
		
		
		
	}

}
