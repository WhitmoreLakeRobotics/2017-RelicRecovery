/*
*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "teleop_8492", group = "")  // @Autonomous(...) is the other common choice

public class TeleOp_8492 extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    Chassis robotChassis = new Chassis();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        robotChassis.telemetry = telemetry;
        robotChassis.hardwareMap = hardwareMap;
        robotChassis.init();


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
        robotChassis.init_loop();
    }


    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        robotChassis.start();
        runtime.reset();
        //shootTrigger.setPosition(Settings.reset);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        robotChassis.loop();

        //telemetry.addData("Status", shootTrigger.getPosition());
        telemetry.addData("Status", "Running: " + runtime.toString());
        robotChassis.doTeleOp(joystickMath(gamepad1.left_stick_y),
                joystickMath(gamepad1.right_stick_y));


    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robotChassis.stop();
    }

    public double joystickMath(double joyValue) {
        int sign = 1;
        double retValue = 0;
        if (joyValue < 0) {
            sign = -1;
        }
        return Math.abs(Math.pow(joyValue, 2)) * sign;

    }
}
