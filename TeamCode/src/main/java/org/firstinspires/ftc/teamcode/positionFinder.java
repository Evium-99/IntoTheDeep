package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="POSFINDER-Auto Test", group="TeleOp Test")
public class positionFinder extends OpMode {
    public static float p = 0.005F;
    public static float i = 0;
    public static float d = 0.001F;
    public static int referenceA = 0;
    public static int referenceB = 200;

    static class PIDController {
        private static float p;
        private static float i;
        private static float d;
        private static float integralSummation;
        private static float lastError;
        ElapsedTime timer;

        public static void setP(float pX) {
            p = pX;
        }

        public static void setI(float iX) {
            i = iX;
        }

        public static void setD(float dX) {
            d = dX;
        }

        public void init(float Xp, float Xi, float Xd) {
            p = Xp;
            i = Xi;
            d = Xd;
            timer = new ElapsedTime();
        }
        public float getOutput(float state, float reference) {
            float error = reference - state;
            integralSummation += (float) (error * timer.seconds());
            float derivative = (float) ((error - lastError) / timer.seconds());
            lastError = error;
            return (error * p) + (derivative * d) + (integralSummation * i);
        }
    }

    private DcMotor extendo;
    private static PIDController extendoPID = new PIDController();
    private DcMotor cap;
    private RevTouchSensor Lswitch;
    private autoTeleArm.PIDController capPID = new autoTeleArm.PIDController();


    @Override
    public void init() {
        extendo = hardwareMap.get(DcMotor.class, "spindle");
        extendoPID.init(p, i, d);
        extendo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        cap = hardwareMap.get(DcMotor.class, "cap");
        Lswitch = hardwareMap.get(RevTouchSensor.class, "Lswitch");
        capPID.init(p, i, d);
        cap.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        cap.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        if (!Lswitch.isPressed()) {
            cap.setPower(capPID.getOutput(cap.getCurrentPosition(), referenceA));
        }
        extendo.setPower(extendoPID.getOutput(extendo.getCurrentPosition(), referenceB));

        telemetry.addData("Capstan Pos", cap.getCurrentPosition());
        telemetry.addData("Extendo Pos", extendo.getCurrentPosition());
    }
}