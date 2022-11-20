import java.io.*;
import java.util.BitSet;
import java.util.Scanner;

/***
 * @author Federico Giarrè
 */
public class Main {

    public static int nthread_encoding; //number of threads to use for the encoding
    public static int nthread_decoding; //number of threads to use for the decoding

    public static String examplename; //name of the example used
    public static void main(String[] args) throws InterruptedException, IOException {
        Graph g = new Graph(); //Initialize a Graph
        //Menu prompt
        graphParser(g); //parse the graph from file
        menu(g);
    }

    /***
     * Ask for an example to load and update g consequently
     * @param g Graph to fill with edges
     */
    public static void graphParser(Graph g){
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter the name of the file to load situated in the Examples folder:");
        examplename= sc.nextLine();
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
    }

    /***
     * Menu for the operation. Ask for the number of thread to use and calls the selected execution mode
     * @param g Graph to encode/decode
     * @throws InterruptedException
     */
    public static void menu(Graph g) throws InterruptedException, IOException {
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter the number of threads you wish to use to run the encoding:");
        nthread_encoding = sc.nextInt();
        System.out.print("Enter the number of threads you wish to use to run the decoding:");
        nthread_decoding = sc.nextInt();
        System.out.print("Select which mode of the program should be run from:\n1)Verbose (Print to screen every step of the process)\n2)Performance Test (Print to screen time spent and size of the encoding)\n3)Infinite (Run the program in a while loop) ");
        int choice = sc.nextInt();
        System.out.flush();

        //Menu dispatcher
        switch (choice) {
            case 1:
                start_verbose(g); //Calls for an execution in which every step is printed
                break;
            case 2:
                start_performance_test(g); //Calls for an execution in which the performance are tested
                break;
            case 3:
                start_infinite_loop(g); //Calls for an infinite loop of executions
                break;
            default:
                start_performance_test(g);
                break;

        }
    }

    /***
     * Execute the encode/decoding printing also the entire bit representation and list of decoded edges
     * @param g Graph to encode/decode
     * @throws InterruptedException
     */
    public static void start_verbose(Graph g) throws InterruptedException {
        BitSet bs;

        Encoder e = new Encoder(g,nthread_encoding); //Generate the encoder
        bs = e.encode(); //Get the encoded BitSet
        //Print the encoded BitSet
        System.out.println("Finished dumping, obtained bits:");
        System.out.println(e.printBitSet(bs));

        Decoder d = new Decoder(bs,nthread_decoding); //Generate the decoder
        Graph result = d.decode();
        //Print original and decoded graph
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

    /***
     * Start an infinite loop of encoding/decoding
     * @param g Graph to encode/decode
     * @throws InterruptedException
     */
    public static void start_infinite_loop(Graph g) throws InterruptedException {
        while(true){
            //Encoding
            BitSet bs;
            Encoder e = new Encoder(g,nthread_encoding);
            bs = e.encode();
            //Decoding
            Decoder d = new Decoder(bs,nthread_decoding);
            Graph result = d.decode();

            if(result.equals(g)){
                System.out.println("\nOriginal and Encoded/Decoded Graph are the same :D");
            }else{
                System.out.println("\nOriginal and Encoded/Decoded Graph are not the same :c");
                return;
            }
        }
    }

    /***
     * Start an iteration of encoding/decoding, printing performance data such as avg speed of encoding, decoding and Omega(bit used for encoding)
     * @param g Graph to encode/decode
     * @throws InterruptedException
     */
    public static void start_performance_test(Graph g) throws InterruptedException, IOException {

        float sume=0;
        float sumd=0;
        float avg_encoding=0;
        float avg_decoding=0;
        Graph result = null;
        Encoder e = null;
        File test=new File("Tests/"+examplename+".csv");
        FileWriter writer = null;
        if (!test.exists()){
            writer = new FileWriter(test);
            String str = "number,type,time\n";
            writer.write(str);
        }else{
            writer = new FileWriter(test,true);
        }


        //Execute 100 the program, in order to get an avg (Singular execution seems to have better result on 1 thread)
        for(int i = 1; i<1000; i++) {
            BitSet bs;
            e = new Encoder(g, nthread_encoding);
            long startencoding = System.nanoTime();
            bs = e.encode();
            long stopencoding = System.nanoTime();
            writer.write(nthread_encoding+",encoder,"+((float)(stopencoding-startencoding)/1000000)+"\n");

            Decoder d = new Decoder(bs, nthread_decoding);
            long startdecoding = System.nanoTime();
            result = d.decode();
            long stopdecoding = System.nanoTime();
            writer.write(nthread_decoding+",decoder,"+((float)(stopdecoding-startdecoding)/1000000)+"\n");

            //writer.write(nthread_encoding+","+nthread_decoding+","+((float)(stopencoding-startencoding)/1000000)+","+((float)(stopdecoding-startdecoding)/1000000)+"\n");
            sume += ((float)(stopencoding-startencoding)/1000000);
            sumd += ((float)(stopdecoding-startdecoding)/1000000);
        }
        writer.close();
        avg_encoding = sume/1000;
        avg_decoding = sumd/1000;
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