package frc.robot.command;
import frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;


import frc.robot.Robot;
import frc.robot.command.IntakeBalls;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.GenericHID.Hand;
public class IntakeBalls extends Command{
    
    public static boolean initTimeNeeded = true;
    public static double initTime = 0;
    
    public IntakeBalls(){
        requires(Robot.m_intake);
        
    }

    @Override
    protected void initialize() {
        super.initialize();
    }
    
    @Override
    protected boolean isFinished() {
        if(initTimeNeeded){
            initTime = Timer.getFPGATimestamp();
            initTimeNeeded = false;
        }
        if(Timer.getFPGATimestamp() - initTime < 1){
            return false;
        }
        return true;
    }

    @Override
    protected void execute() {
        Intake.Limit();
        go();
    }

    @Override
    protected void end() {
        Intake.FrontIndex(0);
        Intake.go(0.0);
        initTimeNeeded = true;
    }

    public static void go(){
        Intake.go(0.85);
        Intake.FrontIndex(0.25);
    }
    public static void goTrigger(){
     //Hand hand= new Hand();
       // double power= -Robot.m_oi.getTriggerAxis(Hand);
        Intake.go(0.85);
        Intake.FrontIndex(0.25);
    }


    public static void tilt(){
        Intake.tilt(0.5);
    }

    public static void FrontIndex(double run){
        Intake.FrontIndex(run);
    }

    
}
