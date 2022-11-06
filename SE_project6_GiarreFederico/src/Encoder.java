import java.util.ArrayList;
import java.util.BitSet;

public class Encoder implements Runnable {

    private Graph g;
    private int bitedges;
    private int bitnodes;
    private int bitweights;
    private int bitline;
    private int nthread;

    private int count;

    private BitSet bs;

    private ArrayList<Thread> threads;


    public BitSet getBs() {
        return bs;
    }

    public Encoder(Graph g, int nthread) throws InterruptedException {
        this.g = g;
        bitedges = roundup(g.getEdges().size());
        //bitnodes = roundup(g.getNodes().size());
        bitnodes = g.getMaxnodelenght();
        bitweights = g.getMaxweight()+1;
        if(bitweights == 1){
            bitweights--;
        }
        bitline = (2*bitnodes + bitweights);
        this.nthread = nthread;
        bs = new BitSet();
        count = 0;
        threads = new ArrayList<Thread>();

        BitSet syncedges = new BitSet();
        BitSet syncnodes = new BitSet();
        BitSet syncweight = new BitSet();
        BitSet synclines = new BitSet();
        BitSet syncdir = new BitSet();




        syncinsert(g.getEdges().size(),syncedges,32);
        syncinsert(bitnodes,syncnodes,16);
        syncinsert(bitweights,syncweight,16);
        syncinsert(g.isDirected(),syncdir,1);
        concatenateBitSet(syncnodes,syncweight,16);
        concatenateBitSet(syncedges,syncnodes,32);
        concatenateBitSet(syncdir,syncedges,1);
        concatenateBitSet(bs,syncdir,0);

        //System.out.println("edges:"+g.getEdges().size()+" line:"+bitline+" bitnode:"+bitnodes+" bitweight:"+bitweights);
        for(int i = 0; i<nthread;i++){

            Thread t = new Thread (this, ""+i);
            t.start();
            threads.add(t);
        }

        for(Thread t : threads){
            t.join();
        }

        //chiedi se è meglio fare stringona o sommare bitsets ogni volta

    }


    public void dump(){
        int tn = Integer.parseInt(Thread.currentThread().getName());
        int lines = g.getEdges().size();
        int d = (int) Math.floor(lines/nthread);

        if(tn != nthread-1){
            //System.out.println("THread "+ tn + "if normale");
            for(int i = tn*d; i<(tn+1)*d; i++){
                BitSet line = concatenateLine(g.getEdges().get(i));
                 concatenateBitSet(bs,line,65 + (i*bitline));            }
        }else{
            //System.out.println("THread "+ tn + "if speciale");
            for(int i = tn*d; i<lines; i++){
                concatenateBitSet(bs,concatenateLine(g.getEdges().get(i)),65 + (i*bitline));            }
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
    public void syncinsert(int number, BitSet bs,int total){


        String bits = String.format("%"+total+"s", Integer.toBinaryString(number)).replace(" ", "0");

        for (int i = 0; i < total; i++) {
            if (bits.charAt(i) == '1') {

                bs.set(i);
            } else {
                bs.clear(i);
            }
        }
        //bs.set(bits.length(),15,false);
    }


    public synchronized void concatenateBitSet(BitSet a, BitSet b,int pad){
        for(int i = 0; i<b.length(); i++){

            a.set(pad + i,b.get(i));
        }
    }
    public BitSet concatenateLine(Edge e){
        int n1 = e.getN1();
        int n2 = e.getN2();
        int weight = e.getWeight();
        //System.out.println(e);
        BitSet line = new BitSet();
        String n1bits = String.format("%"+bitnodes+"s", Integer.toBinaryString(n1)).replace(" ", "0");
        String n2bits = String.format("%"+bitnodes+"s", Integer.toBinaryString(n2)).replace(" ", "0");


        for (int i = 0; i < n1bits.length(); i++) {
            if (n1bits.charAt(i) == '1') {
                line.set(i);
            } else {
                line.clear(i);
            }
        }

        for (int i = 0; i < n2bits.length(); i++) {
            if (n2bits.charAt(i) == '1') {
                line.set(bitnodes+i);
            } else {
                line.clear(bitnodes+i);
            }
        }
        if(bitweights>1) {
            String weights = String.format("%" + (bitweights - 1) + "s", Integer.toBinaryString(Math.abs(weight))).replace(" ", "0");

            for (int i = 0; i < weights.length(); i++) {
                if (weights.charAt(i) == '1') {
                    line.set((2 * bitnodes) + i + 1);
                } else {
                    line.clear((2 * bitnodes) + i + 1);
                }

            }
            if(weight<0){
                line.set((2*bitnodes));
            }
            else {
                line.clear((2*bitnodes));
            }
        }

        //System.out.println(printBitSet(line));
        return line;

    }

    public  String printBitSet(BitSet bs){
        String res = "";
        for(int i=0;i<bs.size();i++){
            int flag = bs.get(i) ? 1 : 0;
            res+=""+flag;
            if(i==0||i==32 || i==48 || i==64  || i>64 && (i-64)%bitline==0){
                res+="\n";
            }
        }
        return res;
    }

    @Override
    public void run() {
        dump();
    }

    public int getBitedges() {
        return bitedges;
    }

    public int getBitnodes() {
        return bitnodes;
    }

    public int getBitweights() {
        return bitweights;
    }

    public int getBitline() {
        return bitline;
    }
}
