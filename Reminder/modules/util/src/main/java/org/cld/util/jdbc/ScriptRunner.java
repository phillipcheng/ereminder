package org.cld.util.jdbc;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
/**
 * Tool to run database scripts
 */
public class ScriptRunner {
	public Logger logger = LogManager.getLogger(ScriptRunner.class);
	private static final String DEFAULT_DELIMITER = ";";
 
	private Connection connection;
 
	private boolean stopOnError;
    private boolean autoCommit;
 
    private String delimiter = DEFAULT_DELIMITER;
 
    public ScriptRunner(Connection connection, boolean autoCommit,
                        boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
    }
 
    public void setDelimiter(String delimiter) {
    	this.delimiter = delimiter;
    }
 
 
	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 *
	 * @param reader
	 *            - the source of the script
	 */
	public void runScript(Reader reader) throws IOException, SQLException {
	    try {
	        boolean originalAutoCommit = connection.getAutoCommit();
	        try {
	                if (originalAutoCommit != this.autoCommit) {
	                        connection.setAutoCommit(this.autoCommit);
	                }
	                runScript(connection, reader);
	        } finally {
	                connection.setAutoCommit(originalAutoCommit);
	        }
	    } catch (IOException e) {
	            throw e;
	    } catch (SQLException e) {
	            throw e;
	    } catch (Exception e) {
	            throw new RuntimeException("Error running script.  Cause: " + e, e);
	    }
	}
 
    /**
	 * Runs an SQL script (read in using the Reader parameter) using the
	 * connection passed in
	 *
	 * @param conn
	 *            - the connection to use for the script
	 * @param reader
	 *            - the source of the script
	 */
	private void runScript(Connection conn, Reader reader) throws IOException,
            SQLException {
		    StringBuffer command = null;
		    try {
		    	LineNumberReader lineReader = new LineNumberReader(reader);
			    String line = null;
			    while ((line = lineReader.readLine()) != null) {
			    	if (command == null) {
			        	command = new StringBuffer();
			        }
			        String trimmedLine = line.trim();
			        if (trimmedLine.startsWith("--")) {
			        	logger.info("comment:" + trimmedLine);
			        } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
			                    // Do nothing
			        } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
			                    // Do nothing
			        } else if (trimmedLine.endsWith(getDelimiter())) {
			        	command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
			            command.append(" ");
			            Statement statement = conn.createStatement();
			            logger.info("sql:" + command);
			            if (stopOnError) {
                        	statement.execute(command.toString());
                        } else {
                        	try {
                            	statement.execute(command.toString());
                            } catch (SQLException e) {
                            	logger.error("", e);
                            }
                        }
                        
                        if (autoCommit && !conn.getAutoCommit()) {
                        	conn.commit();
                        }
                        command = null;
                        try {
                        	statement.close();
                        } catch (Exception e) {
                        }
		                Thread.yield();
		            } else {
		            	command.append(line);
		                command.append(" ");
		            }
			    }
			    if (!autoCommit) {
			    	conn.commit();
			    }
			} catch (SQLException e) {
			    logger.error("", e);
			} catch (IOException e) {
			    logger.error("", e);
            } finally {
                conn.rollback();
            }
        }
 
        private String getDelimiter() {
                return delimiter;
        }
}