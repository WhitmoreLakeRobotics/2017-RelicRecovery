/*

*/


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;



@Autonomous(name = "RecoveryZone", group = "")  // @Autonomous(...) is the other common choice

public class RecoveryZone extends OpMode {


    public static int stage_1GripBlock = 10;
    public static int stage_0PreStart = 0;
    public static int stage_2StingerExtend = 10;
    public static int stage_3ReadPlatformColor = 2;
    public static int stage_4ReadColorOfjewell = 5;
    public static int stage_5Knockjewelloff = 10;
    public static int stage_6LiftStinger = 10;
    public static int stage_7Turn1 = 20;
    public static int stage_8MoveFoward = 12;
    public static int stage_9Turn2 = 20;
    public static int stage_10MoveFoward7 = 20;
    public static int stage_11DropBlock = 10;
    public static int stage_12MoveBack1 = 5;

    int stage = stage_0PreStart;




    Chassis robotChassis = new Chassis();

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        robotChassis.hardwareMap = hardwareMap;
        robotChassis.telemetry = telemetry;
        robotChassis.init();
        telemetry.addData("Status", "Initialized");


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

        runtime.reset();
        //shootTrigger.setPosition(Settings.reset);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //telemetry.addData("Status", shootTrigger.getPosition());
        telemetry.addData("Status", "Running: " + runtime.toString());
        robotChassis.loop();

        if (stage == stage_0PreStart) {
            //Start Stage 1
            stage = stage_1GripBlock;
            robotChassis.cmdDrive(.5, 0, stage_8MoveFoward);
            robotChassis.stop();
        }


    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

 }
