/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cld.stockmlscala

import scala.collection.mutable
import scala.language.reflectiveCalls

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.DataFrame

object LinearRegressionExample {

  def run(params: LinearRegressionParams, masterUrl:String) {
    val conf = new SparkConf().setAppName(s"LinearRegressionExample with $params")
    if (masterUrl!=null)
      conf.setMaster(masterUrl)
    val sc = new SparkContext(conf)

    println(s"LinearRegressionExample with parameters:\n$params")

    // Load training and test data and cache it.
    val (training: DataFrame, test: DataFrame) = LoadDataUtil.loadDatasets(sc, params.input,
      params.dataFormat, params.testInput, "regression", params.fracTest)

    val lir = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setRegParam(params.regParam)
      .setElasticNetParam(params.elasticNetParam)
      .setMaxIter(params.maxIter)
      .setTol(params.tol)

    // Train the model
    val startTime = System.nanoTime()
    val lirModel = lir.fit(training)
    val elapsedTime = (System.nanoTime() - startTime) / 1e9
    println(s"Training time: $elapsedTime seconds")

    // Print the weights and intercept for linear regression.
    println(s"Weights: ${lirModel.weights} Intercept: ${lirModel.intercept}")

    println("Training data results:")
    EvalUtil.evaluateRegressionModel(lirModel, training, "label")
    println("Test data results:")
    EvalUtil.evaluateRegressionModel(lirModel, test, "label")

    sc.stop()
  }
}
