
public class Pointxy {
private double x;
private double y;
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


public double dist(Pointxy other){
	return Math.sqrt(Math.pow(this.x-other.x,2)+Math.pow(this.y-other.y,2));
}
}
