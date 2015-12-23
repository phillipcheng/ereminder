package org.cld.datastore;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class QueryUtil {
	
	public static List<Query> getQuerys(Session session, String namedQuery, String param, 
			List<?> paramValue, int batchSize){
		List<Query> querys = new ArrayList<Query>();
		int total = paramValue.size();
		int turns = total/batchSize;
		
		for (int i=0; i<turns; i++){
			List<?> bParamValue = paramValue.subList(i*batchSize, (i+1)*batchSize);
			Query q = session.getNamedQuery(namedQuery).setParameterList(param, bParamValue);
			querys.add(q);
		}
		List<?> bParamValue = paramValue.subList(turns*batchSize, total);
		Query q = session.getNamedQuery(namedQuery).setParameterList(param, bParamValue);
		querys.add(q);
		return querys;
	}
	


}
