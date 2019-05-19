/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.subsystems.drivetrain;

import java.util.TreeMap;

import org.team5940.pantry.exparimental.command.SendableCommandBase;
import org.team5940.pantry.exparimental.command.Subsystem;

import com.team254.lib.physics.DifferentialDrive.ChassisState;

import frc.robot.lib.InterpolatableLut;
import frc.robot.lib.InterpolatableLutEntry;
import frc.robot.lib.motion.Util;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimeLight;

public class TurnToFaceVisionTarget extends SendableCommandBase {

	private InterpolatableLut skewCorrection;

	private double targetAngle;

	private int count = 0;

	public TurnToFaceVisionTarget() {
		addRequirements((Subsystem) DriveTrain.getInstance());

		var map = new TreeMap<Double, InterpolatableLutEntry>();
		map.put(Double.valueOf(0), new InterpolatableLutEntry(0));

		skewCorrection = new InterpolatableLut(map);
	}

	// Called just before this Command runs the first time
	@Override
	public void initialize() {
		var targetX = LimeLight.getInstance().getDx().getDegree();
		var robotYaw = DriveTrain.getInstance().getGyro();
		var interpolationOffset = skewCorrection
				.interpolate(LimeLight.getInstance().getTargetSkew());

		targetAngle = targetX + robotYaw + interpolationOffset;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	public void execute() {
		final double kp = 0.3;

		var error = DriveTrain.getInstance().getGyro() - targetAngle;

		if (Math.abs(error) < 2) {
			count++;
		} else if (count > 0) {
			count--;
		}

		var turnPower = kp * error;

		turnPower = Util.limit(turnPower, 4);

		ChassisState state = new ChassisState(0, turnPower);

		DriveTrain.getInstance().setOutputFromKinematics(state);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	public boolean isFinished() {
		return count > 10;
	}

	// Called once after isFinished returns true
	@Override
	public void end(boolean interrupted) {
		DriveTrain.getInstance().stop();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	//	@Override
	//	protected void interrupted() {}
}
