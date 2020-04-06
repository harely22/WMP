# WMP
Collective Multi Agent Deployment for Wireless Sensor Network Maintenance
In order to compare the performance of the different algorithms, we developed a custom software simulator in Java,
to represent the Wireless Network Maintenance Problem (WMP). 
The area of the simulated problem is an X over Y plane. 
Any number of nodes N, and agents M can be positioned in the area. 
Nodes have weights W associated with their importance. 
Within the total duration of the experiment E_t, we can induce any
number of failures F on the nodes. The nodes which fail are
chosen uniformly at random from N and the start time of each
failure is chosen uniformly at random from (0,E_t).

The file Main.java holds all the parameters, runs the simulation, and outputs the results to the console.
