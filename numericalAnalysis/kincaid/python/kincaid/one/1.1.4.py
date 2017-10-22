from __future__ import division
import numpy as np;
a = float(1)
e = np.exp(1)
for i in range(1, 1000):
    n = i*8;
    a = (1+float(1/n))**n;
    print(a)
    print(a-e)
