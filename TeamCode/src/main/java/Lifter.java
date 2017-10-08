/*

*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;


@TeleOp(name = "Lifter", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Lifter extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();


    /*
    Life cycle of the lifter in auton

    Start in Unknown position logiclly but we know that we start in Bottom position.

    Set encoders to 0 before we move any where.

    in auton we only need to move from bottom position to a carry position
    and from carry position to bottom

    After the grippers a closed elsewhere issue cmdDoMove_Carry_Pos()

    go to mode LIFTMODE_MOVING_2_CARRY_POS
    set motor power to LIFT_POWER_UP
    Then loop until at Carry_Position

    Once in Carry Position do nothing until the command to move to Bottom



     */


    //Encoder positions for the lift
    public static final int LIFTPOS_BOTTOM = 0;
    public static final int LIFTPOS_CARRY = 1700;
    public static final int LIFTPOS_MAX = 35000;
    public static final int LIFTPOS_TOL = 500;

    public static int LIFTPOS_Target = LIFTPOS_BOTTOM;

    //set the lift powers... We will need different speeds for up and down.
    public static final double LIFTPOWER_UP = .75;
    public static final double LIFTPOWER_DOWN = -.5;


    //Lifter Modes
    // we have 3 positions  BOTTOM, 1 and 2
    public static final int LIFTMODE_UNKNOWN = 0;
    public static final int LIFTMODE_AT_BOTTOM = 1;
    public static final int LIFTMODE_MOVING_2_CARRY_POS = 2;
    public static final int LIFTMODE_AT_CARRY_POS = 3;
    public static final int LIFTMODE_MOVING_2_BOTTOM = 4;

    private int LIFTMODE_current = LIFTMODE_UNKNOWN;

    private DcMotor Lift_Motor = null;


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

        Lift_Motor = hardwareMap.dcMotor.get("MOTOR_LIFT");
        Lift_Motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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
        Lift_Motor.setPower(0);
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


        if (LIFTMODE_current == LIFTMODE_MOVING_2_BOTTOM) {
            // Moving to position 1 from Bottom
            do_MovingUp();
        }

        if (LIFTMODE_current == LIFTMODE_MOVING_2_CARRY_POS) {
            // Moving to position 2 from position 1
            do_MovingUp();

        }

    }


    private void do_MovingDown() {
        // if the motor is in within the position window stop it at the correct position
        // set the mode to the correct position

        int curr_pos = Lift_Motor.getCurrentPosition();
        double motor_power = LIFTPOWER_DOWN;

        //if in position then stop
        if (curr_pos > (LIFTPOS_Target - LIFTPOS_TOL) ||
                curr_pos < (LIFTPOS_Target + LIFTPOS_TOL)) {
            motor_power = 0;
        }

        //Do not move below the bottom position
        if (curr_pos < LIFTPOS_BOTTOM) {
            motor_power = 0;
        }
        Lift_Motor.setPower(motor_power);
    }


    private void do_MovingUp() {
        // if the motor is within the position window stop it at the correct position
        // set the mode to the correct position

        int curr_pos = Lift_Motor.getCurrentPosition();

        double motor_power = LIFTPOWER_UP;

        //Stop if in position
        if (curr_pos > (LIFTPOS_Target - LIFTPOS_TOL) ||
                curr_pos < (LIFTPOS_Target + LIFTPOS_TOL)) {
            motor_power = 0;

        }
        // Do not go above MAX height
        if (curr_pos > LIFTPOS_MAX) {
            motor_power = 0;
        }

        Lift_Motor.setPower(motor_power);
    }

    public void cmdDo_MoveCarryPos() {
        //This is used in Auton to move to the carry Pos in Aution to pick the block up

        if (LIFTMODE_current == LIFTMODE_AT_BOTTOM) {
            LIFTMODE_current = LIFTMODE_MOVING_2_CARRY_POS;
            LIFTPOS_Target = LIFTPOS_CARRY;
            Lift_Motor.setPower(LIFTPOWER_UP);
        }
    }


    public void cmdDo_Move_BottomPos() {
        //This is used in Auton to move to the bottom position to set the blocks down.

        if (LIFTMODE_current == LIFTMODE_AT_CARRY_POS) {
            LIFTMODE_current = LIFTMODE_MOVING_2_BOTTOM;
            LIFTPOS_Target = LIFTPOS_BOTTOM;
            Lift_Motor.setPower(LIFTPOWER_DOWN);
        }

    }


    public void cmdDo_Move_TelleOp(double MotorPower) {
        //Called in TelleOP to move  the lift
        //Get the current Position
        int curr_pos = Lift_Motor.getCurrentPosition();
        double motor_power = MotorPower;

        //Do not move beyond MAX position
        if (curr_pos >= LIFTPOS_MAX) {
            motor_power = 0;
        }

        //Do not move below bottom position
        if (curr_pos <= LIFTPOS_BOTTOM) {
            motor_power = 0;
        }

        Lift_Motor.setPower(motor_power);

    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        Lift_Motor.setPower(0);
    }

}
