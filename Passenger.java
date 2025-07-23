public class Passenger {
	
	private int currentFloor;
	private int destinationFloor;
	private String name;
	//private int waitTime;
	
	public Passenger(int currentFloor, int destinationFloor, String name) {
		
		this.currentFloor = currentFloor;
		this.destinationFloor = destinationFloor;
		this.name = name;
		
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
	
} // end Passenger
