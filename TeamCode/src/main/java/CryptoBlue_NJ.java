import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Crypto-Blue_NJ", group = "")  // @Autonomous(...) is the other common choice

public class CryptoBlue_NJ extends OpMode {

    public static int stage_0PreStart = 0;
    public static int stage_05CloseGripper=5;
    public static int stage_10LiftBlock = 10;
    public static int stage_20StingerExtend = 20;
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

    private double AUTO_TurnPower = .3333;
    private double AUTO_DrivePower = .3;
    private int AUTO_NextHeading = 0;

    private int AUTO_RED_Factor = 1;
    private int AUTO_BLUE_Factor = -1;
    private int AUTO_REDBLUE_Factor_Stone = AUTO_RED_Factor;
    private int AUTO_REDBLUE_Factor_Jewel = AUTO_RED_Factor;

    private int AUTO_Jewel_Swing = 45;


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
        //telemetry.addData("Status", shootTrigger.getPosition());
        telemetry.addData("Stage", CurrentStage);
        robotChassis.loop();

        if (CurrentStage == stage_0PreStart) {
            //Start Stage 1
            robotChassis.gripper.cmd_Close();
            //robotChassis.stinger.cmdDoExtend();
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
            if (runtime.seconds() > 4) {
                CurrentStage = stage_30PullOffStone;
            }
        }

        //Pull off the stone
        if (CurrentStage == stage_30PullOffStone) {
            robotChassis.cmdDrive(AUTO_DrivePower, 0, 30);
            CurrentStage = stage_80OpenGripper;

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
                robotChassis.cmdDrive(-AUTO_DrivePower, 0, 2.0);
                CurrentStage = stage_150Done;
            }
        }
		

        if (CurrentStage == stage_150Done) {
            if (robotChassis.getcmdComplete()) {
                if (runtime.seconds()> 25){
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
