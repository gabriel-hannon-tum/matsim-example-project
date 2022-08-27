package Final.problem3;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PopGenerator {
    //Paths to input and output files and column name of zone id
    private static final String COMMUTER_FILE = "scenarios/final/ebersberg/inputs/georef-germany-kreis/keep_track.csv";
    private static final String ZONE_FILE = "scenarios/final/ebersberg/inputs/georef-germany-kreis/georef-germany-kreis-millesime.shp";
    private static final String ZONE_ID = "krs_code";
    private static final String OUTPUT_FILE = "./scenarios/final/ebersberg/commuterPopulation.xml";
    private static final double SCALE_FACTOR = 0.1;
    private static final double MODE_SPLIT_CAR = 0.9;
    private static final Random rand1 = new Random(1);
    //Object to convert CRS
    private static final CoordinateTransformation ct = TransformationFactory.
            getCoordinateTransformation(TransformationFactory.WGS84, TransformationFactory.DHDN_GK4);
    //Data containers for MATSim objects and the geometry of zones
    private static final Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
    private static Map<String, Geometry> shapeMap;
    public static void main(String[] args) {

        shapeMap = readShapeFile(ZONE_FILE, ZONE_ID);
        //Create the commuter flow by OD relations
        readCommuterFile();

        //Write out the generated commuter flow
        PopulationWriter pw = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
        pw.write(OUTPUT_FILE);

    }

    private static void readCommuterFile() {
        String line = "";
        String splitBy = ",";
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(COMMUTER_FILE));
            while ((line = br.readLine()) != null)
            //returns a Boolean value
            {
                String[] row = line.split(splitBy);
                //use comma as separator
                int count = (int) (Integer.parseInt(row[4])*SCALE_FACTOR);
                createOD(count, row[0],row[2],row[5]);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }


    private static void createOD(int commuter, String origin, String destination, String odPrefix) {
        Geometry home = shapeMap.get(origin);
        Geometry work = shapeMap.get(destination);

        for (int i = 1; i <= commuter; i++) {
            String mode;

            if(rand1.nextDouble() < MODE_SPLIT_CAR){
                mode = "car";
            }
            else{
                mode = "teleTransit";
            }

            Coord homeCoord = drawRandomPointFromGeometry(home);


            Coord workCoord = drawRandomPointFromGeometry(work);

            createOneCommuter(i, homeCoord, workCoord, mode, odPrefix);
        }
    }



    private static void createOneCommuter(int i, Coord homeCoord, Coord workCoord, String mode, String odPrefix) {
        double departureTimeVariance = 2 * Math.random() * 60 * 60;
        double durationTimeVariance = Math.pow(-1,(int)Math.round(Math.random())) * 2 * Math.random() * 60 * 60;


        Id<Person> personId = Id.createPersonId(odPrefix + "_" + i);
        Person person = scenario.getPopulation().getFactory().createPerson(personId);
        scenario.getPopulation().addPerson(person);

        Plan plan = scenario.getPopulation().getFactory().createPlan();
        person.addPlan(plan);

        Activity home = scenario.getPopulation().getFactory().createActivityFromCoord("home", homeCoord);
        home.setEndTime(7 * 60 * 60 + departureTimeVariance);
        plan.addActivity(home);

        Leg legToWork = scenario.getPopulation().getFactory().createLeg(mode);

        plan.addLeg(legToWork);

        Activity work = scenario.getPopulation().getFactory().createActivityFromCoord("work", workCoord);
        work.setMaximumDuration(8 * 60 * 60 + durationTimeVariance);
        plan.addActivity(work);

        Leg legToHome = scenario.getPopulation().getFactory().createLeg(mode);
        plan.addLeg(legToHome);

        Activity endAtHome = scenario.getPopulation().getFactory().createActivityFromCoord("home", homeCoord);
        plan.addActivity(endAtHome);
    }

    private static Coord drawRandomPointFromGeometry(Geometry g) {
        Random rmd = MatsimRandom.getLocalInstance();
        Point p;
        double x;
        double y;
        do {
            x = g.getEnvelopeInternal().getMinX()
                    + rmd.nextDouble() * (g.getEnvelopeInternal().getMaxX() - g.getEnvelopeInternal().getMinX());
            y = g.getEnvelopeInternal().getMinY()
                    + rmd.nextDouble() * (g.getEnvelopeInternal().getMaxY() - g.getEnvelopeInternal().getMinY());
            p = MGC.xy2Point(x, y);
        } while (g.contains(p));
        return new Coord(p.getX(), p.getY());
    }


    public static Map<String, Geometry> readShapeFile(String filename, String attrString) {
        Map<String, Geometry> shapeMap = new HashMap<>();
        for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader wktReader = new WKTReader(geometryFactory);
            Geometry geometry;
            try {
                geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
                shapeMap.put(ft.getAttribute(attrString).toString(), geometry);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return shapeMap;
    }
}
