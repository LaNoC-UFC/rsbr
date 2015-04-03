import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.Path;

public class rsbr {
	
	public static boolean canHasPerc (int dimX, int dimY, double perc) {
		
		System.out.println("e ai?");
		int nArests = (dimX-1)*dimY + dimX*(dimY-1);
		System.out.println(nArests);
		int nFalts = (int)Math.ceil((double)nArests*perc);
		System.out.println(nFalts);
		int links = nArests - nFalts;
		System.out.println(links);
		int nRouters = dimX*dimY;
		System.out.println(nRouters);
		
		if(links < nRouters-1) return false;
		
		return true;
	}

	public static void main(String[] args) {
		
		int dimX=4,dimY=4;
		int montCarl = 1000;
		double[] faltPercs={0.0,0.05,0.1,0.15,0.2,0,25,0.30};
		ArrayList<ArrayList<Path>> mw2Paths = null;
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(new File(dimX+"x"+dimY+"monteCarlo.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch (args.length) 
		{
			case 4:
				dimX = Integer.parseInt(args[0]);
				dimY = Integer.parseInt(args[1]);
				montCarl = Integer.parseInt(args[3]);
				break;
		}

		for(double faltPerc : faltPercs)
		{
			
			Graph graph;
			double[] stats = null;
			ArrayList<Double> all=new ArrayList<Double>();
			ArrayList<Double> mw2=new ArrayList<Double>();	
			RBR rbr = null;
			
			if(!canHasPerc(dimX,dimY,faltPerc)) break;

			for(int s=0;s<montCarl;s++)
			{
				System.out.println("Generating graph");
				//	graph = new Graph(new File(topologyFile));
				graph = new Graph(dimX,dimY,faltPerc);
				//graph.printGraph("montCarlo"+montCarl);
				System.out.println("Isolado?: "+graph.haveIsolatedCores());			
			
				//System.out.println("graph: "+graph);
				System.out.println(" - SR Section");
				SR sbr = new SR(graph);

				System.out.println("Compute the segments");
				sbr.computeSegments();
				//sbr.listSegments();

				System.out.println("Set the restrictions");
				sbr.setrestrictions();
				sbr.printRestrictions();		

				System.out.println(" - RBR Section");
				rbr = new RBR(graph);
				System.out.println("Paths Computation");
				ArrayList<ArrayList<Path>> paths = rbr.pathsComputation();
				mw2Paths = rbr.pathSelection(paths, new Path.MaxWeight(), 2);
				
				rbr.addRoutingOptions(paths);
				rbr.regionsComputation();
				rbr.merge(1.0);
				stats = rbr.getRegionsStats();
				rbr.doRoutingTable("all");
				System.out.println("All");
				System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
				all.add(stats[0]);
				
				rbr.addRoutingOptions(mw2Paths);
				rbr.regionsComputation();
				rbr.merge(1.0);
				stats = rbr.getRegionsStats();
				rbr.doRoutingTable("mw2");
				System.out.println("mw2");
				System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
				mw2.add(stats[0]);							

			}
			
			rbr.printMontCarl(output, faltPerc, all, mw2); //FaltPerc	AllPaths mean allpaths std	MaxWeightx2 mean MaxWeightx2 std 
						
		}
		
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("All done!");

	}

}
