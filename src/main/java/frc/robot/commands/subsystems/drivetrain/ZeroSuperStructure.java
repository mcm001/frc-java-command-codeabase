package frc.robot.commands.subsystems.drivetrain;

import org.ghrobotics.lib.mathematics.units.Rotation2dKt;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.superstructure.SuperStructure;

public class ZeroSuperStructure extends Command {
  String p;
  public ZeroSuperStructure(String piece){
    this.p = piece;
  }

  @Override
  public void initialize(){
    System.out.println("Zeroing encoders.");
    if(p.equals("elevator")){
      SuperStructure.getInstance().getElevator().zeroEncoder();
    }else if(p.equals("elbow")){
      SuperStructure.getInstance().getElbow().getMaster().setSensorPosition(Rotation2dKt.getDegree(0));
    }else if(p.equals("wrist")){
      SuperStructure.getInstance().getWrist().getMaster().setSensorPosition(Rotation2dKt.getDegree(0));
    }
  }

  @Override
  public void execute(){

  }

  @Override
  public boolean isFinished(){
    return true;
  }

  @Override
  public void end(){

  }

  @Override
  public void interrupted(){

  }
}