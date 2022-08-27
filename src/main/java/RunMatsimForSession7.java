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

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 */
public class RunMatsimForSession7 {

    public static void main(String[] args) {

        //Create a network for testing

        final Network network = NetworkUtils.createNetwork();
        final Node muc = NetworkUtils.createNode(Id.createNodeId("muc"), new Coord(0, 0));
        network.addNode(muc);
        final Node muc2 = NetworkUtils.createNode(Id.createNodeId("muc2"), new Coord(-500, 0));
        network.addNode(muc2);
        final Node airport = NetworkUtils.createNode(Id.createNodeId("airport"), new Coord(10000, 0));
        network.addNode(airport);
        final Node airport2 = NetworkUtils.createNode(Id.createNodeId("airport2"), new Coord(10500, 0));
        network.addNode(airport2);
        final Node garching = NetworkUtils.createNode(Id.createNodeId("garching"), new Coord(5000, 3000));
        network.addNode(garching);

        network.addLink(NetworkUtils.createLink(Id.createLinkId("access_muc"),
                muc2, muc, network, 500, 120 / 3.6, 1000, 1));
        network.addLink(NetworkUtils.createLink(Id.createLinkId("access_muc_return"),
                muc, muc2, network, 500, 120 / 3.6, 2000, 2));

        network.addLink(NetworkUtils.createLink(Id.createLinkId("local"),
                muc, airport, network, 10000, 40 / 3.6, 1000, 1));
        network.addLink(NetworkUtils.createLink(Id.createLinkId("motorway_1"),
                muc, garching, network, 7000, 120 / 3.6, 2000, 2));
        network.addLink(NetworkUtils.createLink(Id.createLinkId("motorway_2"),
                garching, airport, network, 7000, 120 / 3.6, 2000, 2));

        network.addLink(NetworkUtils.createLink(Id.createLinkId("access_airport"),
                airport, airport2, network, 500, 120 / 3.6, 1000, 1));
        network.addLink(NetworkUtils.createLink(Id.createLinkId("access_airport_return"),
                airport2, airport, network, 500, 120 / 3.6, 1000, 1));

        network.addLink(NetworkUtils.createLink(Id.createLinkId("return"),
                airport, muc, network, 7000, 120 / 3.6, 2000, 2));


        NetworkUtils.writeNetwork(network, "scenarios/test/test_network.xml");


        Config config;
        config = ConfigUtils.createConfig("scenarios/equil/");

        config.global().setCoordinateSystem("Atlantis");
        config.global().setRandomSeed(1111);

        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(10);
        config.controler().setOutputDirectory("output/modeChoice/");
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);


        {
            PlanCalcScoreConfigGroup.ActivityParams act = new PlanCalcScoreConfigGroup.ActivityParams();
            act.setActivityType("h");
            act.setTypicalDuration(12 * 60 * 60);
            config.planCalcScore().addActivityParams(act);
        }

        {
            PlanCalcScoreConfigGroup.ActivityParams act = new PlanCalcScoreConfigGroup.ActivityParams();
            act.setActivityType("w");
            act.setTypicalDuration(8 * 60 * 60);
            act.setOpeningTime(7*60*60);
            act.setClosingTime(18*60*60);
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
            strategySettings.setWeight(0.1);
            strategySettings.setStrategyName("ReRoute");
            config.strategy().addStrategySettings(strategySettings);
        }

        // possibly modify config here

        //PlanCalcScoreConfigGroup.ModeParams modeParamsCar
        //     = config.planCalcScore().getOrCreateModeParams(TransportMode.car);


        //modeParamsCar.setMarginalUtilityOfTraveling(0);
        //modeParamsCar.setMarginalUtilityOfDistance(-100);
        //create plans for testing


        int numberOfAgents = 100;

        final Population population = PopulationUtils.createPopulation(config);
        final PopulationFactory factory = population.getFactory();

        for (int i = 0; i < numberOfAgents; i++) {
            final Person person = factory.createPerson(Id.createPersonId(i));
            population.addPerson(person);

            final Plan plan = factory.createPlan();
            person.addPlan(plan);
            final Activity home = factory.createActivityFromCoord("h", new Coord(-500, 1));
            home.setEndTime(8 * 60 * 60 + i * 10);
            plan.addActivity(home);

            plan.addLeg(factory.createLeg(TransportMode.car));

            final Activity work = factory.createActivityFromCoord("w", new Coord(10500, 1));
            work.setEndTime(18 * 60 * 60 + i * 10);
            plan.addActivity(work);

            plan.addLeg(factory.createLeg(TransportMode.car));

            final Activity home2 = factory.createActivityFromCoord("h", new Coord(-1, 1));
            plan.addActivity(home2);

        }

        PopulationUtils.writePopulation(population, "./scenarios/test/test_plans.xml");


        config.network().setInputFile("../test/test_network.xml");
        config.plans().setInputFile("../test/test_plans.xml");

        //create a mode that is teleported:

        PlanCalcScoreConfigGroup.ModeParams modeParamsTrain
                = config.planCalcScore().getOrCreateModeParams("ice");
        config.planCalcScore().addModeParams(modeParamsTrain);


        PlansCalcRouteConfigGroup.ModeRoutingParams train = new PlansCalcRouteConfigGroup.ModeRoutingParams("ice");
        //train.setTeleportedModeFreespeedFactor(30.);
        train.setTeleportedModeSpeed(50.);
        train.setBeelineDistanceFactor(1.);
        config.plansCalcRoute().addModeRoutingParams(train);

        PlansCalcRouteConfigGroup.ModeRoutingParams walk = new PlansCalcRouteConfigGroup.ModeRoutingParams("walk");
        //train.setTeleportedModeFreespeedFactor(30.);
        walk.setTeleportedModeSpeed(4/3.6);
        walk.setBeelineDistanceFactor(1.3);
        config.plansCalcRoute().addModeRoutingParams(walk);


        StrategyConfigGroup.StrategySettings strategy = new StrategyConfigGroup.StrategySettings();
        strategy.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode);
        strategy.setWeight(0.4);
        config.changeMode().setModes(new String[]{"car", "ice"});
        config.strategy().addStrategySettings(strategy);



        Scenario scenario = ScenarioUtils.loadScenario(config);

        // possibly modify scenario here


        // ---

        Controler controler = new Controler(scenario);

        // possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


        // ---

        controler.run();
        new ConfigWriter(config).write("scenarios/exampleConfig.xml");
    }

}