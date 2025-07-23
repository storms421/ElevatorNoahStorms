import java.util.Deque;
import java.util.LinkedList;

public class Floor {
	
	private int floorNumber;
	private Deque<Passenger> waitingPassengers;
//	private boolean upButtonPressed;
	//private boolean downButtonPressed;
	 
	public Floor(int floorNumber) {
		
		this.floorNumber = floorNumber;
		//this.upButtonPressed = upButtonPressed;
		//this.downButtonPressed = downButtonPressed;
		waitingPassengers = new LinkedList<>();
		
	} // end constructor
	
	public int getFloorNumber() {
		
		return floorNumber;
		
	} // end getFloorNumber
	
	public void addPassenger(Passenger p) {
		
		waitingPassengers.addLast(p);
		
	} // end addPassenger
	
	
	//public Deque<Passenger> getPassengerQueue(){
		
//		return waitingPassengers;
		
	//} // end getPassengerQueue
	
	public boolean hasWaitingPassengers() {
		
		return !waitingPassengers.isEmpty();
	
	} // end hasWaitingPassengers
	
	public Passenger peekNextPassenger() {
		
		return waitingPassengers.peekFirst();
	
	} // end peekNextPassenger
	
	public Passenger getNextPassenger() {
		
		return waitingPassengers.pollFirst();
		
	} // end getNextPassenger

} // end Floor
