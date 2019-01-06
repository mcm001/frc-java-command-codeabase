
package frc.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.ArrayList;

import frc.robot.auto.AutoMotion.*;
import frc.robot.auto.groups.*;

public class AutoSelector{
    public SendableChooser<AutoMotion.startingPiece> sp;
    public SendableChooser<AutoMotion.goalHeight> gh;
    public SendableChooser<AutoMotion.goalType> gt;
    public SendableChooser<AutoMotion> backupAutoSelect = new SendableChooser<AutoMotion>();


    ArrayList<AutoMotion> usableMotions;
    ArrayList<AutoMotion> possibleMotions;

    AutoMotion defaultMotion = new AutoMotion("Default motion", startingPiece.NONE, goalHeight.LOW, goalType.CARGO);
    goalHeight goalH;
    startingPiece sPiece;
    goalType goalT;

    /**
     * sets up required SendableChoosers
     * generates list of possible AutoMotions
     */
    public AutoSelector(){

        backupAutoSelect.setDefaultOption("Default Path", defaultMotion);

        /* TODO so this should really all be done with buttons for speed, but we don't have enough atm, so I'm putting it in sendable chooser for now*/

        sp = new SendableChooser<AutoMotion.startingPiece>();
        sp.setDefaultOption("None", startingPiece.NONE);
        sp.addOption("Hatch", startingPiece.HATCH);
        sp.addOption("Cargo", startingPiece.CARGO);

        gh = new SendableChooser<AutoMotion.goalHeight>();
        gh.setDefaultOption("Low", goalHeight.LOW);
        gh.addOption("Middle", goalHeight.MIDDLE);
        gh.addOption("High", goalHeight.HIGH);

        gt = new SendableChooser<AutoMotion.goalType>();
        gt.setDefaultOption("Rocket", goalType.ROCKET);
        gt.addOption("Cargo Ship", goalType.CARGO);

        //TODO add posible motions

    }
    

    /**
     * selects the best AutoMotion for the game
     * all inputs from sendable chooser
     */
    public AutoMotion chooseMotion() {
        this.goalH = gh.getSelected();
        this.goalT = gt.getSelected();
        this.sPiece = sp.getSelected();

        usableMotions = checkCompat(possibleMotions);
        
        if(usableMotions.size()<=1){
            return usableMotions.get(0);
        }else{
            for (AutoMotion motion : usableMotions){
                backupAutoSelect.addOption(motion.getName(), motion);
            }
            // TODO find out if this just makes it select the default
            return backupAutoSelect.getSelected();
        }
        
    }

    /**
     * generates an ArrayList of AutoMotions that work with the current user inputs
     */
    private ArrayList<AutoMotion> checkCompat(ArrayList<AutoMotion> motions){
        ArrayList<AutoMotion> toReturn = new ArrayList<AutoMotion>();
        for(AutoMotion motion : motions){
            if (motion.getGoalHeight() == this.goalH
                    && motion.getGoalType() == this.goalT
                    && motion.getStartingPiece() == this.sPiece){
                toReturn.add(motion);
            }
        }
        return toReturn;
    }

}