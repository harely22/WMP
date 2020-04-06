

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Node implements Comparable<Node>{
	public static int indexCount = 1;

	public int index ;
	double weight = 1.0;

	public double x = 0.0;
	public double y = 0.0;
	private Environment env;
	public double downDuration=0.0;
	public double failStartTime=0.0;
	public double failEndTime=0.0;

	private boolean working=true;
	private boolean hasMule=false;//for k median

	private boolean beingFixed=false;
	private double minDistFromMules=Main.MAX_X+Main.MAX_Y;
	private double minDistFromCenter=Main.MAX_X+Main.MAX_Y;//used for non uniform failure setting
	private double maxDistFromCenter=0;//used for non uniform failure setting

	private Mule closestMule;
	private Mule originalMule;

	public Node(double x, double y, Environment Env) {

		index = indexCount;
		indexCount++;
		//System.out.println("Index count is "+indexCount);
		this.x = x;
		this.y = y;
		env = Env;
		setWorking(true);

	}
	public Node(double x, double y, double weight, Environment Env) {

		index = indexCount;
		indexCount++;
		//System.out.println("Index count is "+indexCount);
		this.x = x;
		this.y = y;
		this.weight=weight;
		env = Env;
		setWorking(true);

	}
	public void setFail(boolean fail, double statusChangeTime) {
		
		if(fail==true){
			failStartTime=statusChangeTime;
			setWorking(false);
		}
		else{
			failEndTime=statusChangeTime;
			setWorking(true);
		}
	}
		public String toString(){
			return "Node"+index+ "   "+x+ "  "+y+" W="+weight+" isWorking="+working+" minDist "+minDistFromMules+" dist from center "+minDistFromCenter;
		}

		@Override
		public int compareTo(Node other) {
			if(this.minDistFromMules>other.minDistFromMules){
				return 1;
			}
			return 0;
		}

		public void setClosestMule(Mule m) {
			closestMule=m;		
		}
		public Mule getClosestMule() {
			return closestMule;		
		}
		public void setClosestAvailableMule(Mule m) {
			closestMule=m;		
		}
		public double getDownDuration() {
			return downDuration;
		}

		public void setDownDuration(double downDuration) {
			this.downDuration = downDuration;
		}

		public Mule getOriginalMule() {
			return originalMule;
		}

		public void setOriginalMule(Mule originalMule) {
			this.originalMule = originalMule;
		}
		public double getMinDistFromMules() {
			return minDistFromMules;
		}

		public void setMinDistFromMules(double minDistFromMules) {
			this.minDistFromMules = minDistFromMules;
		}

		public boolean hasMule() {
			return hasMule;
		}

		public void setHasMule(boolean hasMule) {
			this.hasMule = hasMule;
		}

		public double getMinDistFromCenter() {
			return minDistFromCenter;
		}

		public void setMinDistFromCenter(double minDistFromCenter) {
			this.minDistFromCenter = minDistFromCenter;
		}

		public double getMaxDistFromCenter() {
			return maxDistFromCenter;
		}

		public void setMaxDistFromCenter(double maxDistFromCenter) {
			this.maxDistFromCenter = maxDistFromCenter;
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}
		public boolean isWorking() {
			return working;
		}
		public void setWorking(boolean working) {
			this.working = working;
		}

	}
