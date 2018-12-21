package frc.robot.commands;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Default command to drive the robot with joysticks. It may be overridden during autonomous mode to run drive sequence. This command grabs
 * the left and right joysticks on an XBox controller and sets them to arcade drive.
 */
public class stick_drive extends Command {
    // private Logger logger = LoggerFactory.getLogger(StickDrive.class);

    /**
     * Requires DriveTrain
     */
    public stick_drive() {
    	requires(Robot.drivetrain);
    }

    protected void initialize() {
        // logger.info("Using stick drive");
    }

    protected void execute() {
        // Robot.drivetrain.arcadeDrive(-Robot.oi.getController().getRawAxis(1), -Robot.oi.getController().getRawAxis(4));
        Robot.intake.talon_left.set(ControlMode.PercentOutput, 1);
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    	// Robot.drivetrain.arcadeDrive(0, 0);
    }

    protected void interrupted() {
        // logger.info("Stick drive interrupted");
    }
}