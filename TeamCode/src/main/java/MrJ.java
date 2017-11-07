


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "MrJ", group = "")  // @Autonomous(...) is the other common choice

public class MrJ extends OpMode {

    public static int stage_0PreStart = 0;
    public static int stage_10GripBlock = 10;
    public static int stage_20StingerExtend = 20;
    public static int stage_30PullOffStone = 30;
    public static int stage_40Turn1 = 40;
    public static int stage_50driveToFront = 50;
    public static int stage_60TurnTwo = 60;
    public static int stage_70driveToBox =70;
    public static int stage_150Done = 150;

    int CurrentStage = stage_0PreStart;


    Chassis robotChassis = new Chassis();

    private double AUTO_MotorPower = .3333;
    private double AUTO_MotorPower_Fast = .3;
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
            robotChassis.stinger.cmdDoExtend();
            if (runtime.seconds() > 2) {
                CurrentStage = stage_30PullOffStone;
            }
        }

        if (CurrentStage == stage_30PullOffStone){
                robotChassis.cmdDrive(AUTO_MotorPower_Fast,0,30);
                CurrentStage = stage_40Turn1;

        }

        if (CurrentStage == stage_40Turn1) {
            if (robotChassis.getcmdComplete()) {
                // Stay in this stage until complete move
                robotChassis.cmdTurn(AUTO_MotorPower, -AUTO_MotorPower, 90);
                CurrentStage = stage_50driveToFront;
            }
        }

        if (CurrentStage == stage_50driveToFront) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.cmdDrive( AUTO_MotorPower_Fast, 90, 36);
                CurrentStage = stage_60TurnTwo;
            }
        }

        if (CurrentStage == stage_60TurnTwo) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.cmdTurn(AUTO_MotorPower, -AUTO_MotorPower, 180);
                CurrentStage = stage_70driveToBox;
            }
        }
        if (CurrentStage == stage_70driveToBox) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.cmdDrive( AUTO_MotorPower_Fast, 180, 36.5);
                CurrentStage = stage_150Done;
            }
        }
        if (CurrentStage == stage_150Done) {
            if (robotChassis.getcmdComplete()) {
                stop();
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
