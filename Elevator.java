import java.util.ArrayList;
import java.util.Iterator;

public class Elevator {
	
	/* Making these set variables since they should not need to change.
	 * Possible they could make the elevator bigger or have floors marked
	 * off for travel. Maybe in another implementation. */
	private final int capacity;
	private final int minFloor;
	private final int maxFloor; 
	
	private int currentFloor;
	private String direction; // For 'up' and 'down'
	private ArrayList<Passenger> passengers;
	//private int elevatorID; -> this will be for if I can implement a second elevator
	
	public Elevator(int capacity, int minFloor, int maxFloor) {
		
		this.capacity = capacity;
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
		this.currentFloor = minFloor;
		this.direction = "up";
		passengers = new ArrayList<>();
		
	} // end constructor
	
	public int getCurrentFloor() {
		
		return currentFloor;
		
	} // end getCurrentFloor
	
	public String getDirection() {
		
		return direction;
		
	} // end getDirection

	public ArrayList<Passenger> getPassengers(){
		
		return passengers;
		
	} // end getPassengers
	
	public int getMinFloor() {
		
		return minFloor;
		
	} // end getMinFloor
	
	public int getMaxFloor() {
		
		return maxFloor;
		
	} // end getMaxFloor
	
	// This method checks if the elevator is empty
	public boolean isElevatorEmpty() {
		
		return passengers.isEmpty();
	
	} // end isElevatorEmpty
	
	// This method checks if the elevator is full
	public boolean isElevatorFull() {
		
		return passengers.size() >= capacity;
		
	} // end isElevatorFull
	
	// This method adds a passenger if the elevator is not full
	public boolean addPassenger(Passenger p) {
		
		if(!isElevatorFull()) {
			passengers.add(p);
			return true;
		}
		
		return false;
	
	} // end addPassenger
	
	// This method removes passengers from elevator that is at their destination
	public ArrayList<Passenger> unloadPassenger() {
		
		ArrayList<Passenger> leavingPassenger = new ArrayList<>(); // List of passengers unloading at current floor
		Iterator<Passenger> iterator = passengers.iterator(); // Helps to iterate through the lists of passengers
		while(iterator.hasNext()) {
			Passenger passenger = iterator.next();
			// If elevator arrives at passenger's destination floor, add them to unloading list
			if(passenger.getDestinationFloor() == currentFloor) {
				leavingPassenger.add(passenger); // Using for logging their departure
				iterator.remove(); // Remove them from elevator officially
			}
		}	
		
		return leavingPassenger;
		
	} // end unloadPassenger
	
	// This method moves elevator up a floor if not at top
	public void moveUp() {
		
		if(currentFloor < maxFloor) {
			currentFloor++;
			System.out.printf("Elevator has moved up to floor %d\n", currentFloor);
		}
	
	} // end moveUp
	
	// This method moves elevator down if not at bottom
	public void moveDown() {
		
		if(currentFloor > minFloor) {
			currentFloor--;
			System.out.printf("Elevator has moved down to floor %d\n", currentFloor);
		}
	
	} // end moveDown
	
	// This method moves elevator in current direction (up/down)
	public void move() {
		
		//System.out.printf("Current floor: %d, Direction: %s\n", currentFloor, direction);  // Debug
		
		if(isGoingUp()){
			moveUp();
		} else {
			moveDown();
		}
		
	} // end move
	
	// This method checks if the elevator is going up
	public boolean isGoingUp() {
		
		return direction.equals("up");
		
	} // end isGoingUp
	
	// This method reverses elevator's direction up to down (vice versa)
	public void reverseDirection() {
		
		if(direction.equals("up")) {
			direction = "down";
		} else {
			direction = "up";
		}
		
	} // end reverseDirection
	
} // end Elevator