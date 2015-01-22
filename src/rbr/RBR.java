package rbr;

import util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RBR {
	// public HashMap<String, String> restrictions = new HashMap<>();
	private Graph graph;

	private static class BySize implements Comparator<ArrayList<Path>> {

		@Override
		public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
			if(p0.size() < p1.size()) return -1;
			if(p0.size() > p1.size()) return +1;
			return 0;
		}

	}
	
	public RBR(Graph g) {
		graph = g;

	}

// Get restrictions and add to HashMap ( RouterName: -> Forbidden ports )
	/*
	public void setRestricitions(String fileName) { File restrictionsFile =
			new File(fileName);

	try { Scanner sc = new Scanner(new FileReader(restrictionsFile)); while
		(sc.hasNextLine()) { String[] rest = sc.nextLine().split(" "); for (int i
				= 0; i < rest.length; i++) { this.restrictions.put(
						rest[0].concat(rest[i].substring(0, 1)), rest[i].substring(2,
								rest[i].length() - 1)); } }
	System.out.println("Restrictions setted from file: " +
			restrictionsFile.getName()); sc.close(); } catch (Exception ex) {
				Logger.getLogger(RBRTools.class.getName()).log(Level.SEVERE, null, ex); }
	}
	*/

	// Make log file for each input file
	public void makeLog() {
		// Print all regions of all routers
		for (Vertice router : graph.getVertices()) {
			System.out.println("");
			System.out.println("Router " + router.getNome() + ":\n");
			for (Region r : router.getRegions()) {
				System.out.println(r.getUpRight() + " " + r.getDownLeft()
						+ " Ip: " + r.getIp() + " Op: " + r.getOp());
			}
		}
	}

	// Make stats files
	public void makeStats(ArrayList<ArrayList<Path>> paths) {
		// double[] hopCount = getHopCountStats(paths);
		double[] Regions = getRegionsStats();
		double ard = getRoutingDistance(paths);
		double[] linkWeight = linkWeightStats();
		try {

			FileWriter ardfs = new FileWriter(new File("ard"));
			FileWriter lwMeanfs = new FileWriter(new File("lw-Mean"));
			FileWriter lwStdfs = new FileWriter(new File("lw-Std"));
			FileWriter regionMaxfs = new FileWriter(new File("region-max"));

			ardfs.write("" + ard);
			lwMeanfs.write("" + linkWeight[0]);
			lwStdfs.write("" + linkWeight[1]);
			regionMaxfs.write("" + Regions[0]);

			ardfs.close();
			lwMeanfs.close();
			lwStdfs.close();
			regionMaxfs.close();

		} catch (IOException ex) {
			Logger.getLogger(RBR.class.getName()).log(Level.SEVERE, null,
					ex);
		}

	}

// Check if r2 can be reached through r1 
	private static boolean canBeReached(Vertice r1, Vertice r2) {
		String corArestaPai; // Output port
		String corArestaAtual;// Input port
		String rest = ""; // Restriction
		corArestaAtual = r1.getAresta(r2).getCor();

		if (r1.preds.size() == 0) {
			corArestaPai = "I";
			return true;
		}

		for (Vertice pred : r1.preds) {
			corArestaPai = r1.getAresta(pred).getCor();
			rest = pred.getRestriction(corArestaPai);

			if (!rest.contains(corArestaAtual))
				return true;
		}

		return false;
	}

	// Calculates the hop-count stats - [0] - Max / [1] - Min / [2] - Average
	private static double[] getHopCountStats(ArrayList<Path> paths) {
		double[] hcStats = new double[3];
		double averageHopCount = 0;
		double maxHopCount = 0;
		double minHopCount = Double.POSITIVE_INFINITY;

		for (Path path : paths) {
			maxHopCount = (maxHopCount > path.size() - 1) ? maxHopCount : path
					.size() - 1;
			minHopCount = (minHopCount < path.size() - 1) ? minHopCount : path
					.size() - 1;
			averageHopCount += (path.size() - 1);
		}

		hcStats[0] = maxHopCount;
		hcStats[1] = minHopCount;
		hcStats[2] = averageHopCount / paths.size();

		return hcStats;
	}

	// Get just one path (from source to sink) from all paths computed
	/*
	public ArrayList<Path> getSimplePaths(ArrayList<Path> p) {
		ArrayList<Path> simplePaths = new ArrayList<Path>();
		ArrayList<Path> paths = new ArrayList<Path>(p);

		for (Vertice source : graph.getVertices()) {
			for (Vertice sink : graph.getVertices()) {
				if (source.getNome().equals(sink.getNome()))
					continue;
				List<Integer> indexs = new ArrayList<Integer>();
				for (Path path : paths) {
					if (path.get(0).getNome().equals(source.getNome())
							&& path.get(path.size() - 1).getNome()
									.equals(sink.getNome()))
						indexs.add(paths.indexOf(path));
				}
				int randIndex = 0;
				if (indexs.size() > 1) {
					randIndex = (int) (Math.random() * ((double) indexs.size() - 1));
				}
				simplePaths.add(paths.get(indexs.get(randIndex)));
				for (Integer i : indexs) {
					paths.remove(i);
				}
			}
		}

		return simplePaths;

	}
	*/

	public void doRoutingTable(String ext) {
		double[] stats = getRegionsStats();
		String routingTableFile = "Table_package_"+ext+".vhd";
		File routingTable = new File(routingTableFile);
		int size = (graph.dimX() >= graph.dimY()) ? graph.dimX() : graph.dimY();
		int nBits = (int) Math.ceil(Math.log(size) / Math.log(2));

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(routingTable));
			bw.append("library IEEE;\n"
					+ "use ieee.std_logic_1164.all;\n"
					+ "use ieee.numeric_std.all;\n"
					+ "use work.HermesPackage.all;\n\n"
					+ "package TablePackage is\n\n"
					+ "constant NREG : integer := "
					+ (int) stats[0]
					+ ";\n"
					+ "constant MEMORY_SIZE : integer := NREG;\n"
					+ "constant NBITS : integer := "
					+ nBits
					+ ";\n"
					+ "constant CELL_SIZE : integer := 2*NPORT+4*NBITS;\n\n"
					+ "subtype cell is std_logic_vector(CELL_SIZE-1 downto 0);\n"
					+ "subtype regAddr is std_logic_vector(2*NBITS-1 downto 0);\n"
					+ "type memory is array (0 to MEMORY_SIZE-1) of cell;\n"
					+ "type tables is array (0 to NROT-1) of memory;\n\n"
					+ "constant TAB: tables :=(");

			for (Vertice router : graph.getVertices()) {
				router.PrintRegions(stats, bw, nBits);
				if (graph.getVertices().indexOf(router) != graph.getVertices()
						.size() - 1)
					bw.append(",");
			}

			bw.append("\n);\nend TablePackage;\n\npackage body TablePackage is\n"
					+ "end TablePackage;\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Calculate routing distance -> all paths lengths / #paths
	private double getRoutingDistance(ArrayList<ArrayList<Path>> paths) {
		double routingDistance = 0.0;

		for (ArrayList<Path> alp : paths)
			routingDistance += alp.get(0).size();

		// Cover paths with the same source and destination
		routingDistance += graph.getVertices().size();

		return routingDistance / (paths.size() + graph.getVertices().size());
	}

	// Set all links weight -> #paths that cross the link
	/*
	private static void setLinkWeight(ArrayList<Path> paths) {

		for (Path path : paths) {
			for (int v = 0; v < path.size() - 1; v++) {
				Aresta link = path.get(v).getAresta(path.get(v + 1));
				link.incremWeight();
			}
		}
	}
	*/

	// Link weight stats [0] - mean / [1] - standard deviation
	private double[] linkWeightStats() {
		//setLinkWeight(paths); // After that we have the weight of all
								// links
		double linksWeight = 0.0;
		double[] stats = new double[2];
		double mean = 0.0;
		double std = 0.0;

		for (Aresta link : graph.getArestas())
			linksWeight += (double) link.getWeight();

		mean = linksWeight / (double) graph.getArestas().size();
		stats[0] = mean;

		double temp = 0.0;
		for (Aresta link : graph.getArestas())
			temp += ((double) link.getWeight() - mean)
					* ((double) link.getWeight() - mean);

		double variance = (temp / (double) (graph.getArestas().size()));
		// size-1 for sample. We have population

		std = Math.sqrt(variance);
		stats[1] = std;

		return stats;
	}

	// Calculates the regions stats - [0] - Max / [1] - Min / [2] - Average
	public double[] getRegionsStats() {

		double[] stats = new double[3];
		double average;
		List<Integer> regSizes = new ArrayList<>();

		for (Vertice r : graph.getVertices()) {
			regSizes.add(r.getRegions().size());
		}
		Collections.sort(regSizes);

		int sum = 0;
		for (int size : regSizes) {
			sum += size;
		}
		average = sum / regSizes.size();

		stats[0] = (double) regSizes.get(regSizes.size() - 1);
		stats[1] = (double) regSizes.get(0);
		stats[2] = (double) average;
		return stats;
	}
	
	public void printMontCarl(File file,double percent, ArrayList<Double> c1,ArrayList<Double> c2)
	{
		double[] statsC1 = montCarlStats(c1);
		double[] statsC2 = montCarlStats(c2);
		try 
		{
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.append(percent+"\t"+statsC1[0]+"\t"+statsC1[1]+"\t"+statsC2[0]+"\t"+statsC2[1]);
			output.close();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private double[] montCarlStats(ArrayList<Double> input)
	{
		double[] stats = new double[2]; //0-Mean, 1-Std		
		double sum=0;
		double mean=0;
		double sigma = 0;
		double variance = 0;
		double dblmean = 0;		
		
		for(double vlr : input)
			sum+=vlr;
		mean=sum/input.size();
		
		double ArAccum=0;
		for(double vlr : input)
			ArAccum+=(vlr*vlr);
		dblmean= ArAccum/input.size();
		
		variance = dblmean-(mean*mean); //Variance
		sigma=Math.sqrt(variance); //Standard Deviation
		
		
		stats[0] = mean;
		stats[1] = sigma;
		return stats;
	}

	// Pack routing options if they have the same input port and the same
	// destination
	private static void packOutputPort(Vertice atual) {

		ArrayList<RoutingPath> actRP = atual.getRoutingPaths();
		atual.setRoutingPaths(new ArrayList<RoutingPath>());
		for (RoutingPath a : actRP) {
			String op = a.getOp();
			String dst = a.getDst();
			String ip = a.getIp();

			for (RoutingPath b : actRP) {
				if (ip.equals(b.getIp()) && dst.equals(b.getDst())) {
					if (!op.contains(b.getOp()))
						op = op.concat(b.getOp());
				}
			}
			atual.addRP(ip, dst, op);
		}
	}

	// Pack routing options if they have the same output port and the same
	// destination
	public static void packInputPort(Vertice atual) {
		ArrayList<RoutingPath> actRP = atual.getRoutingPaths();
		atual.setRoutingPaths(new ArrayList<RoutingPath>());
		for (RoutingPath a : actRP) {
			String op = a.getOp();
			String dst = a.getDst();
			String ip = a.getIp();

			for (RoutingPath b : actRP) {
				if (op.equals(b.getOp()) && dst.equals(b.getDst())) {
					if (!ip.contains(b.getIp()))
						ip = ip.concat(b.getIp());
				}
			}
			atual.addRP(ip, dst, op);
		}
	}

// Calculates the size of minimum path length // If destination cannot be
// reached starting at source the return is null 
	/*
	private int MinimumPathLength(Vertice o, Vertice d) {
		int min = 0;
		ArrayList<Vertice> bestPath = new ArrayList<>();
		Vertice atual;
		Vertice vizinho;
		ArrayList<Vertice> naoVisitados = new ArrayList<>();

		graph.setGraph();
		bestPath.add(o);

		// Setting the initial distance for all vertices
		for (int i = 0; i < graph.getVertices().size(); i++) {
			graph.getVertices().get(i).preds = new ArrayList<Vertice>();
			if (graph.getVertices().get(i).getNome().equals(o.getNome())) {
				graph.getVertices().get(i).setDistancia(0);
			} else {
				graph.getVertices().get(i).setDistancia(9999);
			}

			naoVisitados.add(graph.getVertices().get(i));
		}

		Collections.sort(naoVisitados);

		while (!naoVisitados.isEmpty()) {
			atual = naoVisitados.get(0);

			for (int i = 0; i < atual.getAdj().size(); i++) {
				vizinho = atual.getAdj().get(i).getDestino();
				if (!canBeReached(atual, vizinho))
					continue;

				if (!vizinho.verificarVisita()) {
					if (vizinho.getDistancia() >= (atual.getDistancia() + atual
							.getAdj().get(i).getPeso())) {
						vizinho.setDistancia(atual.getDistancia()
								+ atual.getAdj().get(i).getPeso());
						vizinho.preds.add(atual);

						if (vizinho == d) {
							bestPath.clear();
							bestPath.add(vizinho);
							min = vizinho.getDistancia();
						}
					} else {
						atual.visitar();
					}
				}
			}

			naoVisitados.remove(atual);

			Collections.sort(naoVisitados);
		}

		return min;
	}
	*/

/* Implements Dijkstra's algorithm : computes all minimal paths for a
pair (source, sink)
	 */
	/*
	public ArrayList<Path> getPaths(Vertice src, Vertice dst) {
		int max = MinimumPathLength(src, dst);
		ArrayList<Path> paths = new ArrayList<Path>();
		depthFirst(0, max, dst, paths);

		ArrayList<Path> removePaths = new ArrayList<Path>();
		for (Path path : paths) {
			for (int i = 1; i < path.size() - 1; i++) {
				Vertice atual = path.get(i);
				String corAnt = atual.getAresta(path.get(i - 1)).getCor();
				String corProx = atual.getAresta(path.get(i + 1)).getCor();
				String restric = atual.getRestriction(corAnt);
				if (restric.contains(corProx)) {
					removePaths.add(path);
					break;
				}
			}
		}
		paths.removeAll(removePaths);

		return paths;
	}
	*/

	/*
	private static void depthFirst(int niv, int max, Vertice act, ArrayList<Path> paths) {
		if (++niv > max) {
			Path temp = new Path();
			temp.add(act);
			paths.add(temp);
			return;
		}
		for (Vertice parent : act.preds) {
			depthFirst(niv, max, parent, paths);
			for (Path path : paths)
				if (path.get(path.size() - 1) == parent)
					path.add(act);

		}
	}
	*/

// Do getPaths for all pairs (source, sink) 
	/*
	public ArrayList<Path> pathsComputation() {
		ArrayList<Path> paths = null;// = new ArrayList<>();
		ArrayList<Path> allPaths = new ArrayList<>();
		for (Vertice src : graph.getVertices()) {
			for (Vertice dst : graph.getVertices()) {
				if (!src.getNome().equals(dst.getNome())) {
					paths = getPaths(src, dst);
					allPaths.addAll(paths);
					for (Path path : paths) {
						String dest = dst.getNome();
						for (Vertice sw : path) {
							if (path.indexOf(sw) != path.size() - 1) {
								String op = sw.getAresta(path.get(path.indexOf(sw) + 1)).getCor();
								String ip = (path.indexOf(sw) == 0) ? "I" : sw.getAresta(	path.get(path.indexOf(sw) - 1)).getCor();
								//sw.addRP(ip, dest, op); // Need to change to
														// just make RP for //
														// simplePaths
							}
						}
					}
				}
			}
		}
		for (Vertice atual : graph.getVertices()) {
			packOutputPort(atual); //
			packInputPort(atual);
		}

		return allPaths;
	}
	*/
	
	/*
	 * Nova versao da busca de pacotes.
	 */
	/*
	public ArrayList<Path> pathComputation2() {
		ArrayList<Path> allPaths = new ArrayList<Path>();
		ArrayList<Path> lastPaths = new ArrayList<Path>();
		ArrayList<String> pairs = new ArrayList<String>();
		// N = 1 hop
		for (Vertice src : graph.getVertices()) {
			for (Aresta e : src.getAdj()) {
				Vertice dst = e.getDestino();
				Path p = new Path();
				p.add(src);
				p.add(dst);
				lastPaths.add(p);
				pairs.add(src.getNome() + ":" + dst.getNome());
			}
		}
		int nPairs = graph.dimX() * graph.dimY()
				* (graph.dimX() * graph.dimY() - 1);
		// N > 1 hop
		while (pairs.size() < nPairs) { // pares cadastrados menor que numero de
										// fluxos
			ArrayList<Path> aux = new ArrayList<Path>();
			System.out.println("Tamanho anterior: " + lastPaths.get(0).size());
			for (Path p : lastPaths) {
				Vertice src = p.dst(); // fonte atual
				Vertice pre = p.get(p.size() - 2); // predecessor
				String inColor = src.getAresta(pre).getCor(); // porta de
																// entrada
				for (Aresta e : src.getAdj()) {
					Vertice dst = e.getDestino();
					if (dst == pre) // esta voltando
						continue;
					if (src.getRestriction(inColor).contains(
							src.getAresta(dst).getCor())) // nao eh permitido
						continue;
					if (pairs.contains(p.src().getNome() + ":" + dst.getNome())) // nao
																					// eh
																					// minimo
						continue;
					Path q = new Path(p);
					q.add(dst);
					aux.add(q);
				}
			}
			allPaths.addAll(lastPaths);
			lastPaths = aux;
			for (Path p : lastPaths) {
				String pair = p.src().getNome() + ":" + p.dst().getNome();
				if (!pairs.contains(pair))
					pairs.add(pair); // util contar o numero de paths entre cada
										// par?
			}
		}
		allPaths.addAll(lastPaths);
		return allPaths;
	}

	*/

	/*
	 * Nova versao da busca de pacotes. Retorna dividido por par de comunicação
	 * ordenado por tamanho dos caminhos.
	 */
	public ArrayList<ArrayList<Path>> pathsComputation() {
		ArrayList<ArrayList<Path>> allPaths = new ArrayList<ArrayList<Path>>();
		ArrayList<ArrayList<Path>> lastPaths = new ArrayList<ArrayList<Path>>();
		ArrayList<String> pairs = new ArrayList<String>();
		// N = 1 hop
		for (Vertice src : graph.getVertices()) {
			for (Aresta e : src.getAdj()) {
				Vertice dst = e.getDestino();
				Path p = new Path();
				p.add(src);
				p.add(dst);
				ArrayList<Path> s = new ArrayList<Path>();
				s.add(p);
				lastPaths.add(s);
				pairs.add(src.getNome() + ":" + dst.getNome());
			}
		}
		int nPairs = graph.dimX() * graph.dimY()
				* (graph.dimX() * graph.dimY() - 1);
		// N > 1 hop
		while (pairs.size() < nPairs) { // pares cadastrados menor que numero de
										// fluxos
			ArrayList<Path> aux = new ArrayList<Path>();
			
				System.out.println("Tamanho anterior: " + lastPaths.get(0).get(0).size());
			for (ArrayList<Path> alp : lastPaths) {
				for(Path p: alp) {
					Vertice src = p.dst(); // fonte atual
					Vertice pre = p.get(p.size() - 2); // predecessor
					String inColor = src.getAresta(pre).getCor(); // porta de
																	// entrada
					for (Aresta e : src.getAdj()) {
						Vertice dst = e.getDestino();
						if (dst == pre) // esta voltando
							continue;
						if (src.getRestriction(inColor).contains(
								src.getAresta(dst).getCor())) // nao eh permitido
							continue;
						if (pairs.contains(p.src().getNome() + ":" + dst.getNome())) // nao minimo
							continue;
						
						Path q = new Path(p);
						q.add(dst);
						aux.add(q);
					}				
				}
			}
			allPaths.addAll(lastPaths);
			lastPaths = new ArrayList<ArrayList<Path>>();
			for (Path p : aux) {
				String pair = p.src().getNome() + ":" + p.dst().getNome();
				if (!pairs.contains(pair)) {
					pairs.add(pair);
					ArrayList<Path> q = new ArrayList<Path>();
					q.add(p);
					lastPaths.add(q);
				}
				else {
					for(ArrayList<Path> alp: lastPaths) {
						if(alp.get(0).src().equals(p.src()) && alp.get(0).dst().equals(p.dst())) {
							alp.add(p);
							break;
						}
					}
				}
			}
		}
		allPaths.addAll(lastPaths);
		return allPaths;
	}

	/*
	 * Seleciona aleatoriamente 1 caminho para cada par de comunicação.
	 */
	public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p) {
		return pathSelection(p, 0);
	}
	
	/*
	 * Seleciona aleatoriamente perc dos caminhos de cada par de comunicação.
	 */
	public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, double perc) {
		ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();
		for(ArrayList<Path> alp: p) {
			Collections.shuffle(alp);
			int n = (perc*alp.size() < 1.0) ? 1 : (int) Math.round(perc*alp.size());
			ArrayList<Path> sub = new ArrayList<Path>();
			for(int i = 0; i < n; i++) {
				alp.get(i).incremWeight();
				sub.add(alp.get(i));
			}
			selec.add(sub);
		}
		return selec;
	}
		
	/*
	 * Seleciona 1 caminho para cada par de comunicação, usando o comparador passado
	 */
	public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, Comparator<Path> c) {
		return pathSelection(p, 0, c, 1);
	}
	
	/*
	 * Seleciona 1 caminho para cada par de comunicação, usando o comparador passado
	 */
	public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, Comparator<Path> c, int iterat) {
		return pathSelection(p, 0, c, iterat);
	}
	
	/*
	 * Seleciona perc dos caminhos de cada par de comunicação, usando o comparador passado
	 */
	public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, double perc, Comparator<Path> c, int iterat) {
		ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();
		Collections.sort(p, new RBR.BySize()); // sort by number of paths by pair
		for(ArrayList<Path> alp: p) {
			Collections.sort(alp, c);
			int n = (perc*alp.size() < 1.0) ? 1 : (int) (perc*alp.size());
			ArrayList<Path> sub = new ArrayList<Path>();
			for(int i = 0; i < n; i++) {
				alp.get(i).incremWeight();
				sub.add(alp.get(i));
			}
			selec.add(sub);
		}
		

		for(int i = iterat; i > 1; i--) {
			for(int j = 0; j < p.size(); j++) { // each pair
				ArrayList<Path> pair = selec.get(j);
				for(Path path : pair)
					path.decremWeight();
				pair.removeAll(pair); // esvazia sublista
				ArrayList<Path> alp = p.get(j);
				Collections.sort(alp, c);
				int n = (perc*alp.size() < 1.0) ? 1 : (int) (perc*alp.size());
				for(int k = 0; k < n; k++) {
					alp.get(k).incremWeight();
					pair.add(alp.get(k));
				}
			}
		}
		
		return selec;
	}
	
	/*
	public void addRoutingOptions(ArrayList<Path> paths) {
		for (Path path : paths) {
			String dest = path.dst().getNome();
			for (Vertice sw : path) {
				if (path.indexOf(sw) != path.size() - 1) {
					String op = sw.getAresta(path.get(path.indexOf(sw) + 1))
							.getCor();
					String ip = (path.indexOf(sw) == 0) ? "I" : sw.getAresta(
							path.get(path.indexOf(sw) - 1)).getCor();
					sw.addRP(ip, dest, op);
				}
			}
		}
		for (Vertice atual : graph.getVertices()) {
			packOutputPort(atual);
			// packInputPort(atual);
		}
	}
	*/

	public void addRoutingOptions(ArrayList<ArrayList<Path>> paths) {
		
		//inicializa op��es de roteamento
		for(Vertice v : graph.getVertices())
			v.initRoutingOptions();
		
		for(ArrayList<Path> alp : paths) {			
			for (Path path : alp) {
				String dest = path.dst().getNome();
				for (Vertice sw : path) {
					if (path.indexOf(sw) != path.size() - 1) {
						String op = sw.getAresta(path.get(path.indexOf(sw) + 1))
								.getCor();
						String ip = (path.indexOf(sw) == 0) ? "I" : sw.getAresta(
								path.get(path.indexOf(sw) - 1)).getCor();
						sw.addRP(ip, dest, op);
					}
				}
			}
		}
		for (Vertice atual : graph.getVertices()) {
			packOutputPort(atual);
			// packInputPort(atual);
		}
	}

	// Do output combinations
	private static ArrayList<String> getOutputCombinations() {
		ArrayList<String> oPComb = new ArrayList<String>();
		char[] op = "ENSW".toCharArray();

		for (int m = 1; m != 1 << op.length; m++) {
			String a = "";
			for (int i = 0; i != op.length; i++) {
				if ((m & (1 << i)) != 0) {
					a = a.concat(Character.toString(op[i]));
				}
			}
			oPComb.add(a);
		}
		return oPComb;
	}

	public void printLengthofPaths(ArrayList<ArrayList<Path>> paths) {
		int dimX = graph.dimX();
		int dimY = graph.dimY();
		int[][] sizePath = new int[dimX * dimY][dimX * dimY];

		for(ArrayList<Path> alp : paths) {
			Path path = alp.get(0);
			int sourceX = Integer
					.parseInt(path.src().getNome().split("\\.")[0]);
			int sourceY = Integer
					.parseInt(path.src().getNome().split("\\.")[1]);
			int sinkX = Integer.parseInt(path.dst().getNome().split("\\.")[0]);
			int sinkY = Integer.parseInt(path.dst().getNome().split("\\.")[1]);
			int sourceN = sourceX + sourceY * dimX;
			int sinkN = sinkX + sinkY * dimX;

			sizePath[sourceN][sinkN] = path.size();
		}
		
		try {
			Formatter output = new Formatter("sizeOfPaths.txt");

			for (int x = 0; x < dimX * dimY; x++) {
				for (int y = 0; y < dimX * dimY; y++) {
					output.format("%d \t", sizePath[x][y]);
				}
				output.format("\r\n");
			}

			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Compute the regions
	public void regionsComputation() {
		ArrayList<String> opComb = getOutputCombinations();
		for (Vertice sw : graph.getVertices()) {			
			sw.initRegions();
			for (String op : opComb) {
				String ip = new String();
				ArrayList<String> destinations = new ArrayList<String>();
				for (RoutingPath rp : sw.getRoutingPaths()) {
					if (rp.getOp().equals(op)) {
						if (!destinations.contains(rp.getDst()))
							destinations.add(rp.getDst());
						ip = mergeString(ip, rp.getIp());
					}
				}
				if (destinations.size() != 0) {
					sw.addRegion(ip, destinations, op);
				}
			}

			for (Region reg : sw.getRegions()) {
				reg.setextrems();
			}
		}
		
		adjustsRegions();

	}

	// Adjust the regions to avoid overlap
	private void adjustsRegions() {
		for (Vertice sw : graph.getVertices()) {
			ArrayList<Region> regionsTemp = new ArrayList<>();
			ArrayList<Region> regionsRemov = new ArrayList<>();
			for (Region reg : sw.getRegions()) {
				ArrayList<String> strgs = getStranges(reg);

				if (strgs != null) {
					String[] extrems = getExtrems(strgs);

					String[] Min = extrems[0].split("\\.");
					int xmin = Integer.valueOf(Min[0]);
					int ymin = Integer.valueOf(Min[1]);
					String[] Max = extrems[1].split("\\.");
					int xmax = Integer.valueOf(Max[0]);
					int ymax = Integer.valueOf(Max[1]);

					ArrayList<String> dests = reg
							.getDst(xmin, ymin, xmax, ymax);

					if (nSides(reg, strgs) == 3) {
						deleteFromRegion(extrems, reg);
						reg.setextrems();
					} else {
						regionsRemov.add(reg);
						ArrayList<ArrayList<String>> dsts = getDestinations(
								xmin, xmax, ymin, ymax, reg);
						if (dsts != null) {
							for (ArrayList<String> dst : dsts) {
								Region r = new Region(reg.getIp(), dst,
										reg.getOp());
								// r.setextrems();
								regionsTemp.add(r);
							}
						}
					}
					// use others routers to make others regions
					if (dests != null)
						regionsTemp.addAll(makeRegions(dests, reg.getIp(),
								reg.getOp()));
				}
			}
			sw.getRegions().removeAll(regionsRemov);
			sw.getRegions().addAll(regionsTemp);
		}
	}

	// Get destinations depending on the min and max from region and from
	// excluded box
	private static ArrayList<ArrayList<String>> getDestinations(int xmin, int xmax,
			int ymin, int ymax, Region reg) {
		ArrayList<ArrayList<String>> dsts = new ArrayList<>();
		ArrayList<String> dstTemp1 = new ArrayList<>();
		ArrayList<String> dstTemp2 = new ArrayList<>();
		ArrayList<String> dstTemp3 = new ArrayList<>();
		ArrayList<String> dstTemp4 = new ArrayList<>();
		boolean left = touchLeft(xmin, reg);
		boolean right = touchRight(xmax, reg);
		boolean up = touchUp(ymax, reg);
		boolean down = touchDown(ymin, reg);

		if (left && down && !up && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && up && !right && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && up && !left && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && down && !left && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (up && down && !right && !left) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (x > xmax)
					dstTemp2.add(dst);
			}
		} else if (left && right && !up && !down) {
			for (String dst : reg.getDst()) {
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y < ymin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && !up && !down && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (right && !left && !down && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (down && !up && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y > ymax)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y < ymin)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (!up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (x > xmax)
					dstTemp2.add(dst);
				else if (y > ymax)
					dstTemp3.add(dst);
				else
					dstTemp4.add(dst);
			}
		} else {
			;// System.err.println("Severe Error: total overlap!!");
		}
		if (dstTemp1.size() != 0)
			dsts.add(dstTemp1);
		if (dstTemp2.size() != 0)
			dsts.add(dstTemp2);
		if (dstTemp3.size() != 0)
			dsts.add(dstTemp3);
		if (dstTemp4.size() != 0)
			dsts.add(dstTemp4);
		if (dsts.size() == 0)
			dsts = null;
		return dsts;
	}

	private static boolean touchLeft(int xmin, Region reg) {
		return (xmin == reg.getXmin());
	}

	private static boolean touchRight(int xmax, Region reg) {
		return (xmax == reg.getXmax());
	}

	private static boolean touchUp(int ymax, Region reg) {
		return (ymax == reg.getYmax());
	}

	private static boolean touchDown(int ymin, Region reg) {
		return (ymin == reg.getYmin());
	}

	// [0] - DownLeft [1]- UpRight
	private static String[] getExtrems(ArrayList<String> dsts) {
		String[] xtrems = new String[2];

		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
		int xMax = 0, yMax = 0;

		for (String s : dsts) {
			String[] xy = s.split("\\.");
			int x = Integer.valueOf(xy[0]);
			int y = Integer.valueOf(xy[1]);

			xMin = (xMin < x) ? xMin : x;
			yMin = (yMin < y) ? yMin : y;
			xMax = (xMax > x) ? xMax : x;
			yMax = (yMax > y) ? yMax : y;
		}

		xtrems[1] = xMax + "." + yMax;
		xtrems[0] = xMin + "." + yMin;

		return xtrems;

	}

	// Return number of common sides of the box formed by strangers and the
	// region
	private static int nSides(Region reg, ArrayList<String> strgs) {
		String[] strgsXtrems = getExtrems(strgs);
		int sides = 0;

		if (Integer.parseInt(strgsXtrems[0].split("\\.")[0]) == reg.getXmin())
			sides++;
		if (Integer.parseInt(strgsXtrems[0].split("\\.")[1]) == reg.getYmin())
			sides++;
		if (Integer.parseInt(strgsXtrems[1].split("\\.")[0]) == reg.getXmax())
			sides++;
		if (Integer.parseInt(strgsXtrems[1].split("\\.")[1]) == reg.getYmax())
			sides++;

		return sides;

	}

	// Delete routers inside of box defined by extremes
	private static void deleteFromRegion(String[] extrems, Region reg) {

		String[] Min = extrems[0].split("\\.");
		int xmin = Integer.valueOf(Min[0]);
		int ymin = Integer.valueOf(Min[1]);
		String[] Max = extrems[1].split("\\.");
		int xmax = Integer.valueOf(Max[0]);
		int ymax = Integer.valueOf(Max[1]);
		for (int i = xmin; i <= xmax; i++) {
			for (int j = ymin; j <= ymax; j++) {
				String dst = i + "." + j;
				reg.getDst().remove(dst);
			}
		}
	}

	// Return wrong destinations
	private static ArrayList<String> getStranges(Region reg) {
		ArrayList<String> strg = new ArrayList<String>();
		int xmin = reg.getXmin(), xmax = reg.getXmax();
		int ymin = reg.getYmin(), ymax = reg.getYmax();
		for (int x = xmin; x <= xmax; x++) {
			for (int y = ymin; y <= ymax; y++) {
				String dest = x + "." + y;
				if (!reg.getDst().contains(dest)) {
					strg.add(dest);
				}
			}
		}
		if (strg.size() == 0)
			strg = null;
		return strg;

	}

	// Make regions only with correct destinations
	private static ArrayList<Region> makeRegions(ArrayList<String> dsts, String ip,
			String op) {
		ArrayList<Region> result = new ArrayList<Region>();
		String[] extrems = getExtrems(dsts);
		String[] Min = extrems[0].split("\\.");
		int Xmin = Integer.valueOf(Min[0]);
		int Ymin = Integer.valueOf(Min[1]);
		String[] Max = extrems[1].split("\\.");
		int Xmax = Integer.valueOf(Max[0]);
		int Ymax = Integer.valueOf(Max[1]);

		while (!dsts.isEmpty()) {
			int Lmin = Ymin, Cmax = Xmax;
			int Cmin = Xmin, Lmax = Ymax;

			boolean first = true;
			for (int line = Lmax; line >= Lmin; line--) {
				for (int col = Cmin; col <= Cmax; col++) {
					if (first) {
						if (dsts.contains(col + "." + line)) {
							Cmin = col;
							Lmax = line;
							first = false;
						}
					} else {
						if (!dsts.contains(col + "." + line)) { // if stranger
							if (line == Lmax) { // first line
								Cmax = col - 1;
							} else if (col > (Cmax - Cmin) / 2 && col > Cmin) {
								Cmax = col - 1;
							} else {
								Lmin = ++line;
							}

							if (line == Lmin) { // last line
								Region rg = montaRegiao(Cmin, Lmin, Cmax, Lmax,
										ip, op);
								dsts.removeAll(rg.getDst());
								result.add(rg);
							}
							break;
						}
					}
					if (line == Lmin && col == Cmax) { // last line
						Region rg = montaRegiao(Cmin, Lmin, Cmax, Lmax, ip, op);
						dsts.removeAll(rg.getDst());
						result.add(rg);
					}
				}
			}
		}
		return result;
	}

	private static Region montaRegiao(int xmin, int ymin, int xmax, int ymax,
			String ip, String op) {
		ArrayList<String> dst = new ArrayList<String>();
		for (int x = xmin; x <= xmax; x++)
			for (int y = ymin; y <= ymax; y++)
				dst.add(x + "." + y);

		return (new Region(ip, dst, op));
	}

	// Check if regions r1 and r2 can be merged
	private static boolean CanBeMerged(Region r1, Region r2) {
		boolean canBeMerged = false;

		if (AreNeighbours(r1, r2) && FormBox(r1, r2) && OpIsSub(r1, r2)) {
			canBeMerged = true;
		}

		return canBeMerged;
	}

	// Calculates reachability
	private double reachability(Vertice orig) {
		double reaches = 0, total = graph.getVertices().size() - 1;
		for (Vertice dest : graph.getVertices()) {
			if (orig != dest) {
				if (orig.reaches(dest)) {
					reaches++;
				}
			}
		}
		return (reaches / total);
	}
	
	

	// Merge the regions of a router
	public void merge(Vertice router, double reachability) {
		ArrayList<Region> bkpListRegion = null;
		boolean wasPossible = true;

		while (reachability(router) >= reachability && wasPossible) {
			bkpListRegion = new ArrayList<Region>(router.getRegions());
			wasPossible = mergeUnitary(router);
		}
		if (bkpListRegion != null)
			router.setRegions(bkpListRegion);

	}

	/*
	 * Tries to make one (and only one) merge and returns true in case of
	 * success
	 */
	private static boolean mergeUnitary(Vertice router) {
		for (int a = 0; a < router.getRegions().size(); a++) {
			Region ra = router.getRegions().get(a);
			for (int b = a + 1; b < router.getRegions().size(); b++) {
				Region rb = router.getRegions().get(b);

				if (CanBeMerged(ra, rb)) {
					String upRight = getUpRightMerged(ra, rb);
					String downLeft = getDownLeftMerged(ra, rb);
					String op = getOpMerged(ra, rb);
					String ip = getIpMerged(ra, rb);

					Region reg = new Region(ip, ra.getDst(), op);
					reg.setUpRight(upRight);
					reg.setDownLeft(downLeft);
					reg.getDst().addAll(rb.getDst());
					reg.setSize();

					router.getRegions().add(reg);
					router.getRegions().remove(ra);
					router.getRegions().remove(rb);

					Collections.sort(router.getRegions());

					return true;
				}
			}
		}
		return false;
	}

	// Return UpRight identifier after merge
	private static String getUpRightMerged(Region r1, Region r2) {
		String upRight;

		upRight = Integer.toString(Math.max(
				Integer.parseInt(r1.getUpRight().split("\\.")[0]),
				Integer.parseInt(r2.getUpRight().split("\\.")[0])))
				+ "."
				+ Integer.toString(Math.max(
						Integer.parseInt(r1.getUpRight().split("\\.")[1]),
						Integer.parseInt(r2.getUpRight().split("\\.")[1])));

		return upRight;
	}

	// Return DownLeft identifier after merge
	private static String getDownLeftMerged(Region r1, Region r2) {
		String downLeft;

		downLeft = Integer.toString(Math.min(
				Integer.parseInt(r1.getDownLeft().split("\\.")[0]),
				Integer.parseInt(r2.getDownLeft().split("\\.")[0])))
				+ "."
				+ Integer.toString(Math.min(
						Integer.parseInt(r1.getDownLeft().split("\\.")[1]),
						Integer.parseInt(r2.getDownLeft().split("\\.")[1])));

		return downLeft;
	}

	// return the Output ports after merge
	private static String getOpMerged(Region r1, Region r2) {
		String op;

		if (r1.getOp().contains(r2.getOp())) {
			op = r2.getOp();
		} else {
			op = r1.getOp();
		}

		return op;
	}

	// return the Input ports after merge
	private static String getIpMerged(Region r1, Region r2) {
		String ip = new String(r2.getIp());

		for (int i = 0; i < r1.getIp().length(); i++) {
			if (!ip.contains(r1.getIp().substring(i, i + 1)))
				ip += r1.getIp().substring(i, i + 1);
		}
		return ip;
	}

	private static String mergeString(String s1, String s2) {
		String ip = new String(s2);

		for (int i = 0; i < s1.length(); i++) {
			if (!ip.contains(s1.substring(i, i + 1)))
				ip += s1.substring(i, i + 1);
		}
		return ip;
	}

	// Check if regions r1 and r2 are neighbours
	private static boolean AreNeighbours(Region r1, Region r2) {
		boolean areNeighbours = false;

		int Xmax1 = Integer.parseInt(r1.getUpRight().split("\\.")[0]);
		int Xmax2 = Integer.parseInt(r2.getUpRight().split("\\.")[0]);
		int Ymax1 = Integer.parseInt(r1.getUpRight().split("\\.")[1]);
		int Ymax2 = Integer.parseInt(r2.getUpRight().split("\\.")[1]);

		int Xmin1 = Integer.parseInt(r1.getDownLeft().split("\\.")[0]);
		int Xmin2 = Integer.parseInt(r2.getDownLeft().split("\\.")[0]);
		int Ymin1 = Integer.parseInt(r1.getDownLeft().split("\\.")[1]);
		int Ymin2 = Integer.parseInt(r2.getDownLeft().split("\\.")[1]);

		if (Xmax1 > Xmax2) {
			if (Xmin1 == Xmax2 + 1)
				areNeighbours = true;
		}

		if (Xmax1 < Xmax2) {
			if (Xmin2 == Xmax1 + 1)
				areNeighbours = true;
		}

		if (Ymax1 > Ymax2) {
			if (Ymax2 == Ymin1 - 1)
				areNeighbours = true;
		}

		if (Ymax1 < Ymax2) {
			if (Ymax1 == Ymin2 - 1)
				areNeighbours = true;
		}
		return areNeighbours;
	}

	// Check if regions form a box
	private static boolean FormBox(Region r1, Region r2) {

		if ((Integer.parseInt(r1.getUpRight().split("\\.")[0]) == Integer
				.parseInt(r2.getUpRight().split("\\.")[0]) && Integer
				.parseInt(r1.getDownLeft().split("\\.")[0]) == Integer
				.parseInt(r2.getDownLeft().split("\\.")[0]))
				|| (Integer.parseInt(r1.getUpRight().split("\\.")[1]) == Integer
						.parseInt(r2.getUpRight().split("\\.")[1]) && Integer
						.parseInt(r1.getDownLeft().split("\\.")[1]) == Integer
						.parseInt(r2.getDownLeft().split("\\.")[1]))) {
			return true;
		}

		return false;
	}

	// Check if output port are subsets
	private static boolean OpIsSub(Region r1, Region r2) {

		String r1Op = Vertice.sortStrAlf(r1.getOp());
		String r2Op = Vertice.sortStrAlf(r2.getOp());
		if (r1Op.contains(r2Op) || r2Op.contains(r1Op)) {
			return true;
		}

		return false;
	}
	
	public static ArrayList<ArrayList<Path>> divideByPair(ArrayList<Path> paths) {
		ArrayList<ArrayList<Path>> paths2 = new ArrayList<ArrayList<Path>>();
		ArrayList<Path> aux = new ArrayList<Path>();
		Collections.sort(paths);
		for(int i = 0; i < paths.size(); i++){
			Path act = paths.get(i);
			aux.add(act);
			if(i < paths.size()-1) {
				Path next = paths.get(i+1);
				if(!act.src().equals(next.src()) || !act.dst().equals(next.dst())) {
					paths2.add(aux);
					aux = new ArrayList<Path>();
				}
			}
			else
				paths2.add(aux);
		}
		return paths2;
	}


}