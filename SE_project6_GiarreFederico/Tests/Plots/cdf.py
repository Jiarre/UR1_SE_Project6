import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os
import time

sns.set_theme(style="whitegrid")
count = 0
for filename in os.listdir("../"):
    f = os.path.join("../", filename)
    # checking if it is a file
    
    if os.path.isfile(f):
        plt.figure(count)
        count+=1
        df=[]
        df = pd.read_csv(f)
        df_enc = df[df.type == "encoder"]
        sns.ecdfplot(data=df_enc, x="time", hue="number",palette=sns.color_palette("viridis", as_cmap=True))
        plt.legend(loc='best')
        plt.ylabel("Probability")
        plt.xlabel("Time used for encoding (ms)")
        mean = df_enc.mean()
        plt.xlim(0,2*mean["time"])
        plt.grid(True,color="#D3D3D3")
        plt.legend(loc="best",labels=["5 threads",'4 threads', '3 threads', '2 threads',"1 thread"])
        plt.title(f"Encoding {filename[:-4]}")
        plt.savefig(f"{filename[:-4]}_cdf_encoding.png",bbox_inches='tight')


        plt.figure(count)
        count+=1
        df2=[]
        df2 = pd.read_csv(f)
        df_dec = df2[df2.type == "decoder"]
        sns.ecdfplot(data=df_dec, x="time", hue="number",palette=sns.color_palette("viridis", as_cmap=True))
        plt.legend(loc='best')
        plt.ylabel("Probability")
        plt.xlabel("Time used for decoding (ms)")
        mean = df_enc.mean()
        plt.xlim(0,2*mean["time"])
        plt.grid(True,color="#D3D3D3")
        plt.legend(loc="best",labels=["5 threads",'4 threads', '3 threads', '2 threads',"1 thread"])
        plt.title(f"Decoding {filename[:-4]}")
        plt.savefig(f"{filename[:-4]}_cdf_decoding.png",bbox_inches='tight')

