


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@Autonomous(name = "JewelsBlue", group = "")  // @Autonomous(...) is the other common choice

public class JewelsBlue extends OpMode {
    private static final String TAGJewB = "8492-JewellsBlue";

    public static int stage_0PreStart = 0;
    public static int stage_05J1Retracted = 5;
    public static int stage_10J1Angled = 10;
    public static int stage_20J2Straight = 20;
    public static int stage_30J1Extend = 30;
    public static int stage_40ReadColorOfjewell = 40;
    public static int stage_50J2CCW = 50;
    public static int stage_51J2CW = 51;
    public static int stage_60J2Straight = 60;
    public static int stage_70J1Angled = 70;
    public static int stage_80J2CW = 80;
    public static int stage_90J1Retract = 90;
    public static int stage_150Done = 150;

    boolean JewelsDone = false;
    boolean robotChassisWasNull = false;
    int CurrentStage = stage_0PreStart;

    private Chassis.gameColor stoneColor = Chassis.gameColor.BLUE;

    Chassis robotChassis = new Chassis();

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

       // if (robotChassis == null) {
            robotChassis = new Chassis();
            robotChassis.hardwareMap = hardwareMap;
            robotChassis.telemetry = telemetry;
            robotChassis.init();
            robotChassisWasNull = true;
        //}

        stoneColor = Chassis.gameColor.BLUE;

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        if (robotChassisWasNull) {
            robotChassis.init_loop();
        }
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
        if (robotChassisWasNull) {
            robotChassis.start();
        }
    }

    /*
     * Code to run REPEATEDLY after the driver hits org.firstinspires.ftc.teamcodePLAY but before they hit STOP
     */
    @Override
    public void loop() {
        RobotLog.aa(TAGJewB,"Stage: "+ CurrentStage );
        RobotLog.aa(TAGJewB, "Runtime: " + runtime.seconds());

        //if (robotChassisWasNull) {
       //     robotChassis.loop();
        //}

        telemetry.update();
        telemetry.addLine("Jewel_Stage = " + CurrentStage);

        if (CurrentStage == stage_0PreStart) {
            robotChassis.stinger.cmdDoJ1Retract();
            robotChassis.stinger.cmdDoJ2STOW();
            CurrentStage = stage_05J1Retracted;
        }

        if (CurrentStage == stage_05J1Retracted) {
            if (robotChassis.stinger.IsJ2STOWED()) {
                CurrentStage = stage_10J1Angled;
                robotChassis.stinger.cmdDoJ1Angle();
            }
        }


        if (CurrentStage == stage_10J1Angled) {
            if (robotChassis.stinger.IsJ1Angled()) {
                robotChassis.stinger.cmdDoJ2Straight();
                CurrentStage = stage_20J2Straight;
            }
        }


        if (CurrentStage == stage_20J2Straight) {
            if (robotChassis.stinger.IsJ2Striaght()) {
                robotChassis.stinger.cmdDoJ1Extend();
                CurrentStage = stage_30J1Extend;
            }
        }

        if (CurrentStage == stage_30J1Extend) {
            if (robotChassis.stinger.IsJ1Extended()) {
                CurrentStage = stage_40ReadColorOfjewell;
            }
        }


        if (CurrentStage == stage_40ReadColorOfjewell) {

            if (robotChassis.stinger.IsBlue() && stoneColor == Chassis.gameColor.BLUE) {
                CurrentStage = stage_50J2CCW;
                robotChassis.stinger.cmdDoJ2CCW();

            } else if (robotChassis.stinger.IsBlue() && stoneColor == Chassis.gameColor.RED) {

                CurrentStage = stage_51J2CW;
                robotChassis.stinger.cmdDoJ2CW();

            } else if (robotChassis.stinger.IsRed() && stoneColor == Chassis.gameColor.RED) {

                CurrentStage = stage_50J2CCW;
                robotChassis.stinger.cmdDoJ2CCW();

            } else if (robotChassis.stinger.IsRed() && stoneColor == Chassis.gameColor.BLUE) {

                CurrentStage = stage_51J2CW;
                robotChassis.stinger.cmdDoJ2CW();

            } else {
                // We are unsure what color we are dealing with.
                CurrentStage = stage_70J1Angled;
                robotChassis.stinger.cmdDoJ1Angle();
            }

        }

        if (CurrentStage == stage_50J2CCW) {
            if (robotChassis.stinger.IsJ2CCW()) {
                CurrentStage = stage_60J2Straight;
                robotChassis.stinger.cmdDoJ2Straight();
            }
        }

        if (CurrentStage == stage_51J2CW) {
            if (robotChassis.stinger.IsJ2CW()) {
                CurrentStage = stage_60J2Straight;
                robotChassis.stinger.cmdDoJ2Straight();
            }
        }

        if (CurrentStage == stage_60J2Straight) {
            if (robotChassis.stinger.IsJ2Striaght()) {
                CurrentStage = stage_70J1Angled;
                robotChassis.stinger.cmdDoJ1Angle();
            }
        }

        if (CurrentStage == stage_70J1Angled) {
            if (robotChassis.stinger.IsJ1Angled()) {
                CurrentStage = stage_80J2CW;
                robotChassis.stinger.cmdDoJ2STOW();
            }
        }

        if (CurrentStage == stage_80J2CW) {
            if (robotChassis.stinger.IsJ2STOWED()) {
                CurrentStage = stage_90J1Retract;
                robotChassis.stinger.cmdDoJ1Retract();
            }
        }

        if (CurrentStage == stage_90J1Retract) {
            if (robotChassis.stinger.IsJ1Retracted()) {
                CurrentStage = stage_150Done;
            }
        }

        if (CurrentStage == stage_150Done) {
            JewelsDone = true;
        }
    }


    public void setStoneColor(Chassis.gameColor newColor) {
        stoneColor = newColor;
    }

    public boolean IsJewelsDone() {
        return (JewelsDone);
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        if (robotChassisWasNull) {
            robotChassis.stop();
        }
    }
}
