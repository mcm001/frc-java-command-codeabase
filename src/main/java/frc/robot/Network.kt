package frc.robot

import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import frc.robot.commands.auto.Autonomous
import org.ghrobotics.lib.utils.capitalizeEachWord
import org.ghrobotics.lib.wrappers.networktables.enumSendableChooser
import org.ghrobotics.lib.wrappers.networktables.sendableChooser

object Network {

    val AutoTab by lazy {Shuffleboard.getTab("Auto")}

    val DriveTrainTab by lazy {Shuffleboard.getTab("DriveTrain")}

    val SuperStructureTab by lazy {Shuffleboard.getTab("SuperStructure")}

    val IntakeTab: ShuffleboardTab by lazy {Shuffleboard.getTab("Intake")}

    val VisionTab: ShuffleboardTab by lazy {Shuffleboard.getTab("Vision")}

    private val visionLayout = VisionTab.getLayout("Vision", BuiltInLayouts.kGrid)
            .withSize(3, 3)
//            .withPosition(0, 2)

    val visionDriveAngle: NetworkTableEntry = visionLayout.add("Vision Drive Angle", 0.0).entry
    val visionDriveActive: NetworkTableEntry = visionLayout.add("Vision Drive Active", false).entry

//    val startingPositionChooser = enumSendableChooser<Autonomous.StartingPositions>()
    val autoModeChooser = SendableChooser<Autonomous.Mode>()
//    val autoModeChooser = enumSendableChooser<Autonomous.Mode>()

    private inline fun <reified T : Enum<T>> enumSendableChooser(
            crossinline block: (T) -> String = { it.name.replace('_', ' ').capitalizeEachWord() }
    ) = sendableChooser<T> {
        enumValues<T>().forEach { enumValue ->
            addOption(block(enumValue), enumValue)
        }
    }

    private inline fun <T> sendableChooser(crossinline block: SendableChooser<T>.() -> Unit) =
            SendableChooser<T>().apply(block)

    private val autoLayout = AutoTab.getLayout("Autonomous", BuiltInLayouts.kList)
            .withSize(2, 2)
            .withPosition(0, 0)

    init {

        // Put choosers on dashboard
        autoLayout.add(
                "Auto Mode",
                autoModeChooser
        )

    }

}