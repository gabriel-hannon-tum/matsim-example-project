package Final.problem3;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class NetworkPrep {


        public static void main(String[] args) {
            Network network = NetworkUtils.createNetwork();
            MatsimNetworkReader reader = new MatsimNetworkReader(network);
            reader.readFile("scenarios/final/ebersberg/combined_region.xml");


            new NetworkWriter(network).write("scenarios/final/ebersberg/compiled_network.xml");
        }
    }

