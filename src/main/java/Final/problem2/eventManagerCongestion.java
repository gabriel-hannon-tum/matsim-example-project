package Final.problem2;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class eventManagerCongestion {
    public static void main(String[] args) {


        EventsManager manager = EventsUtils.createEventsManager();


//        {
//            Network network = NetworkUtils.createNetwork();
//            new MatsimNetworkReader(network).readFile("output/problem2/base/output_network.xml.gz");
//            congestionHandler congestionHandler = new congestionHandler(network);
//            manager.addHandler(congestionHandler);
//
//            new MatsimEventsReader(manager).readFile("output/problem2/base/output_events.xml.gz");
//            System.out.println("Total delays of over a minute: " + congestionHandler.getDelays());
//            System.out.println("Upper Branch: "+congestionHandler.getUpperPath());
//            System.out.println("Lower Branch: "+congestionHandler.getLowerPath());
//
//        }
//        {
//            Network network = NetworkUtils.createNetwork();
//            new MatsimNetworkReader(network).readFile("output/problem2/base/output_network.xml.gz");
//            congestionHandler congestionHandler = new congestionHandler(network);
//            manager.addHandler(congestionHandler);
//
//            new MatsimEventsReader(manager).readFile("output/problem2/base/ITERS/it.0/0.events.xml.gz");
//            System.out.println("Total delays of over a minute: " + congestionHandler.getDelays());
//            System.out.println("Upper Branch: "+congestionHandler.getUpperPath());
//            System.out.println("Lower Branch: "+congestionHandler.getLowerPath());
//        }


        {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("output/problem2/with8/output_network.xml.gz");
            congestionHandler congestionHandler = new congestionHandler(network);
            manager.addHandler(congestionHandler);

            new MatsimEventsReader(manager).readFile("output/problem2/with8/output_events.xml.gz");
            System.out.println("Total delays of over a minute: " + congestionHandler.getDelays());
            System.out.println("Upper Branch: "+congestionHandler.getUpperPath());
            System.out.println("Lower Branch: "+congestionHandler.getLowerPath());
            System.out.println("Middle Branch: "+congestionHandler.getMiddlePath());

        }
        {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("output/problem2/with8/output_network.xml.gz");
            congestionHandler congestionHandler = new congestionHandler(network);
            manager.addHandler(congestionHandler);

            new MatsimEventsReader(manager).readFile("output/problem2/with8/ITERS/it.0/0.events.xml.gz");
            System.out.println("Total delays of over a minute: " + congestionHandler.getDelays());
            System.out.println("Upper Branch: "+congestionHandler.getUpperPath());
            System.out.println("Lower Branch: "+congestionHandler.getLowerPath());
            System.out.println("Middle Branch: "+congestionHandler.getMiddlePath());
        }


}
}
