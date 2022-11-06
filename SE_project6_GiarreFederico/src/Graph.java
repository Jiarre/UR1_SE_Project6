import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Graph {

    private ArrayList<Edge> edges;
    //private HashSet nodes;
    private int maxweight;

    private ConcurrentHashMap<Integer,String> nodesMap;
    private Set<Integer> nodesSet;

    private int maxnodelenght;



    private boolean isDirected;
    public Graph(boolean isDirected){
        this.edges = new ArrayList<>();
        //this.nodes = new HashSet();
        this.maxweight = 0;
        this.isDirected = isDirected;
        //this.nodesMap = new ConcurrentHashMap<>();
        //this.nodesSet = nodesMap.keySet("SET-ENTRY");
        this.maxnodelenght = 0;


    }
    public Graph(boolean isDirected,int nedges){
        this.edges = new ArrayList<>(nedges);
        for (int i = 0; i < nedges; i++) {
            edges.add(null);
        }

        //this.nodes = new HashSet();
        this.maxweight = 0;
        this.isDirected = isDirected;
        //this.nodesMap = new ConcurrentHashMap<>();
        //this.nodesSet = nodesMap.keySet("SET-ENTRY");
        this.maxnodelenght = 0;


    }
    public void addEdge(Edge e){
        this.edges.add(e);
        //this.nodesSet.add(e.getN1());
        //this.nodesSet.add(e.getN2());
        if(roundup(e.getN1())>this.maxnodelenght){
            this.maxnodelenght = roundup(e.getN1());
        }
        if(roundup(e.getN2())>this.maxnodelenght){
            this.maxnodelenght = roundup(e.getN2());
        }
        if(e.getWeight()>this.maxweight){
            this.maxweight = e.getWeight();
        }

    }
    public void addEdgePostion(Edge e,int position){
        this.edges.set(position,e);

        synchronized (this){
            if(roundup(e.getN1())>this.maxnodelenght){
                this.maxnodelenght = roundup(e.getN1());
            }
            if(roundup(e.getN2())>this.maxnodelenght){
                this.maxnodelenght = roundup(e.getN2());
            }
            if(e.getWeight()>this.maxweight){
                this.maxweight = e.getWeight();
            }
        }

    }
    public int roundup(int k){
        int power = 1;
        int count = 0;
        while(power <= k){
            power*=2;
            count++;
        }
        return count;
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

    public Set<Integer> getNodes() {
        return nodesSet;
    }

    public void setNodes(Set<Integer> nodes) {
        this.nodesSet = nodes;
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
        if(this.maxnodelenght != e.maxnodelenght){
            System.out.println("Original and Decoded graph don't have the size of nodes ["+nodesSet.size()+"!="+e.nodesSet.size()+"]");
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

    public int getMaxnodelenght() {
        return maxnodelenght;
    }
}
