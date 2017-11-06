


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "MrJ2", group = "")  // @Autonomous(...) is the other common choice

public class MrJ2 extends OpMode {

    public static int stage_0PreStart = 0;
    public static int stage_10GripBlock = 10;
    public static int stage_20StingerExtend = 20;
    public static int stage_30BackUp = 30;
    public static int stage_40ReadColorOfjewell = 40;
    public static int stage_50ReturnToZero = 50;
    public static int stage_60Return2Start = 60;
    public static int stage_150Done = 150;

    int CurrentStage = stage_0PreStart;


    Chassis robotChassis = new Chassis();

    private double AUTO_MotorPower = .4;
    private double AUTO_MotorPower_Fast = 6;
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
            CurrentStage = stage_30BackUp;
        }

        if (CurrentStage == stage_30BackUp) {
            if (runtime.seconds() > 2) {
                robotChassis.cmdDrive(-AUTO_MotorPower_Fast, 0, 2);
                CurrentStage = stage_40ReadColorOfjewell;
            }
        }

        if (CurrentStage == stage_40ReadColorOfjewell) {
            if (robotChassis.getcmdComplete()) {
                // Stay in this stage until complete move
                robotChassis.cmdTurn(AUTO_MotorPower, -AUTO_MotorPower, 45);
                CurrentStage = stage_50ReturnToZero;
            }
        }

        if (CurrentStage == stage_50ReturnToZero) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.cmdTurn(-AUTO_MotorPower, AUTO_MotorPower, 0);
                CurrentStage = stage_60Return2Start;
            }
        }

        if (CurrentStage == stage_60Return2Start) {
            if (robotChassis.getcmdComplete()) {
                robotChassis.cmdDrive(AUTO_MotorPower_Fast, 0, 30);
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
