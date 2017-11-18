import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Jewels_test", group = "")  // @Autonomous(...) is the other common choice

public class Jewels_test extends OpMode {

    public static int stage_0PreStart = 0;
    public static int stage_20StingerExtend = 20;
    public static int stage_70Turn = 70;
    public static int stage_140Turn = 140;
    public static int stage_150Done = 150;

    int CurrentStage = stage_0PreStart;


    Chassis robotChassis = new Chassis();

    private double AUTO_TurnPower = .2;
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
            robotChassis.stinger.cmdDoJ1Extend();
            CurrentStage = stage_70Turn;

        }

        if (CurrentStage == stage_70Turn) {
            if (robotChassis.getcmdComplete()) {
                // Stay in this stage until complete move
                robotChassis.cmdTurn(-AUTO_TurnPower, AUTO_TurnPower, -45);
                CurrentStage = stage_140Turn;

            }
        }
        if (CurrentStage == stage_140Turn) {
            if (robotChassis.getcmdComplete()) {
                // Stay in this stage until complete move
                robotChassis.cmdTurn(AUTO_TurnPower, -AUTO_TurnPower, 90);
                robotChassis.stinger.cmdDoJ1Retract();
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
