package Final.problem2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.Collections;

public class FinalNetwork {
    public static final int LANE_CAP = 1800;
    public static final double STUDENT_ID = 0.3750576;
    public static final int FREESPEED = 12;

    public static void main(String[] args) {
        Network emptyNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = emptyNetwork.getFactory();


        Coord coordA = CoordUtils.createCoord(0,0);
        Coord coordB = CoordUtils.createCoord(1, 0);
        Coord coordC = CoordUtils.createCoord(2, 0);
        Coord coordD = CoordUtils.createCoord(3, 1);
        Coord coordE = CoordUtils.createCoord(3, -1);
        Coord coordF = CoordUtils.createCoord(4, 0);
        Coord coordG = CoordUtils.createCoord(5, 0);


        Node nodeA = networkFactory.createNode(Id.createNodeId("NodeA"), coordA);
        Node nodeB = networkFactory.createNode(Id.createNodeId("NodeB"), coordB);
        Node nodeC = networkFactory.createNode(Id.createNodeId("NodeC"), coordC);
        Node nodeD = networkFactory.createNode(Id.createNodeId("NodeD"), coordD);
        Node nodeE  = networkFactory.createNode(Id.createNodeId("NodeE"), coordE);
        Node nodeF = networkFactory.createNode(Id.createNodeId("NodeF"), coordF);
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
        link1.setLength(1250+STUDENT_ID);
        link2.setLength(1250+STUDENT_ID);
        suburbanRoads.add(link1);
        suburbanRoads.add(link2);
        allRoads.add(link3);
        allRoads.add(link4);
        allRoads.add(link5);
        allRoads.add(link6);
        link3.setLength(6000+STUDENT_ID);
        link6.setLength(6000+STUDENT_ID);
        link4.setLength(13500+STUDENT_ID);
        link5.setLength(13500+STUDENT_ID);
        urbanRoads.add(link3);
        urbanRoads.add(link4);
        urbanRoads.add(link5);
        urbanRoads.add(link6);
        allRoads.add(link7);
        link7.setLength(1250+STUDENT_ID);
        suburbanRoads.add(link7);


        for(Link i : allRoads){
            emptyNetwork.addLink(i);
            i.setFreespeed(FREESPEED);
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
//        Link link8 = networkFactory.createLink(Id.createLinkId("link8"), nodeD, nodeE);
//        link8.setLength(6000+STUDENT_ID);
//        emptyNetwork.addLink(link8);
//        link8.setFreespeed(FREESPEED);
//        link8.setAllowedModes(Collections.singleton("car"));
//        link8.setNumberOfLanes(1);
//        link8.setCapacity(1800);



        NetworkWriter writer = new NetworkWriter(emptyNetwork);
        writer.write("scenarios/final/finalNetwork.xml");

    }


}
