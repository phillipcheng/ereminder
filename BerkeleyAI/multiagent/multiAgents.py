# multiAgents.py
# --------------
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


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
        
        foodList = newFood.asList()
        ghostList = [ghostState.getPosition() for ghostState in newGhostStates]
        score = 0
        for foodPos in foodList:
            foodDist = abs(newPos[0]-foodPos[0]) + abs(newPos[1]-foodPos[1])
            if foodDist!=0 :
                score += 4/foodDist
            else:
                score += 4
        for ghostPos in ghostList:
            ghostDist = abs(newPos[0]-ghostPos[0]) + abs(newPos[1]-ghostPos[1])
            if ghostDist!=0:
                score -= 3/ghostDist
            else:
                score -= 3
        return successorGameState.getScore() + score

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

import logging, sys
logging.basicConfig(stream=sys.stderr, level=logging.DEBUG)
class MinimaxAgent(MultiAgentSearchAgent):
    def minValue(self, gameState, level, idx):
        minV = sys.maxint
        actionV = Directions.STOP
        actions = gameState.getLegalActions(idx)
        if len(actions)==0:
            minV = self.evaluationFunction(gameState)
        else:
            if idx+1==gameState.getNumAgents(): #reached last ghost
                if level==self.depth: #reached leaves
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.evaluationFunction(nextGameState)
                        if v<minV:
                            minV=v
                            actionV = action
                else:
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.maxValue(nextGameState, level+1)[0]
                        if v<minV:
                            minV=v
                            actionV = action
            else: #not yet last ghost
                for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.minValue(nextGameState, level, idx+1)[0]
                        if v<minV:
                            minV=v
                            actionV = action
        #logging.debug('min level:%d, agentIdx:%d, value:%d, action:%s', level, idx, minV, actionV)
        return (minV, actionV)
    
    def maxValue(self, gameState, level):
        maxV = -sys.maxint
        actionV = Directions.STOP
        actions = gameState.getLegalActions(0)
        if (len(actions)>0):
            for action in gameState.getLegalActions(0):
                nextGameState = gameState.generateSuccessor(0, action)
                v = self.minValue(nextGameState, level, 1)[0]
                if maxV<v : 
                    maxV=v
                    actionV = action
        else:
            maxV = self.evaluationFunction(gameState)
        #logging.debug('level:%s, value:%s, action:%s', level, maxV, actionV)
        return (maxV, actionV)
            
    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        v, direction = self.maxValue(gameState, 1) #start with level 1   
        return direction
        

class AlphaBetaAgent(MultiAgentSearchAgent):
    def minValue(self, gameState, level, idx, a, b):
        minV = sys.maxint
        actionV = Directions.STOP
        actions = gameState.getLegalActions(idx)
        if len(actions)==0:
            minV = self.evaluationFunction(gameState)
            if minV < b:
                b = minV
        else:
            if idx+1==gameState.getNumAgents(): #reached last ghost
                if level==self.depth: #reached leaves
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.evaluationFunction(nextGameState)
                        if v<minV:
                            minV=v
                            actionV = action
                            b=v
                        if minV<a:
                            break
                        if minV < b:
                            b = minV
                else:
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.maxValue(nextGameState, level+1, a, b)[0]
                        if v<minV:
                            minV=v
                            actionV = action
                            b=v
                        if minV<a:
                            break
                        if minV<b:
                            b = minV
            else: #not yet last ghost
                for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        v = self.minValue(nextGameState, level, idx+1, a, b)[0]
                        if v<minV:
                            minV=v
                            actionV = action
                            b=v
                        if minV<a:
                            break
                        if minV<b:
                            b = minV
        #logging.debug('min level:%d, agentIdx:%d, value:%d, action:%s, a:%d, b:%d', level, idx, minV, actionV, a, b)
        return (minV, actionV, a, b)
    
    def maxValue(self, gameState, level, a, b): #return (value, action, a, b)
        maxV = -sys.maxint
        actionV = Directions.STOP
        actions = gameState.getLegalActions(0)
        if len(actions)==0:
            maxV = self.evaluationFunction(gameState)
            if maxV > a:
                a = maxV
        else:
            for action in gameState.getLegalActions(0):
                nextGameState = gameState.generateSuccessor(0, action)
                v = self.minValue(nextGameState, level, 1, a, b)[0]
                if maxV<v: 
                    maxV=v
                    actionV = action
                if maxV>b:
                    break
                if maxV > a:
                    a = maxV
        #logging.debug('max level:%s, value:%s, action:%s, a:%d, b:%d', level, maxV, actionV, a, b)
        return (maxV, actionV, a, b)

    def getAction(self, gameState):
        directionV = self.maxValue(gameState, 1, -sys.maxint, sys.maxint)[1]
        return directionV

class ExpectimaxAgent(MultiAgentSearchAgent):
    def randomValue(self, gameState, level, idx):
        actions = gameState.getLegalActions(idx)
        if len(actions)==0:
            v = self.evaluationFunction(gameState)
        else:
            total = 0
            if idx+1==gameState.getNumAgents(): #reached last ghost
                if level==self.depth: #reached leaves
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        total += self.evaluationFunction(nextGameState)
                else:
                    for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        total += self.maxValue(nextGameState, level+1)[0]
            else: #not yet last ghost
                for action in actions:
                        nextGameState = gameState.generateSuccessor(idx, action)
                        total += self.randomValue(nextGameState, level, idx+1)
            v = float(total) / float(len(actions))
        #logging.debug('min level:%d, agentIdx:%d, value:%d', level, idx, v)
        return v
    
    def maxValue(self, gameState, level):
        maxV = -sys.maxint
        actionV = Directions.STOP
        actions = gameState.getLegalActions(0)
        if (len(actions)>0):
            for action in gameState.getLegalActions(0):
                nextGameState = gameState.generateSuccessor(0, action)
                v = self.randomValue(nextGameState, level, 1)
                if maxV<v : 
                    maxV=v
                    actionV = action
        else:
            maxV = self.evaluationFunction(gameState)
        #logging.debug('level:%s, value:%s, action:%s', level, maxV, actionV)
        return (maxV, actionV)
    
    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        return self.maxValue(gameState, 1)[1]

def betterEvaluationFunction(currentGameState):
    newPos = currentGameState.getPacmanPosition()
    newFood = currentGameState.getFood()
    newGhostStates = currentGameState.getGhostStates()
    newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
    foodList = newFood.asList()
    ghostList = [ghostState.getPosition() for ghostState in newGhostStates]
    score = 0
    for foodPos in foodList:
        foodDist = abs(newPos[0]-foodPos[0]) + abs(newPos[1]-foodPos[1])
        if foodDist!=0 :
            score += 4/foodDist
        else:
            score += 4
    for ghostPos in ghostList:
        ghostDist = abs(newPos[0]-ghostPos[0]) + abs(newPos[1]-ghostPos[1])
        if ghostDist!=0:
            score -= 3/ghostDist
        else:
            score -= 3
    return currentGameState.getScore() + score

# Abbreviation
better = betterEvaluationFunction

