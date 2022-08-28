package Final.problem3; // Adjust for your project structure

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.Map;

/**
 * This EventHandler implementation counts the travel time of
 * all agents and provides the average travel time per
 * agent.
 * Actually, handling Departures and Arrivals should be sufficient for this (may 2014)
 * @author dgrether
 *
 */
public class congestionHandler implements LinkEnterEventHandler,
        LinkLeaveEventHandler, PersonDepartureEventHandler{

    private Map<Id<Vehicle>,Double> earliestLinkExitTime = new HashMap<>() ;
    private Network network;
    private int delays = 0;
    private int tenDelays = 0;

    public congestionHandler(Network network ) {
        this.network = network ;
    }

    @Override
    public void reset(int iteration) {
        this.earliestLinkExitTime.clear();
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        Link link = network.getLinks().get( event.getLinkId() ) ;
        double linkTravelTime =  link.getLength() / link.getFreespeed( event.getTime() );
        this.earliestLinkExitTime.put( event.getVehicleId(), event.getTime() + linkTravelTime ) ;
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        if(this.earliestLinkExitTime.containsKey(event.getVehicleId())) {
            double excessTravelTime = event.getTime() - this.earliestLinkExitTime.get(event.getVehicleId());
            if(excessTravelTime > 60){
                //System.out.println("excess travel time: " + excessTravelTime);
                delays += 1;
            }
            if(excessTravelTime > 600){
                tenDelays += 1;
            }
        }

    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
        Id<Vehicle> vehId = Id.create( event.getPersonId(), Vehicle.class ) ; // unfortunately necessary since vehicle departures are not uniformly registered
        this.earliestLinkExitTime.put( vehId, event.getTime() ) ;
    }
    public int getDelays(){
        return delays;
    }
    public int getTenDelays(){return tenDelays;}
}