package org.cld.stockmlscala

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors


object LR {
  
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("Linear Regression")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    // Load and parse the data
    val data : RDD[String] = sc.textFile("hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/1/000001-r-00000")
    val parsedData = data.map { line =>
      val parts : Array[String] = line.split(',')
      val len : Int = parts.length
      val twopart = parts.splitAt(len-1)
      println(twopart)
      LabeledPoint(twopart._2(0).toDouble, Vectors.dense(twopart._1.map(_.toDouble)))
    }.cache()
    
    // Building the model
    val numIterations = 100
    val step = 0.1
    val algorithm = new LinearRegressionWithSGD
    algorithm.optimizer.setStepSize(step)
    algorithm.setIntercept(true)
    algorithm.optimizer.setNumIterations(numIterations)
    val model = algorithm.run(parsedData);
    //val model = LinearRegressionWithSGD.train(parsedData, numIterations)
    
    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
    println("training Mean Squared Error = " + MSE)
    
    // Save and load model
    model.save(sc, "myModelPath")
    val sameModel = LinearRegressionModel.load(sc, "myModelPath")
  }
}