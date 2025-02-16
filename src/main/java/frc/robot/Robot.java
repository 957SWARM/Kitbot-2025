// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.SPI.Port;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  TalonSRX coralMotor;
  Drivetrain drive;
  XboxController xbox;

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");

  int autoStep = 0;

  Timer time;

  AHRS navx = new AHRS(NavXComType.kMXP_SPI);

  String autoMode = "Center";
  
  public Robot() {
    drive = new Drivetrain();
    xbox = new XboxController(0);
    coralMotor = new TalonSRX(13);
    time = new Timer();
  }

  @Override
  public void robotPeriodic() {


        //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

        //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    
        //System.out.println(drive.getPosition());
    System.out.println(navx.getYaw());
    System.out.println(tx.getDouble(0));
    System.out.println(ty.getDouble(0));
    System.out.println(ta.getDouble(0));


  }

  @Override
  public void autonomousInit() {
    drive.resetEncoders();
    autoStep = 0;
    navx.reset();
    coralMotor.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void autonomousPeriodic() {

    switch(autoMode){

      case "Center":
        centerAuto();
        break;

      case "Right":
        sideAuto(-45);
        break;

      case "Left":
        sideAuto(45);
        break;

      default:
        break;

    }

  }
// getLeftY is for the Y-axis of the left stick on the xbox controller, as is getRightX for the X-axis
  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    drive.driveArcade(xbox.getLeftY() / 2, xbox.getRightX() / 2);

    if (xbox.getAButton()) {
      coralMotor.set(ControlMode.PercentOutput, -0.35);
    } else if (xbox.getBButton()){
      coralMotor.set(ControlMode.PercentOutput, -0.6);
    } else if (xbox.getYButton()){
      coralMotor.set(ControlMode.PercentOutput, 1);
    } else if (xbox.getRightBumperButton()) {
      coralMotor.set(ControlMode.PercentOutput, -0.3);
    
    } else {
      coralMotor.set(ControlMode.PercentOutput, 0); 
    }

  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}

  public void centerAuto() {
    double speed = 0;

    double turn = 0;
    
    switch(autoStep) {

      // Drive x feet, with angle adjustment
      case 0:

        speed = 0.8;

        turn = 0;

        if(navx.getAngle() > 0.5) {
          turn = -0.11;
        } else if(navx.getAngle() < -0.5) {
          turn = 0.11;
        }

        drive.driveArcade(-speed, turn);

        if(drive.getPosition() > 4)
          autoStep = 1;

        break;

      // Slow down, wait until velocity is 0
      case 1:
        speed = 0.35;

        drive.driveArcade(-speed, 0);
        
        if(Math.abs(drive.getVelocity()) < 0.05){
          time.restart();
          autoStep = 2;
        }
          
       
        break;

      // Stop and Score for 2 seconds
      case 2:
        drive.driveArcade(0, 0);
        coralMotor.set(ControlMode.PercentOutput, -0.4);

        if(time.get() > 2)
          autoStep = 4;

        break;

      // Stop all operation
      default:
        drive.driveArcade(0, 0);
        coralMotor.set(ControlMode.PercentOutput, 0);
        break;

    }
  }

  public void sideAuto(double angle){
      double speed = 0;
  
      double turn = 0;
      
      switch(autoStep) {
  
        // Drive x feet, with angle adjustment
        case 0:
  
          speed = 0.5;
  
          turn = 0;
  
          if(navx.getAngle() > 0.5) {
            turn = -0.11;
          } else if(navx.getAngle() < -0.5) {
            turn = 0.11;
          }
  
          drive.driveArcade(-speed, turn);
  
          if(drive.getPosition() > 5)
            autoStep = 1;
  
          break;
  
        // Stop, turn x degrees right
        case 1:
          speed = 0;

          turn = 0.5;
  
          drive.driveArcade(0, 0.5);
      
          
          if(Math.abs(navx.getAngle()) > angle){
            drive.driveArcade(0, 0);
            drive.resetEncoders();
            autoStep = 2;
          }
            
         
          break;
  
        // Move forward 4, stop
        case 2:
          speed = 0.5;
    
          turn = 0;

          if(navx.getAngle() > angle + .5) {
            turn = -0.11;
          } else if(navx.getAngle() < angle -.5) {
            turn = 0.11;
          }

          drive.driveArcade(-speed, turn);

          if(drive.getPosition() > 4)
            autoStep = 3;

          break;
          
        case 3:
          speed = 0.35;

          drive.driveArcade(-speed, 0);
          
          if(Math.abs(drive.getVelocity()) < 0.05){
            time.restart();
            autoStep = 4;
          }
            
        
          break;

        // Stop and Score for 2 seconds
        case 4:
          drive.driveArcade(0, 0);
          coralMotor.set(ControlMode.PercentOutput, -0.4);

          if(time.get() > 2)
            autoStep = 5;

          break;
          
        // Stop all operation
        default:
          drive.driveArcade(0, 0);
          coralMotor.set(ControlMode.PercentOutput, 0);
          break;
  
      }
    }

  
}
