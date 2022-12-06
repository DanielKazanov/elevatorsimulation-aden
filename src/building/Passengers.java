package building;

// TODO: Auto-generated Javadoc
/**
 * The Class Passengers. Represents a GROUP of passengers that are 
 * traveling together from one floor to another. Tracks information that 
 * can be used to analyze Elevator performance.
 */
public class Passengers {
	
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;
	
	/**  ID represents the NEXT available id for the passenger group. */
	private static int ID=0;

	/** id is the unique ID assigned to each Passenger during construction.
	 *  After assignment, static ID must be incremented.
	 */
	private int id;
	
	/** These fields will be passed into the constructor by the Building.
	 *  This data will come from the .csv file read by the SimController
	 */
	private int time;         // the time that the Passenger will call the elevator
	private int numPass;      // the number of passengers in this group
	private int onFloor;      // the floor that the Passenger will appear on
	private int destFloor;	  // the floor that the Passenger will get off on
	private boolean polite;   // will the Passenger let the doors close?
	private int waitTime;     // the amount of time that the Passenger will wait for the
	                          // Elevator
	
	/** These values will be calculated during construction.
	 */
	private int direction;      // The direction that the Passenger is going
	private int timeWillGiveUp; // The calculated time when the Passenger will give up
	
	/** These values will actually be set during execution. Initialized to -1 */
	private int boardTime=-1;
	private int timeArrived=-1;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param numPass the number of people in this Passenger
	 * @param on the floor that the Passenger calls the elevator from
	 * @param dest the floor that the Passenger is going to
	 * @param polite - are the passengers polite?
	 * @param waitTime the amount of time that the passenger will wait before giving up
	 */
	public Passengers(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
	// TODO: Write the constructor for this class
	//       Remember to appropriately adjust the onFloor and destFloor to account  
	//       to convert from American to European numbering...
		id = ID++;
		
		this.time = time;
		this.numPass = numPass;
		this.onFloor = on - 1;
		this.destFloor = dest - 1;
		this.polite = polite;
		this.waitTime = waitTime;
		
		direction = (destFloor - onFloor) / Math.abs(destFloor - onFloor);
	}
	
	
	// TODO: Write any required getters/setters for this class


	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTime() {
		return this.time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getNumPass() {
		return this.numPass;
	}

	public void setNumPass(int numPass) {
		this.numPass = numPass;
	}

	public int getOnFloor() {
		return this.onFloor;
	}

	public void setOnFloor(int onFloor) {
		this.onFloor = onFloor;
	}

	public int getDestFloor() {
		return this.destFloor;
	}

	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	public boolean isPolite() {
		return this.polite;
	}

	public boolean getPolite() {
		return this.polite;
	}

	public void setPolite(boolean polite) {
		this.polite = polite;
	}

	public int getWaitTime() {
		return this.waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public int getDirection() {
		return this.direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getTimeWillGiveUp() {
		return this.timeWillGiveUp;
	}

	public void setTimeWillGiveUp(int timeWillGiveUp) {
		this.timeWillGiveUp = timeWillGiveUp;
	}

	public int getBoardTime() {
		return this.boardTime;
	}

	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}

	public int getTimeArrived() {
		return this.timeArrived;
	}

	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}

	// 
	/**
	 * Reset static ID. 
	 * This method MUST be called during the building constructor BEFORE
	 * reading the configuration files. This is to provide consistency in the
	 * Passenger ID's during JUnit testing.
	 */
	static void resetStaticID() {
		ID = 0;
	}

	/**
	 * toString - returns the formatted string for this class
	 *
	 * @return the 
	 */
	@Override
	public String toString() {
		return("ID="+id+"   Time="+time+"   NumPass="+numPass+"   From="+(onFloor+1)+"   To="+(destFloor+1)+"   Polite="+polite+"   Wait="+waitTime);
	}

}
