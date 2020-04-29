package com.melax.contract.general;

import java.io.File;

import edu.uth.clamp.nlp.cancer.PerformanceCollector;

public class compareFolder {
	
	public static void main(String[] argv) {
		
		//compare xmi files with relation
		//PerformanceCollector.compareFolderOri(new File(goldstandardxmi_folder), new File(predictxmi_folder), true);
		
		//compare xmi files without relation
		//PerformanceCollector.compareFolderOri(new File(goldstandardxmi_folder), new File(predictxmi_folder), false);
		
		//replace the standard and predict folder with the your directory
		PerformanceCollector.compareFolderOri( new File( "xmi_gold_standard_folder" )
				, new File( "xmi_predict_folder" ), true );
		
		
			
		
	}

}
