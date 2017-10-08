/*
 * Created by mg15 on 9/20/17.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

//package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

@TeleOp(name = "Chassis", group = "Chassis")

public class Chassis extends OpMode {
    // basic modes of operation for the chassis

//@Disabled                            // Uncomment this to add to the opmode list

    public static final int ChassisMode_Stop = 0;
    public static final int ChassisMode_Drive = 1;
    public static final int ChassisMode_Turn = 2;
    public static final int ChassisMode_Idle = 3;
    public static final int ChassisMode_TeleOp = 4;

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


    public Stinger stinger = new Stinger();

    // The IMU sensor object
    BNO055IMU imu;
    // State used for updating telemetry
    Orientation angles;

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
        composeTelemetry();
        telemetry.log().add("Waiting for start...");

        LDM1 = hardwareMap.dcMotor.get("LDM1");
        RDM1 = hardwareMap.dcMotor.get("RDM1");
        LDM2 = hardwareMap.dcMotor.get("LDM2");
        RDM2 = hardwareMap.dcMotor.get("RDM2");

        if (LDM1 == null) {
            telemetry.log().add("LDM1 is null...");
        }

        if (LDM2 == null) {
            telemetry.log().add("LDM2 is null...");
        }

        if (RDM1 == null) {
            telemetry.log().add("RDM1 is null...");
        }

        if (RDM2 == null) {
            telemetry.log().add("RDM2 is null...");
        }

        LDM1.setDirection(DcMotor.Direction.REVERSE);
        LDM2.setDirection(DcMotor.Direction.REVERSE);
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        LDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        RDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        stinger.hardwareMap = hardwareMap;
        stinger.telemetry = telemetry;
        stinger.init();
    }

    /*
   * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
   */
    @Override
    public void init_loop() {
        stinger.init_loop();

    }


    @Override
    public void loop() {
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
        stinger.loop();
        if (ChassisMode_Stop == ChassisMode_Current) {
            Dostop();
        }

        if (ChassisMode_Drive == ChassisMode_Current) {
            DoDrive();
        }

        if (ChassisMode_Turn == ChassisMode_Current) {
            DoTurn();
        }
        telemetry.update();
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
        RDM1.setPower(TargetMotorPowerRight);
        RDM2.setPower(TargetMotorPowerRight);

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
            Dostop();
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

        int totaltics = LDM1.getCurrentPosition() +
                LDM2.getCurrentPosition() +
                RDM1.getCurrentPosition() +
                RDM2.getCurrentPosition();
        double averagetics = totaltics / 4;
        double inches = averagetics / ticksPerInch;

        return inches;
    }

    public void doTeleOp(double LDMpower, double RDMpower) {


        ChassisMode_Current = ChassisMode_TeleOp;
        LDM1.setPower(LDMpower);
        LDM2.setPower(LDMpower);
        RDM1.setPower(RDMpower);
        RDM2.setPower(RDMpower);
    }

    /*
        * Code to run ONCE when the driver hits PLAY
        */
    @Override
    public void start() {
        stinger.start();
        runtime.reset();

    }


    @Override
    public void stop() {

        //go to brake mode at the end of program
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    void composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() {
            @Override
            public void run() {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            }
        });

        telemetry.addLine()
                .addData("status", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getSystemStatus().toShortString();
                    }
                })
                .addData("calib", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getCalibrationStatus().toString();
                    }
                });

        telemetry.addLine()
                .addData("heading", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }


}


