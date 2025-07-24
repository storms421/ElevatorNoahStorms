public class Passenger implements Comparable<Passenger> {
	
	private int currentFloor;
	private int destinationFloor;
	private String name;
	private int arrivalTime;
	
	public Passenger(int currentFloor, int destinationFloor, String name, int arrivalTime) {
		
		this.currentFloor = currentFloor;
		this.destinationFloor = destinationFloor;
		this.name = name;
		this.arrivalTime = arrivalTime;
		
	} // end constructor
	
	public int getCurrentFloor() {
		
		return currentFloor;
		
	} // end getCurrentFloor
	
	public int getDestinationFloor() {
		
		return destinationFloor;
		
	} // end getDestinationFloor
	
	public String getName() {
		
		return name;
		
	} // end getName
	
	public int getArrivalTime() {
		
		return arrivalTime;
		
	} // end getArrivalTime
	
	
	// This method is for organizing my list of passengers by their arrival time
	@Override
	public int compareTo(Passenger other) {
		
		return Integer.compare(this.arrivalTime, other.arrivalTime);
		
	} // end compareTo
	
} // end Passenger
