package Final.problem3.practice.codeSample;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.ArrayList;

public class MyRunMATSimSolution {
    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();

        config.global().setCoordinateSystem(TransformationFactory.DHDN_GK4);

        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(1);
        config.controler().setOutputDirectory("./scenarios/week4/output");
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setWriteEventsInterval(5);

        config.network().setInputFile("./scenarios/week4/munich.xml");
        config.plans().setInputFile("./scenarios/week4/commuterPopulation.xml");

        config.qsim().setStartTime(4*60*60);
        config.qsim().setEndTime(20*60*60);

        config.qsim().setFlowCapFactor(0.01);
        config.qsim().setStorageCapFactor(0.01);

        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
        home.setTypicalDuration(16*60*60);
        config.planCalcScore().addActivityParams(home);

        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
        work.setTypicalDuration(8*60*60);
        config.planCalcScore().addActivityParams(work);

        StrategyConfigGroup.StrategySettings reRoute = new StrategyConfigGroup.StrategySettings();
        reRoute.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
        reRoute.setWeight(0.15);
        config.strategy().addStrategySettings(reRoute);

        StrategyConfigGroup.StrategySettings logitSelect = new StrategyConfigGroup.StrategySettings();
        logitSelect.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
        logitSelect.setWeight(0.85);
        config.strategy().addStrategySettings(logitSelect);

        config.strategy().setMaxAgentPlanMemorySize(5);
        config.strategy().setFractionOfIterationsToDisableInnovation(0.9);

        Scenario scneario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scneario);
        controler.run();

    }
}
