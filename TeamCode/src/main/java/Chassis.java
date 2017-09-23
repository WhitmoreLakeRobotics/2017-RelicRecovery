/**
 * Created by mg15 on 9/20/17.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Created by mg15 on 9/12/17.
 */

//package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Chassis extends OpMode {
    // basic modes of operation for the chassis


    public static final int ChassisMode_Stop = 0;
    public static final int ChassisMode_Drive = 1;
    public static final int ChassisMode_Turn = 2;
    public static final int ChassisMode_Idle = 3;

    //current mode of operation for chassis
    private int ChassisMode_Current = ChassisMode_Stop;


    //Gyro KP for driving straight
    public static final double chassis_KPGyroStraight = .02;

    //for truning this is the tolerance of trun in degrees
    public static final int chassis_GyroHeadingTol = 2;

    //Timeout value for executing turns
    public static final int chassis_TurnTimeout_mS = 4000;

    public static final double chassis_driveTolInches = .25;
    public static final int chassis_driveTimeout_mS = 5000;

    private int cmdStartTime_mS = 0;

    //From http://www.revrobotics.com/content/docs/HDMotorEncoderGuide.pdf
    //Page 6

    public static final int ticksPerMotorOutputRev = 2240;
    public static final double wheelDistPreRev = 4 * 3.14159;
    public static final double gearRatio = 80 / 80;   // Motor Gear over Wheel Gear
    public static final double ticksPerInch = ticksPerMotorOutputRev / gearRatio / wheelDistPreRev;

    private boolean cmdComplete = true;

    //LDM=leftDriveMotor
    //RDM=rightDriveMotor
    private DcMotor LDM1 = null;
    private DcMotor LDM2 = null;
    private DcMotor RDM1 = null;
    private DcMotor RDM2 = null;

    // The IMU sensor object
    BNO055IMU imu;

    private ElapsedTime runtime = new ElapsedTime();

    //Target values go here.   The robot is trying to get to these values
    private double TargetMotorPowerLeft = 0;
    private double TargetMotorPowerRight = 0;
    private int TargetHeadingDeg = 0;
    private double TargetDistanceInches = 0;

    //private LightSensor lightSensorLineFollow = null;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        LDM1 = hardwareMap.dcMotor.get("LDM1");
        RDM1 = hardwareMap.dcMotor.get("RDM1");
        LDM2 = hardwareMap.dcMotor.get("LDM2");
        RDM2 = hardwareMap.dcMotor.get("RDM2");

        LDM1.setDirection(DcMotor.Direction.REVERSE);
        LDM2.setDirection(DcMotor.Direction.REVERSE);

        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

    }

    /*
   * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
   */
    @Override
    public void init_loop() {

    }


    @Override
    public void loop() {
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
        if (ChassisMode_Stop == ChassisMode_Current) {
            Dostop();
        }

        if (ChassisMode_Drive == ChassisMode_Current) {
            DoDrive();
        }

        if (ChassisMode_Turn == ChassisMode_Current) {
            DoTurn();
        }

    }

    private void Dostop() {
       /*
        * executes the logic needed to stop the chassis
       */

        //set start speeds to zero
        TargetMotorPowerLeft = 0;
        TargetMotorPowerRight = 0;
        TargetDistanceInches = 0;
        //stop the motors
        LDM1.setPower(TargetMotorPowerLeft);
        LDM2.setPower(TargetMotorPowerLeft);
        RDM1.setPower(TargetMotorPowerLeft);
        RDM2.setPower(TargetMotorPowerLeft);

        //goto chassis idle mode
        ChassisMode_Current = ChassisMode_Idle;

    }

    private void DoDrive() {
        /*
        * executes the logic for a single scan of driving straight by gyro
        */
        double deltaHeading = TargetHeadingDeg - getGyroHeading();  //NEED TO DETEMINE ACTUAL VALUE

        double leftPower = TargetMotorPowerLeft + (deltaHeading * chassis_KPGyroStraight);
        double rightPower = TargetMotorPowerRight - (deltaHeading * chassis_KPGyroStraight);

        if (leftPower < 0) {
            leftPower = 0;
        }
        if (rightPower < 0) {
            rightPower = 0;
        }

        if (leftPower > 1) {
            leftPower = 1;
        }
        if (rightPower > 1) {
            rightPower = 1;
        }


        LDM1.setPower(leftPower);
        LDM2.setPower(leftPower);
        RDM1.setPower(rightPower);
        RDM2.setPower(rightPower);
        ChassisMode_Current = ChassisMode_Drive;

        double inchesTraveled = getEncoderInches();

        if ((inchesTraveled >= (TargetDistanceInches - chassis_driveTolInches)) ||
                (runtime.milliseconds() > chassis_driveTimeout_mS)) {
            cmdComplete = true;
        }


    }

    private void DoTurn() {
        /*
        *   executes the logic of a single scan of turning the robot to a new heading
         */

        int deltaHeading = 0; //Math.abs(getGyroHeading() - headingTarget); //NEED TO DETEMINE ACTUAL TARGET
        if ((deltaHeading <= chassis_GyroHeadingTol) ||
                runtime.milliseconds() > chassis_TurnTimeout_mS) {
            //We are there stop
            ChassisMode_Current = ChassisMode_Stop;
        }
    }

    public void cmdDrive(double speed, int headingDeg, double inches) {
        /*
        called by other opmodes to start a drive straight by gyro command
         */
        runtime.reset();
        cmdComplete = false;
    }

    public void cmdTurn(double LSpeed, double RSpeed, int NewHeadingDeg) {
        runtime.reset();
        cmdComplete = false;
    }


    public int getGyroHeading() {
        //Read the gyro and return its reading in degrees

        return TargetHeadingDeg;
    }


    public double getEncoderInches() {
        // read the values from the encoders
        // LDM1.getCurrentPosition()
        // convert that to inches
        // by dividing by ticksPerInch

        // average the distance traveled by each wheel to determine the distance travled by the
        // robot
        return 1;
    }

}


