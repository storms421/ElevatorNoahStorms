import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ElevatorSimulation {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		Scanner elevatorFile = new Scanner(new File("Elevator.txt"));
		
		System.out.println("Starting up system...\n");
		
		// Pull constants for elevator
		int maxCapacity = elevatorFile.nextInt();
		int minFloor = elevatorFile.nextInt();
		int maxFloor = elevatorFile.nextInt();
		
		// Create Elevator object
		Elevator elevator = new Elevator(maxCapacity, minFloor, maxFloor);
		
		// Establish floor levels
		int totalFloors = (maxFloor - minFloor + 1);
		Floor[] floors = new Floor[totalFloors];
		for(int i = 0; i < totalFloors; i++) {
			int floorNum = minFloor + i;
			floors[i] = new Floor(floorNum);
			System.out.printf("Floor %d has been established.\n", floorNum);
		}
		
		System.out.printf("\nThis elevator has a max capacity of %d passengers.\n", maxCapacity);
		
		System.out.println("\nLoading information...");
		
		// While reading through the file, pull passenger information
		while(elevatorFile.hasNextLine()) {
			
			// Checking to see if there is more info for passenger. Buggy without
			if(!elevatorFile.hasNext()) {
				break;
			}
			String name = elevatorFile.next();
			
			if(!elevatorFile.hasNextInt()) {
				break;
			}
			int currentFloor = elevatorFile.nextInt();
			
			if(!elevatorFile.hasNextInt()) {
				break;
			}
			int destinationFloor = elevatorFile.nextInt();
			
			Passenger passenger = new Passenger(currentFloor, destinationFloor, name); // Create Passenger objects
			
			// Adding passengers to their starting points in the building
			int floorIndex = currentFloor - minFloor;
			// If the passenger is on a legitimate floor, add them to that floor
			if(floorIndex >= 0 && floorIndex < floors.length) {
				floors[floorIndex].addPassenger(passenger);
				System.out.printf("Passenger %s is waiting on floor %d to go to %d%n", passenger.getName(), passenger.getCurrentFloor(), passenger.getDestinationFloor());
			}
			else {
				System.out.printf("Invalid starting floor %d for passenger %s\n", currentFloor, name);
			}
		}
		
		elevatorFile.close();
		
		System.out.println("\nStarting simulation...");
		
		// Loop simulation while there are passengers (currently will stop too if reaches the top)
		while(true) {
			int current = elevator.getCurrentFloor();
			Floor floor = floors[current - minFloor]; // Account for zero based indexing in array
			
			// Adding a timer to simulate time for doors open/close and moving to next floor
			System.out.printf("Doors opening on floor %d...\n", current);
			Thread.sleep(3000); // 3 seconds
			
			//First, unload passengers at the floor if any. This is how we make room for new passengers
			ArrayList<Passenger> exiting = elevator.unloadPassenger();
			for(Passenger passenger : exiting) {
				System.out.printf("Passenger %s got off at floor %d\n", passenger.getName(), current);
			}
			
			ArrayList<Passenger> waitingList = new ArrayList<>(); // Load passengers waiting
			ArrayList<Passenger> blockedList = new ArrayList<>(); // Block waiting passengers if full
			
			// Elevator checks to see if floor has passengers waiting to board
			while(floor.hasWaitingPassengers()) {
				
				waitingList.add(floor.getNextPassenger());
				
			}
			
			
			for(Passenger passenger : waitingList) {
				//If elevator is not full, let passenger(s) on
				if(!elevator.isElevatorFull()) {
					floor.getNextPassenger();
					elevator.addPassenger(passenger);
					System.out.printf("Passenger %s boarded on floor %d going to %d%n", passenger.getName(), elevator.getCurrentFloor(), passenger.getDestinationFloor());
				}
				else { // If elevator is full, add to list of those that cannot enter
					blockedList.add(passenger);
					floor.addPassenger(passenger);
				}
			} // end for
			
			// Announce all the passengers that could not get on the floor
			/* Side note: Originally, you could not see if multiple passengers had to wait,
			 * so this extra array list being used allows for that
			 */
			if(!blockedList.isEmpty()) {
				System.out.printf("Elevator is full. The following passenger(s) could not board on floor %d: ", current);
				for(int i = 0; i < blockedList.size(); i++) {
					System.out.print(blockedList.get(i).getName());
					if(i < blockedList.size() - 1) {
						System.out.print(", ");
					}
				}
				System.out.println();
			} // end if
			
			System.out.printf("\nDoors closing on floor %d...\n", current);
			Thread.sleep(3000);
			
			// If elevator is empty and no passengers are waiting, end elevator travel
			/* Side note: we would just let the elevator be idle until called upon again.
			 * This elevator is currently moving based on information stored in a text
			 * file, so we can just end the program when there are no more individuals
			 * looking to take the elevator. */
			if(elevator.isElevatorEmpty() && allFloorsEmpty(floors)) {
				System.out.println("All Passengers have made it to their final destinations. End Simulation");
				break;
			}
			
			// Takes care of whether the elevator moves up or down
			elevator.move();
			Thread.sleep(3000);
			
		} // end while
		
	} // end main
	
	// This method checks to see if all floors are empty
	public static boolean allFloorsEmpty(Floor[] floors) {
		for(Floor f : floors) {
			if (f.hasWaitingPassengers()) { // If floor has a passenger waiting, say floor not empty
				return false;
			}
		}
		return true;
		
	} // end allFloorsEmpty
	

} // end ElevatorSimulation
