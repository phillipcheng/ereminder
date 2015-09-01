package org.cld.stockml;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;

import scala.Tuple2;

public class LR {

	private static Logger logger =  LogManager.getLogger(LR.class);
	
	public static void main(String[] args){
		SparkConf conf = new SparkConf().setAppName("Linear Regression");
	    conf.setMaster("local");
	    
		JavaSparkContext sc = new JavaSparkContext(conf);
	    
	    // Load and parse the data
	    String path = "hdfs://192.85.247.104:19000/reminder/items/GenNdLable/sina-stock-market-fq/3/000001-r-00000";
	    JavaRDD<String> data = sc.textFile(path);
	    JavaRDD<LabeledPoint> parsedData = data.map(
	      new Function<String, LabeledPoint>() {
	        public LabeledPoint call(String line) {
	          String[] parts = line.split(","); 
	          double[] v = new double[parts.length-1];//feature last lable
	          for (int i = 0; i < parts.length - 1; i++){
	            v[i] = Double.parseDouble(parts[i]);
	          }
	          String lable = parts[parts.length-1];
	          logger.info("features:" + Arrays.toString(v) + ", lable:" + lable);
	          return new LabeledPoint(Double.parseDouble(lable), Vectors.dense(v));
	        }
	      }
	    );
	    parsedData.cache();

	    // Building the model
	    int numIterations = 200;
	    double step = 0.1;
	    LinearRegressionWithSGD algorithm = new LinearRegressionWithSGD();
	    algorithm.setIntercept(true);
	    algorithm.setFeatureScaling(true);
	    algorithm.optimizer().setNumIterations(numIterations);
	    algorithm.optimizer().setStepSize(step);
	    final LinearRegressionModel model = 
	      algorithm.run(JavaRDD.toRDD(parsedData));
	    logger.info(model.toPMML());
	    // Evaluate model on training examples and compute training error
	    JavaRDD<Tuple2<Double, Double>> valuesAndPreds = parsedData.map(
	      new Function<LabeledPoint, Tuple2<Double, Double>>() {
	        public Tuple2<Double, Double> call(LabeledPoint point) {
	          double prediction = model.predict(point.features());
	          return new Tuple2<Double, Double>(prediction, point.label());
	        }
	      }
	    );
	    double MSE = new JavaDoubleRDD(valuesAndPreds.map(
	      new Function<Tuple2<Double, Double>, Object>() {
	        public Object call(Tuple2<Double, Double> pair) {
	          return Math.pow(pair._1() - pair._2(), 2.0);
	        }
	      }
	    ).rdd()).mean();
	    logger.info("training Mean Squared Error = " + MSE);

	    // Save and load model
	    model.save(sc.sc(), "myModelPath");
	    LinearRegressionModel sameModel = LinearRegressionModel.load(sc.sc(), "myModelPath");
	    sc.stop();
	    sc.close();
	}
}
