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


    //button latches
    boolean gamepad2_a_pressed = false;
    boolean gamepad2_b_pressed = false;
    boolean gamepad2_x_pressed = false;
    boolean gamepad2_y_pressed = false;

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
        robotChassis.setMotorMode_RUN_WITHOUT_ENCODER();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        robotChassis.loop();


        if (gamepad1.a && !gamepad1.b) {
            robotChassis.stinger.cmdDoJ1Extend();
        }
        if (gamepad1.b && !gamepad1.a) {
            robotChassis.stinger.cmdDoJ1Retract();
        }

        if (gamepad2.right_bumper && !gamepad2.left_bumper) {
            robotChassis.gripper.cmd_Close();

        }
        if (gamepad2.left_bumper && !gamepad2.right_bumper) {
            telemetry.addData("Status", "gripper left: " + runtime.toString());
            robotChassis.gripper.cmd_Open();

        }


        // Lifter buttons control

        // Requirements for triggering a command to move the lift

        // 1) only 1 button is pressed.
        // 2) The button must released to get another command
        //
        // if (gamepad2.a && ! gamepad2_a_pressed && ! gamepad2.b && ! gamepad2.x && ! gamepad2.y){
        if (gamepad2.a) {
            robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_BOTTOM);
            gamepad2_a_pressed = true;
            gamepad2_b_pressed = false;
            gamepad2_x_pressed = false;
            gamepad2_y_pressed = false;
        }

        // if (gamepad2.b && ! gamepad2_b_pressed && ! gamepad2.a && ! gamepad2.x && ! gamepad2.y){
        if (gamepad2.b) {
            robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_CARRY);
            gamepad2_a_pressed = false;
            gamepad2_b_pressed = true;
            gamepad2_x_pressed = false;
            gamepad2_y_pressed = false;
            ;
        }

        // if (gamepad2.x && ! gamepad2_x_pressed && ! gamepad2.a && ! gamepad2.b && ! gamepad2.y){
        if (gamepad2.y) {
            robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_STACK1);
            gamepad2_a_pressed = false;
            gamepad2_b_pressed = false;
            gamepad2_x_pressed = true;
            gamepad2_y_pressed = false;
            ;
        }
        // if (gamepad2.y && ! gamepad2_y_pressed && ! gamepad2.a && ! gamepad2.b && ! gamepad2.x){
        if (gamepad2.x) {
            robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_STACK2);
            gamepad2_a_pressed = false;
            gamepad2_b_pressed = false;
            gamepad2_x_pressed = false;
            gamepad2_y_pressed = true;
        }


        robotChassis.lifter.cmdStickControl(joystickMath(gamepad2.right_stick_y));
        robotChassis.extender.cmdStickControl(joystickMath(gamepad2.left_stick_y));

        if (gamepad1.left_bumper) {
            robotChassis.doTeleOp(joystickMath(gamepad1.left_stick_y * 0.8), joystickMath(gamepad1.right_stick_y * 0.8));

        }
        if (gamepad1.right_bumper) {
            robotChassis.doTeleOp(joystickMath(gamepad1.left_stick_y) * 9, joystickMath(gamepad1.right_stick_y * .9));
        } else if (gamepad1.left_bumper && gamepad1.right_bumper) {
            robotChassis.doTeleOp(joystickMath(gamepad1.left_stick_y), joystickMath(gamepad1.right_stick_y));

        } else {
            robotChassis.doTeleOp(joystickMath(gamepad1.left_stick_y * 0.70), joystickMath(gamepad1.right_stick_y * 0.70));
        }
        if (gamepad2.dpad_down) {
            robotChassis.gripper.cmd_Stow();

        }

        if (gamepad2.dpad_right) {
            robotChassis.gripper.cmd_Ready();

        }
        if (gamepad2.dpad_up) {
            robotChassis.gripper.cmd_Clamp();

        }



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
