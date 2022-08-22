package Final.problem2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;

import java.util.ArrayList;
import java.util.Random;

public class FinalPop {
    public static final int SIZE = 3600;
    public static final double X_DUMMY2 = 5;

    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        Population emptyPopulation = PopulationUtils.createPopulation(config);
        PopulationFactory popFactory = emptyPopulation.getFactory();

        int id = 1;
        ArrayList<Person> personList = new ArrayList<Person>();
        while(id <= SIZE) {
            Person tempPerson = popFactory.createPerson(Id.createPersonId(id));
            emptyPopulation.addPerson(tempPerson);
            personList.add(tempPerson);
            id++;
        }

        Coord dummy1 = new Coord(0,0);
        Coord dummy2 = new Coord(X_DUMMY2, 0);


        for(Person p : personList){
            Plan personPlan = popFactory.createPlan();
            p.addPlan(personPlan);

            int depSecond = personList.indexOf(p);

            Activity dummyAct = popFactory.createActivityFromCoord("dummy", dummy1);
            dummyAct.setEndTime(11*60*60 + depSecond);
            personPlan.addActivity(dummyAct);

            Leg leg1 = popFactory.createLeg(TransportMode.car);
            personPlan.addLeg(leg1);

            Activity dummyAct2 = popFactory.createActivityFromCoord("dummy", dummy2);
            dummyAct2.setMaximumDurationUndefined();
            personPlan.addActivity(dummyAct2);
        }

        PopulationWriter writer = new PopulationWriter(emptyPopulation);
        writer.write("scenarios/final/finalPop.xml");

    }
}
