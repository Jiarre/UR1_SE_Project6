import java.util.Objects;

public class Edge {

    private int n1;
    private int n2;
    private int weight;

    public Edge(int n1,int n2,int weight){
        this.n1 = n1;
        this.n2 = n2;
        this.weight = weight;
    }
    @Override
    public String toString(){
        return ""+n1+"->"+n2+" weight:" + weight;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return n1 == edge.n1 && n2 == edge.n2 && weight == edge.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, weight);
    }
}
