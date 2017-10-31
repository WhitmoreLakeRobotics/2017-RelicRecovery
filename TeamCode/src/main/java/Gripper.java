/*

*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "Gripper", group = "")  // @Autonomous(...) is the other common choice

public class Gripper extends OpMode {


    /*
    Gripper Life Cycle
    Start
    Gripper_current == Gripper_unknown
    cmd_Close()
    Gripper_current == Gripper_closing
    reset close_timer to 0
    Loop until close_timer to be greater than GRIPPER_CLOSE_TIME_MS
    When close_timer  > GRIPPER_CLOSE_TIME_MS
    set Gripper_current = Gripper_closed
    Do Nothing until we recieve another cmd

    cmd_open
    Gripper_current == Gripper_opening;
    reset open_timer = 0
    loop until open_timer to be greater than GRIPPER_OPEN_TkIME_MS
    When open_timer > GRIPPER_OPEN_TIME_MS
    set Gripper_current = Gripper_open
    Do Nothing until we recieve another cmd


     */

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime close_timer = new ElapsedTime();
    private ElapsedTime open_timer = new ElapsedTime();

    //Time delays for opening and closing the gripper... These will need to be tuned
    private final int GRIPPER_OPEN_TIME_MS = 500;
    private final int GRIPPER_CLOSE_TIME_MS = 750;


    // Gripper states
    public static final int GRIPPER_MODE_UNKNOWN = 0;
    public static final int GRIPPER_MODE_CLOSING =1;
    public static final int GRIPPER_MODE_CLOSED = 2;
    public static final int GRIPPER_MODE_OPENING = 3;
    public static final int GRIPPER_MODE_OPEN = 4;
    private int Gripper_current = GRIPPER_MODE_UNKNOWN;


    // Servo Position variables
    // Servo positions are 0 to 1  and represent positons 0 to 180 degrees
    // this means that .5 is about 90 degrees

    //Servo positions are given as a number from 0 (fully counter-clockwise) to 1 (fully clockwise)
    private final double SERVO_POS_LEFT_OPEN = .5;
    private final double SERVO_POS_LEFT_CLOSED = 0;
    private final double SERVO_POS_RIGHT_OPEN = 0;
    private final double SERVO_POS_RIGHT_CLOSED = 1;

    private Servo ServoGripperL = null;
    private Servo ServoGripperR = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //telemetry.addData("Status", "Gripper Initialized");


        ServoGripperR = hardwareMap.servo.get("Servo_GripperR");
        ServoGripperL = hardwareMap.servo.get("Servo_GripperL");
        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */


        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        // telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {


    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        cmd_Close();
        runtime.reset();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //telemetry.addData("Status", shootTrigger.getPosition());
//        telemetry.addData("Status", "gripper java Running: " + runtime.toString());
        //telemetry.addData("Status", "gripper : Gripper_current = " + Gripper_current);
        if (Gripper_current == GRIPPER_MODE_CLOSING) {
            Do_closing();
        }

        if (Gripper_current == GRIPPER_MODE_OPENING) {
            Do_opening();
        }


    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        cmd_Open();
    }


    private void Do_closing(){
        // check timer to see if we are closed
        // if so set Gripper_current = Gripper_closed
        // warning calling to get server current position is only going to return last set position
        if (close_timer.time() >= GRIPPER_CLOSE_TIME_MS) {
            Gripper_current = GRIPPER_MODE_CLOSED;
            close_timer.reset();
        }
        //telemetry.addData("Status", "gripper : close_timer = " + close_timer);

    }

    private void Do_opening(){
        //check timer to see if we are open
        // if so set Gripper_current - Gripper_open
        if (open_timer.time() >= GRIPPER_OPEN_TIME_MS) {
            Gripper_current = GRIPPER_MODE_OPEN;
            open_timer.reset();
        }
        //telemetry.addData("Status", "gripper : open_timer = " + close_timer);
    }


    public void cmd_Close(){
        //set gripper position to closed
        //set gripper mode to closing
        //reset timmer
        ServoGripperL.setPosition(SERVO_POS_LEFT_CLOSED);

        ServoGripperR.setPosition(SERVO_POS_RIGHT_CLOSED);
        Gripper_current = GRIPPER_MODE_CLOSING;
        close_timer.reset();
        close_timer.startTime();


    }


    public void cmd_Open (){
        //set gripper position to open
        //set gripper mode to opening
        //reset open timer
        ServoGripperL.setPosition(SERVO_POS_LEFT_OPEN);

        ServoGripperR.setPosition(SERVO_POS_RIGHT_OPEN);
        Gripper_current = GRIPPER_MODE_OPENING;
        open_timer.reset();
        open_timer.startTime();

    }

    // public method to see if gripper is closed
    public boolean Is_Closed(){
        //Warning calls to hardware getCurrentPosition return the last commanded position
        return (Gripper_current == GRIPPER_MODE_CLOSED);
    }

    // public method to see if gripper is open
    public boolean Is_Open(){
        //Warning calls to hardware getCurrentPosition return the last commanded position
        return (Gripper_current == GRIPPER_MODE_OPEN);
    }

 }
