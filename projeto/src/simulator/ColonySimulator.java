package simulator;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import exceptions.NonPositive;
import grafo.Graph;
import my_java_dom_parser.Data;
import my_java_dom_parser.SaxParser;
import my_java_dom_parser.Weights;



public class ColonySimulator {
	
	protected static int antcolsize;
	
	static Graph grafo;
	static Data dados;
	static PEC pec;
	static List<Ant> ants = new LinkedList<Ant>();
	static EvAnt_Move EventMoveClass;
	static EvPhero_Evap EventEvapClass;
	static SaxParser MySax = new SaxParser();
	static final int SPACEDVALUES =20;
	static double reportincrements;
	

	

	static double time;
	

	

	public static void main(String[] args) {
		
		dados = MySax.MySaxParser("data1.xml"); 
		
		grafo = new Graph(dados.getNbNodes());
		antcolsize =1;
		reportincrements = dados.getSimulation().getFinalinst()/SPACEDVALUES;
		Report report = new Report(reportincrements);
		
		
		int currentpath=0;
		
		

		
		try {
			for (Weights w :  dados.getEdges()) {
				grafo.addEdge(w.getDeparture(), w.getArraival(), w.getWeight());
			}
		}
		catch (NonPositive ex) {
			System.out.println(ex.getMessage());
			System.exit(2);
		}
		
		
		
		
		pec = new PEC();

		for (int i=0; i<dados.getSimulation().getColonySize(); i++)
			ants.add(new Ant(antcolsize, i));


		for (Ant ant: ants)
			pec.addEvPEC(new EvAnt_Move(Event.expRandom(dados.getDelta()), ant));
		
		
		EvReport evreport = new EvReport(reportincrements);
				
			pec.addEvPEC(evreport);
		
		Event currentEvent = pec.nextEvPEC();
		
		
		
		double currentTime = currentEvent.time_stamp;
		
		
		while (currentTime < dados.getSimulation().getFinalinst()) {
			
			if (currentEvent instanceof EvAnt_Move) {
				currentEvent.simulate();
				int current_ant = ((EvAnt_Move) currentEvent).getAnt();
				//System.out.println("current ant : "+ current_ant + " hamilton atual " + report.getHamilton() + "\n");
				if(ants.get(current_ant).hasHamiltonCycle(dados.getNbNodes()+1)) {
					try {
						currentpath=ants.get(current_ant).bestPath(report.getOptimalCycleWeight() ,ants.get(current_ant).calcWeight(ants.get(current_ant).getPath().getPathWeight()));
						ants.get(current_ant).placingPheroSetEvents(currentpath, ants.get(current_ant).getPath().getVisited(), ants.get(current_ant).getPath().getPathWeight(), currentTime );
						if (report.CheckForCycleUpdate(currentpath)) {
							ants.get(current_ant).getPath().getVisited().remove(dados.getNbNodes());
							ants.get(current_ant).getPath().getPathWeight().remove(dados.getNbNodes()-1);
							report.setHamilton((List<Integer>) deepClone(ants.get(current_ant).getPath().getVisited()));
						}
					}
					catch (NonPositive ex) {
						System.out.println("Path weight cannot be negative");
						System.exit(1);
					}
					ants.get(current_ant).removeCycle();
				}
				report.updateReport(currentTime, currentEvent);
			}
			else if (currentEvent instanceof EvPhero_Evap) {
				currentEvent.simulate();
				report.updateReport(currentTime, EventEvapClass);
			}
			else {
				currentEvent.simulate();
				System.out.println(report);
				report.updateinstant();
			}
			if(!pec.isEmpty()) {	
				currentEvent = pec.nextEvPEC();
				currentTime = currentEvent.time_stamp;
			}
					
		}
		currentEvent.simulate();
		System.out.println(report);
		report.updateinstant();
		System.out.println(pec);
	}
	
	public static Object deepClone(Object object) {
	    try {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      ObjectOutputStream oos = new ObjectOutputStream(baos);
	      oos.writeObject(object);
	      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	      ObjectInputStream ois = new ObjectInputStream(bais);
	      return ois.readObject();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    }
	  }
}