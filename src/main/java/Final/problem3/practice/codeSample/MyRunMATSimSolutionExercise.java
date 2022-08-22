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

public class MyRunMATSimSolutionExercise {
    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();

        //Todo global() modue to set the CRS

        //Todo controler() modue to set first iteration, last iteration, output directory, overwrite file setting, write event interval

        //Todo network() to set input file

        //Todo plans() to set input file

        //Todo qsim() to set start time, end time, flow capacity factor, and the storage capacity factor

        //Todo create PlanCalcScoreConfigGroup.ActivityParams for home and work activity, and set their typical duration and add them to config.planCalcScore().addActivityParams

        //Todo create StrategyConfigGroup.StrategySettings for reRoute and add to config

        //Todo create StrategyConfigGroup.StrategySettings for logitSelect and add to config

        //Todo strategy() to set maximum agent plan memory size and the fraction of iteration to disable innovation

        Scenario scneario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scneario);
        controler.run();

    }
}
