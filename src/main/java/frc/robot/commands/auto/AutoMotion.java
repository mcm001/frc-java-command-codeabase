package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.commands.auto.groups.AutoCommandGroup;
import frc.robot.commands.subsystems.superstructure.elevator.SetElevatorHeight;
import frc.robot.commands.subsystems.drivetrain.FollowVisionTarget;
import frc.robot.subsystems.superstructure.Elevator;
import frc.robot.subsystems.superstructure.Wrist;
import frc.robot.subsystems.superstructure.Elevator.ElevatorPresets;
import frc.robot.subsystems.superstructure.Wrist.WristPos;
import frc.robot.commands.auto.groups.*;
import frc.robot.Robot;
import frc.robot.commands.auto.actions.*;

import java.util.ArrayList;

/**
 * Creates a command group for a specific automatic motion. 
 * Input a type of goal and a height then start the mBigCommandGroup externally
 * In the future, this could change to more inputs depending on the button setup
 * 
 * @author Jocelyn McHugo
 */
public class AutoMotion {

  public enum mHeldPiece{
    HATCH, CARGO, NONE
  }
  
  /**
   * different heights of goals.
   * LOW: the lowest level of the rocket and through the hatch of the cargo ship;
   * MIDDLE: the middle level of the rocket;
   * HIGH: the highest level of the rocket;
   * OVER: dropped into the cargo ship from above
   */
  public enum mGoalHeight{
    LOW, MIDDLE, HIGH, OVER
  }

  /**
   * different types of goals on the field.
   * CARGO_CARGO: put cargo in the cargo ship;
   * ROCKET_CARGO: put cargo in the rocket;
   * CARGO_HATCH: put a hatch on the cargo ship;
   * ROCKET_HATCH: put a hatch on the rocket;
   * RETRIEVE_HATCH: pick up a hatch from the loading station;
   * RETRIEVE_CARGO: pick up cargo from an unspecified location
   */
  public enum mGoalType{
    CARGO_CARGO, CARGO_HATCH, ROCKET_CARGO, ROCKET_HATCH, RETRIEVE_HATCH, RETRIEVE_CARGO
  }

  private mGoalHeight gHeight;
  private mGoalType gType;
  private mHeldPiece piece;
  private AutoCommandGroup mBigCommandGroup;

  /**
   * generates the command groups based on the inputted goal height/type
   * @param gHeight
   *    the height of the goal the robot should aim for (LOW, MIDDLE, HIGH, OVER)
   * @param gType
   *    the type of goal 
   */

  public AutoMotion (mGoalHeight gHeight, mGoalType gType){
    this.gHeight = gHeight;
    this.gType = gType;
    //select heldPiece
    if (this.gType == mGoalType.CARGO_CARGO || this.gType == mGoalType.ROCKET_CARGO){
      this.piece = mHeldPiece.CARGO;
    }else if (this.gType == mGoalType.CARGO_HATCH || this.gType == mGoalType.ROCKET_HATCH){
      this.piece = mHeldPiece.HATCH;
    }else{
      this.piece=mHeldPiece.NONE;
    }

    if (this.piece!=mHeldPiece.NONE){
      this.mBigCommandGroup = new AutoCommandGroup(genPlaceCommands());
    }else{
      this.mBigCommandGroup = new AutoCommandGroup(genGrabCommands());
    }
  }
  /**
   * Generates commands to pick up a piece based on the parameters of the current AutoMotion
   * @return
   *  an ArrayList of commands
   */
  private ArrayList<Command> genGrabCommands(){
    ArrayList<Command> toReturn = new ArrayList<Command>();
    if (this.gType==mGoalType.RETRIEVE_CARGO){
      // Set the intake to cargo mode
      toReturn.add(Robot.superstructure.moveSuperstructureWrist(WristPos.CARGO));
      // Predefined grab command
      toReturn.add(new GrabCargo());
    }else if (this.gType==mGoalType.RETRIEVE_HATCH){
      // Set the intake to hatch mode
      toReturn.add(Robot.superstructure.moveSuperstructureWrist(WristPos.HATCH));
      // Predefined grab command
      toReturn.add(new GrabHatch());
    }
    return toReturn;
  }
  
  /**
   * Generates commands to place a piece based on the parameters of the current AutoMotion
   * @return
   *  an ArrayList of commands
   */
  private ArrayList<Command> genPlaceCommands(){
    ArrayList<Command> toReturn = new ArrayList<Command>();

    // Set intake mode
    if(this.piece==mHeldPiece.HATCH){
      toReturn.add(Robot.superstructure.moveSuperstructureWrist(WristPos.HATCH));
    }else{
      toReturn.add(Robot.superstructure.moveSuperstructureWrist(WristPos.CARGO));
    }

    // Align with the vision targets, slightly back from the goal
    toReturn.add(new FollowVisionTarget(0.7, 70, 20)); // TODO check % value TODO this assumes a perfect FollowVisionTarget command

    // Set the elevator to the correct height
    toReturn.add(Robot.superstructure.moveSuperstructureElevator(Elevator.getHeightEnumValue(getElevatorPreset())));

    if(this.gType==mGoalType.CARGO_CARGO){
      // Drive forward so the intake is over the bay and the bumpers are in the indent thingy
      toReturn.add(new DriveDistance(2,20)); // TODO check distances

      // Actuate intake so it points down into the bay
      toReturn.add(Robot.superstructure.moveSuperstructureWrist(Wrist.WristPos.DOWN));
    }else{
      // Drive forward so the intake is flush with the port/hatch
      toReturn.add(new DriveDistance(1,20)); // TODO check distances
    }

    if(this.piece==mHeldPiece.CARGO){
      toReturn.add(new AutoIntake(-1, 5));
    }else if (this.piece==mHeldPiece.HATCH){
      toReturn.add(new PlaceHatch());
    }

    return toReturn;

  }

  /**
   * selects the correct ElevatorPreset from the Elevator subsystems enum based on 
   * the mGoalHeight, the mGoalType, and the mHeldPiece
   */
  private ElevatorPresets getElevatorPreset(){
    switch (this.gType){
      case CARGO_CARGO:
        if (this.gHeight == mGoalHeight.LOW){
          return ElevatorPresets.CARGO_SHIP_HATCH;
        }else{
          return ElevatorPresets.CARGO_SHIP_WALL;
        }
      case CARGO_HATCH:
        return ElevatorPresets.CARGO_SHIP_HATCH;
      case ROCKET_CARGO:
        if (this.gHeight == mGoalHeight.LOW){
          return ElevatorPresets.LOW_ROCKET_PORT;
        }else if (gHeight == mGoalHeight.MIDDLE){
          return ElevatorPresets.MIDDLE_ROCKET_PORT;
        }else{
          return ElevatorPresets.HIGH_ROCKET_PORT;
        }
      case ROCKET_HATCH:
        if (this.gHeight == mGoalHeight.LOW){
          return ElevatorPresets.LOW_ROCKET_HATCH;
        }else if (this.gHeight == mGoalHeight.MIDDLE){
          return ElevatorPresets.MIDDLE_ROCKET_HATCH;
        }else{
          return ElevatorPresets.HIGH_ROCKET_HATCH;
        }
      default:
        return ElevatorPresets.CARGO_SHIP_HATCH;
    }
  }

  // id functions

  /**
   * 
   * @return
   *  the mGoalHeight of the AutoMotion
   */
  public mGoalHeight getGoalHeight(){
    return this.gHeight;
  }

  /**
   * identification function
   * @return
   *  the mGoalType of the AutoMotion
   */
  public mGoalType getmGoalType(){
    return this.gType;
  }

  /**
   * identification function
   * @return
   *  the mHeldPiece of the AutoMotion
   */
  public mHeldPiece getmHeldPiece(){
    return this.piece;
  }

  /**
   * identification function
   * @return
   *  the mBigCommandGroup of the function
   */
  public AutoCommandGroup getBigCommandGroup(){
    return this.mBigCommandGroup;
  }
  
}
