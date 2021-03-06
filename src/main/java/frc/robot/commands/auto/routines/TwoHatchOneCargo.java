package frc.robot.commands.auto.routines;

import java.util.ArrayList;
import java.util.Arrays;

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature;
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory;
import org.ghrobotics.lib.mathematics.units.LengthKt;

import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.RobotConfig.auto.fieldPositions;
import frc.robot.commands.auto.AutoMotion;
import frc.robot.commands.auto.Trajectories;
import frc.robot.commands.auto.groups.AutoCommandGroup;
import frc.robot.commands.auto.groups.VisionCommandGroup;
import frc.robot.commands.subsystems.drivetrain.DriveDistanceTheThird;
import frc.robot.commands.subsystems.drivetrain.FollowVisionTargetTheSecond;
import frc.robot.commands.subsystems.superstructure.ArmMove;
import frc.robot.commands.subsystems.superstructure.ElevatorMove;
import frc.robot.commands.subsystems.superstructure.JankyGoToState;
import frc.robot.commands.subsystems.superstructure.RunIntake;
import frc.robot.lib.obj.factories.SequentialCommandFactory;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.Gear;
import frc.robot.subsystems.DriveTrain.TrajectoryTrackerMode;
import frc.robot.subsystems.LimeLight;
import frc.robot.subsystems.LimeLight.PipelinePreset;
import frc.robot.subsystems.superstructure.SuperStructure.iPosition;

/**
 * 2-hatch 1-cargo auto
 */
public class TwoHatchOneCargo extends VisionCommandGroup {
	// private AutoCommandGroup mBigCommandGroup;
	public ArrayList<TimedTrajectory<Pose2dWithCurvature>> trajects = new ArrayList<TimedTrajectory<Pose2dWithCurvature>>();
	public ArrayList<AutoMotion> motions = new ArrayList<AutoMotion>();

	public TwoHatchOneCargo(char arg1, char arg2) {
		this();
	}

	/**
	 * 2-hatch 1-cargo hard-coded auto. ow. This is fine. Everything is fine. 
	 * @param side to target (L or R)
	 * @param startPos L M or R on the hab
	 * @author Matthew Morley
	 */
	public TwoHatchOneCargo(/*char startPos, char side*/) {
		// HeldPiece cPiece = HeldPiece.HATCH; // we start with a hatch
		// String cStart = "hab" + startPos;

		// addSequential(new InstantRunnable(() -> {
		// 	SuperStructure.getElevator().getMaster().configPeakOutputForward(0);
		// 	SuperStructure.getElevator().getMaster().configPeakOutputReverse(0);

		// 	SuperStructure.getInstance().getWrist().getMaster().configPeakOutputForward(0);
		// 	SuperStructure.getInstance().getWrist().getMaster().configPeakOutputReverse(0);

		// 	SuperStructure.getInstance().getElbow().getMaster().configPeakOutputForward(0);
		// 	SuperStructure.getInstance().getElbow().getMaster().configPeakOutputReverse(0);
		// }, true));

		// boolean doIntake = false;
		// boolean doVision = false;

		/* Get a trajectory to move to the cargo ship. THE ROBOT IS REVERSED */
		TimedTrajectory<Pose2dWithCurvature> traject = Trajectories.generatedLGTrajectories.get("habR" + " to " + "rocketRF"); //current trajectory from hashmap in Trajectories

		addParallel(new JankyGoToState(fieldPositions.hatchLowGoal, iPosition.HATCH)); // move arm inside to prep state

		addSequential(new LimeLight.SetLEDs(LimeLight.LEDMode.kON));
		addSequential(new LimeLight.setPipeline(PipelinePreset.k3dVision));
		addSequential(DriveTrain.getInstance().followTrajectoryWithGear(traject, TrajectoryTrackerMode.RAMSETE, Gear.LOW, true)); //drive to goal

		// addParallel(new SuperstructureGoToState(fieldPositions.hatchMiddleGoal, iPosition.HATCH));
		addSequential(new FollowVisionTargetTheSecond(3.8));

		// addSequential(new DriveDistanceTheThird(LengthKt.getFeet(0.4), false));

		// addSequential(new PrintCommand("GOT TO RUN INTAKE"));

		addSequential(new RunIntake(-1, 0, 1.5));

		// addSequential(new PrintCommand("GOT TO BACKING UP"));

		// back up 3 feet
		// addParallel(new JankyGoToState(iPosition.HATCH_GRAB_INSIDE_PREP)); // TODO fix this broken logic!

		addParallel(new DriveDistanceTheThird(LengthKt.getFeet(3), true));

		addSequential(SequentialCommandFactory.getSequentialCommands(
				Arrays.asList(
						new ElevatorMove(iPosition.HATCH_GRAB_INSIDE.getElevator()),
						new JankyGoToState(iPosition.HATCH_GRAB_INSIDE))));

		addSequential(new PrintCommand("GOT TO next spline"));

		// // spline over to the rocket
		var rocketToLoading = Trajectories.generatedLGTrajectories.get("rocketRF to loadingR");
		addSequential(DriveTrain.getInstance().followTrajectoryWithGear(rocketToLoading, TrajectoryTrackerMode.RAMSETE, Gear.LOW, false)); //drive to goal

		// // // addParallel(new SuperstructureGoToState(fieldPositions.hatchMiddleGoal, iPosition.HATCH));

		addSequential(new FollowVisionTargetTheSecond(4.5));

		addSequential(new RunIntake(1, 0, 1));

		// addSequential(new DriveDistanceTheThird(LengthKt.getFeet(1), false));

		addParallel(SequentialCommandFactory.getSequentialCommands(
				Arrays.asList(
						new WaitCommand("Wait for clearance", 2),
						new ArmMove(iPosition.HATCH),
						new ElevatorMove(fieldPositions.hatchMiddleGoal))));

		// // // addParallel(new LimeLight.SetLEDs(LimeLight.LEDMode.kOFF));
		// addParallel(new JankyGoToState(fieldPositions.hatchLowGoal, iPosition.HATCH)); // move arm inside to prep state
		var loadingToRocketFar = Trajectories.generatedLGTrajectories.get("loadingR to rocketRF");
		addSequential(DriveTrain.getInstance().followTrajectoryWithGear(loadingToRocketFar, TrajectoryTrackerMode.RAMSETE, Gear.LOW, false)); //drive to goal
		addSequential(new FollowVisionTargetTheSecond(3.8));
		addSequential(new RunIntake(-1, 0, 1));

		// // // addSequential(new LimeLight.SetLEDs(LimeLight.LEDMode.kOFF));

	}

	// id functions

	/**
	 * identification function
	 * @return
	 *  the mBigCommandGroup of the function
	 */
	public AutoCommandGroup getBigCommandGroup() {
		return this;
	}

	//not id functions

}
