package emissions;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.*;

public class CreateTransitVehicles {

    public static void main(String[] args) {

        Vehicles vehicles = VehicleUtils.createVehiclesContainer();

        VehicleType type = VehicleUtils.createVehicleType(Id.create("ExpressBus", VehicleType.class));

        type.setNetworkMode("bus");
        VehicleCapacity capacity = type.getCapacity();
        capacity.setSeats(30);
        capacity.setStandingRoom(30);

        vehicles.addVehicleType(type);

        for (int i = 8 * 3600; i < 18 * 3600; i += 5 * 60) {
            Id<Vehicle> vehicleId = Id.createVehicleId("departure_" + i);
            Vehicle vehicle = VehicleUtils.createVehicle(vehicleId, type);
            vehicles.addVehicle(vehicle);
        }

        EngineInformation engineInformation = type.getEngineInformation();
        VehicleUtils.setHbefaVehicleCategory(engineInformation, "urban bus");
        VehicleUtils.setHbefaTechnology(engineInformation, "average");
        VehicleUtils.setHbefaSizeClass(engineInformation, "average");
        VehicleUtils.setHbefaEmissionsConcept(engineInformation, "average");

        new MatsimVehicleWriter(vehicles).writeFile("./scenarios/lecture12/expressBusVehicles.xml");
    }
}
