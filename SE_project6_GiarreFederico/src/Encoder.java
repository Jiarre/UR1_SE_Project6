import java.util.ArrayList;
import java.util.BitSet;

public class Encoder implements Runnable {

    private Graph g;
    private int bitedges;
    private int bitnodes;
    private int bitweights;
    private int bitline;
    private int nthread;

    private BitSet bs;

    private ArrayList<Thread> threads;

    public BitSet getBs() {
        return bs;
    }

    /***
     * Constructor, prepares the Encoder to encode
     * @param g Graph to encode
     * @param nthread Number of threads to use for the encoding
     * @throws InterruptedException
     */
    public Encoder(Graph g, int nthread) throws InterruptedException {
        this.g = g;
        bitedges = roundup(g.getEdges().size()); //get the power of two that cover the number of edges
        bitnodes = g.getMaxnodelength(); //get the power of two that cover the representation of the nodes
        bitweights = g.getMaxweightlength()+1; //get the power of two that cover the representation of the weights (and their sign)
        if(bitweights == 1){ //If there is no weight, just use 0
            bitweights--;
        }
        bitline = (2*bitnodes + bitweights);
        this.nthread = nthread;
        bs = new BitSet();
        threads = new ArrayList<Thread>();




    }

    /***
     * Setup the header of the encoding and start the threads for the proper encoding
     * @return Resulting BitSet
     * @throws InterruptedException
     */
    public BitSet encode() throws InterruptedException {
        //Create header bitset
        BitSet syncedges = new BitSet(); //Number of edges
        BitSet syncnodes = new BitSet();//Bits used for each node
        BitSet syncweight = new BitSet();//Bits used for each weight
        BitSet syncdir = new BitSet();//Directed / NonDirected graph?




        syncinsert(g.getEdges().size(),syncedges,32); //Maximum of 2^32 edges
        syncinsert(bitnodes,syncnodes,16);//Maximum of 2^16 nodes in the graph
        syncinsert(bitweights,syncweight,16);//Maximum of 2^15 as weight (1 bit for sign)
        syncinsert(g.isDirected(),syncdir,1);//1 bit flag for the directed/nondirected

        //Compose the order flag->#edges->#bitpernode->#bitperweight with the correct padding
        concatenateBitSet(syncnodes,syncweight,16);
        concatenateBitSet(syncedges,syncnodes,32);
        concatenateBitSet(syncdir,syncedges,1);
        concatenateBitSet(bs,syncdir,0);
        //Start threads
        for(int i = 0; i<nthread;i++){
            Thread t = new Thread (this, ""+i);
            t.start();
            threads.add(t);
        }
        //Wait for all threads to finish
        for(Thread t : threads){
            t.join();
        }
        return bs;
    }

    /***
     * Function to be executed by threads, split the edges encoding among threads
     */
    public void dump(){
        /*
        Create division of values between threads, every thread will take a slice of edges
        Threads should not overlap.
        */

        int tn = Integer.parseInt(Thread.currentThread().getName());
        int lines = g.getEdges().size();
        int d = (int) Math.floor(lines/nthread);

        if(tn != nthread-1){
            for(int i = tn*d; i<(tn+1)*d; i++){
                //Append next line at position header (65) + i*line, so no line is overlapped in the Bitset
                concatenateBitSet(bs, convertLine(g.getEdges().get(i)),65 + (i*bitline));
            }
        }else{
            for(int i = tn*d; i<lines; i++){
                //Same of latter
                concatenateBitSet(bs, convertLine(g.getEdges().get(i)),65 + (i*bitline));
                }
        }
    }


    /***
     *  Utility function to find the next power of two able to describe the int given
      * @param k: int to be represented
     * @return the number of bits needed to represent k
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
     * Insert an int inside the BitSet, caring about the padding and total amount of bit to use
     * @param number: int to be converted in binary
     * @param bs: BitSet in which to add the number
     * @param total: Total number of bit used to represent number in binary
     */
    public void syncinsert(int number, BitSet bs,int total){
        String bits = String.format("%"+total+"s", Integer.toBinaryString(number)).replace(" ", "0");
        for (int i = 0; i < total; i++) {
            if (bits.charAt(i) == '1') {
                bs.set(i);
            } else {
                bs.clear(i);
            }
        }

    }

    /***
     * Function to append one bitset to another
     * @param a: first bitset, bitset b will be added to a
     * @param b: second bitset, to be appended to a
     * @param pad: pad to be respected in order to maintain the static padding
     */
    public synchronized void concatenateBitSet(BitSet a, BitSet b,int pad){
        for(int i = 0; i<b.length(); i++){

            a.set(pad + i,b.get(i));
        }
    }

    /**
     * Convert an edge in binary form
     * @param e: Edge to be converted
     * @return BitSet created from the edge
     */
    public BitSet convertLine(Edge e){
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
        return line;

    }

    /***
     * Utility function to print a BitSet in human-readable format (newlines for each edge and clear header)
     * @param bs: BitSet to be printed
     * @return BitSet to be printed
     */
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

    /***
     * Run function to start threads
     */
    @Override
    public void run() {
        dump();
    }

    /***
     * Getter for bitnodes
     * @return bitnodes value
     */
    public int getBitnodes() {
        return bitnodes;
    }

    /***
     * Getter for bitweights
     * @return bitweights value
     */
    public int getBitweights() {
        return bitweights;
    }

}
