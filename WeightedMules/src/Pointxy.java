

import java.util.ArrayList;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;


public class Mule {

	public static int indexCount = 1;
	int index = 0;

	double x = 0.0;
	double y = 0.0;

	double origin_x = 0.0;
	double origin_y = 0.0;

	double origin_LS_x = 0.0;//used for local search iterations
	double origin_LS_y = 0.0;
	
	double Px = 0.0;//used for potential field
	double Py = 0.0;
	
	double currentSumOfDistansesToClosestNodes = 0.0;

	double minDistToTestMule=0.0;//used for kMedian placement
	Mule minDistMule;
	int numberOfClosestNodes=0;//used for EMS coverage. maintains the number of nodes which are closest to it than to other mules
	public int EMSLocationIndex;//ems

	public Environment env = null;


	public static Random rand = null;

	public double distance;
	public ArrayList<Node> AllNodes;
	public ArrayList<Node> CloseNodes;
	public ArrayList<Node> FarNodes;
	public Node atNode;

	public int numberOfFixs=0;//for constraint version

	public boolean converged = false;
	public boolean active = true;//for capacitated version

	public double availabilityTime=0;
	public double travelDistance=0;


	private Node bestAlternativeNode;


	public Mule(double x, double y, Environment Env) {
		index = indexCount;
		indexCount++;
		//    rand = Main.r1;//HY to plant random seed
		rand=new Random();

		this.x = x;
		this.y = y;
		origin_x = x;
		origin_y = y;
		origin_LS_x = x;
		origin_LS_y = y;
		Px = x;
		Py = y;
		env = Env;
		AllNodes=env.getNodes();
		CloseNodes=new ArrayList<Node>();

	}
	
	public Mule(Pointxy p, Environment Env) {//ems
		index = indexCount;
		indexCount++;
		//    rand = Main.r1;//HY to plant random seed
		rand=new Random();

		this.x = p.getX();
		this.y = p.getY();
		origin_x = p.getX();
		origin_y = p.getY();
		origin_LS_x = p.getX();
		origin_LS_y = p.getY();
		Px = p.getX();
		Py = p.getY();
		env = Env;
		AllNodes=env.getNodes();
		CloseNodes=new ArrayList<Node>();

	}
	public void addNode(Node n) {
		//	System.out.println("adding  node "+n+" to close nodes");
		CloseNodes.add(n);
	}
	public void removeNode(Node n) {
		//	System.out.println("adding  node "+n+" to close nodes");
		CloseNodes.remove(n);
	}
	public void move(Node n) {
		x=n.x;
		y=n.y;
	}
	public void move(double newx, double newy){
		x=newx;
		y=newy;
	}
	public String toString(){
		return "Mule"+index+ " x="+x+ " y="+y+" available at time"+getAvailabilityTime()+" travelDistance="+travelDistance;
	}
	public double getAvailabilityTime() {
		return availabilityTime;
	}
	public double getTimeToAvailability(double time) {
		return availabilityTime-time;
	}
	public void setAvailabilityTime(double availabilityTime) {
		this.availabilityTime = availabilityTime;
	}
	public boolean isAvailable(double time) {
		return availabilityTime<=time;
	}
	public double getTravelDistance() {
		return travelDistance;
	}
	public void setTravelDistance(double travelDistance) {
		this.travelDistance = travelDistance;
	}
	public double moveToCentroid() {
//		for(Node n1:CloseNodes){
//			if(n1.isWorking()==false){
//				CloseNodes.remove(n1);
//			}
//		}
		System.out.println("calculating centroid from "+CloseNodes.size()+" close nodes - only working ones");

		double sumX=0;
		double sumY=0;
		double totalWeight=0;
	
		if(CloseNodes.size()>0){
			for(Node n:CloseNodes){
				System.out.println("checking  "+n);
				sumX+=n.x*n.getWeight();
				sumY+=n.y*n.getWeight();
				totalWeight+=n.getWeight();
				
			}
			System.out.println("sumX= "+sumX+" sumY= "+sumY+ " totalWeight= "+totalWeight);

			sumX=sumX/totalWeight;
			sumY=sumY/totalWeight;
			double dist=Math.sqrt(Math.pow((x-sumX),2)+Math.pow((y-sumY),2));
			if(dist!=0){
				System.out.println("moving "+this +" to centroid at  <"+ sumX+","+sumY+"> dist "+dist);
				move(sumX,sumY);
			}
			return dist;
		}
		else{
			System.out.println("not moving "+this +" no close nodes");

			return 0;
		}
	}

	public Node getAtNode() {
		return atNode;
	}
	public void setAtNode(Node atNode) {
		this.atNode = atNode;
	}
	public double getMinDistToTestMule() {
		return minDistToTestMule;
	}
	public void setMinDistToTestMule(double minDistToTestMule) {
		this.minDistToTestMule = minDistToTestMule;
	}
	public Mule getMinDistMule() {
		return minDistMule;
	}
	public void setMinDistMule(Mule minDistMule) {
		this.minDistMule = minDistMule;
	}
	public boolean isConverged() {
		return converged;
	}
	public void setConverged(boolean converged) {
		this.converged = converged;
	}
	public double calculateSumOfDistansesToClosestNodes(double x, double y){
		double sum=0;
		double dist=env.MAX_X+env.MAX_Y;
		System.out.println("calculating sum of distances to  "+CloseNodes.size() +"  close nodes");
		if(CloseNodes.isEmpty()){
			System.out.println("no close nodes returning 0");
			return 0.0;
		}
		else{
			for(Node n:CloseNodes){
				dist=Math.sqrt(Math.pow(x-n.x, 2)+Math.pow(y-n.y, 2));
				sum+=dist*n.getWeight();//for weighted version
			}

		}		
		System.out.println("sum of distances for "+x+","+y+" = "+sum);

		return sum;
	}
	public boolean isActive() {
		return numberOfFixs<Main.MaxFixesPerMule;
	}
	
	public int getNumberOfClosestNodes() {
		return numberOfClosestNodes;
	}

	public void setNumberOfClosestNodes(int numberOfClosestNodes) {
		this.numberOfClosestNodes = numberOfClosestNodes;
	}
	
}
