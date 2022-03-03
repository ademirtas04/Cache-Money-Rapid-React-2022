package frc.robot.subsystems;


import edu.wpi.first.math.estimator.AngleStatistics;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.SerialPort.Port;

import java.io.PrintWriter;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.SerialPort.Port;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DriveTrain extends Subsystem { 
    //I2C Port Setup  
    private static I2C.Port i2cport = I2C.Port.kOnboard;
    //Motor Variable setup
    private static CANSparkMax motorLeft1 = new CANSparkMax(RobotMap.MOTOR_LEFT_1_ID, MotorType.kBrushless);
    private static CANSparkMax motorLeft2 = new CANSparkMax(RobotMap.MOTOR_LEFT_2_ID, MotorType.kBrushless);
    private static CANSparkMax motorRight1 = new CANSparkMax(RobotMap.MOTOR_RIGHT_1_ID, MotorType.kBrushless);
    private static CANSparkMax motorRight2 = new CANSparkMax(RobotMap.MOTOR_RIGHT_2_ID, MotorType.kBrushless);
    private static RelativeEncoder l1 = motorLeft1.getEncoder();
    private static RelativeEncoder l2 = motorLeft2.getEncoder();
    private static RelativeEncoder r1 = motorRight1.getEncoder();
    private static RelativeEncoder r2 = motorRight2.getEncoder();
    private static SparkMaxPIDController pid = motorLeft1.getPIDController();    
  //  private static ColorSensorV3 colorsensor = new ColorSensorV3(i2cport);
   // private static ColorMatch colormatcher = new ColorMatch();
    private static AHRS gyro = new AHRS(Port.kUSB);
    public static boolean initAngleNeeded = true;
    public static double initAngle = 0;
    public static boolean headerWritten = false;
    
    public static void setLeftMotors(double speed){
        motorLeft1.set(speed);
        motorLeft2.set(speed);
    }
    public static void setRightMotors(double speed){
        motorRight1.set(-speed);
        motorRight2.set(-speed);
    }
    public static void setRight2(double speed){
        motorRight2.set(speed);
    }
    public static void move(double left, double right){
        setLeftMotors(-left);
        setRightMotors(-right);
    }
    public static void pidMove(){
        motorRight1.follow(motorLeft1);
        motorRight2.follow(motorLeft1);
        motorLeft2.follow(motorLeft1);
        motorLeft1.getPIDController().setP(RobotMap.DriveTrain_P_Value);
        motorLeft1.getPIDController().setI(RobotMap.DriveTrain_I_Value);
        motorLeft1.getPIDController().setD(RobotMap.DriveTrain_D_Value);
        motorLeft1.getPIDController().setReference(0.5, ControlType.kPosition, 0);
        
    }
    public static void SetPID(double p, double i, double d, double ff, double izone){
        pid.setP(p);
        pid.setI(i);
        pid.setD(d);
        pid.setFF(ff);
        pid.setIZone(izone);
    }

    public static AHRS getGyro(){
        return gyro;
    }

    public static double getGyroAngle(){
        return gyro.getAngle();
    }

    public static String getFirmWare(){
        return gyro.getFirmwareVersion();
    }

    public static void turn(double angle){        
        while(gyro.getAngle()<=angle){
            DriveTrain.move(0.3,0);

        } 
        DriveTrain.move(0,0);
    }
    
    //returns the velocity of the wheels in feet per second
    public static double getVelocity(){
        return l1.getVelocity() * 2 /*radius of wheel in in*/ * (1/12) /*in to ft*/ * 60 /*s*/;
    }
    
    //returns the distance traveled in feet
    public static double getPosition(){
        l1.setPositionConversionFactor(4 * Math.PI * (1/12));
        return l1.getPosition();
    }

    public static void PIDturn(double setpointAngle, double kP, double kD, double kI, double iZone, double kF){
        if(initAngleNeeded){
            initAngle = gyro.getAngle();
            initAngleNeeded = false;
        }
        if(!headerWritten){
            DriveTrain.writeOutput("Time", "Error");
        }

        double currentAngle = gyro.getAngle() - initAngle;
        double error = setpointAngle - currentAngle;

        DriveTrain.writeOutput(Timer.getFPGATimestamp() + "", error + "");

        double p = error * kP;
        double i = 0.0;
        if(Math.abs(error) <= iZone || iZone == 0.0) {
            i = error * kI;
        } 

        double d = gyro.getRate()*kD;

        double f = setpointAngle * kF;

        double output = p + i + d + f;
        if(output > 1){
            output = 1;
        } else if(output < -1){
            output = -1;
        }
       
        DriveTrain.move(output, -output);
    }



    public static boolean turnCompleted(double setpointAngle){ 
        if(initAngleNeeded){
            return false;
        }
        if(Math.abs(gyro.getAngle() - initAngle - setpointAngle) < RobotMap.CLEARENCE_SETPOINT){
            initAngleNeeded = true;
            initAngle = 0;
            return true;
        }
        return false;
    }
    

  /*  public static Color getColor(){
        return colorsensor.getColor();
    }  

    public static boolean colorMatchCheck(Color c){
        ColorMatchResult match = colormatcher.matchClosestColor(DriveTrain.getColor());
        if(match.color == c && match.confidence >= RobotMap.COLOR_MATCH_CONFIDENCE_INTERVAL){
            return true;
        } else {
            return false;
        }*/

    //}
    //write output to csv file
    public static void writeOutput(String colOne, String colTwo){
        try (PrintWriter writer = new PrintWriter("../outputs/errorGraph.csv")) {
            StringBuilder sb = new StringBuilder();
            sb.append(colOne);
            sb.append(',');
            sb.append(colTwo);
            sb.append('\n');
            writer.write(sb.toString());      
          } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
          }
    }

    @Override
    protected void initDefaultCommand() {
        
    }
    public static void moveDistance(double d) {
    }

}
    