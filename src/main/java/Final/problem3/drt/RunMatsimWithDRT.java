package Final.problem3.drt;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.drt.optimizer.insertion.ExtensiveInsertionSearchParams;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.DrtControlerCreator;
import org.matsim.contrib.drt.run.MultiModeDrtConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Arrays;

public class RunMatsimWithDRT {
    public static void main(String[] args) {
        Config config;
        config = ConfigUtils.createConfig("scenarios/final/ebersberg");

        config.global().setRandomSeed(1000);

        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(75);
        config.controler().setOutputDirectory("output/ebersberg/DRT");
        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

        config.network().setInputFile("combinedRegion.xml");
        config.plans().setInputFile("drtPopulation.xml");


        config.qsim().setFlowCapFactor(0.1);
        config.qsim().setStorageCapFactor(0.3);
        config.strategy().setFractionOfIterationsToDisableInnovation(.9);
        config.qsim().setSimStarttimeInterpretation(QSimConfigGroup.StarttimeInterpretation.onlyUseStarttime);

        {
            PlanCalcScoreConfigGroup.ActivityParams act = new PlanCalcScoreConfigGroup.ActivityParams();
            act.setActivityType("home");
            act.setTypicalDuration(12 * 60 * 60);
            config.planCalcScore().addActivityParams(act);
        }

        {
            PlanCalcScoreConfigGroup.ActivityParams act = new PlanCalcScoreConfigGroup.ActivityParams();
            act.setActivityType("work");
            act.setTypicalDuration(8 * 60 * 60);
            act.setOpeningTime(7*60*60);
            act.setClosingTime(19*60*60);
            act.setLatestStartTime(9 * 60 * 60);
            config.planCalcScore().addActivityParams(act);
        }

        config.planCalcScore().setLearningRate(1.);
        config.planCalcScore().setBrainExpBeta(2.);
        config.planCalcScore().setLateArrival_utils_hr(-18.);
        config.planCalcScore().setPerforming_utils_hr(6.);

        {
            StrategyConfigGroup.StrategySettings strategySettings = new StrategyConfigGroup.StrategySettings();
            strategySettings.setWeight(0.9);
            strategySettings.setStrategyName("BestScore");
            config.strategy().addStrategySettings(strategySettings);
        }

        {
            StrategyConfigGroup.StrategySettings strategySettings = new StrategyConfigGroup.StrategySettings();
            strategySettings.setWeight(0.15);
            strategySettings.setStrategyName("ReRoute");
            config.strategy().addStrategySettings(strategySettings);
        }

        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode.toString());
            strat.setWeight(0.2);
            config.strategy().addStrategySettings(strat);
        }
        config.changeMode().setModes(new String[]{"car", "drt"});


        PlanCalcScoreConfigGroup.ModeParams drt = config.planCalcScore().getOrCreateModeParams("drt");
        drt.setMarginalUtilityOfTraveling(-5);

        config.vspExperimental().setWritingOutputEvents(true);

        DvrpConfigGroup dvrpConfig = ConfigUtils.addOrGetModule( config, DvrpConfigGroup.class );

        MultiModeDrtConfigGroup multiModeDrtCfg = ConfigUtils.addOrGetModule(config, MultiModeDrtConfigGroup.class);
        DrtConfigGroup drtConfig = new DrtConfigGroup();
        drtConfig.setMode( "drt" )
                .setStopDuration(60.)
                .setMaxWaitTime(900.)
                .setMaxTravelTimeAlpha(1.3)
                .setMaxTravelTimeBeta(10. * 60.)
                .setMaxWalkDistance(1000)
                .setPlotDetailedCustomerStats(true)
                .setOperationalScheme(DrtConfigGroup.OperationalScheme.door2door);

        drtConfig.setRejectRequestIfMaxWaitOrTravelTimeViolated( true );
        drtConfig.setVehiclesFile("drtFleet.xml");
        drtConfig.addParameterSet( new ExtensiveInsertionSearchParams() );
        multiModeDrtCfg.addDrtConfig(drtConfig);

        Scenario scenario = DrtControlerCreator.createScenarioWithDrtRouteFactory(config);
        ScenarioUtils.loadScenario(scenario);

        Controler controler = DrtControlerCreator.createControler(config, false);
        controler.run();

    }
}
