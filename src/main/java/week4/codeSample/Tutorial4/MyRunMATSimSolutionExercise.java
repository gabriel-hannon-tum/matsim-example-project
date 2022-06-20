package week4.codeSample.Tutorial4;

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

public class MyRunMATSimSolutionExercise {
    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();

        //Todo global() modue to set the CRS
        config.global().setCoordinateSystem(TransformationFactory.DHDN_GK4);


        //Todo controler() modue to set first iteration, last iteration, output directory, overwrite file setting, write event interval
        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(1);
        config.controler().setOutputDirectory("./output/outputweek4");
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setWriteEventsInterval(5);

        //Todo network() to set input file
        config.network().setInputFile("./inputs/munich.xml");
        //Todo plans() to set input file
        config.plans().setInputFile("./scenarios/week4/commuterPopulation.xml");


        //Todo qsim() to set start time, end time, flow capacity factor, and the storage capacity factor

        config.qsim().setStartTime(0);
        config.qsim().setEndTime(24*60*60);
        config.qsim().setFlowCapFactor(0.01);
        config.qsim().setStorageCapFactor(0.01);

        //Todo create PlanCalcScoreConfigGroup.ActivityParams for home and work activity, and set their typical duration and add them to config.planCalcScore().addActivityParams

        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
        home.setTypicalDuration(16*60*60);
        config.planCalcScore().addActivityParams(home);

        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
        work.setTypicalDuration(8*60*60);
        config.planCalcScore().addActivityParams(work);

        //Todo create StrategyConfigGroup.StrategySettings for reRoute and add to config

        StrategyConfigGroup.StrategySettings reRoute = new StrategyConfigGroup.StrategySettings();
        reRoute.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
        reRoute.setWeight(0.15);
        config.strategy().addStrategySettings(reRoute);


        //Todo create StrategyConfigGroup.StrategySettings for logitSelect and add to config
        StrategyConfigGroup.StrategySettings logitSelect = new StrategyConfigGroup.StrategySettings();
        logitSelect.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
        logitSelect.setWeight(0.85);
        config.strategy().addStrategySettings(logitSelect);



        //Todo strategy() to set maximum agent plan memory size and the fraction of iteration to disable innovation

        config.strategy().setMaxAgentPlanMemorySize(5);
        config.strategy().setFractionOfIterationsToDisableInnovation(0.9);

        Scenario scneario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scneario);
        controler.run();

    }
}
