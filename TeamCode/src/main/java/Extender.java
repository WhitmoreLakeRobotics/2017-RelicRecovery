


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "Extender", group = "")  // @Autonomous(...) is the other common choice

public class Extender extends OpMode {


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime close_timer = new ElapsedTime();
    private ElapsedTime open_timer = new ElapsedTime();


    private CRServo ServoExtender = null;


    //Servos 0 is counter clockwise
    //Servos .5 is
    //Servos 1 is clockwise
    public static final double SERVO_RETRACT = -1;
    public static final double SERVO_STOP = 0;
    public static final double SERVO_EXTEND = 1;

    private double ExtendStickDeadBand = .2;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //telemetry.addData("Status", "Gripper Initialized");
        //ServoExtender = hardwareMap.servo.get("Servo_Extender");
        ServoExtender = hardwareMap.crservo.get("Servo_Extender");
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


    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        ServoExtender.setPower(SERVO_STOP);
    }


    //driver is using stick control for lifter
    public void cmdStickControl(double stickPos) {
        telemetry.addLine(" Extender_current_Power= " + ServoExtender.getPower());


        if (Math.abs(stickPos) < ExtendStickDeadBand) {
            ServoExtender.setPower(SERVO_STOP);
        } else if (stickPos > 0) {
            ServoExtender.setPower(SERVO_EXTEND);
        } else {
            ServoExtender.setPower(SERVO_RETRACT);
        }
    }


}
