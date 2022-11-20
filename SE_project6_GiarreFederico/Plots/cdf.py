import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os


sns.set_theme(style="whitegrid")
for filename in os.listdir("../Tests"):
    f = os.path.join("../Tests", filename)
    # checking if it is a file
    if os.path.isfile(f):
        
        df = pd.read_csv(f)
        df_enc = df[df.type == "encoder"]
        sns.ecdfplot(data=df_enc, x="time", hue="number",palette=sns.color_palette("viridis", as_cmap=True))
        plt.legend(loc='best')
        plt.ylabel("Time to perform (ms)")
        plt.xlabel("Number of threads used")
        plt.xlim(0,20)
        plt.grid(True,color="#D3D3D3")
        plt.legend(loc="best",labels=['4 threads', '3 threads', '2 threads',"1 thread"])
        plt.savefig(f"{filename}_cdf_encoding.png",bbox_inches='tight')
        plt.title(f"Encoding {filename}")

        df = pd.read_csv(f)
        df_enc = df[df.type == "decoder"]
        sns.ecdfplot(data=df_enc, x="time", hue="number",palette=sns.color_palette("viridis", as_cmap=True))
        plt.legend(loc='best')
        plt.ylabel("Time to perform (ms)")
        plt.xlabel("Number of threads used")
        plt.xlim(0,20)
        plt.grid(True,color="#D3D3D3")
        plt.legend(loc="best",labels=['4 threads', '3 threads', '2 threads',"1 thread"])
        plt.title(f"Decoding {filename}")
        plt.savefig(f"{filename}_cdf_decoding.png",bbox_inches='tight')

