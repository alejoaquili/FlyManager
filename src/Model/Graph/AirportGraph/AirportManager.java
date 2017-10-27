package Model.Graph.AirportGraph;


import Model.Graph.AirportGraph.Structures.*;
import Model.Graph.GraphStructures.Arc;
import Model.Graph.GraphStructures.ArcInterface;
import Model.Graph.GraphStructures.Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IllegalFormatException;
import java.util.List;


public class AirportManager {

    private FlightGraph airportMap;

    private Comparator<Arc<Airport,Flight>> cmpFlightDuration = new Comparator<Arc<Airport,Flight>>() {
        @Override
        public int compare(Arc<Airport,Flight> o1, Arc<Airport,Flight> o2) {
            return o1.getData().getFlightDuration() - o2.getData().getFlightDuration();
        }
    };

    private Comparator<Arc<Airport,Flight>> cmpPrecio = new Comparator<Arc<Airport,Flight>>() {
        @Override
        public int compare(Arc<Airport,Flight> o1, Arc<Airport,Flight> o2) {
            return (int) (o1.getData().getPrice() - o2.getData().getPrice());
        }
    };

    public AirportManager() {
        List<Comparator<Arc<Airport,Flight>>> comparators = new ArrayList<>();
        comparators.add(cmpFlightDuration);
        comparators.add(cmpPrecio);
        airportMap = new FlightGraph(comparators);
    }

    public void addAirport(String airportName, double lat, double lng) {
        airportMap.addNode(new Airport(airportName, new Location(lat, lng)));
    }

    public void deleteAirport(String airportName) {
        airportMap.deleteNode(new Airport(airportName));
    }

    public void deleteAirports() {
        airportMap.deleteGraph();
    }

    public void addFlight(String airline, String flightNumber, String[] days, String origin, String target,
                          int departureTime, int flightDuration, double price) {
        try {
            Flight flight = new Flight(airline, flightNumber, Day.getDays(days), departureTime, flightDuration, price);
            airportMap.addArc(flight, airportMap.getNodeElement(new Airport(origin)),
                                airportMap.getNodeElement(new Airport(target)));

        } catch (IllegalFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteFlight(String airline, String flightNumber) {
        airportMap.deleteArc(new Flight(airline, flightNumber));
    }

    public void deleteFlights() {
        airportMap.deleteArcs();
    }


    public static void main(String[] args){
        AirportManager a = new AirportManager();
        a.addAirport("ARG", 0,0);
        a.addAirport("CHI", 1,0);
        a.addAirport("BRA", 2,0);
        a.addAirport("URU", 3,0);
        a.addAirport("PAR", 4,0);

        String[] days = new String[1];
        days[0] = "Lu";


        a.addFlight("san", "1",days,"ARG","BRA",7,2,400 );

        a.addFlight("san", "2",days,"ARG","URU",9,2,900 );

        a.addFlight("san", "3",days,"BRA","URU",14,1,100 );

        Airport from = new Airport("ARG",new Location(0,0));
        Airport to = new Airport("URU",new Location(3,0));
        ArcInterface<Arc<Airport,Flight>> arcint = new ArcInterface<Arc<Airport,Flight>>(){
            public double convert(Arc<Airport,Flight> arc ){
                return (double)arc.getData().getFlightDuration();
            }
        };

        List<Day> ldays = new ArrayList<>();
        ldays.add(Day.getDay(0));

        List<Arc<Airport,Flight>> route = a.airportMap.minPath(from, to , arcint,a.cmpPrecio, ldays);

        for(Arc<Airport,Flight> r : route){
            System.out.println(r.getTarget());
        }
        System.out.println("done");
    }

}
