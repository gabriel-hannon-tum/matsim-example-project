package org.matsim.pt2matsim.examples.lecture5;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.minibus.routeProvider.PRouteProvider;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetwork;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateSchedule1 {
    private static final String inputFile = "scenarios/week5/miniNetworkPT.xml";

    public static void main(String[] args) {
        TransitScheduleFactory scheduleFactory = new TransitScheduleFactoryImpl();


        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputFile);


        Coord coordA = new Coord(0,0);
        Coord coordF = new Coord(4000,0);

        TransitStopFacility stopA = scheduleFactory.createTransitStopFacility(Id.create("stopA", TransitStopFacility.class), coordA, false);
        TransitStopFacility stopF = scheduleFactory.createTransitStopFacility(Id.create("stopF", TransitStopFacility.class), coordF, false);

        stopA.setLinkId(Id.createLinkId("b_a"));
        stopF.setLinkId(Id.createLinkId("e_f"));

        TransitSchedule schedule = scheduleFactory.createTransitSchedule();
        schedule.addStopFacility(stopA);
        schedule.addStopFacility(stopF);

        TransitLine expressBusLine = scheduleFactory.createTransitLine(Id.create("expressBus", TransitLine.class));
        schedule.addTransitLine(expressBusLine);

        List<Id<Link>> links = new ArrayList<>();
        links.add(Id.create("a_b", Link.class));
        links.add(Id.create("b_c", Link.class));
        links.add(Id.create("c_e", Link.class));
        links.add(Id.create("e_f", Link.class));
        links.add(Id.create("f_e", Link.class));
        links.add(Id.create("e_d", Link.class));
        links.add(Id.create("d_b", Link.class));
        links.add(Id.create("b_a", Link.class));

        NetworkRoute route = RouteUtils.createNetworkRoute(links);


        List<TransitRouteStop> stops = new ArrayList<>();
        double travelTime = computeTravelTime(route, network);

        TransitRouteStop transitRouteStop1 = scheduleFactory.createTransitRouteStop(stopA, 0, 0);
        TransitRouteStop transitRouteStop2 = scheduleFactory.createTransitRouteStop(stopA, travelTime/2, travelTime/2.);
        TransitRouteStop transitRouteStop3 = scheduleFactory.createTransitRouteStop(stopA, travelTime, travelTime);

        stops.add(transitRouteStop1);
        stops.add(transitRouteStop2);
        stops.add(transitRouteStop3);

        TransitRoute expressBusRoute = scheduleFactory.createTransitRoute(Id.create("Express Bus Route", TransitRoute.class), route, stops, "bus");

        for(int i = 8*3600; i <18*3600; i+= 5*60){
            Departure dep = scheduleFactory.createDeparture(Id.create(("departure_" + i), Departure.class),i);
            dep.setVehicleId(Id.createVehicleId("departure_"+i));
            expressBusRoute.addDeparture(dep);
        }

        expressBusLine.addRoute(expressBusRoute);
        //schedule.addTransitLine(expressBusLine);

        new TransitScheduleWriter(schedule).writeFile("output/outputweek5/expressBusSchedule.xml");

    }

    private static double computeTravelTime(NetworkRoute route, Network network) {
        double travelTime = 0;
        for (Id<Link> linkId : route.getLinkIds()) {
            if (linkId.equals(route.getStartLinkId())) {
                break;
            }
            travelTime += NetworkUtils.getFreespeedTravelTime(network.getLinks().get(linkId));
        }
        return travelTime;
    }
}
