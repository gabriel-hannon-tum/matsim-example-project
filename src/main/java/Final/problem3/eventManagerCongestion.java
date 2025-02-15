package Final.problem3;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class eventManagerCongestion {
    public static void main(String[] args) {


        EventsManager manager = EventsUtils.createEventsManager();


        {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("output/ebersberg/base/output_network.xml.gz");
            congestionHandler congestionHandler = new congestionHandler(network);
            manager.addHandler(congestionHandler);

            new MatsimEventsReader(manager).readFile("output/ebersberg/base/output_events.xml.gz");
            System.out.println("Total delays of over a minute: " + congestionHandler.getDelays());
            System.out.println("Total delays of over 10 minutes: " + congestionHandler.getTenDelays());

        }

        {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("output/ebersberg/drt/output_network.xml.gz");
            congestionHandler congestionHandler = new congestionHandler(network);
            manager.addHandler(congestionHandler);

            new MatsimEventsReader(manager).readFile("output/ebersberg/drt/output_events.xml.gz");
            System.out.println("Total delays of over a minute with PT: " + congestionHandler.getDelays());
            System.out.println("Total delays of over 10 minutes: " + congestionHandler.getTenDelays());
        }

        {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("output/ebersberg/drt/output_network.xml.gz");
            congestionHandler congestionHandler = new congestionHandler(network);
            manager.addHandler(congestionHandler);

            new MatsimEventsReader(manager).readFile("output/ebersberg/DRT/ITERS/it.0/0.events.xml.gz");
            System.out.println("Total delays of over a minute with PT: " + congestionHandler.getDelays());
            System.out.println("Total delays of over 10 minutes: " + congestionHandler.getTenDelays());
        }
}
}
