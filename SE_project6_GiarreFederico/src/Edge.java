import java.util.Objects;

public class Edge {

    private int n1;
    private int n2;
    private int weight;

    /***
     * Constructor for the Edge object
     * @param n1 first node
     * @param n2 second node
     * @param weight weight of the edge
     */
    public Edge(int n1,int n2,int weight){
        this.n1 = n1;
        this.n2 = n2;
        this.weight = weight;
    }

    /***
     * Utility to print an edge, useful when printing a graph
     * @return Stringed representation of the edge infos
     */
    @Override
    public String toString(){
        return ""+n1+"->"+n2+" weight:" + weight;
    }

    /***
     * Getter for source
     * @return the first node
     */
    public int getN1() {
        return n1;
    }


    /***
     * Getter for destination
     * @return the second node
     */
    public int getN2() {
        return n2;
    }


    /***
     * Getter for weight
     * @return the value of the weight
     */
    public int getWeight() {
        return weight;
    }


    /***
     * Equals override to compare graphs
     * @param o Edge object
     * @return boolean result of the equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return n1 == edge.n1 && n2 == edge.n2 && weight == edge.weight;
    }

    /***
     * Hashing of the edge
     * @return the hash of source, destination and weight combined
     */
    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, weight);
    }
}
