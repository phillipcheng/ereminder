package algo.graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationGraph {
	private static Logger logger =  LogManager.getLogger(LocationGraph.class);
	
	private int nv;
	private float[] xs;
	private float[] ys;
	
	public LocationGraph(int nv){
		this.nv = nv;
		xs = new float[nv];
		ys = new float[nv];
	}
	
	public int getNv(){
		return nv;
	}
	
	public void addV(float x, float y, int idx){
		xs[idx]=x;
		ys[idx]=y;
	}
	
	public float cost(int i, int j){
		return (float) Math.sqrt(Math.pow(xs[i]-xs[j], 2)+Math.pow(ys[i]-ys[j],2));
	}
	
	public static LocationGraph createFromFile(InputStream is){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			int nv = Integer.parseInt(line);
			LocationGraph dg = new LocationGraph(nv);
			logger.info(String.format("#v: %d", nv));
			while (--nv>=0){
				line = br.readLine();
				String[] wl = line.split(" ");
				float x = Float.parseFloat(wl[0]);
				float y = Float.parseFloat(wl[1]);
				dg.addV(x, y, nv);
			}
			br.close();
			return dg;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}

}
