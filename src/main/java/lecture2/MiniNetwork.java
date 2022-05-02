package lecture2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MiniNetwork {
    public static void main(String[] args) {
        Network emptyNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = emptyNetwork.getFactory();

        //define coordinates
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

        Link linkAB1 = networkFactory.createLink(Id.createLinkId("linkAB1"), nodeA, nodeB);
        Link linkAB2 = networkFactory.createLink(Id.createLinkId("linkAB2"), nodeB, nodeA);
        suburbanRoads.add(linkAB1);
        suburbanRoads.add(linkAB2);

        Link linkBC1 = networkFactory.createLink(Id.createLinkId("linkBC1"), nodeB, nodeC);
        Link linkBC2 = networkFactory.createLink(Id.createLinkId("linkBC2"), nodeC, nodeB);
        urbanRoads.add(linkBC1);
        urbanRoads.add(linkBC2);



        Link linkBD1 = networkFactory.createLink(Id.createLinkId("linkBD1"), nodeB, nodeD);
        Link linkBD2 = networkFactory.createLink(Id.createLinkId("linkBD2"), nodeD, nodeB);
        urbanRoads.add(linkBD1);
        urbanRoads.add(linkBD2);


        Link linkCE1 = networkFactory.createLink(Id.createLinkId("linkCE1"), nodeC, nodeE);
        Link linkCE2 = networkFactory.createLink(Id.createLinkId("linkCE2"), nodeE, nodeC);
        urbanRoads.add(linkCE1);
        urbanRoads.add(linkCE2);


        Link linkDE1 = networkFactory.createLink(Id.createLinkId("linkDE1"), nodeD, nodeE);
        Link linkDE2 = networkFactory.createLink(Id.createLinkId("linkDE2"), nodeE, nodeD);
        urbanRoads.add(linkDE1);
        urbanRoads.add(linkDE2);

        Link linkEF1 = networkFactory.createLink(Id.createLinkId("linkEF1"), nodeE, nodeF);
        Link linkEF2 = networkFactory.createLink(Id.createLinkId("linkEF2"), nodeF, nodeE);
        suburbanRoads.add(linkEF1);
        suburbanRoads.add(linkEF2);

        allRoads.addAll(suburbanRoads);
        allRoads.addAll(urbanRoads);

        for (Link i : allRoads){
            emptyNetwork.addLink(i);
        }

        for (Link i : suburbanRoads){
            i.setCapacity(500);
            i.setFreespeed(22.22);
        }
        for (Link i : urbanRoads){
            i.setFreespeed(13.88);
            i.setCapacity(250);
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




        NetworkWriter writer = new NetworkWriter(emptyNetwork);
        writer.write("scenarios/week2/miniNetwork.xml");
    }
}
