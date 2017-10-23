from __future__ import division
import numpy as np;
n = 20
p = np.zeros(n+1)
p[n] = 100
e = np.exp(1)
for i in range(n-1, 0, -1):
    p[i] = (e - p[i+1])/(i+1)
    print(i, p[i])

