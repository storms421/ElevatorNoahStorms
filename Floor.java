import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class Floor {
	
	private int floorNumber;
	private Deque<Passenger> waitingPassengers;
	 
	public Floor(int floorNumber) {
		
		this.floorNumber = floorNumber;
		waitingPassengers = new LinkedList<>();
		
	} // end constructor
	
	public int getFloorNumber() {
		
		return floorNumber;
		
	} // end getFloorNumber
	
	public void addPassenger(Passenger p) {
		
		waitingPassengers.addLast(p);
		
	} // end addPassenger
	
	public boolean hasWaitingPassengers() {
		
		return !waitingPassengers.isEmpty();
	
	} // end hasWaitingPassengers
	
	public Passenger peekNextPassenger() {
		
		return waitingPassengers.peekFirst();
	
	} // end peekNextPassenger
	
	public Passenger getNextPassenger() {
		
		return waitingPassengers.pollFirst();
		
	} // end getNextPassenger
	
	public ArrayList<Passenger> peekAllPassengers(){
		
		return new ArrayList<>(waitingPassengers);
		
	} // end peekAllPassengers

} // end Floor
