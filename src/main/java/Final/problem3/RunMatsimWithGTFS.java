package Final.problem3;

/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

import com.google.common.collect.Sets;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 */
public class RunMatsimWithGTFS {


        public static void main(String[] args) {


            Config config;
            config = ConfigUtils.createConfig("scenarios/final/ebersberg/transitInputs");

            config.global().setRandomSeed(1000);

            config.controler().setFirstIteration(0);
            config.controler().setLastIteration(15);
            config.controler().setOutputDirectory("output/ebersberg/withGTFSAndFullFlow");
            config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            config.qsim().setFlowCapFactor(1.);
            config.qsim().setStorageCapFactor(1.);

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





            config.network().setInputFile("combinedRegion.xml");
            config.plans().setInputFile("commuterPopulationTransit.xml");

            config.transit().setVehiclesFile("ebersbergVehicles.xml");
            config.transit().setTransitScheduleFile("ebersbergSchedule.xml");
            config.transit().setTransitModes(Sets.newHashSet(TransportMode.pt));
//            config.qsim().setMainModes(Sets.newHashSet(TransportMode.car,"pt"));
            //create a mode that is teleported:


            StrategyConfigGroup.StrategySettings strategy = new StrategyConfigGroup.StrategySettings();
            strategy.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode);
            strategy.setWeight(0.3);
            config.changeMode().setModes(new String[]{"car", "pt"});
            config.strategy().addStrategySettings(strategy);



            Scenario scenario = ScenarioUtils.loadScenario(config);

            // possibly modify scenario here


            // ---

            Controler controler = new Controler(scenario);

            // possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


            // ---

            controler.run();
            new ConfigWriter(config).write("configTransit.xml");
        }

    }
