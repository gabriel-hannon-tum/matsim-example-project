package Final.problem2.base;
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
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 *
 */
public class RunMatsim {

    public static void main(String[] args) {

        Config config;
        config = ConfigUtils.createConfig("scenarios/final/problem2");

        config.global().setRandomSeed(1000);
        config.qsim().setStartTime(0);
        config.qsim().setEndTime(24*60*60-1);

        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(75);
        config.controler().setOutputDirectory("output/problem2/base");
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);


        {
            PlanCalcScoreConfigGroup.ActivityParams act = new PlanCalcScoreConfigGroup.ActivityParams();
            act.setActivityType("dummy");
            act.setTypicalDuration(11 * 60 * 60);
            config.planCalcScore().addActivityParams(act);
        }
        config.strategy().setFractionOfIterationsToDisableInnovation(.9);
        config.strategy().addParam("ModuleProbability_1", "0.85");
        config.strategy().addParam("Module_1", "ChangeExpBeta");
        config.strategy().addParam("ModuleProbability_2", "0.33750576");
        config.strategy().addParam("Module_2", "ReRoute");

        config.controler().setWriteEventsInterval(25);

        config.planCalcScore().setBrainExpBeta(2.);





        config.network().setInputFile("baseNetwork.xml");
        config.plans().setInputFile("finalPop.xml");






        Scenario scenario = ScenarioUtils.loadScenario(config);

        // possibly modify scenario here


        // ---

        Controler controler = new Controler(scenario);

        // possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


        // ---

        controler.run();
//        new ConfigWriter(config).write("scenarios/final/problem2/config2.xml");
    }

}
