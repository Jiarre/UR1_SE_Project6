# Project 6 Giarrè Federico

## Examples

In the folder **/Examples** you can find a series of example graph, built with the script graph_generator.py (also present inside the folder). Thanks to the latter, you can generate and test whichever graph you want.

#### On the names of the examples

Examples i've made follow a simple name system, usually with names as "xy_w"

x is the size of the graph in terms of nodes present. The values x can have are:

- s (small)
- m (medium)
- b (big)

y is how much the graph is connected. The values y can have are:

- s (sparse, generally just enough edges to connect all the nodes)
- d (dense, full mesh or very near)

w (if present) indicates that the graph is weighted

Some of them are directed, some are not.

#### graph_generator.py

Is a script to generate graphs given a name for the output file, number of nodes, number of edges and a flag to make the graph weighted. Please refer to the help prompt of the script (python3 graph_generator.py -h) for usage

## Tests
Results of the performance tests of the program are saved the Tests directory, to be processed using Matplotlib and Seaborn to derive some interesting plots (available in the Plots subdirectory).

### Plots
For each example tested it's possible to create two plots:

- Bar plot: Useful to see the mean of the performances of encoding/decoding with respect of a changing number of threads used
- eCDF: See the probability distribution of performances among different number of threads

## Program

### Menu

The program will use a prompt to ask for instruction on how to execute. The prompt will ask for three things:

- Name of the file to load, this has to be located inside /Examples;
- Number of threads to use for encoding;
- Number of threads to use for decoding;
- Execution mode of the program.


Choosing the latter, the user has 3 choices:

- **Verbose:** Original, encoded and decoded graph are printed, so that it's possible to verify each step of the process
- **Performance:** Data regarding time used to encode/decode the graph and information about the space used is shown to the user. It is done by averaging 1000 iterations of the program to be sure a fair performance comparison. Also saves the results of each iteration in the Tests directory
- **Infinite loop:** The program is executed over and over, this was used to debug cases of race condition

P.S. Due to the nature of Verbose mode, executing big graphs could lead to freezes and or halting of the stoud buffer. Other modes work as expected

### Graph Class

The graph is going to be represented as an ArrayList of edges, these edges know only the first end, the second end and the weight (0 if the graph is not weighted). The graph also store the maximum amount of bit used to represent its nodes and weights.

#### Why ArrayList?

Initially I chose Vector instead of ArrayList in order to have a structure thread safe directly out-of-the-box, at the cost of sacrificing performances.
Instead, i used ArrayList while taking care of making threads access different memory-area in order to split the work in a thread safe environment: the job of one thread will never bother the others

### Encoding

The encoding has a fixed length header composed by 65 bit. The partition of these 65 bits are the following:

- [0]: Flag to indicate if the graph is directed or not
- [1,32]: Tells the program the total number of edges in the graph
- [33,48]: Tells the program what is the maximum number of bits necessary to describe the nodes
- [49,64]: Tells the program what is the maximum number of bits necessary to describe the weights (if 0, no bits will be used during the encoding for the purpose of weights)

Every other line is dinamically composed of $2* NodeBits + NodeWeight$ in order to describe each edge. In the end the dimension of the encoding should be of $\Omega(65 + nEdges * (2* NodeBits + NodeWeight))$ although due to the implementation via the BitSet API of java, the actual space occupied is equal to the next power of two that could fit the formula above.
This limit only apply to the space occupied, in fact knowing the number of edges from the header it's possible to avoid reading all the BitSet.

### Decoding

The decoding firstly read the header to determine the size of each line, then parses the BitSet in order to compose the new graph. 

