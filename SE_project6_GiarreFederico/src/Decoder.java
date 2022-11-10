import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;

public class Decoder implements Runnable {
    private BitSet bs; //store the BitSet
    private int nedges; //store the number of Edges
    private int line; //store the length (in bit) of every line (header excluded)
    private int bitnode; //store the length (in bit) of the representation of a node
    private int bitweight; //store the length (in bit) if the representation of a weight (can be 0)
    private Thread t;
    private int nthread; //number of threads to be used
    private ArrayList<Thread> threads;

    private ArrayList<Edge> results; //Temporary location for results

    private boolean isDirected;

    private Graph g;
    public Decoder(BitSet bs, int n) throws InterruptedException {
        threads = new ArrayList<Thread>();
        this.bs = bs;
        this.nthread = n;
        isDirected = false;
        if(bs.get(0)){
            isDirected = true;
        }
        nedges = convert(bs.get(1,33),32);
        //line = convert(bs.get(33,65),32);
        bitnode = convert(bs.get(33,49),16);
        bitweight = convert(bs.get(49,65),16);
        line = 2*bitnode + bitweight;
        //System.out.println("edges:"+nedges+" line:"+line+" bitnode:"+bitnode+" bitweight:"+bitweight);
        if(bitweight == 0)
            g = new Graph(isDirected,bitnode,bitweight);
        else
            g = new Graph(isDirected,bitnode,bitweight-1);
        results = new ArrayList<>(nedges);
        for(int i=0; i<nedges;i++){
            results.add(null);
        }



    }
    public Graph decode() throws InterruptedException {
        for(int i = 0; i<nthread;i++){

            t = new Thread (this, ""+i);
            t.start();
            threads.add(t);
        }

        for(Thread t : threads){
            t.join();
        }
        g.setEdges(results);
        return g;
    };

    public Graph getGraph() {
        return g;
    }

    public void decodeBitSet() throws InterruptedException {
        int tn = Integer.parseInt(Thread.currentThread().getName());
        int d = (int) Math.floor(nedges/nthread);

        if(tn != nthread-1){
            //System.out.println("THread "+ tn + "if normale");
            for(int i = tn*d; i<(tn+1)*d; i++){
                decodeEdge(bs.get(65+(i*line),65+((i+1)*line)),i);
            }
        }else{
            //System.out.println("THread "+ tn + "if speciale");
            for(int i = tn*d; i<nedges; i++){
                decodeEdge(bs.get(65+(i*line),65+((i+1)*line)),i);
            }
        }

    }
    public void decodeEdge(BitSet edge,int position){
        int node1 = convert(edge.get(0,bitnode),bitnode);
        int node2 = convert(edge.get(bitnode,2*bitnode),bitnode);
        int weight = 0;
        if(bitweight > 1){
            boolean sign = edge.get(2*bitnode);

            if(sign){
                weight = -convert(edge.get(2 * bitnode + 1, 2 * bitnode + bitweight), bitweight-1);
            }else{
                weight = convert(edge.get(2*bitnode+1,2*bitnode+bitweight),bitweight-1);
            }
        }

        results.set(position,new Edge(node1,node2,weight));
    }
    public  int convert(BitSet bits,int pad) {
        int intValue = 0;

        for (int i = pad-1; i >=0; i--) {
            if (bits.get(i)) {
                intValue += Math.pow(2,pad-1-i);
            }
        }
        return intValue;



    }


    @Override
    public void run() {
        try {
            decodeBitSet();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
