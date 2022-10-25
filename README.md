# Project 6 Giarrè Federico

## Examples

In the folder **/Examples** you can find a series of example graph, build with the script graph_generator.py (also present inside the folder). Thanks to this, you can generate and test whichever graph you want.

#### On the names of the examples

Examples i've made follow a simple name system, usually with names as "xy_w"

x is the size of the graph in terms of nodes and edges present. The values x can have are:

- s (small)
- m (medium)
- b (big)

y is how much the graph is connected. The values y can have are:

- s (sparse, generally just enough edges to connect all the nodes)
- d (dense, full mesh)

w (if present) indicates that the graph is weighted

#### graph_generator.py

Is a script to generate graphs given a name for the output file, number of nodes, number of edges and a flag to make the graph weighted. Please refer to the help prompt of the script (python3 graph_generator.py -h) for usage

## Program

### Menu

The program will use a prompt to ask for instruction on how to execute. The prompt will ask for three things:

- Name of the file to load, this has to be located inside /Examples;
- Number of threads to use for encoding/decoding;
- Execution mode of the program.

Choosing the latter, the user has 3 choices:

- **Verbose:** Original, encoded and decoded graph are printed, so that it's possible to verify each step of the process
- **Performance:** Data regarding time used to encode/decode the graph and information about the space used is shown to the user
- **Infinite loop:** The program is executed over and over, this was used to debug cases of race condition

### Graph Class

The graph is going to be represented as an ArrayList of edges, these edges know only the first end, the second end and the weight (0 if the graph is not weighted). The graph also have an HashMap of nodes, in order to know what is the number of bits needed to represent them.

#### Why ArrayList and HashMap?

Initially I chose Vector instead of ArrayList in order to have a structure thread safe directly out-of-the-box, at the cost of sacrificing performances.

HashMap is the standart implementation of a classic Set structure in java, but it's not a thread-safe object. 

Since the only method that use both the set of edges and the set of nodes is $Graph.addEdge(Edge )$ and this method caused race-condition related problems, I decided to synchronize the method and use ArrayList instead of Vector (since synchronized methods are executed thread safely, i could benefit from the better performances of ArrayList) 

### Encoding

The encoding has a fixed length header composed by 65 bit. The partition of these 65 bits are the following:

- [0]: Flag to indicate if the graph is directed or not
- [1,32]: Tells the program the total number of edges in the graph
- [33,48]: Tells the program what is the maximum number of bits necessary to describe the nodes
- [49,64]: Tells the program what is the maximum number of bits necessary to describe the weights (if 0, no bits will be used during the encoding for the purpose of weights)

Every other line is dinamically composed of $2* NodeBits + NodeWeight$ in order to describe each edge. In the end the dimension of the encoding should be of $ 65 + nEdges * (2* NodeBits + NodeWeight)$ although due to the implementation via the BitSet API of java, the actual space occupied is equal to the next power of two that could fit the formula above.
This limit only apply to the space occupied, in fact knowing the number of edges from the header it's possible to avoid reading all the BitSet.

### Decoding

The decoding firstly read the header to determine the size of each line, then parses the BitSet in order to compose the new graph. 

