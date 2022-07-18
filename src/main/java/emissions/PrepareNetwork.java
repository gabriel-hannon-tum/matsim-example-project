package emissions;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class PrepareNetwork {

    public static final String HBEFA_ROAD_TYPE = "hbefa_road_type";

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("scenarios/week5/miniNetworkPT.xml");

        for(Link link: network.getLinks().values()){
            link.getAttributes().putAttribute(HBEFA_ROAD_TYPE, "URB/Local/50");
        }
        new NetworkWriter(network).write("scenarios/emissions/emissionNet.xml");
    }
}
