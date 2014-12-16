package rbr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RBRTools {
	public HashMap<String, String> restrictions = new HashMap<>();

	// Get restrictions and add to HashMap ( RouterName: -> Forbidden ports )
	public void setRestricitions(String fileName) {
		File restrictionsFile = new File(fileName);

		try {
			Scanner sc = new Scanner(new FileReader(restrictionsFile));
			while (sc.hasNextLine()) {
				String[] rest = sc.nextLine().split(" ");
				for (int i = 0; i < rest.length; i++) {
					this.restrictions.put(
							rest[0].concat(rest[i].substring(0, 1)),
							rest[i].substring(2, rest[i].length() - 1));
				}
			}
			System.out.println("Restrictions setted from file: "
					+ restrictionsFile.getName());
			sc.close();
		} catch (Exception ex) {
			Logger.getLogger(RBRTools.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	// Make log file for each input file
	public void makeLog(Graph noc) {
		// Print all regions of all routers
		for (Router router : noc.getVertices()) {
			System.out.println("");
			System.out.println("Router " + router.getNome() + ":\n");
			for (Region r : router.getRegions()) {
				System.out.println(r.getUpRight() + " " + r.getDownLeft()
						+ " Ip: " + r.getIp() + " Op: " + r.getOp());
			}
		}
	}

	// Make stats files
	public void makeStats(double[] hopCount, float[] Regions, double ard,
			double[] linkWeight) {
		try {
			
			FileWriter ardfs = new FileWriter(new File("ard"));
			FileWriter lwMeanfs = new FileWriter(new File("lw-Mean"));
			FileWriter lwStdfs = new FileWriter(new File("lw-Std"));
			FileWriter regionMaxfs = new FileWriter(new File("region-max"));
			//FileWriter hcMeanfs = new FileWriter(new File("hc-mean"));
			//FileWriter hcMinfs = new FileWriter(new File("hc-min"));
			//FileWriter hcMaxfs = new FileWriter(new File("hc-max"));
			//FileWriter regionMeanfs = new FileWriter(new File("region-mean"));
			//FileWriter regionMinfs = new FileWriter(new File("region-min"));
			

			ardfs.write("" + ard);
			lwMeanfs.write("" + linkWeight[0]);
			lwStdfs.write("" + linkWeight[1]);
			regionMaxfs.write("" + Regions[0]);
			//regionMinfs.write("" + Regions[1]);
			//regionMeanfs.write("" + Regions[2]);
			//hcMaxfs.write("" + hopCount[0]);
			//hcMinfs.write("" + hopCount[1]);
			//hcMeanfs.write("" + hopCount[2]);

			ardfs.close();
			lwMeanfs.close();
			lwStdfs.close();
			regionMaxfs.close();
			//regionMinfs.close();
			//regionMeanfs.close();
			//hcMaxfs.close();
			//hcMinfs.close();
			//hcMeanfs.close();
		} catch (IOException ex) {
			Logger.getLogger(RBRTools.class.getName()).log(Level.SEVERE, null,
					ex);
		}

	}

	// Check if r2 can be reached through r1
	public boolean canBeReached(Router r1, Router r2) {
		String corArestaPai; // Output port
		String corArestaAtual;// Input port
		String rest = ""; // Restriction
		corArestaAtual = r1.getLink(r2).getCor();

		if (r1.preds.size() == 0) {
			corArestaPai = "I";

			// if(!rest.contains(corArestaAtual))
			return true;
		}

		for (Router pred : r1.preds) {
			corArestaPai = r1.getLink(pred).getCor();
			rest = this.restrictions.get(r1.getNome() + ":" + corArestaPai);

			if (!rest.contains(corArestaAtual))
				return true;
		}

		return false;
	}

	// Calculates the hop-count stats - [0] - Max / [1] - Min / [2] - Average
	public double[] getHopCountStats(ArrayList<ArrayList<Router>> paths) {
		double[] hcStats = new double[3];
		double averageHopCount = 0;
		double maxHopCount = 0;
		double minHopCount = Double.POSITIVE_INFINITY;

		for (ArrayList<Router> path : paths) {
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
	public ArrayList<ArrayList<Router>> getSimplePaths(
			ArrayList<ArrayList<Router>> p, Graph graph) {
		ArrayList<ArrayList<Router>> simplePaths = new ArrayList<ArrayList<Router>>();
		ArrayList<ArrayList<Router>> paths = new ArrayList<ArrayList<Router>>(p);

		for (Router source : graph.getVertices()) {
			for (Router sink : graph.getVertices()) {
				if (source.getNome().equals(sink.getNome()))
					continue;
				List<Integer> indexs = new ArrayList<Integer>();
				for (ArrayList<Router> path : paths) {
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

	public void doRoutingTable(float[] stats, Graph graph) {
		String routingTableFile = "Table_package.vhd";
		File routingTable = new File(routingTableFile);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(routingTable));
			bw.append("library IEEE;\nuse ieee.std_logic_1164.all;\nuse ieee.numeric_std.all;\n"
					+ "use work.HermesPackage.all;\n\npackage TablePackage is\n\nconstant NREG : "
					+ "integer := "
					+ (int) stats[0]
					+ ";\nconstant MEMORY_SIZE : integer := NREG;\n\ntype memory is "
					+ "array (0 to MEMORY_SIZE-1) of reg26;\ntype tables is array (0 to NROT-1) "
					+ "of memory;\n\nconstant TAB: tables :=(");

			for (Router router : graph.getVertices()) {
				router.PrintRegions(stats, bw);
				if(graph.getVertices().indexOf(router)!=graph.getVertices().size()-1)
					bw.append(",");
			}

			bw.append("\n);\nend TablePackage;\n\npackage body TablePackage is\n"
					+ "end TablePackage;\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Calculate routing distance -> all paths lengths / #paths
	public double getRoutingDistance(ArrayList<ArrayList<Router>> paths,
			Graph graph) {
		double routingDistance = 0.0;

		for (ArrayList<Router> path : paths)
			routingDistance += path.size();

		// Cover paths with the same source and destination
		routingDistance += graph.getVertices().size();

		return routingDistance / (paths.size() + graph.getVertices().size());
	}

	// Set all links weight -> #paths that cross the link
	private static void setLinkWeight(ArrayList<ArrayList<Router>> paths,
			Graph graph) {

		for (ArrayList<Router> path : paths) {
			for (int v = 0; v < path.size() - 1; v++) {
				Link link = path.get(v).getLink(path.get(v + 1));
				link.incremWeight();
			}
		}
	}

	// Link weight stats [0] - mean / [1] - standard deviation
	public double[] linkWeightStats(ArrayList<ArrayList<Router>> paths,
			Graph graph) {
		setLinkWeight(paths, graph); // After that we have the weight of all
										// links
		double linksWeight = 0.0;
		double[] stats = new double[2];
		double mean = 0.0;
		double std = 0.0;

		for (Link link : graph.getLinks())
			linksWeight += (double) link.getWeight();

		mean = linksWeight / (double) graph.getLinks().size();
		stats[0] = mean;

		double temp = 0.0;
		for (Link link : graph.getLinks())
			temp += ((double) link.getWeight() - mean)
					* ((double) link.getWeight() - mean);

		double variance = (temp / (double) (graph.getLinks().size()));
		// size-1 for sample. We have population

		std = Math.sqrt(variance);
		stats[1] = std;

		return stats;
	}

	// Calculates the regions stats - [0] - Max / [1] - Min / [2] - Average
	public float[] getRegionsStats(Graph grafo) {

		float[] stats = new float[3];
		float average;
		List<Integer> regSizes = new ArrayList<>();

		for (Router r : grafo.getVertices()) {
			regSizes.add(r.getRegions().size());
		}
		Collections.sort(regSizes);

		int sum = 0;
		for (int size : regSizes) {
			sum += size;
		}
		average = sum / regSizes.size();

		stats[0] = (float) regSizes.get(regSizes.size() - 1);
		stats[1] = (float) regSizes.get(0);
		stats[2] = (float) average;
		return stats;
	}

	// Calculates the size of minimum path length
	// If destination cannot be reached starting at source the return is null
	private int MinimumPathLength(Graph grafo, Router o, Router d) {
		int min = 0;
		ArrayList<Router> bestPath = new ArrayList<>();
		Router atual;
		Router vizinho;
		List<Router> naoVisitados = new ArrayList<>();

		grafo.setGraph();
		bestPath.add(o);

		// Setting the initial distance for all vertices
		for (int i = 0; i < grafo.getVertices().size(); i++) {
			grafo.getVertices().get(i).preds = new ArrayList<Router>();
			if (grafo.getVertices().get(i).getNome().equals(o.getNome())) {
				grafo.getVertices().get(i).setDistancia(0);
			} else {
				grafo.getVertices().get(i).setDistancia(9999);
			}

			naoVisitados.add(grafo.getVertices().get(i));
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

	// Pack routing options if they have the same input port and the same
	// destination
	private static void packOutputPort(Router atual) {

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
	public static void packInputPort(Router atual) {
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

	/*
	 * Implements Dijkstra's algorithm : computes all minimal paths for a pair
	 * (source, sink)
	 */
	public ArrayList<ArrayList<Router>> getPaths(Graph grafo, Router src,
			Router dst) {
		int max = MinimumPathLength(grafo, src, dst);
		ArrayList<ArrayList<Router>> paths = new ArrayList<ArrayList<Router>>();
		depthFirst(0, max, dst, paths);

		ArrayList<ArrayList<Router>> removePaths = new ArrayList<ArrayList<Router>>();
		for (ArrayList<Router> path : paths) {
			for (int i = 1; i < path.size() - 1; i++) {
				Router atual = path.get(i);
				String corAnt = atual.getLink(path.get(i - 1)).getCor();
				String corProx = atual.getLink(path.get(i + 1)).getCor();
				String restric = this.restrictions.get(atual.getNome() + ":"
						+ corAnt);
				if (restric.contains(corProx)) {
					removePaths.add(path);
					break;
				}
			}
		}
		paths.removeAll(removePaths);

		return paths;
	}

	private void depthFirst(int niv, int max, Router act,
			ArrayList<ArrayList<Router>> paths) {
		if (++niv > max) {
			ArrayList<Router> temp = new ArrayList<Router>();
			temp.add(act);
			paths.add(temp);
			return;
		}
		for (Router parent : act.preds) {
			depthFirst(niv, max, parent, paths);
			for (ArrayList<Router> path : paths) {
				if (path.get(path.size() - 1) == parent) {
					path.add(act);
				}
			}
		}
	}

	// Do getPaths for all pairs (source, sink)
	public ArrayList<ArrayList<Router>> pathsComputation(Graph graph) {
		ArrayList<ArrayList<Router>> paths = null;// = new ArrayList<>();
		ArrayList<ArrayList<Router>> allPaths = new ArrayList<>();
		for (Router src : graph.getVertices()) {
			for (Router dst : graph.getVertices()) {
				if (!src.getNome().equals(dst.getNome())) {
					paths = getPaths(graph, src, dst);
					allPaths.addAll(paths);
					for (ArrayList<Router> path : paths) {
						String dest = dst.getNome();
						for (Router sw : path) {
							if (path.indexOf(sw) != path.size() - 1) {
								String op = sw.getLink(
										path.get(path.indexOf(sw) + 1))
										.getCor();
								String ip = (path.indexOf(sw) == 0) ? "I"
										: sw.getLink(
												path.get(path.indexOf(sw) - 1))
												.getCor();
								sw.addRP(ip, dest, op);
								// Need to change to just make RP for
								// simplePaths
							}
						}
					}
				}
			}
		}
		for (Router atual : graph.getVertices()) {
			packOutputPort(atual);
			// packInputPort(atual);
		}

		return allPaths;
	}

	// Do output combinations
	private ArrayList<String> getOutputCombinations() {
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

	// Compute the regions
	public void regionsComput(Graph grafo) {
		ArrayList<String> opComb = getOutputCombinations();
		for (Router sw : grafo.getVertices()) {
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

	}

	// Adjust the regions to avoid overlap
	public void adjustsRegions(Graph grafo) {
		for (Router sw : grafo.getVertices()) {
			ArrayList<Region> regionsTemp = new ArrayList<>();
			ArrayList<Region> regionsRemov = new ArrayList<>();
			for (Region reg : sw.getRegions()) {
				ArrayList<String> strgs = getStranges(reg);

				if (strgs != null) {
					String[] extrems = getExtrems(strgs);
					int xmin = Integer.parseInt(extrems[0].substring(0, 1));
					int ymin = Integer.parseInt(extrems[0].substring(1, 2));
					int xmax = Integer.parseInt(extrems[1].substring(0, 1));
					int ymax = Integer.parseInt(extrems[1].substring(1, 2));

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
	private ArrayList<ArrayList<String>> getDestinations(int xmin, int xmax,
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
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && up && !right && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && up && !left && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && down && !left && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (up && down && !right && !left) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				if (x < xmin)
					dstTemp1.add(dst);
				else if (x > xmax)
					dstTemp2.add(dst);
			}
		} else if (left && right && !up && !down) {
			for (String dst : reg.getDst()) {
				int y = Integer.parseInt(dst.substring(1, 2));
				if (y < ymin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && !up && !down && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (right && !left && !down && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (down && !up && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (y > ymax)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
				if (y < ymin)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (!up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.substring(0, 1));
				int y = Integer.parseInt(dst.substring(1, 2));
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

	private boolean touchLeft(int xmin, Region reg) {
		return (xmin == reg.getXmin());
	}

	private boolean touchRight(int xmax, Region reg) {
		return (xmax == reg.getXmax());
	}

	private boolean touchUp(int ymax, Region reg) {
		return (ymax == reg.getYmax());
	}

	private boolean touchDown(int ymin, Region reg) {
		return (ymin == reg.getYmin());
	}

	// [0] - DownLeft [1]- UpRight
	private static String[] getExtrems(ArrayList<String> dsts) {
		String Xs = "";
		String Ys = "";
		String[] xtrems = new String[2];

		for (String s : dsts) {
			Xs = Xs.concat(Character.toString(s.charAt(0)));
			Ys = Ys.concat(Character.toString(s.charAt(1)));
		}
		char[] X = Xs.toCharArray();
		char[] Y = Ys.toCharArray();

		Arrays.sort(X);
		Arrays.sort(Y);

		xtrems[1] = String.valueOf(X[X.length - 1])
				+ String.valueOf(Y[Y.length - 1]);
		xtrems[0] = String.valueOf(X[0]) + String.valueOf(Y[0]);

		return xtrems;

	}

	// Return number of common sides of the box formed by strangers and the
	// region
	private int nSides(Region reg, ArrayList<String> strgs) {
		String[] strgsXtrems = getExtrems(strgs);
		int sides = 0;

		if (Integer.parseInt(strgsXtrems[0].substring(0, 1)) == reg.getXmin())
			sides++;
		if (Integer.parseInt(strgsXtrems[0].substring(1, 2)) == reg.getYmin())
			sides++;
		if (Integer.parseInt(strgsXtrems[1].substring(0, 1)) == reg.getXmax())
			sides++;
		if (Integer.parseInt(strgsXtrems[1].substring(1, 2)) == reg.getYmax())
			sides++;

		return sides;

	}

	// Delete routers inside of box defined by extremes
	private void deleteFromRegion(String[] extrems, Region reg) {
		int xmin = Integer.parseInt(extrems[0].substring(0, 1));
		int ymin = Integer.parseInt(extrems[0].substring(1, 2));
		int xmax = Integer.parseInt(extrems[1].substring(0, 1));
		int ymax = Integer.parseInt(extrems[1].substring(1, 2));
		for (int i = xmin; i <= xmax; i++) {
			for (int j = ymin; j <= ymax; j++) {
				String dst = i + "" + j;
				reg.getDst().remove(dst);
			}
		}
	}

	// Return wrong destinations
	public ArrayList<String> getStranges(Region reg) {
		ArrayList<String> strg = new ArrayList<String>();
		int xmin = reg.getXmin(), xmax = reg.getXmax();
		int ymin = reg.getYmin(), ymax = reg.getYmax();
		for (int x = xmin; x <= xmax; x++) {
			for (int y = ymin; y <= ymax; y++) {
				String dest = x + "" + y;
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
	public ArrayList<Region> makeRegions(ArrayList<String> dsts, String ip,
			String op) {
		ArrayList<Region> result = new ArrayList<Region>();
		String[] extrems = getExtrems(dsts);
		int Xmin = Integer.parseInt(extrems[0].substring(0, 1));
		int Ymin = Integer.parseInt(extrems[0].substring(1, 2));
		int Xmax = Integer.parseInt(extrems[1].substring(0, 1));
		int Ymax = Integer.parseInt(extrems[1].substring(1, 2));

		while (!dsts.isEmpty()) {
			int Lmin = Ymin, Cmax = Xmax;
			int Cmin = Xmin, Lmax = Ymax;

			boolean first = true;
			for (int line = Lmax; line >= Lmin; line--) {
				for (int col = Cmin; col <= Cmax; col++) {
					if (first) {
						if (dsts.contains(col + "" + line)) {
							Cmin = col;
							Lmax = line;
							first = false;
						}
					} else {
						if (!dsts.contains(col + "" + line)) { // if stranger
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

	private Region montaRegiao(int xmin, int ymin, int xmax, int ymax,
			String ip, String op) {
		ArrayList<String> dst = new ArrayList<String>();
		for (int x = xmin; x <= xmax; x++)
			for (int y = ymin; y <= ymax; y++)
				dst.add(x + "" + y);

		return (new Region(ip, dst, op));
	}

	// Check if regions r1 and r2 can be merged
	private boolean CanBeMerged(Region r1, Region r2) {
		boolean canBeMerged = false;

		if (AreNeighbours(r1, r2) && FormBox(r1, r2) && OpIsSub(r1, r2)) {
			canBeMerged = true;
		}

		return canBeMerged;
	}

	// Calculates reachability
	public double reachability(Graph grafo, Router orig) {
		double reaches = 0, total = grafo.getVertices().size() - 1;
		for (Router dest : grafo.getVertices()) {
			if (orig != dest) {
				if (orig.reaches(dest)) {
					reaches++;
				}
			}
		}
		return (reaches / total);
	}

	// Merge the regions of a router
	void Merge(Graph grafo, Router router, double reachability) {
		ArrayList<Region> bkpListRegion = null;
		boolean wasPossible = true;

		while (reachability(grafo, router) >= reachability && wasPossible) {
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
	private boolean mergeUnitary(Router router) {
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
	private String getUpRightMerged(Region r1, Region r2) {
		String upRight;

		upRight = Integer.toString(Math.max(
				Integer.parseInt(r1.getUpRight().substring(0, 1)),
				Integer.parseInt(r2.getUpRight().substring(0, 1))))
				+ "." + Integer.toString(Math.max(
						Integer.parseInt(r1.getUpRight().substring(1, 2)),
						Integer.parseInt(r2.getUpRight().substring(1, 2))));

		return upRight;
	}

	// Return DownLeft identifier after merge
	private String getDownLeftMerged(Region r1, Region r2) {
		String downLeft;

		downLeft = Integer.toString(Math.min(
				Integer.parseInt(r1.getDownLeft().substring(0, 1)),
				Integer.parseInt(r2.getDownLeft().substring(0, 1))))
				+ "." + Integer.toString(Math.min(
						Integer.parseInt(r1.getDownLeft().substring(1, 2)),
						Integer.parseInt(r2.getDownLeft().substring(1, 2))));

		return downLeft;
	}

	// return the Output ports after merge
	private String getOpMerged(Region r1, Region r2) {
		String op;

		if (r1.getOp().contains(r2.getOp())) {
			op = r2.getOp();
		} else {
			op = r1.getOp();
		}

		return op;
	}

	// return the Input ports after merge
	private String getIpMerged(Region r1, Region r2) {
		String ip = new String(r2.getIp());

		for (int i = 0; i < r1.getIp().length(); i++) {
			if (!ip.contains(r1.getIp().substring(i, i + 1)))
				ip += r1.getIp().substring(i, i + 1);
		}
		return ip;
	}

	private String mergeString(String s1, String s2) {
		String ip = new String(s2);

		for (int i = 0; i < s1.length(); i++) {
			if (!ip.contains(s1.substring(i, i + 1)))
				ip += s1.substring(i, i + 1);
		}
		return ip;
	}

	// Check if regions r1 and r2 are neighbours
	public boolean AreNeighbours(Region r1, Region r2) {
		boolean areNeighbours = false;

		int Xmax1 = Integer.parseInt(r1.getUpRight().substring(0, 1));
		int Xmax2 = Integer.parseInt(r2.getUpRight().substring(0, 1));
		int Ymax1 = Integer.parseInt(r1.getUpRight().substring(1, 2));
		int Ymax2 = Integer.parseInt(r2.getUpRight().substring(1, 2));

		int Xmin1 = Integer.parseInt(r1.getDownLeft().substring(0, 1));
		int Xmin2 = Integer.parseInt(r2.getDownLeft().substring(0, 1));
		int Ymin1 = Integer.parseInt(r1.getDownLeft().substring(1, 2));
		int Ymin2 = Integer.parseInt(r2.getDownLeft().substring(1, 2));

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
	public boolean FormBox(Region r1, Region r2) {

		if ((Integer.parseInt(r1.getUpRight().substring(0, 1)) == Integer
				.parseInt(r2.getUpRight().substring(0, 1)) && Integer
				.parseInt(r1.getDownLeft().substring(0, 1)) == Integer
				.parseInt(r2.getDownLeft().substring(0, 1)))
				|| (Integer.parseInt(r1.getUpRight().substring(1, 2)) == Integer
						.parseInt(r2.getUpRight().substring(1, 2)) && Integer
						.parseInt(r1.getDownLeft().substring(1, 2)) == Integer
						.parseInt(r2.getDownLeft().substring(1, 2)))) {
			return true;
		}

		return false;
	}

	// Check if output port are subsets
	public boolean OpIsSub(Region r1, Region r2) {

		String r1Op = Router.sortStrAlf(r1.getOp());
		String r2Op = Router.sortStrAlf(r2.getOp());
		if (r1Op.contains(r2Op) || r2Op.contains(r1Op)) {
			return true;
		}

		return false;
	}

}