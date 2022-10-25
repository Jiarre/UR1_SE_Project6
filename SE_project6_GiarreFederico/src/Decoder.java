import java.util.ArrayList;
import java.util.BitSet;

public class Decoder implements Runnable {
    private BitSet bs;
    private int nedges;
    private int line;
    private int bitnode;
    private int bitweight;
    private Thread t;
    private int nthread;
    private ArrayList<Thread> threads;

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
        g = new Graph(isDirected);
        for(int i = 0; i<nthread;i++){

            Thread t = new Thread (this, ""+i);
            t.start();
            threads.add(t);
        }

        for(Thread t : threads){
            t.join();
        }

    }

    public Graph getGraph() {
        return g;
    }

    public void decodeBitSet() throws InterruptedException {
        int tn = Integer.parseInt(Thread.currentThread().getName());
        int d = (int) Math.floor(nedges/nthread);

        if(tn != nthread-1){
            //System.out.println("THread "+ tn + "if normale");
            for(int i = tn*d; i<(tn+1)*d; i++){
                decodeEdge(bs.get(65+(i*line),65+((i+1)*line)));
            }
        }else{
            //System.out.println("THread "+ tn + "if speciale");
            for(int i = tn*d; i<nedges; i++){
                decodeEdge(bs.get(65+(i*line),65+((i+1)*line)));
            }
        }

    }
    public void decodeEdge(BitSet edge){
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


        Edge tmp = new Edge(node1,node2,weight);
        //System.out.println(tmp);

        g.addEdge(tmp);
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
