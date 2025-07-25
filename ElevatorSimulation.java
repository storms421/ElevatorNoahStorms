import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

 
 public class ElevatorSimulation {
	   public static void main(String[] args) throws IOException, InterruptedException {

		   //Check if a file path was passed as an argument
		   if (args.length == 0) {
		        System.out.println("Usage: java ElevatorSimulation <input_file>");
		        return;
		   }

		   String filename = args[0];
		   Scanner elevatorFile = new Scanner(new File(filename));

	       System.out.println("Starting up system...\n");

	       // Initialize Elevator and Floors
	       Elevator[] elevatorHolder = new Elevator[1]; // Workaround to return Elevator along with Floors
	       Floor[] floors = initializeFloors(elevatorFile, elevatorHolder);
	       Elevator elevator = elevatorHolder[0];

	       System.out.println("\nLoading information...");

	       // Load passengers from file
	       ArrayList<Passenger> unarrivedPassengers = loadPassengers(elevatorFile);
	       elevatorFile.close();

	       Collections.sort(unarrivedPassengers); // Using to sort arrival times

	       System.out.println("\nStarting simulation...");

	       int currentTime = 0; // Time Tracker

	       /* Found this trying to set a boolean value for a message
	        * This lets me store true/false value across methods */
	       AtomicBoolean wasIdle = new AtomicBoolean(false);

	       // This is to help track the elevator's actions
	       int actionStage = 0; // 0 = idle, 1 = door opening, 2 = load/unload, 3 = door closing, 4 = move
	       
	       // Create action object for elevator
	       ElevatorActionState action = new ElevatorActionState();

	       // Loop simulation while there are passengers in simulation
	       while (true) {

	           Thread.sleep(1000); // 1 second
	           currentTime++;

	           // Process passengers that have arrived at elevator to push button
	           processArrivingPassengers(currentTime, unarrivedPassengers, floors, elevator.getMinFloor());

	           // If elevator is empty and no passengers are waiting, end elevator travel
	           /* Side note: we would just let the elevator be idle until called upon again.
	            * This elevator is currently moving based on information stored in a text
	            * file, so we can just end the program when there are no more individuals
	            * looking to take the elevator. */
	           if (elevator.isElevatorEmpty() && allFloorsEmpty(floors) && unarrivedPassengers.isEmpty() && actionStage == 0) {
	               System.out.println("All Passengers have made it to their final destinations. End Simulation");
	               break;
	           }
	           
	           // Control what the elevator should do next based on current stage
	           performElevatorAction(action, elevator, floors, wasIdle);

	       } // end while

	   } // end main

	   // Pull constants for elevator and establish floors
	   private static Floor[] initializeFloors(Scanner elevatorFile, Elevator[] elevatorOut) {
	       int maxCapacity = elevatorFile.nextInt();
	       int minFloor = elevatorFile.nextInt();
	       int maxFloor = elevatorFile.nextInt();

	       // Create Elevator object
	       elevatorOut[0] = new Elevator(maxCapacity, minFloor, maxFloor);

	       // Establish floor levels
	       int totalFloors = (maxFloor - minFloor + 1);
	       Floor[] floors = new Floor[totalFloors];
	       for (int i = 0; i < totalFloors; i++) {
	           int floorNum = minFloor + i;
	           floors[i] = new Floor(floorNum);
	           System.out.printf("Floor %d has been established.\n", floorNum);
	       }

	       System.out.printf("\nThis elevator has a max capacity of %d passengers.\n", maxCapacity);

	       return floors;

	   } // end initializeFloors

	   // While reading through the file, pull passenger information
	   private static ArrayList<Passenger> loadPassengers(Scanner elevatorFile) {
	       ArrayList<Passenger> unarrivedPassengers = new ArrayList<>();

	       while (elevatorFile.hasNextLine()) {

	           /* Had to re-do this section due to it being
	            * buggy. It ended up letting me know one of my
	            * lines was messed up. Less lines though!*/
	           String line = elevatorFile.nextLine().trim();

	           if (line.isEmpty()) {
	               continue;
	           }

	           // Check for any issues in text file
	           String[] parts = line.split("\\s+");
	           if (parts.length != 4) {
	               System.out.printf("Skipping malformed line %s\n", line);
	               continue;
	           }

	           // Store passenger information
	           String name = parts[0];
	           int currentFloor = Integer.parseInt(parts[1]);
	           int destinationFloor = Integer.parseInt(parts[2]);
	           int arrivalTime = Integer.parseInt(parts[3]);

	           Passenger passenger = new Passenger(currentFloor, destinationFloor, name, arrivalTime); // Create Passenger objects
	           //Verify information coming in
	           System.out.printf("Passenger %s is loading into the simulation. This passenger is on floor %d planning to go to floor %d. They will be arriving at time %d seconds.\n",
	                   passenger.getName(), passenger.getCurrentFloor(), passenger.getDestinationFloor(), passenger.getArrivalTime());
	           unarrivedPassengers.add(passenger);

	       } // end while

	       return unarrivedPassengers;

	   } // end loadPassengers

	   // Check and add passengers who have arrived at elevator to floor lists
	   private static void processArrivingPassengers(int currentTime, ArrayList<Passenger> unarrivedPassengers, Floor[] floors, int minFloor) {

	       Iterator<Passenger> iterate = unarrivedPassengers.iterator();
	       while (iterate.hasNext()) {
	           Passenger passenger = iterate.next();

	           // If passenger has arrived to elevator to push button, add them to the floor list
	           if (passenger.getArrivalTime() <= currentTime) {
	               int floorIndex = passenger.getCurrentFloor() - minFloor;

	               // Check if passenger floor is valid
	               if (floorIndex >= 0 && floorIndex < floors.length) {
	                   floors[floorIndex].addPassenger(passenger);

	                   // Lets us know the passenger has pushed the button to the elevator to ride
	                   System.out.printf("Passenger %s arrived at floor %d at time %d to go to %d\n", passenger.getName(), passenger.getCurrentFloor(),
	                           passenger.getArrivalTime(), passenger.getDestinationFloor());
	               } else {
	                   System.out.printf("Invalid floor %d for passenger %s\n", passenger.getCurrentFloor(), passenger.getName());
	               }

	               iterate.remove();

	           } // end outer-if

	       } // end while

	   } // end processArrivingPassengers

	   // Simulate doors opening, passengers unloading/loading, and doors closing
	   private static void handleFloorStop(Elevator elevator, Floor floor) {

	       int current = elevator.getCurrentFloor();

	       // Check if any passengers need to get off/on
	       boolean hasExiting = false;
	       for (Passenger passenger : elevator.getPassengers()) {
	           if (passenger.getDestinationFloor() == current) {
	               hasExiting = true;
	               break;
	           }
	       }

	       boolean hasBoarding = floor.hasWaitingPassengers();

	       if (!hasExiting && !hasBoarding) {
	           //	System.out.printf("No activity on floor %d. Elevator continues without stopping.\n", current);
	           return;
	       }

	       //First, unload passengers at the floor if any. This is how we make room for new passengers
	       ArrayList<Passenger> exiting = elevator.unloadPassenger();
	       for (Passenger passenger : exiting) {
	           System.out.printf("Passenger %s got off at floor %d\n", passenger.getName(), current);
	       }

	       ArrayList<Passenger> waitingList = new ArrayList<>(); // Load passengers waiting

	       // If elevator has space, let passengers in elevator. Otherwise, add to waiting list
	       while (floor.hasWaitingPassengers()) {
	           Passenger passenger = floor.getNextPassenger();
	           if (!elevator.isElevatorFull()) {
	               elevator.addPassenger(passenger);
	               System.out.printf("Passenger %s boarded on floor %d going to %d%n", passenger.getName(), elevator.getCurrentFloor(), passenger.getDestinationFloor());
	           } else {
	               waitingList.add(passenger);
	           }
	       }

	       // Re-add waiting passengers back to floor queue
	       for (Passenger waiting : waitingList) {
	           floor.addPassenger(waiting);
	       }

	       // Announce all the passengers that could not get on the floor
	       /* Side note: Originally, you could not see if multiple passengers had to wait,
	        * so this extra array list being used allows for that. */
	       if (!waitingList.isEmpty()) {
	           System.out.printf("Elevator is full. The following passenger(s) could not board on floor %d: ", current);
	           for (int i = 0; i < waitingList.size(); i++) {
	               System.out.print(waitingList.get(i).getName());
	               if (i < waitingList.size() - 1) {
	                   System.out.print(", ");
	               }
	           }
	           System.out.println();
	       }

	   } // end handleFloorStop

	   // This method checks if requests in current direction exist. If not, reverse direction and move elevator
	   private static void checkAndMoveElevator(Elevator elevator, Floor[] floors, int minFloor, int maxFloor, AtomicBoolean wasIdle) {

	       boolean goingUp = elevator.isGoingUp();

	       // First check: if no requests in current direction, reverse
	       if (!hasRequestsInDirection(elevator, floors, goingUp, minFloor, maxFloor)) {
	           elevator.reverseDirection();

	           // Re-check after reversing
	           goingUp = elevator.isGoingUp();
	           if (!hasRequestsInDirection(elevator, floors, goingUp, minFloor, maxFloor)) {
	               // No requests in either direction, then elevator is idle
	               if (!wasIdle.get()) { // This stops the below message from repeating itself while idling
	                   System.out.println("Elevator is idle. No requests from passengers."); // Bug here. It will print when the floor reaches top or bottom
	                   wasIdle.set(true);
	               }
	               return; // Prevent movement if no valid direction
	           }
	       }

	       // Takes care of whether the elevator moves up or down
	       if (!elevator.isElevatorEmpty() || !allFloorsEmpty(floors)) {
	           elevator.move();
	           wasIdle.set(false);
	       } else {
	           if (!wasIdle.get()) { // This stops the below message from repeating itself while idling
	               System.out.println("Elevator is idle. No requests from passengers.");
	               wasIdle.set(true);
	           }
	       }

	   } // end checkAndMoveElevator

	   // This method checks to see if all floors are empty
	   public static boolean allFloorsEmpty(Floor[] floors) {

	       for (Floor floor : floors) {
	           if (floor.hasWaitingPassengers()) { // If floor has a passenger waiting, say floor not empty
	               return false;
	           }
	       }
	       return true;

	   } // end allFloorsEmpty

	   // This method checks for any active elevator requests from those waiting on floors or currently in the elevator
	   public static boolean hasRequestsInDirection(Elevator elevator, Floor[] floors, boolean goingUp, int minFloor, int maxFloor) {

	       int currentFloor = elevator.getCurrentFloor();

	       // Checks if any on-board passengers want to get off in direction of travel
	       for (Passenger passenger : elevator.getPassengers()) {
	           if (goingUp && passenger.getDestinationFloor() > currentFloor) {
	               return true;
	           }
	           if (!goingUp && passenger.getDestinationFloor() < currentFloor) {
	               return true;
	           }
	       }

	       // Checks if any floors in the given direction have passengers waiting to be picked up
	       for (int i = 0; i < floors.length; i++) {
	           int floorNum = minFloor + i;
	           if (goingUp && floorNum > currentFloor && floors[i].hasWaitingPassengers()) {
	               return true;
	           }
	           if (!goingUp && floorNum < currentFloor && floors[i].hasWaitingPassengers()) {
	               return true;
	           }
	       }

	       return false; // No requests, allow for reverse

	   } // end hasRequestsInDirection
	   
	   // This method performs the elevator actions door open/close, unload/load, and move
	   public static void performElevatorAction(ElevatorActionState action, Elevator elevator, Floor[] floors, AtomicBoolean wasIdle) {

	       // If elevator is not doing anything yet, check if it needs to stop or move
	       if (action.stage == 0) {
	           // This only acts if there are people on-board or people waiting on any floor
	           if (!elevator.isElevatorEmpty() || !allFloorsEmpty(floors)) {
	               Floor floor = floors[elevator.getCurrentFloor() - elevator.getMinFloor()];

	               // This checks if anyone needs to get off or on at this floor
	               boolean hasExiting = false;
	               for (Passenger passenger : elevator.getPassengers()) {
	                   if (passenger.getDestinationFloor() == elevator.getCurrentFloor()) {
	                       hasExiting = true;
	                       break;
	                   }
	               }

	               // Check direction of boarding passengers
	               boolean hasBoardingSameDirection = false;
	               boolean hasBoardingOppositeDirection = false;
	               
	               // Passengers who want to board elevator need to be checked if same or opposite direction of elevator
	               for (Passenger passenger : floor.peekAllPassengers()) {
	                   boolean wantsToGoUp = passenger.getDestinationFloor() > passenger.getCurrentFloor();
	                   if (wantsToGoUp == elevator.isGoingUp()) {
	                       hasBoardingSameDirection = true;
	                       break;
	                   } else {
	                       hasBoardingOppositeDirection = true;
	                   }
	               }

	               if (hasExiting || hasBoardingSameDirection || (hasExiting && hasBoardingOppositeDirection)) {
	                   System.out.printf("\nDoors opening on floor %d...\n", elevator.getCurrentFloor());
	                   action.stage = 1; // This starts door open stage
	                   action.timer = 1; // 1 second
	               } else {
	                   // If there is no one to pick up/drop off, go straight to move stage
	                   action.stage = 4;
	                   action.timer = 1;
	               }
	           } else {
	               // If there is no passengers or anyone waiting, let elevator go idle
	               if (!wasIdle.get()) {
	                   System.out.println("Elevator is idle. No requests from passengers.");
	                   wasIdle.set(true);
	               }
	           }

	       } // end stage 0

	       // Stage 1: doors open
	       else if (action.stage == 1) {
	           if (--action.timer <= 0) {
	               action.stage = 2; // To next stage
	               action.timer = 1; // Resets timer for stage
	           }
	       }

	       // Stage 2: unload/load passengers
	       else if (action.stage == 2) {
	           if (--action.timer <= 0) {
	               Floor floor = floors[elevator.getCurrentFloor() - elevator.getMinFloor()];
	               handleFloorStop(elevator, floor); // Use to help move people in/out
	               System.out.printf("Doors closing on floor %d...\n\n", elevator.getCurrentFloor());
	               action.stage = 3; // To next stage
	               action.timer = 1;
	           }
	       }

	       // Stage 3: door closing
	       else if (action.stage == 3) {
	           if (--action.timer <= 0) {
	               action.stage = 4; // To next stage
	               action.timer = 1;
	           }
	       }

	       // Stage 4: elevator moving
	       else if (action.stage == 4) {
	           if (--action.timer <= 0) {
	               checkAndMoveElevator(elevator, floors, elevator.getMinFloor(), elevator.getMaxFloor(), wasIdle); // Check if idle state now or reverse direction
	               action.stage = 0; // Done with actions
	           }
	       }

	   } // end performElevatorAction


} // end ElevatorSimulation
 
 
 // If I were to add another elevator, I would want to make an ElevatorManagement class to handle two or more elevators add a time and have them communicate
 /* I would want to be able to see these two elevators moving at the same time, so have it where I open up two terminals that connect to one another and run
    simultaneously */
 // I would want to have edge cases for my elevators just in case both elevators are at the opposite ends of the building, needing to get to the middle
 // Have the elevator that may be idle, help the other elevator that may become busy
 // Making sure both of my elevators do not stop on the same floor