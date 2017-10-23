from __future__ import division
import numpy as np;
n = 25
p = np.zeros(n)
p[1]=1
e = np.exp(1)
for i in range(1, n-1):
    p[i+1] = e - (i+1)*p[i]
    print(i, p[i+1])

