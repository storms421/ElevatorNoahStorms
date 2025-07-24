import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class ElevatorSimulation {


    public static void main(String[] args) throws IOException, InterruptedException {
        
        Scanner elevatorFile = new Scanner(new File("Elevator.txt"));
        
        System.out.println("Starting up system...\n");
        
        // Initialize Elevator and Floors
        Elevator[] elevatorHolder = new Elevator[1]; // workaround to return Elevator along with Floors
        Floor[] floors = initializeFloors(elevatorFile, elevatorHolder);
        Elevator elevator = elevatorHolder[0];
        
        System.out.println("\nLoading information...");
        
        // Load passengers from file
        ArrayList<Passenger> unarrivedPassengers = loadPassengers(elevatorFile);
        elevatorFile.close();
        
        System.out.println("\nStarting simulation...");
        
        int currentTime = 0; // Time Tracker
        
        /* Found this trying to set a boolean value for a message
         * This lets me store true/false value across methods */
        AtomicBoolean wasIdle = new AtomicBoolean(false);
        
        // Loop simulation while there are passengers in simulation
        while(true) {
            
            // Process passengers that have arrived at elevator to push button
            processArrivingPassengers(currentTime, unarrivedPassengers, floors, elevator.getMinFloor());
            
            // Get current floor object (account for zero-based indexing)
            Floor floor = floors[elevator.getCurrentFloor() - elevator.getMinFloor()];
            
            // Simulate door operations and boarding/unloading passengers
            handleFloorStop(elevator, floor);
            
            // If elevator is empty and no passengers are waiting, end elevator travel
            /* Side note: we would just let the elevator be idle until called upon again.
             * This elevator is currently moving based on information stored in a text
             * file, so we can just end the program when there are no more individuals
             * looking to take the elevator. */
            if(elevator.isElevatorEmpty() && allFloorsEmpty(floors) && unarrivedPassengers.isEmpty()) {
                System.out.println("All Passengers have made it to their final destinations. End Simulation");
                break;
            }
            
            // Check if requests in current direction. If not, reverse direction pending requests in opposite direction
            checkAndMoveElevator(elevator, floors, elevator.getMinFloor(), elevator.getMaxFloor(), wasIdle);
            
            currentTime += 3; // Accounts for elevator open, close, and move (1 + 1 + 1)
        
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
        for(int i = 0; i < totalFloors; i++) {
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
        
        while(elevatorFile.hasNextLine()) {
            
            /* Had to redo this section due to it being
             * buggy. It ended up letting me know one of my
             * lines was messed up*/
            String line = elevatorFile.nextLine().trim();
            
            if(line.isEmpty()) {
                continue;
            }
            
            // Check for any issues in text file
            String[] parts = line.split("\\s+");
            if(parts.length != 4) {
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
        while(iterate.hasNext()) {
            Passenger passenger = iterate.next();
            
            // If passenger has arrived to elevator to push button, add them to the floor list
            if(passenger.getArrivalTime() <= currentTime) {
                int floorIndex = passenger.getCurrentFloor() - minFloor;
                
                // Check if passenger floor is valid
                if(floorIndex >= 0 && floorIndex < floors.length) {
                    floors[floorIndex].addPassenger(passenger);
                    
                    // Lets us know the passenger has pushed the button to the elevator to ride
                    System.out.printf("\nPassenger %s arrived at floor %d at time %d to go to %d\n", passenger.getName(), passenger.getCurrentFloor(),
                            passenger.getArrivalTime(), passenger.getDestinationFloor());
                }
                else {
                    System.out.printf("Invalid floor %d for passenger %s\n", passenger.getCurrentFloor(), passenger.getName());
                }
                
                iterate.remove();
                
            } // end outer-if
            
        } // end while
        
    } // end processArrivingPassengers
    
    // Simulate doors opening, passengers unloading/loading, and doors closing
    private static void handleFloorStop(Elevator elevator, Floor floor) throws InterruptedException {
    	
        int current = elevator.getCurrentFloor();
        
        // Check if any passengers need to get off/on
        boolean hasExiting = false;
        for(Passenger passenger : elevator.getPassengers()) {
        	if(passenger.getDestinationFloor() == current) {
        		hasExiting = true;
        		break;
        	}
        }
        
        boolean hasBoarding = floor.hasWaitingPassengers();
        
        if(!hasExiting && !hasBoarding) {
        //	System.out.printf("No activity on floor %d. Elevator continues without stopping.\n", current);
        	return;
        }
        
        
        // Adding a timer to simulate time for doors open/close and moving to next floor
        System.out.printf("\nDoors opening on floor %d...\n", current);
        Thread.sleep(1000); // 1 second
        
        //First, unload passengers at the floor if any. This is how we make room for new passengers
        ArrayList<Passenger> exiting = elevator.unloadPassenger();
        for(Passenger passenger : exiting) {
            System.out.printf("Passenger %s got off at floor %d\n", passenger.getName(), current);
        }
        
        ArrayList<Passenger> waitingList = new ArrayList<>(); // Load passengers waiting
        
        // If elevator has space, let passengers in elevator. Otherwise, add to waiting list
        while(floor.hasWaitingPassengers()) {
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
         * so this extra array list being used allows for that
         */
        if(!waitingList.isEmpty()) {
            System.out.printf("Elevator is full. The following passenger(s) could not board on floor %d: ", current);
            for(int i = 0; i < waitingList.size(); i++) {
                System.out.print(waitingList.get(i).getName());
                if(i < waitingList.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        
        System.out.printf("Doors closing on floor %d...\n\n", current);
        Thread.sleep(1000); // 1 second
        
    } // end handleFloorStop
    
    // This method checks if requests in current direction exist. If not, reverse direction and move elevator
    private static void checkAndMoveElevator(Elevator elevator, Floor[] floors, int minFloor, int maxFloor, AtomicBoolean wasIdle) throws InterruptedException {
    	
        boolean goingUp = elevator.isGoingUp();
        if(!hasRequestsInDirection(elevator, floors, goingUp, minFloor, maxFloor)) {
            elevator.reverseDirection();
        //    System.out.printf("No current requests %s. Elevator will start going %s.\n", goingUp ? "above" : "below", elevator.isGoingUp() ? "up" : "down");
        }
        
        // Takes care of whether the elevator moves up or down
        if(!elevator.isElevatorEmpty() || !allFloorsEmpty(floors)) {
        	elevator.move();
            Thread.sleep(1000); // 1 second
            wasIdle.set(false);
        } else {
        	if(!wasIdle.get()) { // This stops the below message from repeating itself while idling
        		System.out.println("Elevator is idle. No requests from passengers.");
        		wasIdle.set(true);
        	}
        }
        
    } // end checkAndMoveElevator
    
    // This method checks to see if all floors are empty
    public static boolean allFloorsEmpty(Floor[] floors) {
    	
        for(Floor floor : floors) {
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
        for(Passenger passenger : elevator.getPassengers()) {
            if(goingUp && passenger.getDestinationFloor() > currentFloor) {
                return true;
            }
            if(!goingUp && passenger.getDestinationFloor() < currentFloor) {
                return true;
            }
        }
        
        // Checks if any floors in the given direction have passengers waiting to be picked up
        for(int i = 0; i < floors.length; i++) {
            int floorNum = minFloor + i;
            if(goingUp && floorNum > currentFloor && floors[i].hasWaitingPassengers()) {
                return true;
            }
            if(!goingUp && floorNum < currentFloor && floors[i].hasWaitingPassengers()) {
                return true;
            }
        }
        
        return false; // No requests, allow for reverse
    
    } // end hasRequestsInDirection
    
} // end ElevatorSimulation
