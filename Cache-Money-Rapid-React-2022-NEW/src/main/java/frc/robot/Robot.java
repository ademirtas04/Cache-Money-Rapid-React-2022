// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;
import frc.robot.subsystems.AutoTimer;
import frc.robot.subsystems.Climb;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.command.IndexBalls;
import frc.robot.command.ShootBalls;
import frc.robot.command.TankDrive;
import frc.robot.command.Tilt;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  public static boolean ballRight = true;
  public static OI m_oi;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Initializing Subsystems
  public static Intake m_intake= new Intake();
  public static Intake m_index = new Intake();
  public static Climb m_climb = new Climb();
  public static DriveTrain m_drivetrain = new DriveTrain();
  public static Shooter m_shooter = new Shooter();
  public static AutoTimer m_autotimer = new AutoTimer();

  public static boolean[] AutoSequence = new boolean[RobotMap.AUTONOMOUS_LENGTH];
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();
    Robot.m_oi = new OI();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
   
    DriveTrain.setPosition(0);
    DriveTrain.resetGyro();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        CompetitionAutoSequece(false);
      case kDefaultAuto:
        CompetitionAutoSequece(true);
      default:
        if(ballRight){
          CompetitionAutoSequece(true);
        } else {
          CompetitionAutoSequece(false);
        }

        // DriveTrain.PIDMove(-60, 0.005, 0.0, 0.0); //STEP ONE
        // DriveTrain.PIDturn(-90, 0.05, 0.5, 0.0, 0.0, 0.0); //STEP TWO

        /*
        //INTAKE TEST
        if(DriveTrain.distanceCompleted(1)){
        Intake.Limit();
        IntakeBalls.go();
        IntakeBalls.FrontIndex();
        IndexBalls.BackIndex();
        }

        /* AUTONOMOUS SEQUENCE 
        1) Shoot
        2) INTAKE BALL
        3) SHOOT BALLLS
        --- THE REST IS OPTIONAL FOR CIR
        4) TURN W/ GYRO
        5) MOVE FORWARD
        6) INTAKE BALL
        7) TURN W/GYRO
        8) SHOOT BALL  */
    }
  }

  public static void CompetitionAutoSequece(boolean turnRight){
    if(!AutoSequence[0]){
      if(AutoTimer.timePassed(1.0)){
        AutoSequence[0] = true;
      } else {
        Shooter.ShootBraindead(0.5);
      }
    } else if(!AutoSequence[1]){
      if(AutoTimer.timePassed(1.0)){
        AutoSequence[1] = true;
        Intake.BackIndex(0.0);
        
        Shooter.ShootBraindead(0.0);
      } else {
        Intake.Limit();
        Intake.BackIndex(0.4);
      }
    } else if(!AutoSequence[2]){
      if(AutoTimer.timePassed(0.3)){
        AutoSequence[2] = true;
        Intake.tilt(0.0);
      } else {
        Intake.tilt(0.25);
      }
    } else if(!AutoSequence[3]){
      if(DriveTrain.distanceCompleted()){
        AutoSequence[3] = true;
      } else {
        DriveTrain.PIDMove(-116.17, 0.005, 0.0, 0.0);
      }
    } else if(!AutoSequence[4]){
      if(DriveTrain.turnCompleted()){
        AutoSequence[4] = true;
      } else {
        int kDir = 1;
        if(!turnRight){
          kDir = -1;
        }
        DriveTrain.PIDturn(kDir*90, 0.05, 0.5, 0, 0, 0);
      }
    } else if(!AutoSequence[5]){
      if(DriveTrain.distanceCompleted()){
        AutoSequence[5] = true;
      } else {
        DriveTrain.PIDMove(0, 0.005, 0.0, 0.0);
      }
    } else if(!AutoSequence[6]){
      if(AutoTimer.timePassed(0.3)){
        AutoSequence[6] = true;
        Intake.tilt(0.0);
      } else {
        Intake.go(0.75);
        Intake.tilt(-0.3);
      }
    } else if(!AutoSequence[7]){
      if(AutoTimer.timePassed(0.3)){
        AutoSequence[7] = true;
        Intake.go(0.0);
        Intake.BackIndex(0.0);
      } else {
        Intake.go(0.75);
        Intake.BackIndex(0.4);
      }
    } else if(!AutoSequence[8]){
      if(AutoTimer.timePassed(0.3)){
        AutoSequence[8] = true;
        Intake.tilt(0.0);
      } else {
        Intake.tilt(-0.3);
      }
    } else if(!AutoSequence[9]){
      if(DriveTrain.turnCompleted()){
        AutoSequence[9] = true;
      } else {
        int kDir = -1;
        if(!turnRight){
          kDir = 1;
        }
        DriveTrain.PIDturn(90*kDir, 0.05, 0.5, 0, 0, 0);
      }
    } else if(!AutoSequence[10]){
      if(DriveTrain.distanceCompleted()){
        AutoSequence[10] = true;
      } else {
        DriveTrain.PIDMove(116.17, 0.005, 0.0, 0.0);
      }
    } else if(!AutoSequence[11]){
      if(AutoTimer.timePassed(1.0)){
        AutoSequence[11] = true;
      } else {
        DriveTrain.move(0.2,0.2);
      }
    } else if(!AutoSequence[12]){
      if(AutoTimer.timePassed(1.0)){
        AutoSequence[12] = true;
        Intake.tilt(0.0);
      } else {
        Intake.tilt(-0.3);
        Shooter.ShootBraindead(0.5);
      }
    } else if(!AutoSequence[13]){
      if(AutoTimer.timePassed(1.0)){
        AutoSequence[13] = true;
      } else {
        Intake.Limit();
        Intake.BackIndex(0.4);
      }
    } else if(!AutoSequence[14]){
      if(AutoTimer.timePassed(0.3)){
        AutoSequence[14] = true;
      } else {
        Shooter.ShootBraindead(0.0);
      }
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    SmartDashboard.putString("teleop.intialize", "executed");
   
  }
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putString("teleop.periodic", "executed");
    Scheduler.getInstance().run();
    Tilt.tiltTrig();
    ShootBalls.shootTrig();
    TankDrive.move();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    DriveTrain.writePositionToCSV();
    DriveTrain.writeAngleToCSV();
  }
  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
