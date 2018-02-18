import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


@Autonomous(name = "Crypto-Blue_GJ", group = "")  // @Autonomous(...) is the other common choice

public class CryptoBlue_GJ extends OpMode {
    private static final String TAGCryBlue_GJ = "8492-CryptoBlue_GJ";
    public static int stage_0PreStart = 0;
    public static int stage_05CloseGripper = 5;
    public static int stage_10LiftBlock = 10;
    public static int stage_20StingerExtend = 20;
    public static int stage_23Call_Stinger = 23;
    public static int stage_30PullOffStone = 30;
    public static int stage_40Turn1 = 40;
    public static int stage_50driveToFront = 50;
    public static int stage_60TurnTwo = 60;
    public static int stage_70driveToBox = 70;
    public static int stage_80OpenGripper = 80;
    public static int stage_90Backup = 90;
    public static int stage_150Done = 150;

    int CurrentStage = stage_0PreStart;


    Chassis robotChassis = new Chassis();
    JewelsBlue jewelsBlue = new JewelsBlue();

    private double AUTO_TurnPower = .2;  //.3333;
    private double AUTO_DrivePower = .3;
    private int AUTO_NextHeading = 0;

    private int AUTO_RED_Factor = 1;
    private int AUTO_BLUE_Factor = -1;
    private int AUTO_REDBLUE_Factor_Stone = AUTO_RED_Factor;
    private int AUTO_REDBLUE_Factor_Jewel = AUTO_RED_Factor;

    private int AUTO_Jewel_Swing = 45;
    private int countcmd = 0;

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime timelapse = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        robotChassis.hardwareMap = hardwareMap;
        robotChassis.telemetry = telemetry;
        robotChassis.init();

        jewelsBlue.hardwareMap = hardwareMap;
        jewelsBlue.telemetry = telemetry;
        jewelsBlue.init();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

        robotChassis.init_loop();
        jewelsBlue.init_loop();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        robotChassis.start();
        runtime.reset();
        robotChassis.setMotorMode_RUN_WITHOUT_ENCODER();
        jewelsBlue.start();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //telemetry.addData("Status", shootTrigger.getPosition());
        telemetry.addData("Crytoblue Stage", CurrentStage);
        RobotLog.aa(TAGCryBlue_GJ, "Stage: " + CurrentStage);
        RobotLog.aa(TAGCryBlue_GJ, "Runtime: " + runtime.seconds());

        robotChassis.loop();

        if (CurrentStage == stage_0PreStart) {
            //Start Stage 1
            robotChassis.gripper.cmd_Close();
            //robotChassis.stinger.cmdDoJ1Extend();
            CurrentStage = stage_05CloseGripper;
        }

        //close the close gripper
        if (CurrentStage == stage_05CloseGripper) {
            if (runtime.seconds() > 2) {
                robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_CARRY);
                CurrentStage = stage_10LiftBlock;
            }
        }

        //Lift the glyph
        if (CurrentStage == stage_10LiftBlock) {
            if (runtime.seconds() > 3) {
                CurrentStage = stage_23Call_Stinger;
            }
        }

//        RobotLog.aa(TAGCryBlue_GJ, "Stage: " + CurrentStage);
        if (CurrentStage == stage_23Call_Stinger) {
            if (runtime.seconds() > 3) {
                //call jewels Blue
                jewelsBlue.loop();
                if (jewelsBlue.IsJewelsDone()) {
                    CurrentStage = stage_30PullOffStone;
                    timelapse.reset();
                    RobotLog.aa(TAGCryBlue_GJ, "Stage: " + CurrentStage);

                }
            }
        }


        //Pull off the stone
        if (CurrentStage == stage_30PullOffStone) {
            RobotLog.aa(TAGCryBlue_GJ, "off stone / Stage: " + CurrentStage + " complete? " + robotChassis.getcmdComplete());
//            if (timelapse.seconds() > 0.2) {
            //          if (! robotChassis.getcmdComplete()) {

            robotChassis.cmdDrive(AUTO_DrivePower, 0, 24);

            if (robotChassis.getcmdComplete()) {
                robotChassis.DriveMotorEncoderReset();
                CurrentStage = stage_40Turn1;
                //         }
            }

        }


        if (CurrentStage == stage_40Turn1) {

            RobotLog.aa(TAGCryBlue_GJ, "turn1 / Stage: " + CurrentStage + "complete? " + robotChassis.getcmdComplete());

            // only want to call turn comand once
            if (countcmd < 1) {
                //              if (! robotChassis.getcmdComplete()) {

//            robotChassis.cmdTurn(AUTO_TurnPower, -AUTO_TurnPower, -90);
                countcmd = 1;

                robotChassis.cmdTurn(-AUTO_TurnPower, AUTO_TurnPower, -90);
            }
            if (robotChassis.getcmdComplete()) {
                timelapse.reset();
                robotChassis.DriveMotorEncoderReset();
                countcmd = 0;
                CurrentStage = stage_50driveToFront;
            }
        }
//                        CurrentStage = stage_150Done; // stop program for debugging


//drive toward cryptobox
        if (CurrentStage == stage_50driveToFront) {
            // if (robotChassis.getcmdComplete()) {
//            if (timelapse.seconds() > 0.2) {
            //              if (! robotChassis.getcmdComplete()) {
            RobotLog.aa(TAGCryBlue_GJ, "drive2 / Stage: " + CurrentStage + "complete? " + robotChassis.getcmdComplete());

            robotChassis.cmdDrive(AUTO_DrivePower, -90, 21);
            //            }
            if (robotChassis.getcmdComplete()) {
                robotChassis.DriveMotorEncoderReset();
                timelapse.reset();
                CurrentStage = stage_60TurnTwo;
            }
            //      }
        }
// turn to face box
        if (CurrentStage == stage_60TurnTwo) {
            //if (robotChassis.getcmdComplete()) {
            if (countcmd < 1) {
                //             if (! robotChassis.getcmdComplete()) {
                RobotLog.aa(TAGCryBlue_GJ, "TURN2 / Stage: " + CurrentStage + "complete? " + robotChassis.getcmdComplete());
                countcmd = countcmd + 1;
                robotChassis.cmdTurn(-AUTO_TurnPower, AUTO_TurnPower, -130);
            }
            if (robotChassis.getcmdComplete()) {
                robotChassis.DriveMotorEncoderReset();
                countcmd = 0;
                timelapse.reset();
                CurrentStage = stage_70driveToBox;
            }

        }
        //Drive to box
        if (CurrentStage == stage_70driveToBox) {
            //if (robotChassis.getcmdComplete()) {
            RobotLog.aa(TAGCryBlue_GJ, "to box / Stage: " + CurrentStage + "complete? " + robotChassis.getcmdComplete());


            robotChassis.cmdDrive(AUTO_DrivePower, -120, 12);

            if (robotChassis.getcmdComplete()) {
         robotChassis.DriveMotorEncoderReset();
                CurrentStage = stage_80OpenGripper;
            }
        }


        // open the gripper
        if (CurrentStage == stage_80OpenGripper) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.gripper.cmd_Open();
                robotChassis.lifter.cmd_MoveToTarget(robotChassis.lifter.LIFTPOS_BOTTOM);
                CurrentStage = stage_90Backup;
            }
        }

        // backup 1 inch to not be touching the glypy
        if (CurrentStage == stage_90Backup) {
            if (robotChassis.gripper.Is_Open()) {
                robotChassis.cmdDrive(-AUTO_DrivePower, -120, 2.0);
                CurrentStage = stage_150Done;
            }
        }


        if (CurrentStage == stage_150Done) {
            if (robotChassis.getcmdComplete()) {
                if (runtime.seconds() > 25) {
                    stop();
                }

            }
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robotChassis.stop();
    }

}
