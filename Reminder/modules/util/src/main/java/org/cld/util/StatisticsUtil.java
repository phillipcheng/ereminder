package org.cld.util;

import java.util.List;

public class StatisticsUtil {
	
	public static float getMean(List<Float> vs){
		double sum = 0.0;
        for(double a : vs)
            sum += a;
        return (float) (sum/vs.size());
	}

	public static float getStddev(List<Float> vs){
		double mean = getMean(vs);
        double temp = 0;
        for(float a :vs)
            temp += (mean-a)*(mean-a);
        return  (float) Math.sqrt((temp/vs.size()));
	}
}
