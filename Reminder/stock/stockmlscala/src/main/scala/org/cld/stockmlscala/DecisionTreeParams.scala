package org.cld.stockmlscala

class DecisionTreeParams (
      _input: String = null,
      _testInput: String = "",
      _dataFormat: String = "libsvm",
      _algo: String = AlgoConstant.algo_classification,
      _maxDepth: Int = 5,
      _maxBins: Int = 32,
      _minInstancesPerNode: Int = 1,
      _minInfoGain: Double = 0.0,
      _fracTest: Double = 0.2,
      _cacheNodeIds: Boolean = false,
      _checkpointDir: Option[String] = None,
      _checkpointInterval: Int = 10){
  
  val input = _input
  val testInput = _testInput
  val dataFormat = _dataFormat
  val algo = _algo
  val maxDepth = _maxDepth
  val maxBins = _maxBins
  val minInstancesPerNode = _minInstancesPerNode
  val minInfoGain = _minInfoGain
  val fracTest = _fracTest
  val cacheNodeIds = _cacheNodeIds
  val checkpointDir = _checkpointDir
  val checkpointInterval = _checkpointInterval
 }
 