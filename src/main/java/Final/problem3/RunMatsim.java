package Final.problem3;/* *********************************************************************** *
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

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Arrays;

/**
 * @author nagel
 */
public class RunMatsim {

    public static void main(String[] args) {


        Config config;
        config = ConfigUtils.createConfig("scenarios/final/ebersberg");

        config.global().setRandomSeed(1111);

        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(75);
        config.controler().setOutputDirectory("output/ebersberg/base");
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

        config.qsim().setFlowCapFactor(0.1);
        config.qsim().setStorageCapFactor(0.3);
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
        config.plans().setInputFile("commuterPopulation.xml");

        //create a mode that is teleported:

        PlanCalcScoreConfigGroup.ModeParams modeParamsTrain
                = config.planCalcScore().getOrCreateModeParams("teleTransit");
        config.planCalcScore().addModeParams(modeParamsTrain);


        PlansCalcRouteConfigGroup.ModeRoutingParams train = new PlansCalcRouteConfigGroup.ModeRoutingParams("teleTransit");
        //train.setTeleportedModeFreespeedFactor(30.);
        train.setTeleportedModeFreespeedFactor(2.5);
        config.plansCalcRoute().addModeRoutingParams(train);


        PlansCalcRouteConfigGroup.ModeRoutingParams walk = new PlansCalcRouteConfigGroup.ModeRoutingParams("walk");
        //train.setTeleportedModeFreespeedFactor(30.);
        walk.setTeleportedModeSpeed(4/3.6);
        walk.setBeelineDistanceFactor(1.3);
        config.plansCalcRoute().addModeRoutingParams(walk);


        StrategyConfigGroup.StrategySettings strategy = new StrategyConfigGroup.StrategySettings();
        strategy.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode);
        strategy.setWeight(0.4);
        config.changeMode().setModes(new String[]{"car", "teleTransit"});
        config.strategy().addStrategySettings(strategy);



        Scenario scenario = ScenarioUtils.loadScenario(config);

        // possibly modify scenario here


        // ---

        Controler controler = new Controler(scenario);

        // possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


        // ---

        controler.run();
        new ConfigWriter(config).write("scenarios/final/ebersberg/config.xml");
    }

}