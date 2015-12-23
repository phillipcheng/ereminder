package org.cld.stockmlscala.test

import org.scalatest.FunSuite
import org.cld.stockmlscala._

class TestLRSuite extends FunSuite {
  
  test("LR 1") {
    val params = new LinearRegressionParams("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/1/000001-r-00000");
    LinearRegressionExample.run(params, "local")
  }
  
  test("LR 2") {
    val params = new LinearRegressionParams("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/2/000001-r-00000");
    LinearRegressionExample.run(params, "local")
  }
  
  test("LR 3") {
    val params = new LinearRegressionParams("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/3/000001-r-00000");
    LinearRegressionExample.run(params, "local")
  }
  
  test("DT 1") {
    val params = new DecisionTreeParams("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/1/000001-r-00000", 
        _algo=AlgoConstant.algo_regression, _maxDepth=10)
    DecisionTreeExample.run(params, "local")
  }
  
  test("DT 2") {
    val params = new DecisionTreeParams("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/2/000001-r-00000", 
        _algo=AlgoConstant.algo_regression, _maxDepth=10)
    DecisionTreeExample.run(params, "local")
  }
}