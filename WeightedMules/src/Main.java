/*
In this project we start creating the model for the mules
 *
 * */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.io.*;

public class Main {

	public static boolean NewFFMode = false;
	public static boolean CentroidAdjustmentMode = false;//Add centroid improvement to farthest first mule placement
	public static boolean KMedianMode = false;//Add centroid improvement to farthest first mule placement
	public static boolean PotentialFieldMode = false;//
	public static boolean localSearchMode = false;//
	public static boolean DSA_Mode = false;//used to turn local search into DSA

	public static boolean basicMode = false; //no cooperation each mule care for its nodes
	public static boolean cooperationMode1 = false; //each mule goes to the closest fall 
	public static boolean cooperationMode2 = false; //mules adjust location at every fall and go to the closest 
	public static boolean MannualSettingMode=false;
	public static boolean UniformSettingMode=false;//works only for power of 4 : 4,16,64... mules
	public static boolean NodeGridMode=false;//number of nodes=x*y
	//PLAIN RUN
	public static  int NUM_Mules =10;//10
	public static  int NUM_Nodes = 100;//100
	public static  int MAX_X = 100;//100
	public static  int MAX_Y =100;//100
	public static  int BaseFailDuration=0;//0
	public static  int failDuration=BaseFailDuration;//0
	public static  int TotalExperimentTime=10000;//10000
	public static int NumberOfExperiments=50;//50
	public static int NumberOfFailures=10;//10
	public static int NumberOfFailDurations=11;//11
	public static int DurationCounterIncrement=1000;//1000
	public static int MaxFixesPerMule =5;//5

	
////	//LINE RUN

	//	public static  int NUM_Nodes = 5;//20
	//	public static  int NUM_Mules =5;//5
	//	public static  int MAX_X = 100;//100
	//	public static  int MAX_Y =0;//0
	//	public static  int BaseFailDuration=0;//0
	//	public static  int failDuration=BaseFailDuration;//0
	//	public static  int TotalExperimentTime=100;//100
	//	public static int NumberOfExperiments=100;//50
	//	public static int NumberOfFailures=10;//10
	//	public static int NumberOfFailDurations=10;//10


	//LINE DEBUG
//			public static  int NUM_Nodes = 3;//20
//			public static  int NUM_Mules =2;//5
//			public static  int MAX_X = 10;//100
//			public static  int MAX_Y =0;//0
//			public static  int BaseFailDuration=0;//0
//			public static  int failDuration=BaseFailDuration;//0
//			public static  int TotalExperimentTime=1;//100
//			public static int NumberOfExperiments=1;//50
//			public static int NumberOfFailures=3;//10
//			public static int NumberOfFailDurations=3;//10
//			public static int DurationCounterIncrement=1;//1000
			
	//Plane DEBUG
//			public static  int NUM_Nodes = 10;//20
//			public static  int NUM_Mules =4;//5
//			public static  int MAX_X = 4;//100
//			public static  int MAX_Y =4;//0
//			public static  int BaseFailDuration=0;//0
//			public static  int failDuration=BaseFailDuration;//0
//			public static  int TotalExperimentTime=10;//100
//			public static int NumberOfExperiments=1;//50
//			public static int NumberOfFailures=3;//10
//			public static int NumberOfFailDurations=3;//10
//			public static int DurationCounterIncrement=2;//1
//	

	//	public static String[] methods={"BasicNR","CM1","CM2","CM3","CM5","k-Centroid","KMedian"};
//		public static String[] methods={"BasicGrid","k-Center","k-Centroid","k-Median","NoCooperation","new_k-Center"};
	public static String[] methods={"BasicGrid","k-Center","k-Centroid","k-Median","localSearch"};
//	public static String[] methods={"localSearch","BasicGrid","k-Center","k-Centroid","BasicGrid"};
//	public static String[] methods={"localSearch","DSA"};

//	public static String[] methods={"localSearch"};
//	public static String[] methods={"k-Center"};
//	public static String[] methods={"BasicGrid"};
//	public static String[] methods={"PotentialField"};
//	public static String[] methods={"k-Median"};
//	public static String[] methods={"k-Centroid"};
	
	
	public static int NumberOfMethods=methods.length;//5

	public static double[][] AvgMovementMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] AvgDownTimeMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] MaxMovementMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] MaxDownTimeMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] TotalMaxMovementMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] TotalMaxDownTimeMatrix=new double[NumberOfMethods][NumberOfFailDurations];
	public static double[][] AvgInactiveMules=new double[NumberOfMethods][NumberOfFailDurations];

	public static double MaxAgentsPerTarget=0;
	public static double MaxTargetsPerAgent=0;
	public static double AvgAgentsPerTarget=0;
	public static double AvgTargetsPerAgent=0;
	public static Environment env;
	public static Random r1;
	public static Random r2;//for DSA

	public static long ExperimentSeed;

	public static int MethodsCounter=0;
	public static int FailuresCounter=0;
	public static int DurationsCounter=0;
	public static int ExperimentsCounter=0;

	public static void main(String[] args) {
		//measuring elapsed time using System.nanoTime
		long startTime = System.nanoTime();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("MultiMule"+".txt"));


			System.out.println("NUM_Mules "+Environment.NUM_Mules);
			out.write("NUM_Mules "+Environment.NUM_Mules);
			out.newLine();
			System.out.println("NUM_Nodes "+Environment.NUM_Nodes);
			out.write("NUM_Nodes "+Environment.NUM_Nodes);
			out.newLine();
			System.out.println("Max X = "+Environment.MAX_X+ " Max Y= "+Environment.MAX_Y);
			out.write("Max X = "+Environment.MAX_X+ " Max Y= "+Environment.MAX_Y);
			out.newLine();
			System.out.println("NumberOfFailures= "+NumberOfFailures);
			out.write("NumberOfFailures= "+NumberOfFailures);
			out.newLine();
			System.out.println("NumberOfExperiments= "+NumberOfExperiments);
			out.write("NumberOfExperiments= "+NumberOfExperiments);
			out.newLine();
			System.out.println("NumberOfFailDurations= "+NumberOfFailDurations);
			out.write("NumberOfFailDurations= "+NumberOfFailDurations);
			out.newLine();
			System.out.println("BaseFailDuration= "+BaseFailDuration);
			out.write("BaseFailDuration= "+BaseFailDuration);
			out.newLine();
			System.out.println("DurationCounterIncrement= "+DurationCounterIncrement);
			out.write("DurationCounterIncrement= "+DurationCounterIncrement);
			out.newLine();

			System.out.println("UniformSettingMode= "+UniformSettingMode+ "  NodeGridMode= "+NodeGridMode);
			out.write("UniformSettingMode= "+UniformSettingMode+ "  NodeGridMode= "+NodeGridMode);
			out.newLine();

			out.newLine();
			System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
			out.write("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
			out.newLine();


			for(MethodsCounter=0;MethodsCounter<NumberOfMethods;MethodsCounter++){
				String method=methods[MethodsCounter];
				System.out.println();
				System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
				out.write("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
				out.newLine();

				System.out.println("Metod "+(MethodsCounter+1)+" out of "+ NumberOfMethods+" = "+ method);
				System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
				out.write("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
				out.newLine();

				out.write("\n"+"Metod "+(MethodsCounter+1)+" out of "+ NumberOfMethods+" = "+ method);
				out.newLine();


				for(DurationsCounter=0;DurationsCounter<NumberOfFailDurations;DurationsCounter++){
					failDuration=BaseFailDuration+DurationsCounter*DurationCounterIncrement;
					System.out.println();
					System.out.println("failDuration " +(DurationsCounter+1)+" out of "+NumberOfFailDurations+" = "+failDuration);
					out.write("\n"+"failDuration " +(DurationsCounter+1)+" out of "+NumberOfFailDurations+" = "+failDuration);
					out.newLine();


					for(ExperimentsCounter=0;ExperimentsCounter<NumberOfExperiments;ExperimentsCounter++){
						//		r1=new Random();
						//	ExperimentSeed = r1.nextLong();
						ExperimentSeed = ExperimentsCounter;
						//	ExperimentSeed = 4;
						Node.indexCount=1;
						System.out.println();
						System.out.println("Experiment "+(ExperimentsCounter+1)+" out of "+ NumberOfExperiments);
						out.write("Experiment "+(ExperimentsCounter+1)+" out of "+ NumberOfExperiments);
						out.newLine();


						r1 = new Random(ExperimentSeed);
						r2 = new Random(ExperimentSeed);

						env= new Environment(); 
						//set grid
						//set nodes
						//set mules - gonzales farthest first
						//assign nodes to mules
						//set failures
						ArrayList<Mule>  mules=env.getMules();
						ArrayList<Node>  nodes=env.getNodes();
						ArrayList<Failure>  failures=env.getFailures();

						double currentMax=0;//used for total max

						for(FailuresCounter=0;FailuresCounter<NumberOfFailures;FailuresCounter++){

							System.out.println("FailuresCounter " + FailuresCounter);
							out.write("FailuresCounter " + FailuresCounter);
							out.newLine();
							Failure currentFailure=failures.get(FailuresCounter);
							env.updateNodeStatus(currentFailure.getStartTime());
							currentFailure.getNode().setFail(true,currentFailure.getStartTime());
							System.out.println("currentFailure " + currentFailure);
							/////////////////////Basic//////////////////////////////////////////
							if(methods[MethodsCounter]=="Basic"){
								//Each mule is responsible for its close nodes as calculated in the initial deployment of FF
								//Each mule returns to its initial location
								Mule m=env.sendMuleA(currentFailure);
								
								//set mule to busy until 2*travel (distance) + fix
								//update downtime to distance
							}//basic

							/////////////////////BasicGrid//////////////////////////////////////////
							if(methods[MethodsCounter]=="BasicGrid"){
								System.out.println("Running BasicGrid ");
								Main.MannualSettingMode=true;
								if(Main.FailuresCounter==0){//if first time
									env.placeMules();//when manual setting is on it sets 10 mules uniformly
									mules=env.getMules();
									//		env.setOriginalMules();
								}
								//Mules are uniformly distributed
								//Each mule is responsible for its close nodes
								//Each mule returns to its initial location
								Mule m=env.sendMuleC(currentFailure);//A1


								//set mule to busy until 2*travel (distance) + fix
								//update downtime to distance
								Main.MannualSettingMode=false;

							}//basic grid
							
							/////////////////////NoCooperation//////////////////////////////////////////
							if(methods[MethodsCounter]=="NoCooperation"){
								System.out.println("Running NoCooperation ");

								Main.MannualSettingMode=true;
								if(Main.FailuresCounter==0){//if first time
									env.placeMules();//when manual setting is on it sets 10 mules uniformly
									//		env.setOriginalMules();
								}
								//Mules are uniformly distributed
								//Each mule is responsible for its close nodes
								//Each mule returns to its initial location
								Mule m=env.sendMuleA1(currentFailure);//A1


								//set mule to busy until 2*travel (distance) + fix
								//update downtime to distance
								Main.MannualSettingMode=false;

							}//basic grid
							/////////////////////Basic No Return//////////////////////////////////////////
							if(methods[MethodsCounter]=="BasicNR"){
								//Each mule is responsible for its close nodes as calculated in the initial deployment of FF
								// mule doesn't return to its initial location
								Mule m=env.sendMuleA1(currentFailure);


								//set mule to busy until travel (distance) + fix
								//update downtime to distance
							}//basic

							/////////////////////CooperationMethod1//////////////////////////////////////////
							//FF is calculated after every failure and all the mules move
							//Each mule is responsible for its close nodes as calculated in the deployment of FF
							//Mules do not return to their initial location

							if(methods[MethodsCounter]=="CM1"){
								Mule m=env.sendMuleB(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to distance
								//update movement to all movements
								//reset mules according to the occupied ones

							}//CM1

							/////////////////////FF//////////////////////////////////////////
							//FF is calculated after every failure and all the mules move
							//Each mule is responsible for its close nodes as calculated in the deployment of FF
							//Mules do not return to their initial location
							//Closest available

							if(methods[MethodsCounter]=="k-Center"){
								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to distance
								//update movement to all movements
								//reset mules according to the occupied ones

							}//FF


							///////////////////// new FF//////////////////////////////////////////
							//FF is calculated after every failure and all the mules move
							//Each mule is responsible for its close nodes as calculated in the deployment of FF
							//Mules do not return to their initial location
							//Closest available
//unlike regular ff here we replace by finding the farthest from newly placed mules not from occupied 
							if(methods[MethodsCounter]=="new_k-Center"){
								NewFFMode=true;
								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to distance
								//update movement to all movements
								//reset mules according to the occupied ones
								NewFFMode=false;

							}//new FF


							/////////////////////CooperationMethod2//////////////////////////////////////////
							//send the closest mule to failure no re-arrangement
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="CM2"){
								Mule m=env.sendMuleB(currentFailure);
								env.updateMinNodeDistance1(m);
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to distance
								//update movement to all movements
								//don't reset mules 
							}//CM2

							/////////////////////CooperationMethod3//////////////////////////////////////////
							//send the closest available mule to failure no re-arrangement
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="CM3"){
								Mule m=env.sendMuleC(currentFailure);
								env.updateMinNodeDistance1(m);
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to distance
								//update movement to all movements
								//don't reset mules 
							}//CM3
							/////////////////////CooperationMethod4//////////////////////////////////////////
							//send the closest available mule to failure with re-arrangement
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="CM4"){
								Mule m=env.sendMuleC(currentFailure);

								env.rePlaceMules(currentFailure.getStartTime());
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to availability + distance
								//update movement 
								//reset mules 
							}//CM4

							/////////////////////CooperationMethod5//////////////////////////////////////////
							//send the min travel mule to failure 
							//which is the mule whose already traveled dist +dist to node is the smallest
							//no re-arrangement
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="CM5"){
								Mule m=env.sendMuleD(currentFailure);
								env.updateMinNodeDistance1(m);
							}//CM4


							/////////////////////Centroid//////////////////////////////////////////
							//send the closest available mule to failure with re-arrangement
							//set at centroid of own close nodes = average x y
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="k-Centroid"){
								CentroidAdjustmentMode = true;
								MannualSettingMode=true;
								if(Main.FailuresCounter==0){//if first time
									env.placeMules();//when manual setting is on it sets 10 mules uniformly
									mules=env.getMules();						
									//		env.centroidAdjustment();
									env.centroid(0);
								}

								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to availability + distance
								//update movement 
								//reset mules 
								CentroidAdjustmentMode = false;
							}//Centroid

							/////////////////////KMedian//////////////////////////////////////////
							//Initial deployment according to Kmedian reverse approximation algorithm 
							//set n mules on n nodes and remove n-k mules iteratively 
							// remove the one that adds the least cost in each iteration:
							//		we have to go over all the mules and remove the one whose dist function is the smallest of all the remaining mules.
							// the dist function is : (distance from closest neighbor +	sum of (sons distances from their closest neighboring mule - sons distances from mule))								
							// We need to sort the mules by the smallest distance and maintain this sort in every removal 
							// so we also have to keep the distance and the neighbor so when the neighbor is removed we calculate for its mules again and resort
							//	every mule must have a sorted list of nodes and their distances and there is a global sorted list  
							//
							// a simple implementation would test every remaining mule and for each removal check the sum of distances of nodes from remaining mules									

							//									send the closest available mule to failure 
							// currently no re-arrangement . would be hard to implement with kmeans maybe with centroid
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="k-Median"){
								KMedianMode = true;
								System.out.println("Placing KMedian ");

								if(Main.FailuresCounter==0){//if first time
									env.kMedian(0);
								}

								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to availability + distance
								//update movement 
								//reset mules 
								KMedianMode = false;
							}//KMedian
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
							/////////////////////Local Search KMedian//////////////////////////////////////////
							//Initial deployment according to Kmedian found by distributed local search 
							//
							//send the closest available mule to failure 
							// currently with re-arrangement 
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="localSearch"){
				
								
								localSearchMode = true;
								
								System.out.println("Placing localSearchMode ");

								if(Main.FailuresCounter==0){//if first time
									env.localSearch(0);
								}

								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to availability + distance
								//update movement 
								//reset mules 
								localSearchMode = false;
							}//LS kMedian
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
							/////////////////////DSA Local Search KMedian//////////////////////////////////////////
							//Initial deployment according to Kmedian found by distributed DSA local search 
							//Distributed Stochastic Algorithm
							//with probability p move to best alternative location
							//send the closest available mule to failure 
							// currently with re-arrangement 
							//Mules do not return to their initial location
							if(methods[MethodsCounter]=="DSA"){
												
								localSearchMode = true;
								DSA_Mode=true;
								
								System.out.println("Placing localSearchMode ");

								if(Main.FailuresCounter==0){//if first time
									env.localSearch(0);
								}

								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());
								//		env.updateMules(currentFailure.getStartTime());

								//set mule to busy until travel (distance) + fix
								//update downtime to availability + distance
								//update movement 
								//reset mules 
								localSearchMode = false;
								DSA_Mode=false;

							}//DSA LS kMedian
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

							
							
							
							/////////////////////PotentialField//////////////////////////////////////////
							//Initial deployment grid
							//Each mule calculates the potential force from all other neighboring mules and nodes
//it then moves to the zero potential point
							if(methods[MethodsCounter]=="PotentialField"){
								System.out.println("Placing PotentialField ");
								PotentialFieldMode = true;

								if(Main.FailuresCounter==0){//if first time
									env.potentialField(0);
								}
								Mule m=env.sendMuleC(currentFailure);
								env.rePlaceMules(currentFailure.getStartTime());
							
								PotentialFieldMode = false;
							}//PotentialFiel
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////																

		//					AvgDownTimeMatrix[MethodsCounter][DurationsCounter]+=(currentFailure.getNode().getDownDuration()*currentFailure.getNode().getWeight());
							System.out.println("env.totalNodesWeight/NUM_Nodes " + env.totalNodesWeight/NUM_Nodes);

						AvgDownTimeMatrix[MethodsCounter][DurationsCounter]+=(currentFailure.getNode().getDownDuration()*currentFailure.getNode().getWeight())/(env.totalNodesWeight/NUM_Nodes);//normalized
							currentFailure.getNode().failEndTime=currentFailure.getStartTime()+currentFailure.getNode().getDownDuration();
						}//Failures

						System.out.println("FailuresDownDuration " + AvgDownTimeMatrix[MethodsCounter][DurationsCounter]);

						double maxTravelDist=0;
						for(Mule m: mules){
							AvgMovementMatrix[MethodsCounter][DurationsCounter]+=m.getTravelDistance();
							System.out.println("AvgMovementMatrix"+AvgMovementMatrix[MethodsCounter][DurationsCounter]);

							System.out.println(m+" add to avg movement "+m.getTravelDistance());
							if(m.getTravelDistance()>maxTravelDist){
								maxTravelDist=m.getTravelDistance();
							}
							
			
							if(m.numberOfFixs==MaxFixesPerMule){
								AvgInactiveMules[MethodsCounter][DurationsCounter]++;
							}
							
						}
						System.out.println("AvgMovementMatrix"+AvgMovementMatrix[MethodsCounter][DurationsCounter]);

						System.out.println("Adding only the max movement from this experiment"+ maxTravelDist); 
						MaxMovementMatrix[MethodsCounter][DurationsCounter]+=maxTravelDist;


						double maxDownDuration=0;
						for(Failure f: failures){
							System.out.println(f+" "+f.getNode().getDownDuration()*f.getNode().getWeight());
							if(f.getNode().getDownDuration()*f.getNode().getWeight()> maxDownDuration){
								maxDownDuration=f.getNode().getDownDuration()*f.getNode().getWeight();
							}
						}
						System.out.println("Adding only the max down duration from this experiment"+ maxDownDuration/(env.totalNodesWeight/NUM_Nodes)); 
						MaxDownTimeMatrix[MethodsCounter][DurationsCounter]+=maxDownDuration/(env.totalNodesWeight/NUM_Nodes);

						if(maxDownDuration/(env.totalNodesWeight/NUM_Nodes)>TotalMaxDownTimeMatrix[MethodsCounter][DurationsCounter]){
							TotalMaxDownTimeMatrix[MethodsCounter][DurationsCounter]=maxDownDuration/(env.totalNodesWeight/NUM_Nodes);
						}
						if(maxTravelDist>TotalMaxMovementMatrix[MethodsCounter][DurationsCounter]){
							TotalMaxMovementMatrix[MethodsCounter][DurationsCounter]=maxTravelDist;
						}
						
					}//Experiments



				}//Durations
			}	//Methods





			///////////////////////////////////////////////////////////////////////Finished experiments Starting to print sum results////////////////////////////////
			System.out.println();
			System.out.println();
			System.out.println("//////////////////////////////");
			System.out.println("//////////////////////////////");
			System.out.println("//////////////////////////////");
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate average movement per mule for "+ NumberOfExperiments+" experiments");
			out.write("Calculate average movement per mule for "+ NumberOfExperiments+" experiments");
			out.newLine();
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.print(" method"+" , "+" , "+" , ");
			out.write(" method ");
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("  movement average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write("  movement average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){

					System.out.print(((AvgMovementMatrix[bb][loopi]/NumberOfExperiments)/NUM_Mules)+"  ");
					out.write(((AvgMovementMatrix[bb][loopi]/NumberOfExperiments)/NUM_Mules)+"  ");
				}
				System.out.println();
				out.newLine();

			}//movement sum
			System.out.println();
			out.newLine();

			//////////////////////////////////////////////////////////////////////////////////////////end movement

			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate average weighted downDuration per Failure for "+ NumberOfExperiments+" experiments");
			out.write("Calculate average weighted downDuration per Failure for "+ NumberOfExperiments+" experiments");
			out.newLine();

			System.out.println("//////////////////////////////");

			System.out.print(" method "+" , "+" , "+" , ");
			out.write(" method ");
			out.newLine();
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("  downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write("  downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){


					System.out.print(((AvgDownTimeMatrix[bb][loopi]/NumberOfExperiments)/NumberOfFailures)+"  ");
					out.write(((AvgDownTimeMatrix[bb][loopi]/NumberOfExperiments)/NumberOfFailures)+"  ");


				}
				System.out.println();
				out.newLine();

			}//downTime sum

///////////////////////////////print fixes stats
//////////////////////////////////////////////////inactive mules

			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate average inactive mules per experiment for "+ NumberOfExperiments+" experiments");
			out.write("Calculate average inactive mules per experiment for  "+ NumberOfExperiments+" experiments");
			out.newLine();

			System.out.println("//////////////////////////////");

			System.out.print(" method "+" , "+" , "+" , ");
			out.write(" method ");
			out.newLine();
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("Average inactive mules "+loopi*DurationCounterIncrement+" ");
				out.write("Average inactive mules  "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){


					System.out.print(((AvgInactiveMules[bb][loopi]/NumberOfExperiments))+"  ");
					out.write(((AvgInactiveMules[bb][loopi]/NumberOfExperiments))+"  ");


				}
				System.out.println();
				out.newLine();

			}//AvgInactiveMules


			///////////////////////////////////////////////////////////////////////Finished experiments Starting to print average max results////////////////////////////////
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate average MAXIMAL movement for "+ NumberOfExperiments+" experiments");
			out.write("Calculate average MAXIMAL movement for "+ NumberOfExperiments+" experiments");
			out.newLine();
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.print(" method"+" , "+" , "+" , "+" , ");
			out.write(" method ");
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("  MAXIMAL movement average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write(" MAXIMAL movement average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){

					System.out.print(((MaxMovementMatrix[bb][loopi]/NumberOfExperiments))+"  ");
					out.write(((MaxMovementMatrix[bb][loopi]/NumberOfExperiments))+"  ");
				}
				System.out.println();
				out.newLine();

			}//movement max
			System.out.println();
			out.newLine();

			//////////////////////////////////////////////////////////////////////////////////////////end average max movement

			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate average MAXIMAL downDuration for "+ NumberOfExperiments+" experiments");
			out.write("Calculate average MAXIMAL downDuration for "+ NumberOfExperiments+" experiments");
			out.newLine();

			System.out.println("//////////////////////////////");

			System.out.print(" method "+" , "+" , "+" , "+" , ");
			out.write(" method ");
			out.newLine();
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("  MAXIMAL downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write("  MAXIMAL downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){


					System.out.print(((MaxDownTimeMatrix[bb][loopi]/NumberOfExperiments))+"  ");
					out.write(((MaxDownTimeMatrix[bb][loopi]/NumberOfExperiments))+"  ");


				}
				System.out.println();
				out.newLine();

			}//downTime max
			//////////////////////////////////////////////////////////////////////////////////////////end average max downTime


			///////////////////////////////////////////////////////////////////////Finished experiments Starting to print total max results////////////////////////////////
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate total MAXIMAL movement for "+ NumberOfExperiments+" experiments");
			out.write("Calculate total MAXIMAL movement for "+ NumberOfExperiments+" experiments");
			out.newLine();
			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.print(" method"+" , "+" , "+" , "+" , "+" , ");
			out.write(" method ");
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+methods[bb]+" ");
				out.write("   "+methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print("  total MAXIMAL movement average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write("total MAXIMAL movement average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){

					System.out.print(((TotalMaxMovementMatrix[bb][loopi]))+"  ");
					out.write(((TotalMaxMovementMatrix[bb][loopi]))+"  ");
				}
				System.out.println();
				out.newLine();

			}//total movement max
			System.out.println();
			out.newLine();

			//////////////////////////////////////////////////////////////////////////////////////////end total max movement

			System.out.println("//////////////////////////////");
			out.write("//////////////////////////////");
			out.newLine();
			System.out.println("Calculate Total MAXIMAL downDuration for "+ NumberOfExperiments+" experiments");
			out.write("Calculate Total MAXIMAL downDuration for "+ NumberOfExperiments+" experiments");
			out.newLine();

			System.out.println("//////////////////////////////");

			System.out.print(" method "+" , "+" , "+" , "+" , "+" , ");
			out.write(" method ");
			out.newLine();
			for(int bb=0;bb<NumberOfMethods;bb++){
				System.out.print("   "+ methods[bb]+" ");
				out.write("   "+ methods[bb]+" ");
			}
			System.out.println();
			out.newLine();
			for (int loopi = 0; loopi < NumberOfFailDurations; loopi++) {
				System.out.print(" Total MAXIMAL downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				out.write("Total  MAXIMAL downDuration average failDuration "+loopi*DurationCounterIncrement+" ");
				for(int bb=0;bb<NumberOfMethods;bb++){


					System.out.print(((TotalMaxDownTimeMatrix[bb][loopi]))+"  ");
					out.write(((TotalMaxDownTimeMatrix[bb][loopi]))+"  ");


				}
				System.out.println();
				out.newLine();

			}//total downTime max
			//////////////////////////////////////////////////////////////////////////////////////////end total max downTime

			//System.out.println( "the average number of neighboring targets per agent is: "+AvgTargetsPerAgent/(NumberOfExperiments*NumberOfFailDurations));
			//System.out.println( "the average number of neighboring agents per target is: "+AvgAgentsPerTarget/(NumberOfExperiments*NumberOfFailDurations));
			//System.out.println( "the max number of neighboring targets per agent is: "+MaxTargetsPerAgent/(NumberOfExperiments*NumberOfFailDurations));
			//System.out.println( "the max number of neighboring agents per target is: "+MaxAgentsPerTarget/(NumberOfExperiments*NumberOfFailDurations));


			System.out.println("NUM_Mules "+Environment.NUM_Mules);
			out.write("NUM_Mules "+Environment.NUM_Mules);
			out.newLine();
			System.out.println("NUM_Nodes "+Environment.NUM_Nodes);
			out.write("NUM_Nodes "+Environment.NUM_Nodes);
			out.newLine();
			System.out.println("Max X = "+Environment.MAX_X+ " Max Y= "+Environment.MAX_Y);
			out.write("Max X = "+Environment.MAX_X+ " Max Y= "+Environment.MAX_Y);
			out.newLine();
			System.out.println("NumberOfFailures= "+NumberOfFailures);
			out.write("NumberOfFailures= "+NumberOfFailures);
			out.newLine();
			System.out.println("NumberOfExperiments= "+NumberOfExperiments);
			out.write("NumberOfExperiments= "+NumberOfExperiments);
			out.newLine();
			System.out.println("NumberOfFailDurations= "+NumberOfFailDurations);
			out.write("NumberOfFailDurations= "+NumberOfFailDurations);
			out.newLine();
			System.out.println("TotalExperimentTime= "+TotalExperimentTime);
			out.write("TotalExperimentTime= "+TotalExperimentTime);
			out.newLine();
			System.out.println("NumberOfMethods= "+NumberOfMethods+" "+ printArray(methods));
			out.write("NumberOfMethods= "+NumberOfMethods+" "+ printArray(methods));
			out.newLine();
			System.out.println("BaseFailDuration= "+BaseFailDuration);
			out.write("BaseFailDuration= "+BaseFailDuration);
			out.newLine();
			System.out.println("DurationCounterIncrement= "+DurationCounterIncrement);
			out.write("DurationCounterIncrement= "+DurationCounterIncrement);
			out.newLine();

			System.out.println("UniformSettingMode= "+UniformSettingMode+ "   NodeGridMode= "+NodeGridMode);
			out.write("UniformSettingMode= "+UniformSettingMode+ "   NodeGridMode= "+NodeGridMode);
			out.newLine();
			System.out.println("Weighted version ");
			
			
			System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
			out.write("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
			out.newLine();
			//	}//SRMR
			long elapsedTime = System.nanoTime() - startTime;
			long   seconds=(elapsedTime/1000000000);
			out.write("Total execution time in seconds = "   +seconds );
			out.close();
		}//try
		catch (IOException e) {	}
		long elapsedTime = System.nanoTime() - startTime;



	}//main

	public static  String printArray(String[] toPrint){
		String s="";
		for(int i=0; i<toPrint.length;i++){
			//	System.out.println(toPrint[i]);
			s+=toPrint[i]+" ";
		}
		return s;
	}

	public static  <T> String printList(ArrayList<T> toPrint){
		String s="";
		for(int i=0; i<toPrint.size();i++){
			//	System.out.println(toPrint[i]);
			s+=toPrint.get(i)+" ";
		}
		return s;
	}




}


