'''
Created on May 6, 2016

@author: chengyi
'''
from count_freqs import Hmm

class Hmm(object):
    
    def __init__(self, counts_file="gene.counts"):
        self.hmm = Hmm()
        self.hmm.read_counts(counts_file)
        
    def emission(self, x, y):
        
        pass
    
if __name__ == '__main__':
    
    pass