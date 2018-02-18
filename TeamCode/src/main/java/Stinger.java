/*

*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "Stinger", group = "")  // @Autonomous(...) is the other common choice

public class Stinger extends OpMode {

    /*

    Life cycle of the stinger

    start in Mode_unknown
    receive a cmdDoJ1Retract
       set mode to retracting
       start timer
       set position to retracted
     Loop until StingerTimer > RetractTime_MS
     set StingerMode_current to STINGER_MODE_J1_RETRACTED

     Do nothing until next command

     receive cmdDoJ1Extend
        set mode to extending
        start timer
        set position extended
      Loop until stingerTimer > ExtendTime_MS
      set StingerMode_current to STINGER_MODE_J1_EXTENDED

      Do nothing until next command

     */

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private static final String TAGStinger = "8492- Stinger";

    private final double EXTEND_TIME_MS = 1000;
    private final double RETRACT_TIME_MS = 1000;

    private ElapsedTime Stingertime = new ElapsedTime();
    public static final int STINGER_MODE_UNKNOWN = 0;
    public static final int STINGER_MODE_J1_EXTENDING = 1;
    public static final int STINGER_MODE_J1_EXTENDED = 2;
    public static final int STINGER_MODE_J1_RETRACTING = 3;
    public static final int STINGER_MODE_J1_RETRACTED = 4;
    public static final int STINGER_MODE_J1_ANGLEING = 5;
    public static final int STINGER_MODE_J1_ANGLED = 6;
    public static final int STINGER_MODE_J2_CCW = 7;
    public static final int STINGER_MODE_J2_CCWING = 8;
    public static final int STINGER_MODE_J2_CW = 9;
    public static final int STINGER_MODE_J2_CWING = 10;
    public static final int STINGER_MODE_J2_STRAIGHTENING = 11;
    public static final int STINGER_MODE_J2_STRAIGHT = 12;
    public static final int STINGER_MODE_J2_STOWING = 13;
    public static final int STINGER_MODE_J2_STOW = 14;

    //current mode of operation for chassis
    private int StingerMode_Current = STINGER_MODE_UNKNOWN;

    private Servo ServoJ1Stinger = null;
    private Servo ServoJ2Stinger = null;
    private ColorSensor sensorColorStinger;    // Hardware Device Object


    //These are the serov position settings to be set
    //later as we figure them out.
    //servos move position from 0 to 1.   Center is .5
    public static final double STINGER_POS_J1_EXTENDED = 1;
    public static final double STINGER_POS_J1_ANGELED = .5;
    //This will be either 0 or 1
    public static final double STINGER_POS_J1_RETRACTED = 0;

    public static final double STINGER_POS_J2_CCW = 0;
    public static final double STINGER_POS_J2_STRAIGHT = .5;
    public static final double STINGER_POS_J2_STOW = .7;
    public static final double STINGER_POS_J2_CW = 1;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //telemetry.addData("Status", "Stinger Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        ServoJ1Stinger = hardwareMap.servo.get("Servo_Stinger");
        ServoJ2Stinger = hardwareMap.servo.get("Servo_J2_Stinger");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        // telemetry.addData("Status", "Initialized");
        // bLedOn represents the state of the LED.
        boolean bLedOn = true;

        // get a reference to our ColorSensor object.
        sensorColorStinger = hardwareMap.get(ColorSensor.class, "sensor_color_stinger");

        // Set the LED in the beginning
        sensorColorStinger.enableLed(bLedOn);


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
        cmdDoJ1Retract();
        cmdDoJ2STOW();
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("StingerMode", " " + StingerMode_Current);
//        RobotLog.aa(TAGStinger,"StingerMode: " + StingerMode_Current);
  //      RobotLog.aa(TAGStinger, "Runtime: " + runtime.seconds());


        if (StingerMode_Current == STINGER_MODE_J1_EXTENDING) {
            DoJ1Extending();

        }
        if (StingerMode_Current == STINGER_MODE_J1_ANGLEING) {
            DoJ1Angling();

        }

        if (StingerMode_Current == STINGER_MODE_J1_RETRACTING) {
            DoJ1Retracting();
        }


        if (StingerMode_Current == STINGER_MODE_J2_CCWING) {
            DoJ2CCWING();

        }
        if (StingerMode_Current == STINGER_MODE_J2_STRAIGHTENING) {
            DoJ2STRAIGHTENING();

        }

        if (StingerMode_Current == STINGER_MODE_J2_CWING) {
            DoJ2CWING();
        }


        if (StingerMode_Current == STINGER_MODE_J2_STOWING) {
            DoJ2STOWING();
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }


    private void DoJ1Extending() {
        if (Stingertime.milliseconds() > EXTEND_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J1_EXTENDED;
        }
    }

    private void DoJ1Angling() {
        if (Stingertime.milliseconds() > EXTEND_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J1_ANGLED;

        }
    }


    private void DoJ1Retracting() {
        if (Stingertime.milliseconds() > RETRACT_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J1_RETRACTED;
        }
    }

    private void DoJ2CCWING() {
        if (Stingertime.milliseconds() > EXTEND_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J2_CCW;
        }
    }

    private void DoJ2STRAIGHTENING() {
        if (Stingertime.milliseconds() > EXTEND_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J2_STRAIGHT;
        }
    }


    private void DoJ2CWING() {
        if (Stingertime.milliseconds() > RETRACT_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J2_CW;
        }
    }


    private void DoJ2STOWING() {
//        RobotLog.aa(TAGStinger,"Stingertime: " + Stingertime.milliseconds());

        if (Stingertime.milliseconds() > RETRACT_TIME_MS) {
            StingerMode_Current = STINGER_MODE_J2_STOW;
        }
    }

    //Gives the command extend
    public void cmdDoJ1Extend() {

        ServoJ1Stinger.setPosition(STINGER_POS_J1_EXTENDED);
        StingerMode_Current = STINGER_MODE_J1_EXTENDING;
        Stingertime.reset();
        //Stingertime.startTime();

    }

    //Gives the command retract
    public void cmdDoJ1Retract() {
//        RobotLog.aa(TAGStinger,"cmdDoJ1Retract / StingerMode: " + StingerMode_Current);
        ServoJ1Stinger.setPosition(STINGER_POS_J1_RETRACTED);
        StingerMode_Current = STINGER_MODE_J1_RETRACTING;
        Stingertime.reset();
        //Stingertime.startTime();

    }

    public void cmdDoJ1Angle() {
        ServoJ1Stinger.setPosition(STINGER_POS_J1_ANGELED);
        StingerMode_Current = STINGER_MODE_J1_ANGLEING;
        Stingertime.reset();
        //Stingertime.startTime();
    }

    public void cmdDoJ2CCW() {

        ServoJ2Stinger.setPosition(STINGER_POS_J2_CCW);
        StingerMode_Current = STINGER_MODE_J2_CCWING;
        Stingertime.reset();
        //Stingertime.startTime();

    }

    //Gives the command retract
    public void cmdDoJ2Straight() {
        ServoJ2Stinger.setPosition(STINGER_POS_J2_STRAIGHT);
        StingerMode_Current = STINGER_MODE_J2_STRAIGHTENING;
        Stingertime.reset();
        //Stingertime.startTime();

    }

    public void cmdDoJ2STOW() {
//        RobotLog.aa(TAGStinger,"cmdDoJ2Stow: " + StingerMode_Current);

        ServoJ2Stinger.setPosition(STINGER_POS_J2_STOW);
        StingerMode_Current = STINGER_MODE_J2_STOWING;
        Stingertime.reset();
        //Stingertime.startTime();

    }


    public void cmdDoJ2CW() {
        ServoJ2Stinger.setPosition(STINGER_POS_J2_CW);
        StingerMode_Current = STINGER_MODE_J2_CWING;
        Stingertime.reset();
        //Stingertime.startTime();

    }


    //returns true if stinger is retracted
    public boolean IsJ1Retracted() {

        return (StingerMode_Current == STINGER_MODE_J1_RETRACTED);
    }


    public boolean IsJ1Angled() {

        return (StingerMode_Current == STINGER_MODE_J1_ANGLED);
    }

    //returns true if stinger is extended
    public boolean IsJ1Extended() {

        return (StingerMode_Current == STINGER_MODE_J1_EXTENDED);
    }


    public boolean IsJ2CCW() {

        return (StingerMode_Current == STINGER_MODE_J2_CCW);
    }


    public boolean IsJ2Striaght() {

        return (StingerMode_Current == STINGER_MODE_J2_STRAIGHT);
    }

    //returns true if stinger is extended
    public boolean IsJ2CW() {

        return (StingerMode_Current == STINGER_MODE_J2_CW);

    }


    public boolean IsJ2STOWED() {
//        RobotLog.aa(TAGStinger," IsJ2STOWED " + StingerMode_Current + " - " +STINGER_MODE_J2_STOW);

        return (StingerMode_Current == STINGER_MODE_J2_STOW);

    }

    //returns true if stinger is retracted
    public boolean IsBlue() {
        int blueValue = sensorColorStinger.blue();
        int redValue = sensorColorStinger.red() + 7;


        return (blueValue > redValue);
    }

    //returns true if stinger is extended
    public boolean IsRed() {
        int blueValue = sensorColorStinger.blue() + 7;
        int redValue = sensorColorStinger.red();


        return (blueValue < redValue);
    }

}



