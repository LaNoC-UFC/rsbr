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

	public static void main(String[] args) {
		
		int dimX=8,dimY=8;
		int montCarl = 1000;
		double[] faltPercs={0.0,0.05,0.1,0.15,0.2,0,25,0.30};
		ArrayList<ArrayList<Path>> toSimPaths = null;
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(new File("monteCarlo.txt")));
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
			
			for(int s=0;s<montCarl;s++)
			{
				System.out.println("Generating graph");
				//	graph = new Graph(new File(topologyFile));
				graph = new Graph(dimX,dimY,faltPerc);
				graph.printGraph("teste");
				System.out.println("Isolado? :"+graph.haveIsolatedCores());			
			
				System.out.println("graph: "+graph);
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
				toSimPaths = rbr.pathSelection(paths, new Path.MaxWeight(), 2);
				
				rbr.addRoutingOptions(paths);
				rbr.regionsComputation();
				rbr.merge(1.0);
				stats = rbr.getRegionsStats();
				rbr.doRoutingTable("all");
				System.out.println("All");
				System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
				all.add(stats[0]);
				
				rbr.addRoutingOptions(toSimPaths);
				rbr.regionsComputation();
				rbr.merge(1.0);
				stats = rbr.getRegionsStats();
				rbr.doRoutingTable("mw2");
				System.out.println("mw2");
				System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
				mw2.add(stats[0]);							

			}
			
			rbr.printMontCarl(output, faltPerc, all, mw2);
						
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
