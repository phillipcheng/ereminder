package org.cld.stockmlscala

/**
 * @author cheyi
 */
class LinearRegressionParams (
      _input: String = null,
      _testInput: String = "",
      _dataFormat: String = "libsvm",
      _fracTest: Double = 0.2,
      _regParam: Double = 0.0,
      _elasticNetParam: Double = 0.0,
      _maxIter: Int = 100,
      _tol: Double = 1E-6){
  val input = _input
  val testInput = _testInput
  val dataFormat = _dataFormat
  val fracTest = _fracTest
  val regParam = _regParam
  val elasticNetParam = _elasticNetParam
  val maxIter = _maxIter
  val tol = _tol
 }
 