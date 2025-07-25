# Elevator Simulation

This project simulates the operation of an elevator system, where multiple passengers arrive at different floors and request to be taken to various destinations. The elevator moves, loads, and unloads passengers based on the specified input data.

## Features

- Simulates multiple passengers arriving at different floors
- Elevator movement based on requests
- Loading and unloading of passengers
- Elevator stops at floors only when needed
- Supports data input from a text file

## Getting Started

### 1. Clone the Repository

To download the project to your local machine, clone the repository from GitHub:

```
git clone https://github.com/storms421/ElevatorNoahStorms.git
cd elevator-simulation
```
### 2. Compile the Code

Before running the simulation, compile all Java files:

```
javac *.java
```

### 3. Run the Simulation

Once compiled, you can run the simulation by providing a path to a test input text file:

```
java ElevatorSimulation <path-to-test-file>
```

### Example Input File Format

The input file should follow this structure:

```
java ElevatorSimulation Elevator.txt
```

# Test File Information

Elevator.txt
- This is a main test file with random data to simulate a long duration of the elevator.

Elevator2.txt
- This tests the program where all the passengers press at the same time, but those going in one direction are taken care of first, then the rest are picked up the other way.

Elevator3.txt
- This tests the program for the elevator to be idle and show a wait time before the button is pressed to get the elevator moving again.

Elevator4.txt
- This tests the program for when the elevator is full and someone has to stay behind.
