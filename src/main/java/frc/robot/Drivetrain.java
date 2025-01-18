package frc.robot;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivetrain {
    SparkMax leftA;
    SparkMax leftB; 
    SparkMax rightA; 
    SparkMax rightB;
    DifferentialDrive drive;

    public Drivetrain(){
        leftA = new SparkMax(0, MotorType.kBrushless);
        leftB = new SparkMax(1, MotorType.kBrushless);
        rightA = new SparkMax(2, MotorType.kBrushless);
        rightB = new SparkMax(3, MotorType.kBrushless);
        drive = new DifferentialDrive(leftA, rightA);
    }

    public void driveArcade(double xSpeed, double zRotation) {
        drive.arcadeDrive(xSpeed, zRotation);
    }
}
