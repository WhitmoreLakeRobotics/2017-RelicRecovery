/*

*/


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Set;


//@TeleOp(name = "Lifter", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Lifter extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();


    /*
     cmd_MoveToTarget takes the new position in tick counts.

        It figures out if we need to move up with positive power or Down with negative power
        It then sets the new LIFTPOS_CmdPos and New LIFTPOWER_current
        It sets a boolean that we are underStickControl to false
        It does NOT set the motor Power... That will happen in the next loop if we are allowed
        to set the next power
        The buttons are used to very quickly move the lift to a given position with minimal
        overshoot or undershoot.

      cmdStickControl takes a double from the joystick position
         It simply sets the new power if it is legal value... AKA it clamps
         the power to the valid powers that must be between LIFTPOWER_UP and LIFTPOWER_DOWN
         Stick control allows the driver to adjust and drive by eye


     */


    //Encoder positions for the lift
    public static final int LIFTTICS_REV = 1120;
    public static final int LIFTPOS_BOTTOM = 0;
    public static final int LIFTPOS_CARRY = 3500 / 2;
    public static final int LIFTPOS_STACK1 = 13000 / 2;
    public static final int LIFTPOS_STACK2 = 21000 / 2;
    public static final int LIFTPOS_MAX = 21000 / 2;
    public static final int LIFTPOS_TOL = 200;

    int LIFTPOS_current = LIFTPOS_BOTTOM;   // This is the current tick counts of the lifter
    int LIFTPOS_CmdPos = LIFTPOS_BOTTOM;    // This is the commanded tick counts of the lifter

    //set the lift powers... We will need different speeds for up and down.
    public static final double LIFTPOWER_UP = 1;
    public static final double LIFTPOWER_DOWN = -.75;
    double LIFTPOWER_current = 0;


    double liftStickDeadBand = .2;

    boolean cmdComplete = false;
    boolean underStickControl = false;

    private DcMotor Motor_Lift = null;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
       // telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */

        Motor_Lift = hardwareMap.dcMotor.get("MOTOR_LIFT");
        Motor_Lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Motor_Lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_Lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_Lift.setDirection(DcMotor.Direction.FORWARD);
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
        SetMotorPower(0);
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //telemetry.addData("Status", "Running: " + runtime.toString());

        if (!underStickControl) {
            testInPosition();
        }

        SetMotorPower(LIFTPOWER_current);

    }


    private void SetMotorPower(double newMotorPower) {
        //Saftey checks for the lift to prevent too low or too high

        LIFTPOS_current = Math.abs(Motor_Lift.getCurrentPosition());

        double newPower = newMotorPower;

        // make sure that we do not attempt to move less than BOTTOM
        if ((LIFTPOS_BOTTOM + LIFTPOS_TOL > LIFTPOS_current) && (newMotorPower < 0)) {
            newPower = 0;
        }

        // make sure that we do not attempt a move greater than MAX
        if ((LIFTPOS_MAX - LIFTPOS_TOL < LIFTPOS_current) && (newMotorPower > 0)) {
            newPower = 0;
        }

        // make sure that we are not going below the bottom
       /* if ((LIFTPOS_BOTTOM + LIFTPOS_TOL > LIFTPOS_current) && (LIFTPOWER_current < 0)) {
            newPower = 0;
        }

        // make sure that we are not going above the top
        if ((LIFTPOS_MAX - LIFTPOS_TOL < LIFTPOS_current) && (LIFTPOWER_current > 0)) {
            newPower = 0;
        }
*/
        //only set the power to the hardware when it is being changed.
        // if (newPower != LIFTPOWER_current) {
        LIFTPOWER_current = newPower;
        Motor_Lift.setPower(newPower);
       // telemetry.addLine(" LIFTPOWER_current= " + LIFTPOWER_current + " curr " + LIFTPOS_current);
        // }
    }

    private void testInPosition() {
        // tests if we are in position and stop if we are;
        //int curr_pos = Motor_Lift.getCurrentPosition();

        if ((LIFTPOS_CmdPos + LIFTPOS_TOL > LIFTPOS_current) &&
                (LIFTPOS_CmdPos - LIFTPOS_TOL < LIFTPOS_current)) {
            cmdComplete = true;
            LIFTPOWER_current = 0;
        }
    }


    //driver is using stick control for lifter
    public void cmdStickControl(double stickPos) {

        if (Math.abs(stickPos) < liftStickDeadBand) {
            if (underStickControl) {
                LIFTPOWER_current = 0;
            }
            // we are inside the deadband do nothing.
            underStickControl = false;
            return;
        } else {
            underStickControl = true;
            cmdComplete = false;
            double currPower = stickPos;

            //clamp the power fo the stick
            if (stickPos > LIFTPOWER_UP) {
                currPower = LIFTPOWER_UP;
            }

            //clamp the power of the stick
            if (stickPos < LIFTPOWER_DOWN) {
                currPower = LIFTPOWER_DOWN;
            }

            LIFTPOWER_current = currPower;
        }
    }

    public boolean getcmdComplete() {
        // used by auton to detect if the move is complete.
        return (cmdComplete);
    }


    // somebody pressed a button or ran Auton to send command to move to a given location.
    public void cmd_MoveToTarget(int LIFTPOS_Target_Ticks) {

        //int curr_pos = Motor_Lift.getCurrentPosition();
        int LIFTPOS_new = LIFTPOS_Target_Ticks;

        //Do not move below BOTTOM
        if (LIFTPOS_new < LIFTPOS_BOTTOM) {
            LIFTPOS_new = LIFTPOS_BOTTOM;
        }

        //Do not move above MAX
        if (LIFTPOS_new > LIFTPOS_MAX) {
            LIFTPOS_new = LIFTPOS_MAX;
        }
        //we are higher than we want to be and
        //not already at the bottom.
        if ((LIFTPOS_current >= LIFTPOS_new) &&
                (LIFTPOS_current > LIFTPOS_BOTTOM)) {
            //We need to go down to target
            LIFTPOWER_current = LIFTPOWER_DOWN;
            cmdComplete = false;
            underStickControl = false;
            LIFTPOS_CmdPos = LIFTPOS_new;
        }

        //We are lower than we want to be and not already at the top
        if ((LIFTPOS_current <= LIFTPOS_new) &&
                (LIFTPOS_current < LIFTPOS_MAX)) {
            //We need to go down to target
            LIFTPOWER_current = LIFTPOWER_UP;
            cmdComplete = false;
            underStickControl = false;
            LIFTPOS_CmdPos = LIFTPOS_new;
        }
    }


    public int getLIFTPOS_Ticks() {
        return LIFTPOS_current;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        SetMotorPower(0);
    }
}
