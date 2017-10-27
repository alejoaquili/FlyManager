package Model.Graph.AirportGraph.Structures;

import Model.Graph.GraphStructures.Arc;
import Model.Graph.GraphStructures.ArcInterface;
import Model.Graph.GraphStructures.Graph;
import Model.Graph.GraphStructures.Node;

import java.util.*;

public class FlightGraph extends Graph<Airport, Flight> {

    public FlightGraph(List<Comparator<Arc<Airport,Flight>>> comparators) {
        super(comparators);
    }



    public List<Arc<Airport,Flight>> minPath(Airport from, Airport to, ArcInterface<Arc<Airport,Flight>> arcInt,
                                                      Comparator<Arc<Airport,Flight>> cmp, List<Day> days ){

        if(from == null || to == null){
            throw new IllegalArgumentException("Bad input.");
        }

        if(!nodes.containsKey(from) || !nodes.containsKey(to)){
            return null;
        }

        clearMarks();
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        List<Arc<Airport,Flight>> path = new ArrayList<>();

        Node<Airport, Flight> origin = nodes.get(from);
        origin.setVisited(true);

        for(Node<Airport, Flight> n : origin.getAdjacents()){
            Arc<Airport,Flight> r = origin.getTree(n, cmp).first();
            if(r.getData().departureOnDate(days)) {
                pq.offer(new PQNode(n, r.getData().getFlightDuration(), r));
            }
        }

        if(pq.isEmpty()){
            // no flight does match the requested departure day
            return null;
        }

        while(!pq.isEmpty()){

            PQNode<Airport,Flight> aux =  pq.poll();
            if(aux.node.getElement() == to){

                return path;
            }
            if(!aux.node.getVisited()){
                aux.node.setVisited(true);
                path.add(aux.usedArc);
                for(Node<Airport, Flight> n : (Set<Node<Airport, Flight>>) aux.node.getAdjacents()){
                    Arc<Airport,Flight> r = (Arc<Airport, Flight>) aux.node.getTree(n, cmp).first();
                    if(!r.getTarget().getVisited())
                        pq.offer(new Graph.PQNode(r.getTarget(),(arcInt.convert(r)-aux.distance) + aux.distance, r));
                }
            }
        }

        return  path;

    }


    public List<Arc<Airport,Flight>> minTotalTimePath(Airport from, Airport to, ArcInterface<Arc<Airport,Flight>> arcInt,
                                             Comparator<Arc<Airport,Flight>> cmp, List<Day> days ){

        if(from == null || to == null){
            throw new IllegalArgumentException("Bad input.");
        }

        if(!nodes.containsKey(from) || !nodes.containsKey(to)){
            return null;
        }

        clearMarks();
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        List<Arc<Airport,Flight>> path = new ArrayList<>();

        Node<Airport, Flight> origin = nodes.get(from);
        origin.setVisited(true);

        for(Node<Airport, Flight> n : origin.getAdjacents()){
            Arc<Airport,Flight> r = origin.getTree(n, cmp).first();
            if (r.getData().departureOnDate(days)) {
                pq.offer(new PQNode(n, r.getData().getFlightDuration(), r));
                r.getData().setTagCurrentTime(r.getData().getFlightDuration());
            }
        }


        while(!pq.isEmpty()){

            PQNode<Airport,Flight> aux =  pq.poll();
            if(aux.node.getElement() == to){

                return path;
            }
            if(!aux.node.getVisited()) {
                aux.node.setVisited(true);
                path.add(aux.usedArc);
                for (Node<Airport, Flight> n : aux.node.getAdjacents()) {
                    // verfy the node is indeed not visited
                    if(!n.getVisited()) {
                        Arc<Airport,Flight> bestFlightToNode = aux.node.getTree(n, cmp).first();
                        int bestTime = Day.closestTimeWithOffset((int) aux.distance, bestFlightToNode.getData().getWeekTime(),
                                bestFlightToNode.getData().getDepartureTime());
                        // we search for the best time in regar to the arrival time
                        for (Arc<Airport, Flight> r : aux.node.getTree(n, cmp)) {
                            // aux.distance is the current time of the week at which the algorithm expects to be
                            // en criollo , es el momento de la semana en que estas cuando llegas a este nodo
                            int currentArcTime = Day.closestTimeWithOffset((int) aux.distance, r.getData().getWeekTime(),
                                    r.getData().getDepartureTime());

                            if (currentArcTime < bestTime) {
                                bestTime = currentArcTime;
                                bestFlightToNode = r;
                            }
                        }

                        pq.offer(new Graph.PQNode(bestFlightToNode.getTarget(), aux.distance + bestTime +
                                bestFlightToNode.getData().getFlightDuration(), bestFlightToNode));
                    }


                }
            }
        }
        return  path;
    }



}
