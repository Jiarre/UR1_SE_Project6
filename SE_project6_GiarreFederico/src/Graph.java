import java.util.*;

public class Graph {

    private ArrayList<Edge> edges;
    //private HashSet nodes;
    private int maxweightlength;

    private int maxnodelength;

    private boolean isDirected;

    /***
     * Base constructor
     */
    public Graph(){
        this.edges = new ArrayList<>();
        this.maxweightlength = 0;
        this.isDirected = false;
        this.maxnodelength = 0;


    }

    /***
     * Constructor useful during the decoding process
     * @param isDirected flag to set if the graph is directed or not
     * @param maxnodelength flag to set the number of bit used by each node
     * @param maxweightlength flag to set the number of bit used by the weight
     */
    public Graph(boolean isDirected, int maxnodelength, int maxweightlength){
        this.maxweightlength = maxweightlength;
        this.isDirected = isDirected;
        this.maxnodelength = maxnodelength;
    }

    /***
     * Allow to add one Edge object to the Graph
     * @param e Edge object to be added
     */
    public void addEdge(Edge e){
        this.edges.add(e);
        if(roundup(e.getN1())>this.maxnodelength){
            this.maxnodelength = roundup(e.getN1());
        }
        if(roundup(e.getN2())>this.maxnodelength){
            this.maxnodelength = roundup(e.getN2());
        }
        if(roundup(e.getWeight())>this.maxweightlength){
            this.maxweightlength = roundup(e.getWeight());
        }

    }

    /***
     * Utility function to find the next power of two that fits the int k
     * @param k int to be fitted in binary representation
     * @return the number of bit to be used for representing k
     */
    public int roundup(int k){
        int power = 1;
        int count = 0;
        while(power <= k){
            power*=2;
            count++;
        }
        return count;
    }

    /***
     * Utility to print the edges of the Graph
     */
    public void printGraph(){
        System.out.println(edges);
    }

    /***
     * Edges getter
     * @return the list of edges composing the graph
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /***
     * Edges setter
     * @param edges list of edges to be set
     */
    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }


    /***
     * maxweightlength getter
     * @return the value of maxweightlength
     */
    public int getMaxweightlength() {
        return maxweightlength;
    }

    /***
     * Function to get if the Graph is directed, but as 0 or 1
     * @return int representation of the boolean value of isDirected
     */
    public int isDirected() {
        return isDirected ? 1 : 0;
    }

    public void setDirected(boolean directed) {
        isDirected = directed;
    }

    /***
     * Ovveride of equal function
     * @param o Graph to be compared
     * @return boolean result of the comparison
     */
    @Override
    public boolean equals(Object o){
        Graph e = (Graph) o;
        if(this.isDirected != e.isDirected){
            System.out.println("Original and Decoded graph are not directed in the same way");
            return false;
        }
        if(this.maxweightlength != e.maxweightlength){
            System.out.println("Original and Decoded graph don't have the same maxweightlenght [Decoded:"+this.maxweightlength +" Original: "+e.maxweightlength +"]");
            return false;
        }
        if(this.maxnodelength != e.maxnodelength){
            System.out.println("Original and Decoded graph don't have the same number of nodes");
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

    /***
     * maxnodelength getter
     * @return maxnodelength;
     */
    public int getMaxnodelength() {
        return maxnodelength;
    }

}
