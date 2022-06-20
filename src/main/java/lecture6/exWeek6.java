package lecture6;

import com.google.inject.internal.asm.$Type;
import org.checkerframework.checker.units.qual.C;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.replanning.ReplanningUtils;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.CollectionUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author nagel
 */
public class exWeek6 {

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


        NetworkUtils.writeNetwork(network, "scenarios/week6/test/test_network.xml");


        Config config;
        if (args == null || args.length == 0 || args[0] == null) {
            config = ConfigUtils.loadConfig("scenarios/week6/test/config.xml");
        } else {
            config = ConfigUtils.loadConfig(args);
        }
        config.controler().setOutputDirectory("output/outputweek6");
        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);


        // possibly modify config here

        PlanCalcScoreConfigGroup.ModeParams modeParamsCar
                = config.planCalcScore().getOrCreateModeParams(TransportMode.car);
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

        PopulationUtils.writePopulation(population, "scenarios/week6/test/test_plans.xml");


        config.network().setInputFile("test_network.xml");
        config.plans().setInputFile("test_plans.xml");

        Scenario scenario = ScenarioUtils.loadScenario(config);

        // possibly modify scenario here


        // ---

        Controler controler = new Controler(scenario);

        // possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


        // ---

        controler.run();
    }

}