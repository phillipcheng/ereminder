package org.cld.stock.persistence;

import org.cld.util.jdbc.JDBCMapper;

public abstract class EarnJDBCMapper extends JDBCMapper {
	
	public abstract boolean cumulativeEps();

}
