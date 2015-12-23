package org.cld.stockmlscala

import org.apache.spark.sql.{SQLContext, DataFrame}
import org.apache.spark.ml.util.MetadataUtils
import org.apache.spark.ml.{Pipeline, PipelineStage, Transformer}
import org.apache.spark.mllib.evaluation.{RegressionMetrics, MulticlassMetrics}

/**
 * @author cheyi
 */
object EvalUtil {
   /**
   * Evaluate the given ClassificationModel on data.  Print the results.
   * @param model  Must fit ClassificationModel abstraction
   * @param data  DataFrame with "prediction" and labelColName columns
   * @param labelColName  Name of the labelCol parameter for the model
   *
   * TODO: Change model type to ClassificationModel once that API is public. SPARK-5995
   */
  def evaluateClassificationModel(
      model: Transformer,
      data: DataFrame,
      labelColName: String): Unit = {
    val fullPredictions = model.transform(data).cache()
    val predictions = fullPredictions.select("prediction").map(_.getDouble(0))
    val labels = fullPredictions.select(labelColName).map(_.getDouble(0))
    val accuracy = new MulticlassMetrics(predictions.zip(labels)).precision
    println(s"  Accuracy: $accuracy")
  }

  /**
   * Evaluate the given RegressionModel on data.  Print the results.
   * @param model  Must fit RegressionModel abstraction
   * @param data  DataFrame with "prediction" and labelColName columns
   * @param labelColName  Name of the labelCol parameter for the model
   *
   * TODO: Change model type to RegressionModel once that API is public. SPARK-5995
   */
  def evaluateRegressionModel(
      model: Transformer,
      data: DataFrame,
      labelColName: String): Unit = {
    val fullPredictions = model.transform(data).cache()
    val predictions = fullPredictions.select("prediction").map(_.getDouble(0))
    val labels = fullPredictions.select(labelColName).map(_.getDouble(0))
    val RMSE = new RegressionMetrics(predictions.zip(labels)).rootMeanSquaredError
    println(s"  Root mean squared error (RMSE): $RMSE")
  }
}