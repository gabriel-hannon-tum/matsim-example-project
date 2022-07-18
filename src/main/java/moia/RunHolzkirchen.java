package io.moia;

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

public class RunHolzkirchen {


    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();
        config.controler().setLastIteration(20);
        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
        config.parallelEventHandling().setNumberOfThreads(1);
        config.qsim().setNumberOfThreads(1);
        config.network().setInputFile("holzkirchen/network.xml");
        config.plans().setInputFile("holzkirchen/plans_prepared.xml");

        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
        home.setTypicalDuration(16 * 60 * 60);
        config.planCalcScore().addActivityParams(home);
        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
        work.setTypicalDuration(8 * 60 * 60);
        PlanCalcScoreConfigGroup.ActivityParams education = new PlanCalcScoreConfigGroup.ActivityParams("education");
        education.setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(education);
        PlanCalcScoreConfigGroup.ActivityParams shopping = new PlanCalcScoreConfigGroup.ActivityParams("shopping");
        shopping.setTypicalDuration(2 * 60 * 60);
        config.planCalcScore().addActivityParams(shopping);
        PlanCalcScoreConfigGroup.ActivityParams other = new PlanCalcScoreConfigGroup.ActivityParams("other");
        other.setTypicalDuration(2 * 60 * 60);
        config.planCalcScore().addActivityParams(other);

        config.planCalcScore().setFractionOfIterationsToStartScoreMSA(0.9);
        config.strategy().setFractionOfIterationsToDisableInnovation(0.9);

        config.qsim().setEndTime(24 * 3600);
        config.qsim().setSimStarttimeInterpretation(QSimConfigGroup.StarttimeInterpretation.onlyUseStarttime);

        config.planCalcScore().addActivityParams(work);

        // define strategies:
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute.toString());
            strat.setWeight(0.15);
            config.strategy().addStrategySettings(strat);
        }
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode.toString());
            strat.setWeight(0.15);
            config.strategy().addStrategySettings(strat);
        }
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta.toString());
            strat.setWeight(0.9);
            config.strategy().addStrategySettings(strat);
        }
        config.strategy().setFractionOfIterationsToDisableInnovation(0.9);
        config.changeMode().setModes(new String[]{"car", "drt"});

        PlanCalcScoreConfigGroup.ModeParams drt = config.planCalcScore().getOrCreateModeParams("drt");
        drt.setMarginalUtilityOfTraveling(-6);

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
        drtConfig.setVehiclesFile("holzkirchen/drtFleet.xml.gz");
        drtConfig.addParameterSet( new ExtensiveInsertionSearchParams() );
        multiModeDrtCfg.addDrtConfig(drtConfig);

        Scenario scenario = DrtControlerCreator.createScenarioWithDrtRouteFactory(config);
        ScenarioUtils.loadScenario(scenario);

        Controler controler = DrtControlerCreator.createControler(config, false);
        controler.run();

    }
}
