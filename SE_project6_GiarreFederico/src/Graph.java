import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Vector;

public class Graph {

    private ArrayList<Edge> edges;
    private HashSet nodes;
    private int maxweight;



    private boolean isDirected;
    public Graph(boolean isDirected){
        this.edges = new ArrayList<>();
        this.nodes = new HashSet();
        this.maxweight = 0;
        this.isDirected = isDirected;


    }
    public synchronized void addEdge(Edge e){
        this.edges.add(e);
        this.nodes.add(e.getN1());
        this.nodes.add(e.getN2());
        if(e.getWeight()>this.maxweight){
            this.maxweight = e.getWeight();
        }

    }
    public void printGraph(){
        System.out.println(edges);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public HashSet getNodes() {
        return nodes;
    }

    public void setNodes(HashSet nodes) {
        this.nodes = nodes;
    }

    public int getMaxweight() {
        return maxweight;
    }

    public void setMaxweight(int maxweight) {
        this.maxweight = maxweight;
    }

    public int isDirected() {
        return isDirected ? 1 : 0;
    }

    public void setDirected(boolean directed) {
        isDirected = directed;
    }

    @Override
    public boolean equals(Object o){
        Graph e = (Graph) o;
        if(this.isDirected != e.isDirected){
            System.out.println("Original and Decoded graph are not directed in the same way");
            return false;
        }
        if(this.maxweight != e.maxweight){
            System.out.println("Original and Decoded graph don't have the same maxweight");
            return false;
        }
        if(this.nodes.size() != e.nodes.size()){
            System.out.println("Original and Decoded graph don't have the size of nodes ["+nodes.size()+"!="+e.nodes.size()+"]");
            return false;
        }

        boolean flag = true;
        if(e.getEdges().size() == edges.size()){
            for(Edge edge : edges){
                if(!e.getEdges().contains(edge)){
                    System.out.println(edge+" is not contained in the decoded graph");
                    flag = false;
                }
            }
        }else{
            flag = false;
        }
        return flag;
    }
}
