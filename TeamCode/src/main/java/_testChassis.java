import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by mg15 on 9/23/17.
 */

public class _testChassis extends OpMode {

    Chassis robotChassis = new Chassis();
    private ElapsedTime runtime = new ElapsedTime();


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
        runtime.reset();
        robotChassis.start();

    }


    @Override
    public void loop() {
        robotChassis.loop();

    }


}
