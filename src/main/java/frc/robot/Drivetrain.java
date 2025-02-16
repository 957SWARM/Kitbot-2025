package frc.robot;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivetrain {
    SparkMax leftA;
    SparkMax leftB; 
    SparkMax rightA; 
    SparkMax rightB;
    DifferentialDrive drive;

    RelativeEncoder leftEncoder;
    RelativeEncoder rightEncoder;

    public Drivetrain(){

        leftA = new SparkMax(4, MotorType.kBrushless);
        leftB = new SparkMax(3, MotorType.kBrushless);
        rightA = new SparkMax(2, MotorType.kBrushless);
        rightB = new SparkMax(1, MotorType.kBrushless);

        leftEncoder = leftA.getEncoder();
        rightEncoder = rightA.getEncoder();
        
        resetEncoders();
        
        SparkMaxConfig globalConfig = new SparkMaxConfig();
        SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
        SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();
        SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();

            /*
        * Set parameters that will apply to all SPARKs. We will also use this as
        * the left leader config.
        */
        globalConfig
            .smartCurrentLimit(50)
            .idleMode(IdleMode.kBrake)
            .inverted(true);

        // Apply the global config and invert since it is on the opposite side
        rightLeaderConfig
            .apply(globalConfig)
            .inverted(false);

        // Apply the global config and set the leader SPARK for follower mode
        leftFollowerConfig
            .apply(globalConfig)
            .follow(leftA);

        // Apply the global config and set the leader SPARK for follower mode
        rightFollowerConfig
            .apply(globalConfig)
            .follow(rightA)
            .inverted(false);

        /*
        * Apply the configuration to the SPARKs.
        *
        * kResetSafeParameters is used to get the SPARK MAX to a known state. This
        * is useful in case the SPARK MAX is replaced.
        *
        * kPersistParameters is used to ensure the configuration is not lost when
        * the SPARK MAX loses power. This is useful for power cycles that may occur
        * mid-operation.
        */
        leftA.configure(globalConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        leftB.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightA.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightB.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        drive = new DifferentialDrive(leftA, rightA);
    }

    public void resetEncoders(){
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);
    }

    public double getPosition(){
        double leftPosition = -leftEncoder.getPosition();
        double rightPosition = -rightEncoder.getPosition();

        return ( ( leftPosition + rightPosition ) / 2 ) / 4.36;
    }

    public double getVelocity(){
        double leftVelocity = -leftEncoder.getVelocity();
        double rightVelocity = -rightEncoder.getVelocity();

        return ( ( leftVelocity + rightVelocity ) / 2 ) / 4.36;
    }

    public void driveArcade(double xSpeed, double zRotation) {

        if(Math.abs(xSpeed) < 0.1) 
            xSpeed = 0;
        if(Math.abs(zRotation) < 0.1) 
            zRotation = 0;

        drive.arcadeDrive(xSpeed, zRotation);
    }

}
