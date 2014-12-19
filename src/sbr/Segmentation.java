package sbr;

import java.io.File;

public class Segmentation {

	public static void main(String[] args) {
		
		String file;
		
		if(args.length > 0)
		{
			file = args[0];
			System.err.println("Topology file: "+file);
		}
		else
		{
			file = "Input11.txt";
			//file = "8x8.1";
		}
		
		// create the graph
		SR teste = new SR(new File(file));
		// print the graph
		//teste.printGraph();
		// run the algorithm
		teste.computeSegments();
		// list segments
		teste.listSegments();
		
		// insert restrictions
		teste.setrestrictions();
		//print restrictions
		teste.printRestrictions();
		//teste.printUnitSeg();
		
		System.out.println("SBR to "+file+" done!");
		
		return;
	}

}
