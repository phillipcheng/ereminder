package org.cld.stockml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;

import scala.Tuple2;

public class DT {

	private static Logger logger =  LogManager.getLogger(DT.class);
	
	public static void main(String[] args){
		SparkConf conf = new SparkConf().setAppName("DecisionTree");
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

	    // Split the data into training and test sets (30% held out for testing)
	    JavaRDD<LabeledPoint>[] splits = parsedData.randomSplit(new double[]{0.7, 0.3});
	    JavaRDD<LabeledPoint> trainingData = splits[0];
	    JavaRDD<LabeledPoint> testData = splits[1];

	    // Set parameters.
	    //  Empty categoricalFeaturesInfo indicates all features are continuous.
	    Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
	    String impurity = "variance";
	    Integer maxDepth = 5;
	    Integer maxBins = 32;

	    // Train a DecisionTree model.
	    final DecisionTreeModel model = DecisionTree.trainRegressor(trainingData,
	      categoricalFeaturesInfo, impurity, maxDepth, maxBins);

	    // Evaluate model on test instances and compute test error
	    JavaPairRDD<Double, Double> predictionAndLabel =
	      testData.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
	        @Override
	        public Tuple2<Double, Double> call(LabeledPoint p) {
	          return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
	        }
	      });
	    Double testMSE =
	      predictionAndLabel.map(new Function<Tuple2<Double, Double>, Double>() {
	        @Override
	        public Double call(Tuple2<Double, Double> pl) {
	          Double diff = pl._1() - pl._2();
	          return diff * diff;
	        }
	      }).reduce(new Function2<Double, Double, Double>() {
	        @Override
	        public Double call(Double a, Double b) {
	          return a + b;
	        }
	      }) / data.count();
	    logger.info("Test Mean Squared Error: " + testMSE);
	    logger.info("Learned regression tree model:\n" + model.toDebugString());

	    // Save and load model
	    model.save(sc.sc(), "myModelPath");
	    DecisionTreeModel sameModel = DecisionTreeModel.load(sc.sc(), "myModelPath");
	}
}
