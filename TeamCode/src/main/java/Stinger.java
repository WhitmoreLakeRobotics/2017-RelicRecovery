/*

*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "Stinger", group = "")  // @Autonomous(...) is the other common choice

public class Stinger extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    private int ExtendTime_mS = 500;
    private int RetractTime_mS = 750;

    private ElapsedTime Stingertime = new ElapsedTime();

    public static final int StingerMode_Extending = 0;
    public static final int StingerMode_Extended = 1;
    public static final int StingerMode_Retracting = 2;
    public static final int StringerMode_Retracted = 3;
    public static final int StingerMode_Idle = 4;

    //current mode of operation for chassis
    private int StingerMode_Current = StingerMode_Idle;

    private Servo ServoStinger = null;


    //These are the serov position settings to be set
    //later as we figure them out.
    //servos move position from 0 to 1.   Center is .5
    public static final double StingerPos_Extended = .5;
    //This will be either 0 or 1
    public static final double StingerPos_Retracted = 1;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Stinger Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        ServoStinger = hardwareMap.servo.get("Servo_Stinger");

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
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());
        if (StingerMode_Current == StingerMode_Idle) {


        }
        if (StingerMode_Current == StringerMode_Retracted) {
            DoRetract();

        }

        if (StingerMode_Current == StingerMode_Retracting) {
            DoRetracting();
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    //Executes the command extend

    private void DoExtend() {

    }

    private void DoExtending() {

    }

    //Executes the command retract
    private void DoRetract() {
        if (ServoStinger.getPosition() != StingerPos_Retracted) {
            ServoStinger.setPosition(StingerPos_Retracted);
            StingerMode_Current = StingerMode_Retracting;
            Stingertime.reset();
            Stingertime.startTime();

        }
    }

    private void DoRetracting() {
        if (Stingertime.time() >= RetractTime_mS) {
            StingerMode_Current = StingerMode_Idle;
            Stingertime.reset();
        }
    }

    //Gives the command extend
    public void cmdDoExtend() {

    }

    //Gives the command retract
    public void cmdDoRetract() {

    }

    //returns true if stinger is retracted
    public boolean IsRetracted() {

        return false;
    }

    //returns true if stinger is extended
    public boolean IsExtended() {
        return false;
    }


}
