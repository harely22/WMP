
public class Pointxy {
private double x;
private double y;
private int numberOfNodesClosestToMe; //for EMS
private boolean hasAailableMule; //for EMS
private int index; //for EMS


public Pointxy(){
	this.x=0;
	this.y=0;
}
public Pointxy(Node n){
	this(n.x,n.y);
//	this.x=n.x;
	//this.y=n.y;
}
public Pointxy(double x,double y){
	this.x=x;
	this.y=y;
}
public Pointxy(double x,double y,int index){
	this.x=x;
	this.y=y;
	this.index=index;
}


public double dist(Pointxy other){
	return Math.sqrt(Math.pow(this.x-other.x,2)+Math.pow(this.y-other.y,2));
}

public double getX() {
	return x;
}
public void setX(double x) {
	this.x = x;
}
public double getY() {
	return y;
}
public void setY(double y) {
	this.y = y;
}
public int getNumberOfNodesClosestToMe() {
	return numberOfNodesClosestToMe;
}
public void setNumberOfNodesClosestToMe(int numberOfNodesClosestToMe) {
	this.numberOfNodesClosestToMe = numberOfNodesClosestToMe;
}
public boolean hasAailableMule() {
	return hasAailableMule;
}
public void setHasAailableMule(boolean hasAailableMule) {
	this.hasAailableMule = hasAailableMule;
}
public String toString(){
	return "Point "+"<"+x+","+y+"> has available mule="+hasAailableMule();
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
}
