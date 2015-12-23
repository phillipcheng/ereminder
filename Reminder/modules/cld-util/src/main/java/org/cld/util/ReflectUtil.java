package org.cld.util;

public class ReflectUtil {
	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 */
	public static String getMethodName()
	{
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

	  //0 is getStackTrace
	  //1 is getMethodName
	  //2 is runTaskByXXX
	  //3 is the method call runTaskByXX
	  return ste[3].getMethodName(); //Thank you Tom Tresansky
	}
}
