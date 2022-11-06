import java.io.File;
import java.io.FileNotFoundException;
import java.util.BitSet;
import java.util.Scanner;

public class Main {

    public static String examplename;
    public static int nthread_encoding;
    public static int nthread_decoding;
    public static void main(String[] args) throws InterruptedException {
        Graph g = new Graph(false);
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter the name of the file to load situated in the Examples folder:");
        examplename= sc.nextLine();
        System.out.print("Enter the number of threads you wish to use to run the encoding:");
        nthread_encoding= sc.nextInt();
        System.out.print("Enter the number of threads you wish to use to run the decoding:");
        nthread_decoding= sc.nextInt();
        System.out.print("Select which mode of the program should be run from:\n1)Verbose (Print to screen every step of the process)\n2)Performance Test (Print to screen time spent and size of the encoding)\n3)Infinite (Run the program in a while loop) ");
        int choice= sc.nextInt();
        System.out.flush();

        try {
            File myObj = new File("Examples/"+examplename);

            Scanner myReader = new Scanner(myObj);
            if(myReader.hasNextLine() && myReader.nextLine().equals("directed")){
                g.setDirected(true);
            }
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String[] edge = data.split(" ");
                int node1 = Integer.parseInt(edge[0]);
                int node2 = Integer.parseInt(edge[1]);
                int weight = Integer.parseInt(edge[2]);


                Edge e = new Edge(node1,node2,weight);
                g.addEdge(e);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        switch(choice){
            case 1:
                start_verbose(g);
                break;
            case 2:
                start_performance_test(g);
                break;
            case 3:
                start_infinite_loop(g);
                break;
            default:
                start_performance_test(g);
                break;

        }



    }
    public static void start_verbose(Graph g) throws InterruptedException {
        BitSet bs;

        Encoder e = new Encoder(g,nthread_encoding);
        bs = e.getBs();
        System.out.println("Finished dumping, obtained bits:");
        System.out.println(e.printBitSet(bs));

        Decoder d = new Decoder(bs,nthread_decoding);
        Graph result = d.getGraph();
        System.out.println("Original Graph");
        g.printGraph();
        System.out.println("Encoded/Decoded Graph");
        result.printGraph();
        if(result.equals(g)){
            System.out.println("\nOriginal and Encoded/Decoded Graph are the same :D");
        }else{
            System.out.println("\nOriginal and Encoded/Decoded Graph are not the same :c");

        }
    }

    public static void start_infinite_loop(Graph g) throws InterruptedException {
        while(true){
            BitSet bs;
            Encoder e = new Encoder(g,nthread_encoding);
            bs = e.getBs();

            Decoder d = new Decoder(bs,nthread_decoding);
            Graph result = d.getGraph();

            if(result.equals(g)){
                System.out.println("\nOriginal and Encoded/Decoded Graph are the same :D");
            }else{
                System.out.println("\nOriginal and Encoded/Decoded Graph are not the same :c");
                return;
            }
        }
    }
    public static void start_performance_test(Graph g) throws InterruptedException {
        float sume=0;
        float sumd=0;
        float avg_encoding=0;
        float avg_decoding=0;
        Graph result = null;
        Encoder e = null;
        for(int i = 1; i<100; i++) {
            BitSet bs;
            long startencoding = System.nanoTime();
            e = new Encoder(g, nthread_encoding);
            long stopencoding = System.nanoTime();

            bs = e.getBs();
            long startdecoding = System.nanoTime();
            Decoder d = new Decoder(bs, nthread_decoding);
            long stopdecoding = System.nanoTime();
            result = d.getGraph();
            sume += ((float)(stopencoding-startencoding)/1000000);
            sumd += ((float)(stopdecoding-startdecoding)/1000000);
        }
        avg_encoding = sume/100;
        avg_decoding = sumd/100;
        if(result.equals(g)){
            System.out.println("\n\nOriginal and Encoded/Decoded Graph are the same :D");
            System.out.println("Performances to encode/decode a Graph with "+g.getEdges().size() +" edges using "+nthread_encoding+" threads to encode and "+nthread_decoding+" threads to decode:");
            System.out.println("Time to encode the graph:" + (avg_encoding) + "ms");
            System.out.println("Time to decode the graph:" + (avg_decoding) + "ms");
            System.out.println("Original Graph successfully encoded in " + (1+32+16+16+(g.getEdges().size()*(2*e.getBitnodes()+e.getBitweights()))) + " bits");
        }else{
            System.out.println("\nOriginal and Encoded/Decoded Graph are not the same :c");

        }

    }

}