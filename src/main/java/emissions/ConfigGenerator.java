package emissions;

import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.MatsimServices;

public class ConfigGenerator {
    private static final String networkFile = "scenarios/final/ebersberg/emissionsNetwork.xml";

    private static final String averageFleetWarmEmissionFactorsFile = "scenarios/final/ebersberg/hot_engine.txt";
    private static final String averageFleetColdEmissionFactorsFile = "scenarios/final/ebersberg/cold_engine.txt";

    private static final String configFilePath ="scenarios/final/ebersberg/config_emissions.xml";

    public static void main(String[] args) {

        Config config = new Config();
        config.addCoreModules();
        MatsimServices controler = new Controler(config);

        config.vehicles().setVehiclesFile("scenarios/emissions/cars.xml");

        NetworkConfigGroup ncg = controler.getConfig().network();
        ncg.setInputFile("scenarios/emissions/emissionNet.xml");

        EmissionsConfigGroup ecg = new EmissionsConfigGroup();
        controler.getConfig().addModule(ecg);

        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setHbefaVehicleDescriptionSource( EmissionsConfigGroup.HbefaVehicleDescriptionSource.asEngineInformationAttributes );
        ecg.setAverageWarmEmissionFactorsFile(averageFleetWarmEmissionFactorsFile);
        ecg.setAverageColdEmissionFactorsFile(averageFleetColdEmissionFactorsFile);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setWritingEmissionsEvents(true);
    }
}
