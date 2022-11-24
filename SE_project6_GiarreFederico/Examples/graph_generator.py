import networkx as nx
import random
import sys
import argparse
import os

parser=argparse.ArgumentParser(
    description='''Weighted non-directed graph generator ''')
parser.add_argument('-f','--filename', type=str, default="example", help='Name of the file containing the Graph information (default is example)')
parser.add_argument('-n','--nodes', type=int, default=10, help='Number of nodes contained in the Graph')
parser.add_argument('-e', '--edges',type=int, default=20, help='Number of edges contained in the Graph')
parser.add_argument('-w', '--weights',action='store_true', help='Add this parameter to make the graph weighted',required=False)
args=parser.parse_args()
print(f"Creating graph directory and file named {args.filename}.txt having {args.nodes} nodes and {args.edges} links")
G = nx.dense_gnm_random_graph(args.nodes,args.edges)
if(args.weights):
    for (u,v,w) in G.edges(data=True):
        w['weight'] = random.randint(-20,20)
else:
    for (u,v,w) in G.edges(data=True):
        w['weight'] = 0


str = args.filename.replace("'", "")

nx.write_edgelist(G, f"{args.filename}.txt", delimiter=' ', data=['weight'])
