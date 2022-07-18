package emissions;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.*;

public class CreateVehicles {

    public static void main(String[] args) {
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new PopulationReader(scenario).readFile("scenarios/week2/miniPop.xml");

        Vehicles vehicles = VehicleUtils.createVehiclesContainer();
        VehicleType type = VehicleUtils.createVehicleType(Id.create("passenger car", VehicleType.class));

        type.setNetworkMode("car");
        vehicles.addVehicleType(type);

        for (Person person: scenario.getPopulation().getPersons().values()){
            Vehicle vehicle = VehicleUtils.createVehicle(Id.createVehicleId(person.getId()), type);
            vehicles.addVehicle(vehicle);
        }
        EngineInformation engineInformation = type.getEngineInformation();
        VehicleUtils.setHbefaVehicleCategory(engineInformation, "PASSENGER_CAR");
        VehicleUtils.setHbefaTechnology(engineInformation, "average");
        VehicleUtils.setHbefaSizeClass(engineInformation, "average");
        VehicleUtils.setHbefaEmissionsConcept(engineInformation, "average");

        new MatsimVehicleWriter(vehicles).writeFile("scenarios/emissions/cars.xml");

    }

}
