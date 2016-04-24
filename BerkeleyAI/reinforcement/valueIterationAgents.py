# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import mdp, util
import logging, sys
logging.basicConfig(stream=sys.stderr, level=logging.DEBUG)
from learningAgents import ValueEstimationAgent

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """

    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        for _ in range(0, iterations):
            self.newValues = util.Counter()
            for st in mdp.getStates():
                if len(mdp.getPossibleActions(st)) != 0:
                    maxV = -sys.maxint
                    for act in mdp.getPossibleActions(st):
                        newV = 0
                        for tst, prob in mdp.getTransitionStatesAndProbs(st, act):
                            r = mdp.getReward(st, act, tst)
                            newV += prob*(r + discount* self.values[tst])
                        if newV > maxV: maxV = newV
                    self.newValues[st]=maxV
                else:
                    self.newValues[st]=self.values[st]
            self.values = self.newValues
            #logging.debug('values:%s', self.values)
            
    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        v = 0
        for tst, prob in self.mdp.getTransitionStatesAndProbs(state, action):
            r = self.mdp.getReward(state, action, tst)
            v += prob*(r + self.discount* self.values[tst])
        return v

    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        action = None
        maxV = -sys.maxint
        for act in self.mdp.getPossibleActions(state):
            v = 0
            for tst, prob in self.mdp.getTransitionStatesAndProbs(state, act):
                r = self.mdp.getReward(state, act, tst)
                v += prob*(r + self.discount* self.values[tst])
            if v>maxV: 
                maxV = v
                action = act
        return action
        

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)
