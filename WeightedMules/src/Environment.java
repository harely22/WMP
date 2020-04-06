import java.util.*;
import java.awt.Point;
import java.awt.geom.Point2D;


public class Environment {

	public static int NUM_Nodes = Main.NUM_Nodes;
	public static int NUM_Mules =Main.NUM_Mules;

	public static int NumberOfFailures=Main.NumberOfFailures;
	public static int NumberOfFailDurations=Main.NumberOfFailDurations;
	public static int MAX_X = Main.MAX_X;
	public static int MAX_Y =Main.MAX_Y;
	public static int MAX_W =10;

	public ArrayList<Node> nodes = null;

	public ArrayList<Mule>  mules=null;
	ArrayList<Mule> testMules=new ArrayList<Mule>();//for kMedian placement
	ArrayList<Node> testNodes=new ArrayList<Node>();//for kMedian placement

	public ArrayList<Mule>  placedMules=null;
	public ArrayList<Failure>  failures=null;
	Random rand = null;
	Random randp = null;//for DSA

	public Grid grid = null;//HY private
	private boolean firstPlacement=true;//so not to count first move
	private boolean firstKPlacement=true;//so not to count first move
	public double totalNodesWeight;

	//	private HungarianAlgorithm ha;//used for optimally solving the assignment problem
	//	private int[] matchMuleToNode;//used for optimally solving the assignment problem

	public Environment() {       //HY added to change ranges
		rand = Main.r1;//HY to plant random seed
		randp = Main.r2;//HY to plant random seed

		System.out.println("Setting up new Grid "+ MAX_X+","+MAX_Y);
		grid = new Grid(MAX_X, MAX_Y);
		Node.indexCount=0;
		placeNodes();
		Mule.indexCount=0;
		placeMules();
		assignNodesToMules();
		Failure.indexCount=0;
		setFailures();
		Collections.sort(failures);
		setOriginalMules();
	}

	private void assignNodesToMules() {
		System.out.println("Assigning nodes to mules");
		Mule m;
		for(Node n:nodes){
			//		System.out.println("Assigning node "+n);

			m=getClosestMule(n);
			//		System.out.println("Assigning node "+n+" to "+m);
			m.addNode(n);
			n.setClosestMule(m);
		}
	}

	private void assignNodesToAvailableMules(ArrayList<Mule> availableMules,double time) {
		System.out.println("Assigning nodes to available mules");
		Mule m;
		for(Node n:nodes){
			//		System.out.println("Assigning node "+n);

			m=getClosestAvailableMule(availableMules,n,time);
			//		System.out.println("Assigning node "+n+" to "+m);
			if(m!=null){
				m.addNode(n);
				n.setClosestMule(m);
			}
		}
	}

	private void assignWorkingNodesToAvailableMules(ArrayList<Mule> availableMules,double time) {
		System.out.println("Assigning working nodes to available mules");
		Mule m;
		for(Node n:nodes){
			//		System.out.println("Assigning node "+n);
			if(n.isWorking()){
				m=getClosestAvailableMule(availableMules,n,time);
				//		System.out.println("Assigning node "+n+" to "+m);
				if(m!=null){
					m.addNode(n);
					n.setClosestMule(m);
				}
			}
		}
	}

	public void placeNodes(){ 

		System.out.println("Placing "+NUM_Nodes+" Nodes");

		nodes = new ArrayList<Node>();

		//		if(Main.MannualSettingMode){
		//			nodes.add(new Node(1,1,this));
		//		}
		//		else 
		if(Main.NodeGridMode){
			if(NUM_Nodes==MAX_X*MAX_Y){//number of nodes must be equal to x*y
				for(int loopx = 0; loopx<MAX_X;loopx++){
					for(int loopy = 0; loopy<MAX_Y; loopy++){
						nodes.add(new Node(loopx,loopy, this));//int for debug
					}
				}
			}
			else{
				for(int loopx = 0; loopx<=(int) Math.sqrt(NUM_Nodes);loopx++){
					for(int loopy = 0; loopy<= (int)Math.sqrt(NUM_Nodes); loopy++){
						if(nodes.size()<Main.NUM_Nodes){
							nodes.add(new Node(loopx*MAX_X/Math.sqrt(NUM_Nodes),loopy*MAX_Y/Math.sqrt(NUM_Nodes), this));//int for debug
						}
					}
				}
			}
		}
		else{
			for(int loopi = 0; loopi< NUM_Nodes; loopi++){
				//				nodes.add(new Node((int)(rand.nextDouble()*MAX_X),(int)(rand.nextDouble()*MAX_Y), this));//int for debug
				int x=(int)(rand.nextDouble()*MAX_X);
				int y=(int)(rand.nextDouble()*MAX_Y);
				int weight=(int)(rand.nextDouble()*MAX_W+1);
			//	int weight=1;
				nodes.add(new Node(x,y,weight, this));//int for debug
				totalNodesWeight+=weight;
			}
		}
		for(int loopi = 0; loopi< NUM_Nodes; loopi++){
			System.out.println(nodes.get(loopi));
		}
	}

	public void placeMules(){ 
		firstPlacement=true;
		System.out.println("placing "+NUM_Mules+" mules");

		mules = new ArrayList<Mule>();
		Mule.indexCount=1;
		placedMules = new ArrayList<Mule>();
		if(Main.MannualSettingMode){
			System.out.println("MannualSettingMode="+ Main.MannualSettingMode);

			//			mules.add(0,new Mule(2.5,2.5,this));
			//			mules.add(1,new Mule(2.5,7.5,this));
			//			mules.add(2,new Mule(7.5,2.5,this));
			//			mules.add(3,new Mule(7.5,7.5,this));

			mules.add(new Mule(MAX_X/6,MAX_Y/6,this));
			mules.add(new Mule(3*MAX_X/6,MAX_Y/6,this));
			mules.add(new Mule(5*MAX_X/6,MAX_Y/6,this));
			mules.add(new Mule(MAX_X/8,MAX_Y/2,this));
			mules.add(new Mule(3*MAX_X/8,MAX_Y/2,this));
			mules.add(new Mule(5*MAX_X/8,MAX_Y/2,this));
			mules.add(new Mule(7*MAX_X/8,MAX_Y/2,this));
			mules.add(new Mule(MAX_X/6,5*MAX_Y/6,this));
			mules.add(new Mule(3*MAX_X/6,5*MAX_Y/6,this));
			mules.add(new Mule(5*MAX_X/6,5*MAX_Y/6,this));


		}
		else if(Main.UniformSettingMode){// only works for power of 4
			for(int loopx = 0; loopx< Math.sqrt(NUM_Mules); loopx++){
				for(int loopy = 0; loopy< Math.sqrt(NUM_Mules); loopy++){
					Mule m=new Mule((1+2*loopx)*MAX_X/(2*Math.sqrt(NUM_Mules)),(1+2*loopy)*MAX_Y/(2*Math.sqrt(NUM_Mules)),this);
					mules.add(m);
					updateMinNodeDistance(m);
				}
			}
		}
		else if(Main.KMedianMode){// 

			kMedian(0);
		}
		else {
			for(int loopi = 0; loopi< NUM_Mules; loopi++){
				mules.add(new Mule((int)(rand.nextDouble()*MAX_X),(int)(rand.nextDouble()*MAX_Y), this));
			}
			farthestFirst2(0);
		}
		for(int loopi = 0; loopi< mules.size(); loopi++){
			System.out.println(mules.get(loopi));
		}
	}

	//	public void centroidAdjustment(){ //used first time with no travel stats
	//
	//		System.out.println("re-placing "+NUM_Mules+" mules to centroid position of their cluster");
	//
	//		for (Mule m:mules){
	//			System.out.println("printing close nodes for "+m +" "+ m.CloseNodes);
	//			m.moveToCentroid();
	//
	//		}
	//
	//	}

	public void rePlaceMules(double time){ 
		firstPlacement=false;
		System.out.println("re-placing "+NUM_Mules+" mules");
		ArrayList<Mule> occupiedMules=getOccupiedMules(time);
		System.out.println("occupiedMules "+occupiedMules);

		ArrayList<Mule> inactiveMules=getInactiveMules(time);
		System.out.println("inactiveMules "+inactiveMules);

		occupiedMules.removeAll(inactiveMules);
		System.out.println("occupiedMules after inactive removal"+occupiedMules);
		resetNodeDistances(); // set all distances to max
		for (Mule m:occupiedMules){
			updateMinNodeDistance1(m); //for ff
		}
		//for rest of mules run FF

		if(Main.CentroidAdjustmentMode){
			centroid(time);
		}
		else if(Main.KMedianMode){
			kMedian(time);
		}
		else if(Main.localSearchMode){
			localSearch(time);
		}
		else if(Main.PotentialFieldMode){
			potentialField(time); //not finished yet !!!
		}
		else{
			farthestFirst2(time);
		}
	}

	public void updateMinNodeToOccupiedMulesDistances(double time){ 
		firstPlacement=false;
		System.out.println("updating "+NUM_Mules+" mules");
		ArrayList<Mule> occupiedMules=getOccupiedMules(time);
		System.out.println("occupiedMules "+occupiedMules);
		//	resetNodeDistances();
		for (Mule m:occupiedMules){
			updateMinNodeDistance(m);
		}

	}
	public void updateMinNodeDistances(){ 
		for (Mule m:testMules){
			updateMinNodeDistance(m);
		}

	}
	public ArrayList<Mule> getOccupiedMules(double time){ 
		System.out.println("getting occupied mules at time "+time);
		ArrayList<Mule> occupiedMules=new ArrayList<Mule>();
		for (Mule m:mules){

			if( m.getAvailabilityTime()>time){
				occupiedMules.add(m);
			}
		}
		return occupiedMules;
	}
	public ArrayList<Mule> getInactiveMules(double time){ 
		System.out.println("getting inactive mules at time "+time);
		ArrayList<Mule> InactiveMules=new ArrayList<Mule>();
		for (Mule m:mules){

			if( m.numberOfFixs==Main.MaxFixesPerMule){
				InactiveMules.add(m);
			}
		}
		return InactiveMules;
	}
	public void resetNodeDistances() {
		System.out.println("resetting Node distances");

		for (Node n:nodes){
			n.setMinDistFromMules(MAX_X+MAX_Y);
		}
	}
	public void resetCloseNodes() {
		System.out.println("resetting close nodes");

		for (Mule m:mules){
			m.CloseNodes.clear();
		}
	}
	public double calcDist (Node n, Mule m) {
		double dist=Math.sqrt(Math.pow((n.x-m.x),2)+Math.pow((n.y-m.y),2));
		return dist;
	}

	public double calcDist (Mule n, Mule m) {
		double dist=Math.sqrt(Math.pow((n.x-m.x),2)+Math.pow((n.y-m.y),2));
		return dist;
	}
	private ArrayList<Mule> getPlacedMules (double time) {

		System.out.println("finding placed mules at time :"+time);
		ArrayList<Mule> placedMules=new ArrayList<Mule>();
		for(Mule m:mules){
			if(m.isActive()){
				if(m.getAvailabilityTime()>time){
					placedMules.add(m);
				}
			}
		}
		if(placedMules.isEmpty()){
			//		System.out.println("mules");
			//		printArray(mules);
			System.out.println("no mules placed and occupied at time :"+time+ " returning null ");

		}
		return placedMules;
	}

	public void farthestFirst (double time) {

		System.out.println("running farthest at time :"+time);
		ArrayList<Mule> placedMules=getPlacedMules(time);
		if(placedMules.isEmpty()){
			placedMules=new ArrayList<Mule>();
			mules.get(0).x=nodes.get(0).x;
			mules.get(0).y=nodes.get(0).y;
			System.out.println("choosing random mule :"+mules.get(0));
			placedMules.add(mules.get(0));
		}
		ArrayList<Mule> unPlacedMules=removePlacedMules(placedMules);
		ArrayList<Mule> inactiveMules=getInactiveMules(time);

		///		System.out.println("Placed mules="+placedMules.size());
		//		printArray(placedMules);
		//		System.out.println(" mules="+mules.size());
		//		printArray(mules);
		resetNodeDistances();
		int numberOfTimesToPlace=(mules.size()-placedMules.size()-inactiveMules.size());
		for(int i=0;i<numberOfTimesToPlace;i++){
			Node farthest=getFarthestNode(placedMules);
			System.out.println("farthest node ="+farthest);
			Mule closestMule=getClosestAvailableMule(unPlacedMules,farthest, time);
			System.out.println("closest mule ="+closestMule);

			if(closestMule==null){
				System.out.println("No availables mules to place. returning null");
				return;
			}
			if(firstPlacement==false){
				double dist=getDist(farthest, closestMule);
				System.out.println("dist ="+dist);
				closestMule.setTravelDistance(closestMule.getTravelDistance()+dist);
				closestMule.setAvailabilityTime(time+dist);

			}
			closestMule.move(farthest);
			closestMule.setAtNode(farthest);

			System.out.println("placing mule "+closestMule +" at node "+farthest);

			placedMules.add(closestMule);
			unPlacedMules.remove(closestMule);
			updateMinNodeDistance(closestMule);
		}

	}

	public void farthestFirst1 (double time) {//this finds the farthest only from newly placed mules not from occupied

		System.out.println("running farthest1 at time :"+time);
		ArrayList<Mule> occupiedMules=getPlacedMules(time);
		ArrayList<Mule> placedMules=new ArrayList<Mule>();
		ArrayList<Mule> unPlacedMules=removePlacedMules(occupiedMules);
		ArrayList<Mule> inactiveMules=getInactiveMules(time);

		if(occupiedMules.size()==mules.size()){
			System.out.println("No availables mules to place. returning null");
			return;
		}
		//find new anchor
		if(!occupiedMules.isEmpty()){
			Node farthest=getFarthestNode(occupiedMules);
			//	System.out.println("farthest node ="+farthest);
			Mule closestMule=getClosestAvailableMule(unPlacedMules,farthest, time);
			//System.out.println("closest mule ="+closestMule);
			double dist=getDist(farthest, closestMule);
			System.out.println("dist ="+dist);
			closestMule.setTravelDistance(closestMule.getTravelDistance()+dist);
			closestMule.setAvailabilityTime(time+dist);

			closestMule.move(farthest);
			closestMule.setAtNode(farthest);
			System.out.println("placing mule "+closestMule +" at node "+farthest);
			placedMules.add(closestMule);
			unPlacedMules.remove(closestMule);
		}
		else{
			System.out.println("choosing random mule :"+unPlacedMules.get(0));
			placedMules.add(unPlacedMules.get(0));
			unPlacedMules.remove(0);
		}
		///		System.out.println("Placed mules="+placedMules.size());
		//		printArray(placedMules);
		//		System.out.println(" mules="+mules.size());
		//		printArray(mules);
		resetNodeDistances();
		int numberOfTimesToPlace=(unPlacedMules.size());
		for(int i=0;i<numberOfTimesToPlace;i++){
			Node farthest=getFarthestNode(placedMules);
			System.out.println("farthest node ="+farthest);
			Mule closestMule=getClosestAvailableMule(unPlacedMules,farthest, time);
			System.out.println("closest mule ="+closestMule);

			if(closestMule==null){
				System.out.println("No availables mules to place. returning null");
				return;
			}
			if(firstPlacement==false){
				double dist=getDist(farthest, closestMule);
				System.out.println("dist ="+dist);
				closestMule.setTravelDistance(closestMule.getTravelDistance()+dist);
				closestMule.setAvailabilityTime(time+dist);

			}
			closestMule.move(farthest);
			closestMule.setAtNode(farthest);

			System.out.println("placing mule "+closestMule +" at node "+farthest);

			placedMules.add(closestMule);
			unPlacedMules.remove(closestMule);
			updateMinNodeDistance(closestMule);
		}

	}


	public void farthestFirst2 (double time) {// the assignment of mules to nodes is done optimally
		//also finds the farthest only from newly placed mules not from occupied

		System.out.println("running farthest2 at time :"+time);
		ArrayList<Mule> occupiedMules=getPlacedMules(time);
		ArrayList<Mule> inactiveMules=getInactiveMules(time);
		ArrayList<Mule> unPlacedMules=removePlacedMules(occupiedMules);
		ArrayList<Mule> VirtualPlacedMules=new ArrayList<Mule>();//used for optimal assignment
		ArrayList<Node> locationsToPlace=new ArrayList<Node>();
		if(occupiedMules.size()==mules.size()-inactiveMules.size()){
			System.out.println("No availables mules to place. returning null");
			return;
		}
		//find new anchor
		if(!occupiedMules.isEmpty()){ //find farthest from occupied
			Node farthest=getFarthestNode(occupiedMules);
			System.out.println("choosing Node :"+farthest +" as anchor");

			locationsToPlace.add(farthest); //add to locations sent to HA
			System.out.println("adding :"+farthest +" to destination list");

			VirtualPlacedMules.add(new Mule (farthest.x,farthest.y,this));


		}			
		else{// or random if no occupied
			System.out.println("choosing random mule :"+unPlacedMules.get(0) +" as anchor");
			Node n=getClosestNode(unPlacedMules.get(0));
			locationsToPlace.add(n); //add to locations sent to HA. it doesn't really have to be a node but for the allocation of HA it is needed
			System.out.println("adding :"+n +" to destination list");
			VirtualPlacedMules.add(unPlacedMules.get(0));
		}

		//Now find farthest from (virtual) newly placed
		resetNodeDistances();
		int numberOfTimesToPlace=(unPlacedMules.size()-1);
		for(int i=0;i<numberOfTimesToPlace;i++){
			Node farthest=getFarthestNode(VirtualPlacedMules);
			locationsToPlace.add(farthest); //add to locations sent to HA
			System.out.println("adding :"+farthest +" to destination list");
			VirtualPlacedMules.add(new Mule (farthest.x,farthest.y,this));
		}

		//Now create the distance matrix between (real) unplaced mules to the location list 
		double[][] distanceMatrix =new double[unPlacedMules.size()][locationsToPlace.size()];
		System.out.println("Distance matrix for "+unPlacedMules.size()+" mules:");

		for(int i=0;i<unPlacedMules.size();i++){
			System.out.print("Mule "+unPlacedMules.get(i).index+"<"+ unPlacedMules.get(i).x+","+unPlacedMules.get(i).y+"> dist:");

			for(int j=0;j<locationsToPlace.size();j++){
				distanceMatrix[i][j]=calcDist(locationsToPlace.get(i), unPlacedMules.get(j));
				System.out.print(" Node "+locationsToPlace.get(i).index+" "+ distanceMatrix[i][j]);

			}		
			System.out.println();
		}
		// solve the optimal assignment using HA	
		HungarianAlgorithm ha =new HungarianAlgorithm(distanceMatrix);
		System.out.println("Executing HA...");
		ha.execute();
		int[] matchMuleToNode=ha.getMatchJobByWorker();
		//		int[] matchMuleToNode=ha.getMatchWorkerByJob();


		//assign mules according to optimal assignment	
		System.out.println("Assigning mules to nodes");

		for(int i=0;i<unPlacedMules.size();i++){
			Mule m=unPlacedMules.get(i);
			int nodeIndex=matchMuleToNode[i];
			Node n=locationsToPlace.get(nodeIndex);
			double dist=calcDist(n, m);

			System.out.println("placing mule "+m +" at node "+n+" at dist "+dist+" distMatrix="+distanceMatrix[i][nodeIndex]);

			m.move(n);
			if(firstPlacement==false){
				m.setTravelDistance(m.getTravelDistance()+distanceMatrix[i][nodeIndex]);
				m.setAvailabilityTime(time+distanceMatrix[i][nodeIndex]);
			}
			m.setAtNode(n);

		}
	}

	@SuppressWarnings("unchecked")
	public void kMedian (double time) {
		//place test mules at every node that is not being fixed
		//remove the node that add the least to the sum of distances from remaining nodes
		//until m mules are left (placed + test)
		System.out.println("running kMedian at time :"+time);
		ArrayList<Mule> placedMules=getPlacedMules(time);
		ArrayList<Mule> unPlacedMules=removePlacedMules(placedMules);
		ArrayList<Mule> inactiveMules=getInactiveMules(time);

		
		resetNodeDistances();//reset to max
		int numberOfTimesToPlace=(mules.size()-placedMules.size()-inactiveMules.size());
		System.out.println("placedMules.size()"+placedMules.size());

		if(numberOfTimesToPlace==0){
			System.out.println("all mules are occupied");
			return;

		}

		// create test mules at nodes that are not being fixed
		testMules=new ArrayList<Mule>();
		testNodes=(ArrayList<Node>) nodes.clone();
		for(int j = 0; j< placedMules.size(); j++){
			testNodes.remove(placedMules.get(j).getAtNode());
			System.out.println("removing node "+ placedMules.get(j).getAtNode()+" because it is being fixed");
		}
		for(int loopi = 0; loopi< testNodes.size(); loopi++){
			testMules.add(new Mule(testNodes.get(loopi).x,testNodes.get(loopi).y, this));
			testMules.get(loopi).setAtNode(nodes.get(loopi));
			testNodes.get(loopi).setHasMule(true);
		}
		//	ArrayList<Mule> remainingMules=mules;
		//ArrayList<Mule> removedMules=null;

		int numberOfTimesToRemove=(testNodes.size()-numberOfTimesToPlace);
		System.out.println("removing "+numberOfTimesToRemove +" test mules");
		for(int i=0;i<numberOfTimesToRemove;i++){
			resetNodeDistances();
			updateMinNodeDistances(); // set for each node its minimal distance to any test mule
			Mule minCost=getMinCostNode(unPlacedMules,time);
			System.out.println("getMinCostNode ="+minCost);
			testMules.remove(minCost);
			updateMinNodeDistance(minCost);
		}

		placeMulesAtTestMules1(unPlacedMules,time);//1 for optimal

	}

	@SuppressWarnings("unchecked")


	private void placeMulesAtTestMules(ArrayList<Mule> unPlacedMules,double time) {
		System.out.println("placeMulesAtTestMules ");
		System.out.println("testMules: ");
		printArray(testMules);
		System.out.println("unPlacedMules :");
		printArray(unPlacedMules);
		//		ArrayList<Mule> placedMules=null;
		Mule test;
		double dist=0;
		double minDist;
		Mule closest=unPlacedMules.get(0);
		for(int loopi = 0; loopi< testMules.size(); loopi++){
			test=testMules.get(loopi);
			minDist=MAX_X+MAX_Y;
			System.out.println("find closest unplaced mule to "+test);
			for(int j = 0; j< unPlacedMules.size(); j++){
				dist=getDist(test,unPlacedMules.get(j));
				if(dist<minDist){
					minDist=dist;
					closest=unPlacedMules.get(j);
				}
				System.out.println("Checking "+unPlacedMules.get(j)+ " dist=" +dist);

			}
			closest.move(test.x,test.y);
			closest.setAtNode(test.getAtNode());
			System.out.println("moving closest "+closest);

			if(!firstKPlacement){
				closest.setTravelDistance(closest.getTravelDistance()+minDist);
				System.out.println("adding min dist "+minDist);
				closest.setAvailabilityTime(time+dist);


			}

			unPlacedMules.remove(closest);		
		}
		firstKPlacement=false;

	}
	private void placeMulesAtTestMules1(ArrayList<Mule> unPlacedMules,double time) {
		System.out.println("placeMulesAtTestMules optimaly ");

		ArrayList<Mule> locationsToPlace= testMules;
		//Now create the distance matrix between (real) unplaced mules to the location list 
		double[][] distanceMatrix =new double[unPlacedMules.size()][testMules.size()];
		System.out.println("Distance matrix for "+unPlacedMules.size()+" mules:");

		for(int i=0;i<unPlacedMules.size();i++){
			System.out.print("Mule "+unPlacedMules.get(i).index+"<"+ unPlacedMules.get(i).x+","+unPlacedMules.get(i).y+"> dist:");

			for(int j=0;j<locationsToPlace.size();j++){
				distanceMatrix[i][j]=calcDist(locationsToPlace.get(i), unPlacedMules.get(j));
				System.out.print(" Node "+locationsToPlace.get(j).index+" "+ distanceMatrix[i][j]);

			}		
			System.out.println();
		}
		// solve the optimal assignment using HA	
		HungarianAlgorithm ha =new HungarianAlgorithm(distanceMatrix);
		System.out.println("Executing HA...");
		ha.execute();
		int[] matchMuleToNode=ha.getMatchJobByWorker();
		//		int[] matchMuleToNode=ha.getMatchWorkerByJob();


		//assign mules according to optimal assignment	
		System.out.println("Assigning mules to nodes");

		for(int i=0;i<unPlacedMules.size();i++){
			Mule m=unPlacedMules.get(i);
			int nodeIndex=matchMuleToNode[i];
			Mule n=locationsToPlace.get(nodeIndex);
			double dist=calcDist(n, m);

			System.out.println("placing mule "+m +" at node "+n+" at dist "+dist+" distMatrix="+distanceMatrix[i][nodeIndex]);

			m.move(n.x,n.y);
			if(firstPlacement==false){
				m.setTravelDistance(m.getTravelDistance()+distanceMatrix[i][nodeIndex]);
				m.setAvailabilityTime(time+distanceMatrix[i][nodeIndex]);
			}
			//			m.setAtNode(n);
		}
	}
	//		private void placeMulesAtTestMules1(ArrayList<Mule> unPlacedMules,double time) {
	//////			System.out.println("placeMulesAtTestMules using smart heuristic ");
	//////			//this method finds for each new location the closest available mule.
	//////			//then sorts the min distances and places the one with the largest min distance first
	//////			//in this way we might lower the max dist
	//////			
	//			System.out.println("testMules: ");
	//			printArray(testMules);
	//			System.out.println("unPlacedMules :");
	//			printArray(unPlacedMules);
	//			//		ArrayList<Mule> placedMules=null;
	//			double dist=0;
	//			double minDist;
	//			Mule test;
	//			Mule closestMule;
	//			Mule maxMinDistMule;
	//			Mule muleToPlace;
	//
	//			Mule unplaced;
	//			for(int loopi = 0; loopi< testMules.size(); loopi++){
	//				test=testMules.get(loopi);
	//				minDist=MAX_X+MAX_Y;
	//				System.out.println("find closest distance from location "+ test+"  to a  mule");
	//
	//			for(int j = 0; j< unPlacedMules.size(); j++){
	//				unplaced =unPlacedMules.get(j);
	//				
	//					
	//					dist=getDist(test,unplaced);
	//					if(dist<minDist){
	//						minDist=dist;
	//						closestMule=unplaced;
	//					}
	//					System.out.println("Checking "+unPlacedMules.get(j)+ " dist=" +dist);
	//				}
	//				test.setMinDistToTestMule(minDist);
	//				test.setMinDistMule(closestMule);
	//
	//			}
	//			//find the mule with the largest mindist
	//			for(int i = 0; i< testMules.size(); i++){
	//				muleToPlace=unPlacedMules.get(i);
	//				maxMinDistMule=muleToPlace;
	//				double maxMinDist=0;
	//
	//			for(int j = 1; j< unPlacedMules.size(); j++){
	//				unplaced =unPlacedMules.get(j);
	//				if(unplaced.getMinDistFromTestMule()>maxMinDist){
	//						maxMinDist=unplaced.getMinDistFromTestMule();
	//						maxMinDistMule=unplaced;
	//					}
	//			//		System.out.println("Checking "+unPlacedMules.get(j)+ " dist=" +dist);
	//				
	//			}
	//			
	//			maxMinDistMule.move(closestTest.x,test.y);
	//			closest.setAtNode(test.getAtNode());
	//			System.out.println("moving closest "+closest);
	//
	//			if(!firstKPlacement){
	//				closest.setTravelDistance(closest.getTravelDistance()+minDist);
	//				System.out.println("adding min dist "+minDist);
	//				closest.setAvailabilityTime(time+dist);
	//
	//
	//			}
	//
	//			unPlacedMules.remove(closest);		
	//			firstKPlacement=false;
	//
	//		}

	private Mule getMinCostNode(ArrayList<Mule> unPlacedMules, double time) {
		//this is a not so elegant brute force way of calculating sum of distances for each test mule
		System.out.println("finding the minimal cost mule out of "+testMules.size());
		printArray(testMules);
		Mule test;
		Mule minMule=testMules.get(0);
		boolean first=true;

		double sum=0;
		double minSum=0;

		for(int loopi = 0; loopi< testMules.size(); loopi++){
			test=testMules.get(loopi);
			System.out.println("Checking "+test);

			testMules.remove(test);
			resetNodeDistances();
			updateMinNodeDistances();

			sum=calculateSumOfDistances();
			if(first){
				minSum=sum;
				first=false;
			}
			if(sum<minSum){
				minSum=sum;
				minMule=test;
			}
			testMules.add(loopi,test);

		}		
		System.out.println("the minimal cost is:"+minSum+" for "+minMule);

		return minMule;
	}

	private double calculateSumOfDistances() {

		double sum=0;
		for(int loopi = 0; loopi< NUM_Nodes; loopi++){
			sum+=nodes.get(loopi).getMinDistFromMules()*nodes.get(loopi).getWeight();//for weighted version
		}	
		System.out.println("calculating sum of distances="+sum);

		return sum;
	}

	public void centroid (double time) {

		System.out.println("running centroid at time :"+time);
		ArrayList<Mule> placedMules=getPlacedMules(time);
		ArrayList<Mule> unPlacedMules=removePlacedMules(placedMules);
		///		System.out.println("Placed mules="+placedMules.size());
		//		printArray(placedMules);
		//		System.out.println(" mules="+mules.size());
		//		printArray(mules);
		resetNodeDistances();
		resetCloseNodes();
		assignWorkingNodesToAvailableMules(unPlacedMules,time);
		//		int numberOfTimesToPlace=(mules.size()-placedMules.size());
		boolean converged=false;
		double sumOfDist=0;
		int roundCounter=0;
		while(!converged){
			//for(roundCounter=0;roundCounter<2;){
			System.out.println("centroid adjustment round "+roundCounter++);

			sumOfDist=0;
			for(Mule m:unPlacedMules){
				double dist=m.moveToCentroid();
				sumOfDist+=dist;
				if(firstPlacement==false){
					m.setTravelDistance(m.getTravelDistance()+dist);
				}
				if(dist!=0){
					System.out.println("moved "+ m+" dist "+dist);
					updateMinNodeDistance(m);
				}
			}
			if(sumOfDist==0){
				converged=true;
			}
		}
	}

	public void centroid1 (double time) {
		//disregards occupied mules 
		System.out.println("running centroid at time :"+time);

		ArrayList<Mule> occupiedMules=getPlacedMules(time);
		ArrayList<Mule> placedMules=new ArrayList<Mule>();
		ArrayList<Mule> unPlacedMules=removePlacedMules(occupiedMules);
		if(occupiedMules.size()==mules.size()){
			System.out.println("No availables mules to place. returning null");
			return;
		}
		if(!occupiedMules.isEmpty()){

			resetNodeDistances();
			assignNodesToAvailableMules(unPlacedMules,time);

			boolean converged=false;
			double sumOfDist=0;
			int roundCounter=0;
			while(!converged){
				System.out.println("centroid adjustment round "+roundCounter++);

				sumOfDist=0;
				for(Mule m:unPlacedMules){
					double dist=m.moveToCentroid();
					sumOfDist+=dist;
					if(firstPlacement==false){
						m.setTravelDistance(m.getTravelDistance()+dist);
					}
					if(dist!=0){
						System.out.println("moved "+ m+" dist "+dist);
						updateMinNodeDistance(m);
					}
				}
				if(sumOfDist==0){
					converged=true;
				}
			}
		}
	}


	public void localSearch (double time) {
		//This method places mules distributedly according to each ones best local position which minimizes :
		// do until all mules converge:
		//1.	for every mule that has not converged or occupied{	
		//2. 		for every value in its domain{
		//3.			calculates its closest neighboring nodes and the sum of distances }
		//4. 		move to min sum value	
		//5. 		if no movement then mark mule as converged	}}


		System.out.println("running localSearch at time :"+time);

		ArrayList<Mule> occupiedMules=getPlacedMules(time);
		ArrayList<Mule> convergedMules=new ArrayList<Mule>();
		ArrayList<Mule> unPlacedMules=removePlacedMules(occupiedMules);
		ArrayList<Mule> notConvergedMules=(ArrayList<Mule>) unPlacedMules.clone();

		if(occupiedMules.size()==mules.size()){
			System.out.println("No availables mules to place. returning null");
			return;
		}

		if(!occupiedMules.isEmpty()){
			boolean allConverged=false;
			int counter=0;
			while(notConvergedMules.isEmpty()==false && counter < Main.NUM_Nodes){
				resetNodeDistances();
				resetCloseNodes();
				assignNodesToAvailableMules(notConvergedMules,time);
				//go over unconverged mules
				System.out.println("going over "+notConvergedMules.size()+" un converged mules "+ " notConvergedMules.isEmpty() "+notConvergedMules.isEmpty());

				for(int i=0; i<notConvergedMules.size();i++){

					Mule m=notConvergedMules.get(i);

					double minSumOfDist=m.calculateSumOfDistansesToClosestNodes(m.x,m.y);
					double minX=m.x;
					double minY=m.y;
					//go over domain
					System.out.println("going over domain of "+m);
					int mr=2;//mobilityRange
					for(int x=Math.max(0,(int)m.x-mr);x<=Math.min(MAX_X, (int)m.x+mr);x++){
						for(int y=Math.max(0,(int)m.y-mr);y<=Math.min(MAX_Y, (int)m.y+mr);y++){
							//calculate sum value for each domain value
							double sumOfDist=m.calculateSumOfDistansesToClosestNodes( x,  y);
							if(sumOfDist<minSumOfDist){
								minX=x;
								minY=y;
							}
						}
					}
					if(minX==m.x&&minY==m.y){
						m.setConverged(true);
						double dist=Math.sqrt(Math.pow(m.x-m.origin_LS_x,2)+Math.pow(m.y-m.origin_LS_y, 2));
						m.setAvailabilityTime(time+dist);
						m.origin_LS_x=m.x;
						m.origin_LS_y=m.y;

						System.out.println("Mule "+ m.index+" has converged and moved "+dist);
						notConvergedMules.remove(m);
					}
					else{
						if(Main.DSA_Mode){
							double p=randp.nextDouble();
							if(p<0.7){
								m.move(minX,minY);
							}
						}
						else{
							m.move(minX,minY);

						}
					}
					//check if all converged					
					allConverged=true;
					for(Mule m1:notConvergedMules){
						if(m1.converged==false){
							allConverged=false;
						}
					}//converged check

				}//for mules
				System.out.println("counter "+counter);

				counter++;
			}//while
		}//if occupies not empty
	}//local search

	public void potentialField (double time) {
		//This method places mules distributedly according to potentialField :
		//Initial deployment grid
		//Each mule calculates the potential force from all other neighboring mules and nodes
		//it then moves to the location in its domain where the field value is the strongest
		// for x iterations :
		//1.	for every unoccupied mule {	
		//2. 		for every value in its domain{
		//3.			calculates its angle and distances to all mules and nodes(can stop at some dist)
		//4. 			calculate total force vector }
		//4. 		move to that direction	
		//5. 			}}


		System.out.println("running PF at time :"+time);

		ArrayList<Mule> occupiedMules=getPlacedMules(time);
		ArrayList<Mule> convergedMules=new ArrayList<Mule>();
		ArrayList<Mule> unPlacedMules=removePlacedMules(occupiedMules);
		ArrayList<Mule> notConvergedMules=(ArrayList<Mule>) unPlacedMules.clone();

		if(occupiedMules.size()==mules.size()){
			System.out.println("No availables mules to place. returning null");
			return;
		}

		//		if(!occupiedMules.isEmpty()){
		boolean allConverged=false;
		int counter=0;
		while(notConvergedMules.isEmpty()==false && counter < Main.NUM_Nodes){
			resetNodeDistances();
			resetCloseNodes();
			assignNodesToAvailableMules(notConvergedMules,time);
			//go over unconverged mules
			System.out.println("going over "+notConvergedMules.size()+" un converged mules "+ " notConvergedMules.isEmpty() "+notConvergedMules.isEmpty());

			for(int i=0; i<notConvergedMules.size();i++){

				Mule m=notConvergedMules.get(i);
				System.out.println("finding PF for "+m);

				double potential= calculatePotential (m,unPlacedMules);//calculates the force vector from all nodes and unoccupied mules. set new coordinates. return vector value. if ==0 set converged =true.

				if(potential==0){
					m.setConverged(true);	
					System.out.println("Mule "+ m.index+" has converged ");
					notConvergedMules.remove(m);

				}
				else{
					m.move(m.Px,m.Py);
					double dist=Math.sqrt(Math.pow(m.x-m.Px,2)+Math.pow(m.y-m.Py, 2));
					m.setAvailabilityTime(time+dist);
					System.out.println("Mule "+ m.index+" moving to PF position at  " +m.Px +" , "+ m.Py);


				}
				//check if all converged					
				allConverged=true;
				for(Mule m1:notConvergedMules){
					if(m1.converged==false){
						allConverged=false;
					}
				}//converged check

			}//for mules
			System.out.println("counter "+counter);

			counter++;
		}//while
		//		}//if occupies not empty
	}//potential field

	private double calculatePotential(Mule m, ArrayList<Mule> unPlacedMules) {
		m.Px=m.x;
		m.Py=m.y;
		double dx;
		double dy;
		double dist ;
		System.out.println("calculatePotential for "+m +"Px="+m.Px+" Py="+m.Py);
		double xForce=0;
		double yForce=0;
		Mule m1;
		for(int i=0;i<unPlacedMules.size();i++){
			if(!unPlacedMules.get(i).equals(m)){
				m1=unPlacedMules.get(i);
				dx=m.Px-m1.x;
				dy=m.Py-m1.y;
				dist= Math.sqrt(Math.pow(dx,2)+Math.pow(dy, 2));
				System.out.println("mule "+ m1.index +"("+m1.x+","+m1.y+")"+" added xForce="+dx/dist +" yForce="+dx/dist);

				if(dx!=0){
					xForce+=dx/dist;//times the weight...1
				}
				if(dy!=0){
					yForce+=dy/dist;
				}
				System.out.println("MULE "+ m1.index +"("+m1.x+","+m1.y+")"+" updated xForce="+xForce +" yForce="+yForce);

			}


		}
		Node n;
		for(int i=0;i<nodes.size();i++){

			n=nodes.get(i);

			dx=n.x-m.Px;//opposite direction
			dy=n.y-m.Py;
			dist= Math.sqrt(Math.pow(dx,2)+Math.pow(dy, 2));

			if(dx!=0){
				xForce+=dx/dist;
			}
			if(dy!=0){
				yForce+=dy/dist;
			}
			System.out.println("Node "+ n.index +"("+n.x+","+n.y+")"+" updated xForce="+xForce +" yForce="+yForce);

		}

		return xForce+yForce;
	}

	private ArrayList<Mule> removePlacedMules(ArrayList<Mule> placedMules) {
		ArrayList<Mule> unPlaced=new ArrayList<Mule>();
		for(Mule m:mules){
			if(m.isActive()){
			if(!placedMules.contains(m)){
				unPlaced.add(m);
			}
			}
		}
		return unPlaced;
	}

	public <T> void printArray(ArrayList<T> toPrint){
		for(int i=0; i<toPrint.size();i++){
			System.out.println(toPrint.get(i));
		}
	}
	private Node getFarthestNode() {
		double maxDist=0;
		Node max=nodes.get(0);
		for (Node n:nodes){
			//	System.out.println("checking node "+n);
			if( maxDist<n.getMinDistFromMules()){
				maxDist=n.getMinDistFromMules();
				max=n;
			}
		}
		return max;
	}
	private Node getFarthestNode(ArrayList<Mule> placedMules) {
		double maxDist=0;
		Node max=nodes.get(0);
		for (Mule m:placedMules){
			updateMinNodeDistance(m);
		}
		for (Node n:nodes){
			//	System.out.println("checking node "+n);
			if( maxDist<n.getMinDistFromMules()*n.getWeight()){//for weighted version
				maxDist=n.getMinDistFromMules();
				max=n;
			}
		}
		return max;
	}
	private ArrayList<Node> getCloseNodes(Node center, int numOfNeighbors) {
		ArrayList<Node>  neighbors=new ArrayList<Node> ();
		ArrayList<Node>  closeNeighbors=new ArrayList<Node> ();
		//	double minDist=Main.MAX_X+Main.MAX_Y;
		double dist;
		for (Node n:nodes){
			if(n!=center){
				dist=getDist(center, n);
				n.setMinDistFromCenter(dist);
				boolean inserted =false;
				int size=neighbors.size();
				for (int i=0;i<size;i++){
					if(dist<neighbors.get(i).getMinDistFromCenter()){
						neighbors.add(i,n);
						inserted =true;
						break;
					}

				}
				if(!inserted){
					neighbors.add(n);
				}
			}
			//	System.out.println(" neighbors for "+center);

			//		printArray(neighbors);
		}
		for (int j=0;j<numOfNeighbors;j++){
			closeNeighbors.add(neighbors.get(j));	
		}
		//	System.out.println(numOfNeighbors+" close neighbors for "+center);

		//	printArray(closeNeighbors);
		return closeNeighbors;
	}


	private ArrayList<Node> getFarNodes(Node center, int numOfNeighbors) {
		ArrayList<Node>  neighbors=new ArrayList<Node> ();
		ArrayList<Node>  farNeighbors=new ArrayList<Node> ();
		//	double minDist=Main.MAX_X+Main.MAX_Y;
		double dist;
		for (Node n:nodes){
			if(n!=center){
				dist=getDist(center, n);
				n.setMaxDistFromCenter(dist);
				boolean inserted =false;
				int size=neighbors.size();
				for (int i=0;i<size;i++){
					if(dist>neighbors.get(i).getMaxDistFromCenter()){
						neighbors.add(i,n);
						inserted =true;
						break;
					}

				}
				if(!inserted){
					neighbors.add(n);
				}
			}
			//	System.out.println(" neighbors for "+center);

			//		printArray(neighbors);
		}
		for (int j=0;j<numOfNeighbors;j++){
			farNeighbors.add(neighbors.get(j));	
		}
		//	System.out.println(numOfNeighbors+" close neighbors for "+center);

		//	printArray(closeNeighbors);
		return farNeighbors;
	}


	private Mule getClosestMule(Node n) {
		double minDist=MAX_X+MAX_Y;
		Mule min=mules.get(0);
		for (Mule m:mules){
			if(m.isActive()){
				double dist =getDist(n,m);
				//	System.out.println("checking node "+n);
				if( minDist>dist){
					minDist=dist;
					min=m;
				}
				else if( minDist==dist){

					if(min.getTravelDistance()> m.getTravelDistance()){
						//				System.out.println("changed to mule  "+m+" due to travel dist");

						min=m;
					}
				}
			}
		}
		return min;
	}
	private Node getClosestNode(Mule m) {
		double minDist=MAX_X+MAX_Y;
		Node min=nodes.get(0);
		for (Node n:nodes){
			double dist =getDist(n,m);
			//	System.out.println("checking node "+n);
			if( minDist>dist){
				minDist=dist;
				min=n;
			}

		}
		return min;
	}

	private Mule getClosestMule(Mule test,ArrayList<Mule> unplacedMules) {
		double minDist=MAX_X+MAX_Y;
		Mule min=mules.get(0);
		for (Mule m:unplacedMules){
			double dist =getDist(test,m);
			//	System.out.println("checking node "+n);
			if( minDist>dist){
				minDist=dist;
				min=m;
			}
			else if( minDist==dist){

				if(min.getTravelDistance()> m.getTravelDistance()){
					//				System.out.println("changed to mule  "+m+" due to travel dist");

					min=m;
				}
			}
		}
		return min;
	}


	private Mule getClosestPlacedMule(Node n) {
		double minDist=MAX_X+MAX_Y;
		Mule min;
		if(placedMules.isEmpty()){

			min=mules.get(0);
		}
		else{
			min=placedMules.get(0);
		}
		for (Mule m:placedMules){
			double dist =getDist(n,m);
			//	System.out.println("checking node "+n);
			if( minDist>dist){
				minDist=dist;
				min=m;
			}
			else if( minDist==dist){

				if(min.getTravelDistance()> m.getTravelDistance()){
					//			System.out.println("changed to mule  "+m+" due to travel dist");

					min=m;
				}
			}
		}
		return min;
	}
	private Mule getClosestAvailableMule(ArrayList<Mule> unplacedMules,Node n,double time) {

		double minDist=MAX_X+MAX_Y;
		Mule min=null;
		for (Mule m:unplacedMules){
			if(m.isAvailable(time)){
				double dist =getDist(n,m);
				//	System.out.println("checking node "+n);
				if( minDist>dist){
					minDist=dist;
					min=m;
				}
			}
		}
		return min;
	}
	private Mule getClosestAvailableMule(Failure f) {
		Node n=f.getNode();
		double minDist=MAX_X+MAX_Y;
		Mule min=null;
		for (Mule m:mules){
			if(m.isAvailable(f.getStartTime())){
				double dist =getDist(n,m);
				//	System.out.println("checking node "+n);
				if( minDist>dist){
					minDist=dist;
					min=m;
				}
			}
		}
		return min;
	}

	private Mule getAccesibleMule(Failure f) {
		Node n=f.getNode();
		double minDist=MAX_X+MAX_Y;
		boolean first=true;
		Mule min=null;
		double delay=0;
		for (Mule m:mules){
			if(m.isActive()){
				System.out.println("checking mule "+m);
				delay=m.getAvailabilityTime()-f.getStartTime();
				if(delay<0){delay=0;}
				double dist =getDist(n,m)+delay;
				if(first){
					min=m;
					minDist=dist;
					first=false;
				}
				//		System.out.println("dist ="+dist +"minDist="+minDist);
				if( minDist>dist){
					minDist=dist;
					min=m;
					//		System.out.println("min mule ="+min);
				}
				else if( minDist==dist){

					if(min.getTravelDistance()> m.getTravelDistance()){
						//			System.out.println("changed to mule  "+m+" due to travel dist");

						min=m;
					}
				}
			}
		}
		return min;
	}
	private Mule getMinTravelMule(Failure f) {
		Node n=f.getNode();
		double minTotalDist=0;
		boolean first=true;
		Mule min=null;
		double muleTotalDist=0;
		double dist =0;
		for (Mule m:mules){
			if(m.isActive()){

				//		System.out.println("checking mule "+m);
				dist =getDist(n,m);
				muleTotalDist=m.getTravelDistance()+dist;

				if(first){
					min=m;
					minTotalDist=muleTotalDist;
					first=false;
				}
				//		System.out.println("dist ="+dist +"minDist="+minDist);
				if( minTotalDist>muleTotalDist){
					minTotalDist=muleTotalDist;
					min=m;
					//		System.out.println("min mule ="+min);
				}
			}
		}
		return min;
	}
	public void updateMinNodeDistance(Mule m) {
		//		System.out.println("updating min dist of all nodes from "+m);
		for (Node n:nodes){
			double dist=getDist(n,m);

			//				System.out.println("checking node "+n +" from "+ m+ " dist = "+ dist+" current min dist =  "+ n.getMinDistFromMules()+ "current closest mule = "+n.getClosestMule());
			if( dist<n.getMinDistFromMules()){
				n.setMinDistFromMules(dist);
				n.setClosestMule(m);
				//			System.out.println("changed node "+n);

			}
			//			else{//for the case that m moved and the dist is now larger, we must find a new closest mule
			//				if( m==n.getClosestMule()){
			//				Mule c=getClosestPlacedMule(n);
			//				n.setClosestMule(c);
			//				dist=getDist(n,c);
			//				n.setMinDistFromMules(dist);
			//				System.out.println("m==n.getClosestMule() dist="+dist +"from "+c);
			//
			//				System.out.println("changed1 node "+n);
			//
			//			}
			//			}
			//		System.out.println("checking node "+n +" min dist =  "+ n.getMinDistFromMules());
		}
	}

	public void updateMinNodeDistance1(Mule m) {
		//		System.out.println("updating min dist of all nodes from "+m);
		for (Node n:nodes){
			double dist=getDist(n,m);

			//				System.out.println("checking node "+n +" from "+ m+ " dist = "+ dist+" current min dist =  "+ n.getMinDistFromMules()+ "current closest mule = "+n.getClosestMule());
			if( dist<n.getMinDistFromMules()){
				n.setMinDistFromMules(dist);
				n.setClosestMule(m);
				//			System.out.println("changed node "+n);

			}
			else{//for the case that m moved and the dist is now larger, we must find a new closest mule
				if( m==n.getClosestMule()){
					Mule c=getClosestMule(n);
					n.setClosestMule(c);
					dist=getDist(n,c);
					n.setMinDistFromMules(dist);
					//			System.out.println("m==n.getClosestMule() dist="+dist +"from "+c);

					//			System.out.println("changed1 node "+n);

				}
			}
			//		System.out.println("checking node "+n +" min dist =  "+ n.getMinDistFromMules());
		}
	}

	public double getDist (Node n, Mule m) {
		double dist=Math.sqrt(Math.pow((n.x-m.x),2)+Math.pow((n.y-m.y),2));
		return dist;
	}
	public double getDist (Node n, Node m) {
		double dist=Math.sqrt(Math.pow((n.x-m.x),2)+Math.pow((n.y-m.y),2));
		return dist;
	}
	public double getDist (Mule n, Mule m) {
		double dist=Math.sqrt(Math.pow((n.x-m.x),2)+Math.pow((n.y-m.y),2));
		return dist;
	}


	public ArrayList<Mule> getMules() {
		return mules;
	}



	public ArrayList<Node> getNodes() {

		return nodes;
	}
	public void setFailures(){ 
		System.out.println("Generating failures");
		failures = new ArrayList<Failure>();


		for(int loopi = 0; loopi< NumberOfFailures; loopi++){
			failures.add(new Failure(nodes.get((int)(rand.nextDouble()*NUM_Nodes)),(int)(rand.nextDouble()*Main.TotalExperimentTime)));
			//			System.out.println(failures.get(loopi));
		}
		Collections.sort(failures);
		System.out.println("sorted failures");
		for(int loopi = 0; loopi< NumberOfFailures; loopi++){
			System.out.println(failures.get(loopi));
		}
	}
	public void setFailures1(){ //increase the chance of repeated failures
		System.out.println("Generating recurrent failures");
		failures = new ArrayList<Failure>();

		Node n=nodes.get((int)(rand.nextDouble()*NUM_Nodes));
		failures.add(new Failure(n,(int)(rand.nextDouble()*Main.TotalExperimentTime)));
		ArrayList<Node> closeNodes=getCloseNodes(n,5);
		for(int loopi = 0; loopi< NumberOfFailures-1; loopi++){
			Node d=closeNodes.get((int)(rand.nextDouble()*closeNodes.size()));
			failures.add(new Failure(d,(int)(rand.nextDouble()*Main.TotalExperimentTime)));

			//			System.out.println(failures.get(loopi));
		}
		Collections.sort(failures);
		System.out.println("sorted failures");
		for(int loopi = 0; loopi< NumberOfFailures; loopi++){
			System.out.println(failures.get(loopi));
		}
	}
	public void setFailures2(){ //decrease the chance of repeated failures
		System.out.println("Generating non recurrent failures");
		failures = new ArrayList<Failure>();

		Node n=nodes.get((int)(rand.nextDouble()*NUM_Nodes));
		failures.add(new Failure(n,(int)(rand.nextDouble()*Main.TotalExperimentTime)));
		ArrayList<Node> farNodes=getFarNodes(n,5);
		for(int loopi = 0; loopi< NumberOfFailures-1; loopi++){
			Node d=farNodes.get((int)(rand.nextDouble()*farNodes.size()));
			failures.add(new Failure(d,(int)(rand.nextDouble()*Main.TotalExperimentTime)));

			//			System.out.println(failures.get(loopi));
		}
		Collections.sort(failures);
		System.out.println("sorted failures");
		for(int loopi = 0; loopi< NumberOfFailures; loopi++){
			System.out.println(failures.get(loopi));
		}
	}

	public void setFailures3(){ //decrease the chance of repeated failures
		System.out.println("Generating non recurrent failures");
		failures = new ArrayList<Failure>();
		@SuppressWarnings("unchecked")
		ArrayList<Node> notFailed=(ArrayList<Node>) nodes.clone(); 
		Node n=notFailed.get((int)(rand.nextDouble()*NUM_Nodes));
		notFailed.remove(n);
		failures.add(new Failure(n,(int)(rand.nextDouble()*Main.TotalExperimentTime)));
		for(int loopi = 0; loopi< NumberOfFailures-1; loopi++){
			Node d=notFailed.get((int)(rand.nextDouble()*notFailed.size()));
			failures.add(new Failure(d,(int)(rand.nextDouble()*Main.TotalExperimentTime)));

			//			System.out.println(failures.get(loopi));
		}
		Collections.sort(failures);
		System.out.println("sorted failures");
		for(int loopi = 0; loopi< NumberOfFailures; loopi++){
			System.out.println(failures.get(loopi));
		}
	}



	//	public void injectFailure() {
	//		int failIndex=rand.nextInt(NUM_Nodes);
	//	//	nodes.get(failIndex).setFail(true,);
	//
	//	}

	public ArrayList<Failure> getFailures() {
		return failures;
	}

	public void setFailures(ArrayList<Failure> failures) {
		this.failures = failures;
	}

	public Mule sendMuleA(Failure currentFailure) {//closest with return to position
		System.out.println("sending muleA to "+ currentFailure);
		Node n=currentFailure.getNode();
		Mule m=n.getClosestMule();
		double dist=getDist(n,m);
		if(m.getAvailabilityTime()<=currentFailure.getStartTime()){

			m.setAvailabilityTime(currentFailure.getStartTime()+2*dist+Main.failDuration);
			n.setDownDuration(dist);
		}
		else{
			n.setDownDuration(m.getAvailabilityTime()-currentFailure.getStartTime()+dist);

			m.setAvailabilityTime(m.getAvailabilityTime()+2*dist+Main.failDuration);
		}
		System.out.println("moving mule "+m+" to "+ currentFailure);
		m.setTravelDistance(m.getTravelDistance()+2*dist);
		System.out.println(m+" traveled "+m.getTravelDistance());
		m.setAtNode(currentFailure.getNode());
		m.numberOfFixs+=1;
		return m;
	}

	public Mule sendMuleA1(Failure currentFailure) {//closest to original position for no cooperation no return
		System.out.println("sending muleA1 (No return) to "+ currentFailure);
		Node n=currentFailure.getNode();
		Mule m=n.getOriginalMule();
		double dist=getDist(n,m);
		if(m.getAvailabilityTime()<=currentFailure.getStartTime()){

			m.setAvailabilityTime(currentFailure.getStartTime()+dist+Main.failDuration);
			n.setDownDuration(dist);
		}
		else{
			n.setDownDuration(m.getAvailabilityTime()-currentFailure.getStartTime()+dist);
			m.setAvailabilityTime(m.getAvailabilityTime()+dist+Main.failDuration);
		}

		System.out.println("moving mule "+m+" to "+ currentFailure);
		m.move(n);
		m.setTravelDistance(m.getTravelDistance()+dist);
		System.out.println(m+" traveled "+m.getTravelDistance());
		m.setAtNode(currentFailure.getNode());

		return m;
	}



	public Mule sendMuleB(Failure currentFailure) {//closest no return
		System.out.println("sending closest mule (B) to "+ currentFailure);
		Node n=currentFailure.getNode();
		Mule m=n.getClosestMule();
		double dist=getDist(n,m);
		if(m.getAvailabilityTime()<=currentFailure.getStartTime()){

			m.setAvailabilityTime(currentFailure.getStartTime()+dist+Main.failDuration);

			n.setDownDuration(dist);
		}
		else{

			n.setDownDuration(m.getAvailabilityTime()-currentFailure.getStartTime()+dist);
			m.setAvailabilityTime(m.getAvailabilityTime()+dist+Main.failDuration);
		}
		System.out.println("moving mule "+m+" to "+ currentFailure);
		m.move(n);

		m.setTravelDistance(m.getTravelDistance()+dist);
		System.out.println(m+" traveled "+m.getTravelDistance());
		m.setAtNode(currentFailure.getNode());
		m.numberOfFixs+=1;

		return m;
	}


	public Mule sendMuleC(Failure currentFailure) {// closest available
		//send the closest available mule - distance + availability
		System.out.println("sending muleC (closest avilable) to "+ currentFailure);
		Node n=currentFailure.getNode();
		Mule m=getAccesibleMule(currentFailure);//closest available
		System.out.println("sending "+ m);

		double dist=getDist(n,m);
		double delay=m.getAvailabilityTime()-currentFailure.getStartTime();
		if(delay<0){delay=0;}
		double timeToNode =dist+delay;

		m.setAvailabilityTime(currentFailure.getStartTime()+timeToNode+Main.failDuration);

		n.setDownDuration(timeToNode);

		System.out.println("moving mule "+m+" to "+ currentFailure);
		m.move(n);

		m.setTravelDistance(m.getTravelDistance()+dist);
		System.out.println(m+" traveled "+m.getTravelDistance());
		m.setAtNode(currentFailure.getNode());
		m.numberOfFixs+=1;

		return m;
	}

	public Mule sendMuleD(Failure currentFailure) { //least traveled
		//send the min travel dist  mule = min (distance + traveled dist)
		System.out.println("sending muleD (min travel) to "+ currentFailure);
		Node n=currentFailure.getNode();
		Mule m=getMinTravelMule(currentFailure);
		double dist=getDist(n,m);
		double delay=m.getAvailabilityTime()-currentFailure.getStartTime();
		if(delay<0){delay=0;}
		double timeToNode =dist+delay;

		m.setAvailabilityTime(currentFailure.getStartTime()+timeToNode+Main.failDuration);

		n.setDownDuration(timeToNode);

		System.out.println("moving mule "+m+" to "+ currentFailure);
		m.move(n);

		m.setTravelDistance(m.getTravelDistance()+dist);
		System.out.println(m+" traveled "+m.getTravelDistance());
		m.setAtNode(currentFailure.getNode());
		m.numberOfFixs+=1;

		return m;
	}


	public Failure getNextFailure() {
		// TODO Auto-generated method stub
		return null;
	}

	private void setOriginalMules() {
		for(Node n:nodes){
			n.setOriginalMule(n.getClosestMule());
		}
	}

	public void updateNodeStatus(int time) {
		for(Node n:nodes){
			if(n.failEndTime<=time){
				n.setWorking(true);
			}
		}		
	}


	//	public Mule findClosestAvailableMule(Node n) {
	//		Mule closestMule=null;
	//		double dist=MAX_X+MAX_Y;
	//		double disti;
	//		Mule m;
	//		for(int loopi = 0; loopi< NUM_Mules; loopi++){
	//			m= mules.get(loopi);
	//			disti=getDist(n,m);
	//			if(m.isAvailable(time) &&disti<dist){
	//				closestMule=m;
	//				dist=disti;
	//
	//			}}
	//
	//		return closestMule;
	//
	//	}
}