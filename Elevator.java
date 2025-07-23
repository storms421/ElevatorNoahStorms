import java.util.ArrayList;
import java.util.Iterator;

public class Elevator {
	
	/* Making these set variables since they should not need to change.
	 * Possible they could make the elevator bigger or have floors marked
	 * off for travel. Maybe in another implementation.
	 */
	private final int capacity;
	private final int minFloor;
	private final int maxFloor; 
	
	private int currentFloor;
	private String direction; // For 'up' and 'down'
	private ArrayList<Passenger> passengers;
	//private boolean doorsOpen;
	//private int elevatorID; -> this will be for if I can implement a second elevator
	
	public Elevator(int capacity, int minFloor, int maxFloor) {
		
		this.capacity = capacity;
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
		this.currentFloor = minFloor;
		passengers = new ArrayList<>();
		
	} // end constructor
	
	public void setDirection(String direction) {
		this.direction = "up";
	}
	
	public int getCurrentFloor() {
		
		return currentFloor;
		
	} // end getCurrentFloor
	
	public String getDirection() {
		
		return direction;
		
	} // end getDirection
	
	// This method checks for if the elevator is empty
	public boolean isElevatorEmpty() {
		
		return passengers.isEmpty();
	
	} // end isElevatorEmpty
	
	// This method checks for if the elevator is full
	public boolean isElevatorFull() {
		
		return passengers.size() >= capacity;
		
	} // end isElevatorFull
	
	// This method adds a passenger if the elevator is not full
	public boolean addPassenger(Passenger p) {
		
		// If not full, passenger may enter elevator
		if(!isElevatorFull()) {
			passengers.add(p);
			return true;
		}
		
		return false;
	
	} // end addPassenger
	
	// This method checks if there are any passengers that request to get off at current floor
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
	
	// This method checks if the elevator can go up
	public void moveUp() {
		
		if(currentFloor < maxFloor) {
			currentFloor++;
			System.out.printf("Elevator has moved up to floor %d\n", currentFloor);
		}
	
	} // end moveUp
	
	// This method checks if the elevator can go down
	public void moveDown() {
		
		if(currentFloor > minFloor) {
			currentFloor--;
			System.out.printf("Elevator has moved down to floor %d\n", currentFloor);
		}
	
	} // end moveDown
	
	// This method checks to see what direction the elevator will go
	public void move() {
		
		updateDirection();
		
		if(direction.equals("up")){
			moveUp();
		} else {
			moveDown();
		}
		
	} // end move
	
	// This method helps determine if the elevator has hit the max/min floor
	/* Will note this is not the most optimal way an elevator should move.
	 * This way might exhaust the elevator or take too much time.
	 */
	public void updateDirection() {
		
		if(currentFloor == maxFloor) {
			direction = "down";
		} else if (currentFloor == minFloor) {
			direction = "up";
		}
		
	} // end updateDirection
	
} // end Elevator
