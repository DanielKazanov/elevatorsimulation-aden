import java.util.ArrayList;
import java.util.Arrays;
import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElevatorSimulation extends Application {
	
	/** Instantiate the GUI fields */
	private ElevatorSimController controller;
	private final int NUM_FLOORS;
	private final int NUM_ELEVATORS;
	private Pane pane;
	private final int PANE_WIDTH = 700;
	private final int PANE_HEIGHT = 700;
	private int currFloor;
	private int passengers;
	private int totalTicks;
	private final int ELEVATOR_WIDTH;
	private final int ELEVATOR_HEIGHT;
	private int ELEVATOR_X_POSITION;
	private int ELEVATOR_Y_POSITION;

	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;
	private final int UP = 1;
	private final int DOWN = -1;

	/** Graphical variable objects for redrawing purposes */
	private Timeline t;
	private final int NORMAL_SPEED = 100;
	private int stepSpeed = NORMAL_SPEED;
	
	private Rectangle elevatorRectangle;
	private Line elevatorLine;
	private Text elevatorText;
	
	
	private final int PIXELS_BTWN_FLOORS;
	private Label clock;
	
	private int[] floorYPositions;
	
	private Circle[] circleArr = new Circle[0];
	private Text[] textArr = new Text[0];
	private Polygon[] directionArr = new Polygon[0];
	private Polygon passengersOffloading = new Polygon();
	
	private Rectangle elevatorOpenDoors;
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrFloor();
		
		floorYPositions = new int[NUM_FLOORS + 1];
		
		ELEVATOR_WIDTH = (PANE_WIDTH / 10);
		ELEVATOR_HEIGHT = (PANE_HEIGHT / NUM_FLOORS);
		ELEVATOR_X_POSITION = (PANE_WIDTH / 7);
		
		PIXELS_BTWN_FLOORS = PANE_HEIGHT / (NUM_FLOORS + 1);
	}
	
	public void endSimulation() {
		t.stop();
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();
		primaryStage.setResizable(false);

		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		mainSetup(primaryStage);
	}
	
	/** 
	 * Calls all setup methods that initiate GUI graphical components (buttons, floors, elevator). 
	 * Called once at the start of the GUI creation.
	 * 
	 * @param primaryStage the primary stage
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void mainSetup(Stage primaryStage) {
		t = new Timeline(new KeyFrame(Duration.millis(stepSpeed), ae -> {controller.stepSim(); updateTotalTicks(); passengersOffloadingAnimation();}));
		t.setCycleCount(Animation.INDEFINITE);
		
		BorderPane borderPane = new BorderPane();
		pane = new Pane();
		HBox hBox = new HBox(3);
		Scene scene = new Scene(borderPane, PANE_WIDTH, PANE_HEIGHT);
		
	    borderPane.setCenter(pane);
		borderPane.setBottom(hBox);
		primaryStage.setScene(scene);
		
		buttonSetup(hBox);
		floorSetup();
		elevatorClosedDoors();
	}
	
	/** 
	 * Updates elevator state information and total ticks. Gets called by the time line
	 * every tick.
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void updateTotalTicks() {
		pane.getChildren().remove(clock);
		totalTicks = controller.getStepCnt();
		clock = new Label("Total ticks: " + totalTicks + " | Elevator state: " + elevatorStateToString(controller.getElevatorState()) + 
				" | Elevator direction: " + elevatorDirectionToString(controller.getElevatorDirection()));
		clock.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		pane.getChildren().add(clock);
	}
	
	/** 
	 * Converts elevator state from integer to string. Gets called by the updateTotalTicks
	 * method.
	 * 
	 * @param input the input
	 * @return string value of input
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected String elevatorStateToString(int input) {
		switch (input) {
			case STOP:
				return "STOP";
			case MVTOFLR:
				return "MVTOFLR";
			case OPENDR:
				return "OPENDR";
			case OFFLD:
				return "OFFLD";
			case BOARD:
				return "BOARD";
			case CLOSEDR:
				return "CLOSEDR";
			case MV1FLR:
				return "MV1FLR";
			default:
				return "ERROR";
		}
	}
	
	/** 
	 * Converts elevator direction from integer to string. Gets called by the updateTotalTicks
	 * method.
	 * 
	 * @param input the input
	 * @return string value of input
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected String elevatorDirectionToString(int input) {
		switch (input) {
			case UP:
				return "UP";
			case DOWN:
				return "DOWN";
			default:
				return "ERROR";
		}
	}
	
	/** 
	 * Sets up the buttons at the bottom of the GUI visually and implements setOnAction
	 * to process presses. Button run starts the elevator simulation by pausing or playing
	 * the time line. Button stepButton plays through the elevator simulation for a certain
	 * amount of integer ticks entered by the user through TextField stepTextField. Button log
	 * actuates print statements in the console which present data on the simulation.
	 * 
	 * @param hBox the HBox
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void buttonSetup(HBox hBox) {
		Font font = new Font(25);
		updateTotalTicks();
		
		Button run = new Button("Run");
		run.setFont(font);
		run.setPrefWidth(PANE_WIDTH / 3);
		run.setPrefHeight(PANE_HEIGHT / 9);
		run.setOnAction(e -> {if (t.getStatus() == Animation.Status.RUNNING) {t.pause(); t.setCycleCount(Animation.INDEFINITE);} else {
			t.setCycleCount(Animation.INDEFINITE); t.play();}});
		
		Button stepButton = new Button("Step: ");
		stepButton.setFont(font);
		stepButton.setPrefWidth(PANE_WIDTH / 5);
		stepButton.setPrefHeight(PANE_HEIGHT / 9);
		
		TextField stepTextField = new TextField("enter integer");
		stepTextField.setFont(font);
		stepTextField.setPrefWidth(PANE_WIDTH / 5);
		stepTextField.setPrefHeight(PANE_HEIGHT / 9);
		stepButton.setOnAction(e -> {if (!stepButtonInputCheck(stepTextField.getText())) {stepTextField.setText("enter integer");} else {
			t.stop(); t.setCycleCount(Integer.parseInt(stepTextField.getText())); updateTotalTicks(); t.play();
		}});
		
		Button log = new Button("Log");
		log.setFont(font);
		log.setPrefWidth(PANE_WIDTH / 3);
		log.setPrefHeight(PANE_HEIGHT / 9);
		log.setOnAction(e -> controller.enableLogging());
	    hBox.getChildren().addAll(run, stepButton, stepTextField, log);
	}
	
	/** 
	 * Checks stepTextField input to only allow integer input values.
	 * 
	 * @param input the input
	 * @return true for integer input, false for non-integer input
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected boolean stepButtonInputCheck(String input) {
		if (input.matches("\\d+")) {
			return true;
		}
		return false;
	}
	
	/** 
	 * Sets up the floors for the building, dependent on NUM_FLOORS local variable. Gets called
	 * by mainSetup().
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void floorSetup() {
		Line[] lineArr = new Line[NUM_FLOORS];
		Text[] labelArr = new Text[NUM_FLOORS];
		int yLocation = PANE_HEIGHT - (PANE_HEIGHT / (NUM_FLOORS));
		
		for (int i = 0; i < NUM_FLOORS; i++) {
			lineArr[i] = new Line();
			labelArr[i] = new Text(PANE_WIDTH / 30, yLocation, i + 1 + "");
			
			lineArr[i].setStrokeWidth(15);
			lineArr[i].setStartX(PANE_WIDTH / 3);
			lineArr[i].setEndX(PANE_WIDTH);
			lineArr[i].setStartY(yLocation);
			lineArr[i].setEndY(yLocation);
			labelArr[i].setFont(Font.font("Tahoma", FontWeight.BOLD, 25));
			floorYPositions[i] = yLocation;
			yLocation -= PIXELS_BTWN_FLOORS;
			pane.getChildren().addAll(lineArr[i], labelArr[i]);
		}
		floorYPositions[floorYPositions.length - 1] = yLocation;
		ELEVATOR_Y_POSITION = (floorYPositions[0] + floorYPositions[1] - ELEVATOR_HEIGHT) / 2;
	}
	
	/** 
	 * Draws elevator with closed doors animation. Gets called by stepSim() in controller every time 
	 * elevator state changes (STOP, MVTOFLR, CLOSEDR, MV1FLR).
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void elevatorClosedDoors() {
		passengers = controller.getNumPassengersInElevator();
		removeOpenElevator();
		removeClosedElevator();
		
		elevatorRectangle = new Rectangle(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);
		elevatorLine = new Line();
		elevatorRectangle.setStyle("-fx-fill: black; -fx-stroke: black; -fx-stroke-width: 5;");
		
		elevatorLine.setStrokeWidth(3);
		elevatorLine.setStroke(Color.LIGHTGRAY);
		elevatorLine.setStartX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
		elevatorLine.setEndX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
		elevatorLine.setStartY(ELEVATOR_Y_POSITION);
		elevatorLine.setEndY(ELEVATOR_Y_POSITION + ELEVATOR_HEIGHT);
		
		elevatorText = new Text(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION - 10, "Passengers: " + this.passengers);
		elevatorText.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		
		pane.getChildren().addAll(elevatorRectangle, elevatorLine, elevatorText);
	}
	
	/** 
	 * Draws elevator with open doors animation. Gets called by stepSim() in controller every time
	 * elevator state changes (OPENDR, OFFLD, BOARD).
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void elevatorOpenDoors() {
		passengers = controller.getNumPassengersInElevator();
		removeClosedElevator();
		removeOpenElevator();
		
		elevatorOpenDoors = new Rectangle(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);
		elevatorOpenDoors.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 5;");
		elevatorText = new Text(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION - 10, "Passengers: " + this.passengers);
		elevatorText.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		
		pane.getChildren().addAll(elevatorOpenDoors, elevatorText);
	}
	
	/** 
	 * Updates ELEVATOR_Y_POSITION dependent on destined floor y position.
	 * 
	 * @param startFloor the starting floor
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void elevatorMoveToFloor(int startFloor) {
		ELEVATOR_Y_POSITION = (int)(-1 * controller.getElevatorDirection() 
				* (((controller.getTimeInState() + 1) / (double)(controller.getFloorTicks())) * PIXELS_BTWN_FLOORS)
				+ (floorYPositions[startFloor] + floorYPositions[startFloor + 1] - ELEVATOR_HEIGHT) / 2f);
		
		elevatorClosedDoors();
	}
	
	/** 
	 * Removes closed elevator objects from the GUI for repositioning purposes.
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void removeClosedElevator() {
		pane.getChildren().removeAll(elevatorRectangle, elevatorLine, elevatorText);
	}
	
	/** 
	 * Removes open elevator objects from the GUI for repositioning purposes.
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void removeOpenElevator() {
		pane.getChildren().removeAll(elevatorOpenDoors);
	}
	
	/** 
	 * Sets up the passenger groups by getting passenger data from the controller.
	 * Gets called by stepSim() in controller every time new passenger groups enter 
	 * the building.
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void passengerGroupSetup() {
		for (int i = 0; i < circleArr.length; i++) {
			pane.getChildren().removeAll(circleArr[i], textArr[i], directionArr[i]);
		}		
		ArrayList<Integer>[] passengerData = controller.getAllPassengerData();
		int[] numPassengersOnFloor = new int[NUM_FLOORS];
		
		circleArr = new Circle[passengerData[0].size()];
		textArr = new Text[passengerData[0].size()];
		directionArr = new Polygon[passengerData[0].size()];
		
		for (int i = 0; i < passengerData[0].size(); i++) {
			int numPeople = passengerData[0].get(i); // Number of people in group
			int currFloor = passengerData[1].get(i); // Current floor
			int destFloor = passengerData[2].get(i); // Destination floor
			int politeness = passengerData[3].get(i); // Politeness (0 is impolite, 1 is polite)
			
			circleArr[i] = new Circle((PANE_WIDTH / 2) + numPassengersOnFloor[currFloor] * 100, 
					(floorYPositions[currFloor + 1] + floorYPositions[currFloor]) / 2, PIXELS_BTWN_FLOORS * 0.35);
			textArr[i] = new Text((PANE_WIDTH / 2) + numPassengersOnFloor[currFloor] * 100, 
					(floorYPositions[currFloor + 1] + floorYPositions[currFloor]) / 2, numPeople + "");
			textArr[i].setStyle("-fx-stroke: lightgray;");
			
			passengerDirectionAnimation(currFloor, destFloor, i);

			numPassengersOnFloor[currFloor]++;
			pane.getChildren().addAll(circleArr[i], textArr[i], directionArr[i]);
		}
	}
	
	/** 
	 * Graphical component of direction of passenger groups on specific floors. Gets 
	 * called by the passengerGroupSetup method.
	 * 
	 * @param currFloor the current floor of the passenger group
	 * @param destFloor the destination floor of the passenger group
	 * @param i the index of the passenger group in the queue
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void passengerDirectionAnimation(int currFloor, int destFloor, int i) {
		int floorYPositonsOneHigher = floorYPositions[currFloor + 1];
		int currentFloorYPosition = floorYPositions[currFloor];
		
		if (currFloor < destFloor) {
			directionArr[i] = new Polygon();
			directionArr[i].getPoints().addAll(new Double[]{
					(PANE_WIDTH / 3.0) + (PANE_WIDTH * 0.04), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0) - PIXELS_BTWN_FLOORS * 0.35,
					(PANE_WIDTH / 3.0) + (PANE_WIDTH * 0.08), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0),
					(PANE_WIDTH / 3.0), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0)});
		} else {
			directionArr[i] = new Polygon();
			directionArr[i].getPoints().addAll(new Double[]{
					(PANE_WIDTH / 3.0) + (PANE_WIDTH * 0.04), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0) + PIXELS_BTWN_FLOORS * 0.35,
					(PANE_WIDTH / 3.0) + (PANE_WIDTH * 0.08), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0),
					(PANE_WIDTH / 3.0), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0)});
		}
	}
	
	/** 
	 * Graphical component of passengers off-loading from the elevator. Gets called by
	 * the time line.
	 * 
	 * Author: DK
	 * Reviewer: RT, BX
	 */
	protected void passengersOffloadingAnimation() {
		int currFloor = controller.getCurrFloor();
		int floorYPositonsOneHigher = floorYPositions[currFloor + 1];
		int currentFloorYPosition = floorYPositions[currFloor]; 
		
		if (controller.getElevatorState() != OFFLD) {
			pane.getChildren().remove(passengersOffloading);
		}
		
		if (controller.getElevatorState() == OFFLD && !pane.getChildren().contains(passengersOffloading)) {
			passengersOffloading = new Polygon();
			passengersOffloading.getPoints().addAll(new Double[]{
				    (PANE_WIDTH / 3.8) + (PANE_WIDTH * 0.04), ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0),
				    PANE_WIDTH / 3.8, ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0) - PIXELS_BTWN_FLOORS * 0.15,
				    PANE_WIDTH / 3.8, ((floorYPositonsOneHigher + currentFloorYPosition) / 2.0) + PIXELS_BTWN_FLOORS * 0.15 });
			passengersOffloading.setStyle("-fx-fill: lightgray;");
			pane.getChildren().add(passengersOffloading);
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}
}