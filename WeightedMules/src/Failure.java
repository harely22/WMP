
public class Failure implements Comparable<Failure> {
	public static int indexCount = 1;
	public static int lastFailureTime = 0;
	private int startTime;
	private int index;
	private int duration;
	private Node node;
	private int downTime;



	public Failure( Node node,int startTime) {
		index = indexCount;
		indexCount++;
		this.node = node;
		setStartTime(startTime);
		setDuration(Main.failDuration);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public int getDownTime() {
		return downTime;
	}
	public void setDownTime(int downTime) {
		this.downTime = downTime;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public String toString(){
		return "Failure "+index+ "  at node  "+node+ " at time  "+startTime;
	}
	@Override
	public int compareTo(Failure other) {
		if(this.startTime>other.getStartTime()){
			return 1;
		}
		else if(this.startTime<other.getStartTime()){
			return -1;
		}
		else{	
			return 0;
		}
	}


}
