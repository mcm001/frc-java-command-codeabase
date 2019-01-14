package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.auto.AutoMotion;
import frc.robot.lib.EncoderLib;
import frc.robot.lib.motion.Odometry;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.Gear;
import jaci.pathfinder.Pathfinder;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LIDARSubsystem;
import frc.robot.subsystems.LimeLight;

/**
 * Main robot class. There shouldn't be a *ton* of stuff here, mostly init
 * functions and smartdashboard stuff.
 *
 * @author Matthew Morley
 */
public class Robot extends TimedRobot {
  public static SendableChooser<AutoMotion.mGoalHeight> mGh;
  public static OI m_oi;
  public static Intake intake = new Intake();
  public static Elevator elevator = new Elevator();
  public static DriveTrain drivetrain = DriveTrain.getInstance();
  public static AHRS gyro = new AHRS(SPI.Port.kMXP);
  public static LimeLight limelight = new LimeLight();
  public static LIDARSubsystem lidarSubsystem = new LIDARSubsystem();
  public static SendableChooser<AutoMotion> backupAutoSelect = new SendableChooser<AutoMotion>();
  private static DoubleSolenoid shifterDoubleSolenoid = new DoubleSolenoid(9, 7, 3);
  private static DoubleSolenoid intakeDoubleSolenoid = new DoubleSolenoid(9, 0, 6);
  public static AutoMotion m_auto;
  SendableChooser<Command> m_chooser = new SendableChooser<Command>();
  public static Compressor compressor = new Compressor(9);
  private static Odometry odometry_;

  

  // Various pneumatic shifting methods
  public static void drivetrain_shift_high() {
    shifterDoubleSolenoid.set(DoubleSolenoid.Value.kForward);
  }

  public static void drivetrain_shift_low() {
    shifterDoubleSolenoid.set(DoubleSolenoid.Value.kReverse);
  }

  public static void intake_close() {
    intakeDoubleSolenoid.set(DoubleSolenoid.Value.kForward);
  }

  public static void intake_open() {
    intakeDoubleSolenoid.set(DoubleSolenoid.Value.kReverse);
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_oi = new OI();

    mGh = new SendableChooser<AutoMotion.mGoalHeight>();
    mGh.setDefaultOption("Low", AutoMotion.mGoalHeight.LOW);
    mGh.addOption("Middle", AutoMotion.mGoalHeight.MIDDLE);
    mGh.addOption("High", AutoMotion.mGoalHeight.HIGH);
    mGh.addOption("Dropped into the cargo ship", AutoMotion.mGoalHeight.OVER);
    SmartDashboard.putData("Goal Height", mGh);
    SmartDashboard.putData("Backup Selector (Will not be used in most cases)", backupAutoSelect);
    SmartDashboard.putData(drivetrain);
    SmartDashboard.putData(elevator);
    SmartDashboard.putData(intake);

    compressor.setClosedLoopControl(true);

    drivetrain.init();
    elevator.init();
    // wrist.init();
    gyro.reset();

    switch (RobotConfig.auto.auto_gear) {
    case HIGH:
      drivetrain.setHighGear();
    case LOW:
      drivetrain.setLowGear();
    default:
      drivetrain.setHighGear();
    }

    // Thanks to RoboLancers for odometry code
    odometry_ = Odometry.getInstance();
    new Notifier(() -> {
        // odometry_.setCurrentEncoderPosition((DriveTrain.getInstance().getLeft().getEncoderCount() + DriveTrain.getInstance().getRight().getEncoderCount()) / 2.0);
        odometry_.setCurrentEncoderPosition((DriveTrain.getInstance().m_left_talon.getSelectedSensorPosition() + DriveTrain.getInstance().m_right_talon.getSelectedSensorPosition()) / 2.0);

        // odometry_.setDeltaPosition(RobotUtil.encoderTicksToFeets(odometry_.getCurrentEncoderPosition() - odometry_.getLastPosition()));
        odometry_.setDeltaPosition(EncoderLib.rawToDistance(odometry_.getCurrentEncoderPosition() - odometry_.getLastPosition(), RobotConfig.driveTrain.POSITION_PULSES_PER_ROTATION,
                  (RobotConfig.driveTrain.left_wheel_effective_diameter + RobotConfig.driveTrain.right_wheel_effective_diameter)/2.0));

        odometry_.setTheta(Math.toRadians(Pathfinder.boundHalfDegrees(gyro.getAngle())));

        odometry_.addX(Math.cos(odometry_.getTheta()) * odometry_.getDeltaPosition());
        odometry_.addY(Math.sin(odometry_.getTheta()) * odometry_.getDeltaPosition());

        odometry_.setLastPosition(odometry_.getCurrentEncoderPosition());
    }).startPeriodic(0.01);

  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
    if (RobotConfig.auto.auto_gear == Gear.LOW) {
      drivetrain.setLowGear();
    } else if (RobotConfig.auto.auto_gear == Gear.HIGH) {
      drivetrain.setHighGear();
    } else {
      System.out.println("default auto gear " + RobotConfig.auto.auto_gear + " is not a valid choice!");
    }
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    // DriveTrajectoryPathfinder meme = new DriveTrajectoryPathfinder("file");
    // meme.start();

    gyro.reset(); // Reset the current gyro heading to zero
    drivetrain.zeroEncoders();

    if (RobotConfig.auto.auto_gear == Gear.LOW) {
      drivetrain.setLowGear();
    } else if (RobotConfig.auto.auto_gear == Gear.HIGH) {
      drivetrain.setHighGear();
    } else {
      System.out.println("default auto gear " + RobotConfig.auto.auto_gear + " is not a valid choice!");
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_auto != null) {
      m_auto.mBigCommandGroup.cancel();
    }
    // TODO reset subsystems on teleop init?
  }

  /**
   * This function is called periodically during operator control.
   *
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() { }

  /**
   * This function is called every robot packet, no matter the mode. Use // * this
   * for items like diagnostics that you want ran during disabled, // *
   * autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    // TODO make a function or class that does all this calculation for us
    SmartDashboard.putNumber("get forward axis", m_oi.getForwardAxis());
    SmartDashboard.putNumber("get turn axis", m_oi.getTurnAxis());
    // SmartDashboard.putenum("Drivetrain gear", drivetrain.current_gear);
    // SmartDashboard.putNumber("setVelocityRight output: ",
    // encoderlib.distanceToRaw(12/12, 4096, 6/12) / 10 ); // This *should* return 1
    // ft/sec to raw/0.1 sec
    SmartDashboard.putNumber("target left speed raw", ((m_oi.getForwardAxis() * 4) / (Math.PI * 6 / 12)) * 4096 / 10);
    
    SmartDashboard.putNumber("Left talon speed", drivetrain.m_left_talon.getSelectedSensorVelocity(0));
    SmartDashboard.putNumber("Left talon error", drivetrain.m_left_talon.getClosedLoopError(0));
    SmartDashboard.putNumber("Right talon speed", drivetrain.m_right_talon.getSelectedSensorVelocity(0));
    SmartDashboard.putNumber("Right talon error", drivetrain.m_right_talon.getClosedLoopError(0));

    SmartDashboard.putNumber("Intake target speed per OI:", m_oi.getIntakeSpeed());

    // SmartDashboard.putNumber("Throttle output", throttle.getRawAxis(1));
    SmartDashboard.putNumber("Elevator setpoint", 20000);
    SmartDashboard.putNumber("Elevator height", elevator.getHeight());
    SmartDashboard.putNumber("Elevator error", 4096 - elevator.getHeight());

    // SmartDashboard.putNumber("Wrist angle setpoint", wrist_setpoint);
    // SmartDashboard.putNumber("Wrist talon pos",
    // elevator.elevator_talon.getSelectedSensorPosition(0));
    // SmartDashboard.putNumber("Wrist error",
    // elevator.elevator_talon.getClosedLoopError(0));
    // SmartDashboard.putNumber("Wrist angle (deg)", wrist.getAngle());
    // SmartDashboard.putNumber("Wrist angular velocity (deg/s)",
    // wrist.getAngularVelocity());

    SmartDashboard.putNumber("Current Gyro angle", gyro.getAngle());

    // SmartDashboard.putString("Limelight Ntables", LimeLight.getData().toString());

    // Limelight stuff
    double[] limelightdata = limelight.getData();

    SmartDashboard.putNumber("Vision targets?", limelightdata[0]);
    SmartDashboard.putNumber("Horizontal offset", limelightdata[1]);
    SmartDashboard.putNumber("Vertical offset", limelightdata[2]);
    SmartDashboard.putNumber("Target area", limelightdata[3]);
    SmartDashboard.putNumber("Target skew", limelightdata[4]);
    SmartDashboard.putNumber("Vision pipeline latency", limelightdata[5]);

    // logger.update();
  }

}
