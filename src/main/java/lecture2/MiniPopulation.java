package lecture2;

import com.sun.jdi.connect.Transport;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;

import java.util.ArrayList;
import java.util.Random;

public class MiniPopulation {
    public static final Random rand1 = new Random(1);
    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();
        Population emptyPopulation = PopulationUtils.createPopulation(config);
        PopulationFactory popFactory = emptyPopulation.getFactory();

        //Create people with ids
        int id = 1;
        ArrayList<Person> personList = new ArrayList<>();
        while(id <= 500){
            Person tempPerson = popFactory.createPerson(Id.createPersonId(id));
            emptyPopulation.addPerson(tempPerson);
            personList.add(tempPerson);
            id++;
        }

        Coord home = new Coord(0,0);
        Coord work = new Coord(4000,0);

        for(Person p : personList){
            Plan personPlan = popFactory.createPlan();
            p.addPlan(personPlan);

            int timeAfter7 = rand1.nextInt(3600);
            int timeAfter16 = rand1.nextInt(3600);

            Activity homeAct = popFactory.createActivityFromCoord("home", home);
            homeAct.setEndTime(7*60*60 + timeAfter7);
            personPlan.addActivity(homeAct);

            Leg leg2work = popFactory.createLeg(TransportMode.car);
            personPlan.addLeg(leg2work);

            Activity workAct = popFactory.createActivityFromCoord("work", work);
            workAct.setEndTime(16*60*60 + timeAfter16);
            personPlan.addActivity(workAct);

            Leg leg2home = popFactory.createLeg(TransportMode.car);
            personPlan.addLeg(leg2home);

            Activity endAct = popFactory.createActivityFromCoord("home", home);
            personPlan.addActivity(endAct);


        }


        //add people to pop

        //plan for each person

        //activities, legs for between


        PopulationWriter writer = new PopulationWriter(emptyPopulation);
        writer.write("scenarios/week2/miniPop.xml");

    }
}
