package Final.problem3.drt;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleSpecification;
import org.matsim.contrib.dvrp.fleet.FleetWriter;
import org.matsim.contrib.dvrp.fleet.ImmutableDvrpVehicleSpecification;

import java.util.ArrayList;
import java.util.List;

public class CreateFleet {

        public static void main(String[] args) {
            int amounfOfVehicles = 100;
            int seats = 6;

            //Muenchner str. in middle of ebers is starting list
            Id<Link> linkId = Id.createLinkId("491375330_0");
            List<DvrpVehicleSpecification> vehicles = new ArrayList<>();

            for (int i = 0; i < amounfOfVehicles; i++) {
                DvrpVehicleSpecification v = ImmutableDvrpVehicleSpecification.newBuilder()
                        .id(Id.create("drt_"+ i, DvrpVehicle.class))
                        .startLinkId(linkId)
                        .capacity(seats)
                        .serviceBeginTime(0)
                        .serviceEndTime(24*3600-1)
                        .build();
                vehicles.add(v);
            }
            new FleetWriter(vehicles.stream()).write("scenarios/final/ebersberg/drtFleet.xml");
        }
    }
