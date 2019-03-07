// package frc.robot.commands.subsystems.drivetrain;

// import java.util.Arrays;
// import java.util.List;

// import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d;
// import org.ghrobotics.lib.mathematics.units.Length;
// import org.ghrobotics.lib.mathematics.units.LengthKt;
// import org.ghrobotics.lib.mathematics.units.Rotation2dKt;
// import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration;
// import org.ghrobotics.lib.mathematics.units.derivedunits.AccelerationKt;
// import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity;
// import org.ghrobotics.lib.mathematics.units.derivedunits.VelocityKt;
// import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnit;

// import edu.wpi.first.wpilibj.command.Command;
// import frc.robot.commands.auto.Trajectories;
// import frc.robot.lib.motion.Util;
// import frc.robot.subsystems.DriveTrain;
// import frc.robot.subsystems.LimeLight;
// import frc.robot.subsystems.DriveTrain.TrajectoryTrackerMode;

// public class DriveDistanceTheSecond extends Command {

// 	private static final Velocity<Length> kCruiseVel = VelocityKt.getVelocity(LengthKt.getFeet(4));
// 	private static final Acceleration<Length> kDefaultAcceleration = AccelerationKt.getAcceleration(LengthKt.getFeet(8));
// 	// private final DriveTrain drive;
// 	private Velocity<NativeUnit> kCruiseVelTicks;
// 	private Acceleration<NativeUnit> kAccelTicks;
// 	private Command mCommand;
// 	private Length distance;
// 	private Velocity<Length> vel;
// 	private boolean reversed;
// 	private boolean commandStarted = false;

// 	/**
// 	 * Drive a distance (straight) forwards or backwards using a spliney boi. Distance is calculated using sovePNP
// 	 * @param distance how far to move
// 	 * @param reversed if the path is reversed
// 	 */
// 	public DriveDistanceTheSecond(Length distance, boolean reversed) {
// 		this(distance, kCruiseVel, reversed);
// 	}

// 	public DriveDistanceTheSecond(Length distance, Velocity<Length> vel, boolean reversed) {
// 		this.distance = distance;
// 		this.vel = vel;
// 		this.reversed = reversed;
// 	}

// 	// Called just before this Command runs the first time
// 	@Override
// 	protected void initialize() {
// 		List<Pose2d> waypoints Arrays.asList(
// 			LimeLight.getInstance().getPose(kOffset, distanceToShiftBy)
// 		)

// 		var trajectory = Trajectories.generateTrajectoryLowGear(waypoints, reversed);

// 		System.out.println("FIRST POSE: " + Util.toString(trajectory.getFirstState().getState().getPose()) + " LAST POSE: " + Util.toString(trajectory.getLastState().getState().getPose()));

// 		mCommand = DriveTrain.getInstance().followTrajectory(trajectory, TrajectoryTrackerMode.RAMSETE, true);

// 		mCommand.start();
// 		commandStarted = true;
// 	}

// 	// Called repeatedly when this Command is scheduled to run
// 	@Override
// 	protected void execute() {}

// 	// Make this return true when this Command no longer needs to run execute()
// 	@Override
// 	protected boolean isFinished() {
// 		return mCommand.isCompleted() && commandStarted;
// 	}

// 	// Called once after isFinished returns true
// 	@Override
// 	protected void end() {}

// 	// Called when another command which requires one or more of the same
// 	// subsystems is scheduled to run
// 	@Override
// 	protected void interrupted() {}
// }