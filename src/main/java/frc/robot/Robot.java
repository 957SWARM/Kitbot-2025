// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.SPI.Port;

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

  int autoStep = 0;

  AHRS navx = new AHRS(NavXComType.kMXP_SPI);
  
  public Robot() {
    drive = new Drivetrain();
    xbox = new XboxController(0);
    coralMotor = new TalonSRX(13);
  }

  @Override
  public void robotPeriodic() {

    //System.out.println(drive.getPosition());
    System.out.println(navx.getYaw());
    
  }

  @Override
  public void autonomousInit() {
    drive.resetEncoders();
    autoStep = 0;
    navx.reset();
  }

  @Override
  public void autonomousPeriodic() {
    
    switch(autoStep) {

      // Drive x feet, with angle adjustment
      case 0:

        double speed = 0.5;

        double turn = 0;

        if(navx.getAngle() > 0.5) {
          turn = -0.11;
        } else if(navx.getAngle() < -0.5) {
          turn = 0.11;
        }

        drive.driveArcade(-speed, turn);

        if(drive.getPosition() > 6)
          autoStep = 1;

        break;

      // Slow down, wait until velocity is 0
      case 1:
        drive.driveArcade(0, 0);
        break;

      // Stop and Score for 2 seconds
      case 2:

        break;

      // Stop all operation
      default:

        break;

    }

  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    drive.driveArcade(xbox.getLeftY() / 2, xbox.getRightX() / 2);

    if (xbox.getAButton()) {
      coralMotor.set(ControlMode.PercentOutput, -0.35);
    } else if(xbox.getBButton()){
      coralMotor.set(ControlMode.PercentOutput, -0.6);
    }else if(xbox.getYButton()){
      coralMotor.set(ControlMode.PercentOutput, 1);

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
}
