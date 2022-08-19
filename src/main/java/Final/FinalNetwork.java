package Final;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Collections;

public class FinalNetwork {
    public static final int LANE_CAP = 1800;
    public static final double STUDENT_ID = 0.3750576;
    public static final int FREESPEED = 12;

    public static void main(String[] args) {
        Network emptyNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = emptyNetwork.getFactory();


        double xB =  1250+STUDENT_ID;
        double xC = xB + 1250+STUDENT_ID;
        double xD = xC + ((6000+STUDENT_ID) * Math.cos(Math.PI/4));
        double yD = ((6000+STUDENT_ID) * Math.sin(Math.PI/4));
        double xE = xC + ((13500+STUDENT_ID) * Math.cos(Math.PI/4));
        double yE = -((13500+STUDENT_ID) * Math.sin(Math.PI/4));
        double xF = xD + ((13500+STUDENT_ID) * Math.cos(Math.PI/4));
        double xG = xF + 1250 + STUDENT_ID;

        Coord coordA = CoordUtils.createCoord(0,0);
        Coord coordB = CoordUtils.createCoord(xB, 0);
        Coord coordC = CoordUtils.createCoord(xC, 0);
        Coord coordD = CoordUtils.createCoord(xD, yD);
        Coord coordE = CoordUtils.createCoord(xE, yE);
        Coord coordF = CoordUtils.createCoord(xF, 0);
        Coord coordG = CoordUtils.createCoord(xG, 0);

        Node nodeA = networkFactory.createNode(Id.createNodeId("NodeA"), coordA);
        Node nodeB = networkFactory.createNode(Id.createNodeId("NodeB"), coordB);
        Node nodeC = networkFactory.createNode(Id.createNodeId("NodeC"), coordC);
        Node nodeD = networkFactory.createNode(Id.createNodeId("NodeD"), coordD);
        Node nodeF = networkFactory.createNode(Id.createNodeId("NodeF"), coordE);
        Node nodeE = networkFactory.createNode(Id.createNodeId("NodeE"), coordF);
        Node nodeG = networkFactory.createNode(Id.createNodeId("NodeG"), coordG);
        Node[] nodeArray = new Node[]{nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG};

        for(Node i : nodeArray){
            emptyNetwork.addNode(i);
        }

        ArrayList<Link> suburbanRoads = new ArrayList<>();
        ArrayList<Link> urbanRoads = new ArrayList<>();
        ArrayList<Link> allRoads = new ArrayList<>();

        Link link1 = networkFactory.createLink(Id.createLinkId("link1"), nodeA, nodeB);
        Link link2 = networkFactory.createLink(Id.createLinkId("link2"), nodeB, nodeC);
        Link link3 = networkFactory.createLink(Id.createLinkId("link3"), nodeC, nodeD);
        Link link4 = networkFactory.createLink(Id.createLinkId("link4"), nodeD, nodeF);
        Link link5 = networkFactory.createLink(Id.createLinkId("link5"), nodeC, nodeE);
        Link link6 = networkFactory.createLink(Id.createLinkId("link6"), nodeE, nodeF);
        Link link7 = networkFactory.createLink(Id.createLinkId("link7"), nodeF, nodeG);

        allRoads.add(link1);
        allRoads.add(link2);
        suburbanRoads.add(link1);
        suburbanRoads.add(link2);
        allRoads.add(link3);
        allRoads.add(link4);
        allRoads.add(link5);
        allRoads.add(link6);
        urbanRoads.add(link3);
        urbanRoads.add(link4);
        urbanRoads.add(link5);
        urbanRoads.add(link6);
        allRoads.add(link7);
        suburbanRoads.add(link7);


        for(Link i : allRoads){
            emptyNetwork.addLink(i);
            i.setFreespeed(FREESPEED);
            setLength(i);
            i.setAllowedModes(Collections.singleton("car"));
        }

        for(Link i : urbanRoads){
            i.setNumberOfLanes(1);
            i.setCapacity(1800);
        }
        for(Link i : suburbanRoads){
            i.setNumberOfLanes(2);
            i.setCapacity(3600);
        }


        NetworkWriter writer = new NetworkWriter(emptyNetwork);
        writer.write("scenarios/final/miniNetwork.xml");

    }

    private static void setLength(Link i) {
        double xLocFrom = i.getFromNode().getCoord().getX();
        double yLocFrom = i.getFromNode().getCoord().getY();
        double xLocTo = i.getToNode().getCoord().getX();
        double yLocTo = i.getToNode().getCoord().getY();

        double length = Math.sqrt(
                Math.pow((xLocFrom - xLocTo), 2) + Math.pow((yLocFrom - yLocTo), 2));
        i.setLength(length);
    }

}
