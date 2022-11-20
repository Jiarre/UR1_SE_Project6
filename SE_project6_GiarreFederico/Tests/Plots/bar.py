import numpy as np
import seaborn as sns 
import pandas as pd
import os
import matplotlib.pyplot as plt 
sns.set_theme(style="whitegrid")
for filename in os.listdir("../"):
    f = os.path.join("../", filename)
    # checking if it is a file
    if os.path.isfile(f):
        
        df = pd.read_csv(f)

        sns.factorplot(x='number', y='time', hue='type', data=df, kind='bar',estimator=np.median)
        plt.legend(loc='best')
        plt.ylabel("Time to perform (ms)")
        plt.xlabel("Number of threads used")
        plt.grid(True,color="#D3D3D3")
        plt.legend([],[], frameon=False)
        plt.savefig(f"{filename[:-4]}_bar.png",bbox_inches='tight')
        #plt.show()