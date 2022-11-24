import networkx as nx
import random
import sys
import argparse
import os

parser=argparse.ArgumentParser(
    description='''Weighted non-directed graph generator ''')
parser.add_argument('-f','--filename', type=str, default="example", help='Name of the file containing the Graph information (default is example)')
parser.add_argument('-n','--nodes', type=int, default=10, help='Number of nodes contained in the Graph (default is 10)')
parser.add_argument('-e', '--edges',type=int, default=20, help='Number of edges contained in the Graph (default is 20)')
parser.add_argument('-w', '--weights',action='store_true', help='Add this parameter to make the graph weighted',required=False)
parser.add_argument('-d', '--directed',action='store_true', help='Add this parameter to make the graph directed',required=False)

args=parser.parse_args()
print(f"Creating graph directory and file named {args.filename} having {args.nodes} nodes and {args.edges} links")


G = nx.gnm_random_graph(args.nodes,args.edges)
if(args.weights):
    for (u,v,w) in G.edges(data=True):
        w['weight'] = random.randint(-20,20)
else:
    for (u,v,w) in G.edges(data=True):
        w['weight'] = 0


str = args.filename.replace("'", "")


nx.write_edgelist(G, f"{args.filename}", delimiter=' ', data=['weight'])

with open(args.filename, 'r') as original: data = original.read()
if args.directed:
    with open(args.filename, 'w') as modified: modified.write("directed\n" + data)
else:
    with open(args.filename, 'w') as modified: modified.write("notdirected\n" + data)

