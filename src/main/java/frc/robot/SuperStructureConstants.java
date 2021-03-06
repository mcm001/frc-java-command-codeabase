package frc.robot;

import org.ghrobotics.lib.mathematics.units.Length;
import org.ghrobotics.lib.mathematics.units.LengthKt;
import org.ghrobotics.lib.mathematics.units.Mass;
import org.ghrobotics.lib.mathematics.units.MassKt;

import frc.robot.lib.obj.RoundRotation2d;

public class SuperStructureConstants {
	public static class Wrist {
		public static final RoundRotation2d kWristMin = RoundRotation2d.getDegree(-90); // relative
		public static final RoundRotation2d kWristMax = RoundRotation2d.getDegree(90); // relative
		public static final RoundRotation2d kWristApproachingThreshold = RoundRotation2d.getDegree(6); // FIXME kinda random rn
		public static final Length intakeOut = LengthKt.getInch(19); //FIXME check
		public static final Length intakeDown = intakeOut;
		public static final Length intakeUp = intakeOut;
		public static final Length intakeAbove = LengthKt.getInch(5); //FIXME check

	}

	public static final Length kCarriageToFramePerimeter = LengthKt.getInch(14); // Fixme check

	public static class Elbow {
		public static final RoundRotation2d kElbowMin = RoundRotation2d.getDegree(-180); // absolute
		public static final RoundRotation2d kElbowMax = RoundRotation2d.getDegree(15); // absolute
		public static final Length carriageToIntake = LengthKt.getInch(12); //FIXME verify
		public static final RoundRotation2d kStowedAngle = RoundRotation2d.getDegree(-110); //FIXED, now less arb.
		public static final RoundRotation2d kClearFirstStageMinElbowAngle = RoundRotation2d.getDegree(-70); //FIXME
		public static final RoundRotation2d kElbowApproachingThreshold = RoundRotation2d.getDegree(6); // FIXME kinda random rn

	}

	public static class Elevator {
		public static final Length bottom = LengthKt.getInch(RobotConfig.elevator.elevator_minimum_height.getInch() + 0.5);
		public static final Length top = RobotConfig.elevator.elevator_maximum_height;
		public static final Length crossbarBottom = LengthKt.getInch(35); //FIXME verify
		public static final Length crossbarWidth = LengthKt.getInch(4); //FIXME verify
		public static final Length kElevatorLongRaiseDistance = LengthKt.getInch(27);//FIXME mostly arb.
		public static final Length kClearFirstStageMaxHeight = crossbarBottom; //yay its redundant
		public static final Length kElevatorApproachingThreshold = LengthKt.getInch(2); //FIXME
		public static final Length electronicsHeight = LengthKt.getInch(0);
		public static final Length longClimb = LengthKt.getInch(20); //FIXME actual number, perhaps?
		public static final Length carriageHeight = LengthKt.getInch(12); //TODO this one is maybe mostly right
		public static final Length minimumPassThroughAboveCrossbar = LengthKt.getInch(50); // TODO check meeeeeeee

	}

	public static final Mass kHatchMass = MassKt.getLb(2.4); // FIXME check mass
	public static final Mass kCargoMass = MassKt.getLb(1); // FIXME check mass
}
