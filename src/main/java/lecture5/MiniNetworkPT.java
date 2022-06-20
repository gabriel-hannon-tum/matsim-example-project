package lecture5;

import com.google.common.collect.Sets;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MiniNetworkPT {
//make constants

    private static final double URBANFREE = 13.88;
    private static final double RURALFREE = 22.22;
    private static final int URBANCAP = 250;
    private static final int RURALCAP = 500;
    //private static final ArrayList<Coord> coordList;

    public static void main(String[] args) {
        Network emptyNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = emptyNetwork.getFactory();

        //define coordinates
        //ArrayList<Coord> coordList = new ArrayList<>();
        Coord CoordA = CoordUtils.createCoord(0,0);
        Coord CoordB = CoordUtils.createCoord(1000,0);
        Coord CoordC = CoordUtils.createCoord(2000,500);
        Coord CoordD = CoordUtils.createCoord(2000,-500);
        Coord CoordE = CoordUtils.createCoord(3000,0);
        Coord CoordF = CoordUtils.createCoord(4000,0);



        //define nodes and add to node list
        Node nodeA = networkFactory.createNode(Id.createNodeId("NodeA"), CoordA);
        Node nodeB = networkFactory.createNode(Id.createNodeId("NodeB"), CoordB);
        Node nodeC = networkFactory.createNode(Id.createNodeId("NodeC"), CoordC);
        Node nodeD = networkFactory.createNode(Id.createNodeId("NodeD"), CoordD);
        Node nodeE = networkFactory.createNode(Id.createNodeId("NodeE"), CoordE);
        Node nodeF = networkFactory.createNode(Id.createNodeId("NodeF"), CoordF);

        //add nodes to Network
        Node[] nodeArray = new Node[]{nodeA, nodeB, nodeC, nodeD, nodeE, nodeF};

        for(Node i : nodeArray){
            emptyNetwork.addNode(i);
        }

        ArrayList<Link> suburbanRoads = new ArrayList<>();
        ArrayList<Link> urbanRoads = new ArrayList<>();
        ArrayList<Link> allRoads = new ArrayList<>();

        Link linkAB1 = networkFactory.createLink(Id.createLinkId("a_b"), nodeA, nodeB);
        Link linkAB2 = networkFactory.createLink(Id.createLinkId("b_a"), nodeB, nodeA);
        suburbanRoads.add(linkAB1);
        suburbanRoads.add(linkAB2);

        Link linkBC1 = networkFactory.createLink(Id.createLinkId("b_c"), nodeB, nodeC);
        Link linkBC2 = networkFactory.createLink(Id.createLinkId("c_b"), nodeC, nodeB);
        urbanRoads.add(linkBC1);
        urbanRoads.add(linkBC2);



        Link linkBD1 = networkFactory.createLink(Id.createLinkId("b_d"), nodeB, nodeD);
        Link linkBD2 = networkFactory.createLink(Id.createLinkId("d_b"), nodeD, nodeB);
        urbanRoads.add(linkBD1);
        urbanRoads.add(linkBD2);


        Link linkCE1 = networkFactory.createLink(Id.createLinkId("c_e"), nodeC, nodeE);
        Link linkCE2 = networkFactory.createLink(Id.createLinkId("e_c"), nodeE, nodeC);
        urbanRoads.add(linkCE1);
        urbanRoads.add(linkCE2);


        Link linkDE1 = networkFactory.createLink(Id.createLinkId("d_e"), nodeD, nodeE);
        Link linkDE2 = networkFactory.createLink(Id.createLinkId("e_d"), nodeE, nodeD);
        urbanRoads.add(linkDE1);
        urbanRoads.add(linkDE2);

        Link linkEF1 = networkFactory.createLink(Id.createLinkId("e_f"), nodeE, nodeF);
        Link linkEF2 = networkFactory.createLink(Id.createLinkId("f_e"), nodeF, nodeE);
        suburbanRoads.add(linkEF1);
        suburbanRoads.add(linkEF2);

        allRoads.addAll(suburbanRoads);
        allRoads.addAll(urbanRoads);

        for (Link i : allRoads){
            emptyNetwork.addLink(i);
        }

        for (Link i : suburbanRoads){
            i.setCapacity(RURALCAP);
            i.setFreespeed(RURALFREE);
        }
        for (Link i : urbanRoads){
            i.setFreespeed(URBANFREE);
            i.setCapacity(URBANCAP);
        }


        for (Link i : allRoads){
            double xLocFrom = i.getFromNode().getCoord().getX();
            double yLocFrom = i.getFromNode().getCoord().getY();
            double xLocTo = i.getToNode().getCoord().getX();
            double yLocTo = i.getToNode().getCoord().getY();

            double length = Math.sqrt(
                    Math.pow((xLocFrom - xLocTo), 2) + Math.pow((yLocFrom - yLocTo), 2));
            i.setLength(length);
        }

        setLinkAttributes(allRoads);


        NetworkWriter writer = new NetworkWriter(emptyNetwork);
        writer.write("scenarios/week5/miniNetworkPT.xml");
    }

    private static void setLinkAttributes(ArrayList<Link> allRoads) {
        for(Link i: allRoads){
            i.setAllowedModes(Sets.newHashSet("bus", TransportMode.car));
        }
    }
}
