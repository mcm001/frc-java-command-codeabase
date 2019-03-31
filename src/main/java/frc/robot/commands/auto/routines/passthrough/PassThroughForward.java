// /*----------------------------------------------------------------------------*/
// /* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

// package frc.robot.commands.auto.routines.passthrough;

// import org.ghrobotics.lib.mathematics.units.LengthKt;

// import frc.robot.RobotConfig.auto.fieldPositions;
// import frc.robot.commands.subsystems.superstructure.SuperstructureGoToState;
// import frc.robot.lib.obj.RoundRotation2d;
// import frc.robot.planners.SuperstructurePlannerOLD;
// import frc.robot.states.ElevatorState;
// import frc.robot.states.SuperStructureState;
// import frc.robot.subsystems.superstructure.RotatingJoint.RotatingArmState;
// import frc.robot.subsystems.superstructure.SuperStructure.iPosition;

// public class PassThroughForward extends SequentialCommandGroup {
// 	/**
// 	 * Pass through the elevator from front to back
// 	 */
// 	public PassThroughForward() {

// 		SuperStructureState passthroughInitState = SuperstructurePlannerOLD.passThroughState;

// 		SuperStructureState passedThrough = new SuperStructureState(
// 				new ElevatorState(LengthKt.getInch(26)),
// 				new RotatingArmState(RoundRotation2d.getDegree(-180)),
// 				new RotatingArmState(RoundRotation2d.getDegree(-120)));

// 		addSequential(new SuperstructureGoToState(new ElevatorState(fieldPositions.hatchLowGoal), iPosition.HATCH));
// 		addSequential(new SuperstructureGoToState(passthroughInitState));
// 		addSequential(new SuperstructureGoToState(passedThrough));
// 		addSequential(new SuperstructureGoToState(new ElevatorState(fieldPositions.hatchLowGoal), iPosition.HATCH_REVERSE));

// 	}
// }
