package building;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/** The up call pending - true if any up calls exist */
	private boolean upCallPending;
	
	/** The down call pending - true if any down calls exit */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	void updateCallStatus() {
		upCallPending = false;
		downCallPending = false;
		for (int i = 0; i < floors.length; i++) {
			upCalls[i] = floors[i].passGoingInDir(UP);
			downCalls[i] = floors[i].passGoingInDir(DOWN);
			if (upCalls[i]) upCallPending = true;
			if (downCalls[i]) downCallPending = true;
		}
	}

	/**
	 * Prioritize passenger calls from STOP STATE
	 *
	 * @param floor the floor the elevator is on
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(int floor) {
		updateCallStatus();
		System.out.println(Arrays.toString(upCalls));
		System.out.println(Arrays.toString(downCalls));
		
		Passengers currFloorPass = checkCurrentFloor(floor);
		if (currFloorPass != null) return currFloorPass;
		
		int numUpCalls = 0;
		for (int i = 0; i < floors.length; i++)
			if (upCalls[floor]) numUpCalls++;
		int numDownCalls = 0;
		for (int i = 0; i < floors.length; i++)
			if (downCalls[floor]) numDownCalls++;
		
		int highestDownCall = 0;
		for (int i = floors.length - 1; i >= 0; i--)
			if (downCalls[i]) highestDownCall = i;
		int lowestUpCall = floors.length - 1;
		for (int i = 0; i < floors.length; i++)
			if (upCalls[i]) lowestUpCall = i;
		
		if (numUpCalls > numDownCalls) {
			System.out.println("1");
			return floors[lowestUpCall].peekFloorQueue(UP);
		} else if (numUpCalls < numDownCalls) {
			System.out.println("2");
			return floors[highestDownCall].peekFloorQueue(DOWN);
		} else {
			if (Math.abs(lowestUpCall - floor) <= Math.abs(highestDownCall - floor)) {
				System.out.println("3");
				return floors[lowestUpCall].peekFloorQueue(UP);
			} else {
				System.out.println("4");
				return floors[highestDownCall].peekFloorQueue(DOWN);
			}
		}
	}
	
	Passengers checkCurrentFloor(int floor) {
				if (upCalls[floor])
					return floors[floor].peekFloorQueue(UP);
				if (downCalls[floor])
					return floors[floor].peekFloorQueue(DOWN);
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	/**
	 * Decide whether or not to change directions based on calls and elevator destinations
	 * 
	 * @param e the elevator object
	 * @return whether or not to change directions
	 */
	boolean changeDirection(Elevator e) {
		updateCallStatus();
		int currFloor = e.getCurrFloor();
		int currDir = e.getDirection();
		
		if (currDir == UP) {
			if (currFloor == floors.length - 1) return true;

			if (e.getPassengers() == 0) {
				for (int i = currFloor + 1; i < floors.length; i++)
					if (callOnFloor(i))
						return false;
				return true;
			} else {
				for (Passengers p : e.getAllPassengers())
					if (p.getDestFloor() > currFloor) return false;
				return true;
			}
		} else {
			if (currFloor == 0) return true;

			if (e.getPassengers() == 0) {
				for (int i = 0; i < currFloor; i++)
					if (callOnFloor(i)) return false;
				return true;
			} else {
				for (Passengers p : e.getAllPassengers())
					if (p.getDestFloor() < currFloor) return false;
				return true;
			}
		}
	}
	
	boolean callPending() {
		updateCallStatus();
		return upCallPending || downCallPending;
	}
	
	boolean callOnFloor(int floor) {
		updateCallStatus();
		return upCalls[floor] || downCalls[floor];
	}
	
	boolean callOnFloor(int floor, int elevatorDirection) {
		updateCallStatus();
		return (elevatorDirection == UP)? upCalls[floor] : downCalls[floor];
	}
	
	boolean callerIsPolite(int floor, int elevatorDirection) {
		updateCallStatus();
		return floors[floor].peekFloorQueue((elevatorDirection == UP)? UP : DOWN).getPolite();
	}
	
	//for debug
	void printCalls() {
		System.out.println(Arrays.toString(upCalls));
		System.out.println(Arrays.toString(downCalls));
	}
}
