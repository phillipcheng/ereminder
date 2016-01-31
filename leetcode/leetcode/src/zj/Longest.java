package zj;

public class Longest {
	public static boolean isDebug = false;
	
    public int lengthOfLongestSubstring(String s) {
    	
	    int start = 0, longest = 1;
	    int currentStart=0, currentLongest=1;
	    int newStart;
	    int newLongest;
	    boolean isRepeat = false;
	    char[] chars = s.toCharArray();          
	                  
	    int count=0;
	    for(int i=1; i<chars.length;i++) {
	    	if (isDebug){
	           System.out.println("i = "+i);
	    	}
	           for(int j=0;j<currentLongest;j++) {
	                  if(chars[i] == chars[currentStart+j]) {
	                	  count++;
	                        newStart = currentStart+j+1;
	                        //if ((chars.length - newStart) < longest ) 
	                              // return longest;
	                        newLongest = i-(currentStart+j);
	                        if(currentLongest>longest) {
	                               start = currentStart;
	                               longest = currentLongest;
	                               if (isDebug){
				                        System.out.println("start has been updated to "+start);
				                        System.out.println("longest has been updated to "+longest);
	                               }
	                        }
	                        currentStart = newStart;
	                        currentLongest = newLongest;
	                        isRepeat = true;
	                        if (isDebug){
		                        System.out.println("currentStart has been updated to "+currentStart);
		                        System.out.println("currentLongest has been updated to "+currentLongest);
	                        }
	                        break;
	                  } 
	
	           }
	           if (!isRepeat) {
	                  currentLongest++;
	                  if (isDebug){
		                  System.out.println("currentStart is still = "+currentStart);
		                  System.out.println("currentLongest has been ++ = "+currentLongest);
	                  }
	           }
	           isRepeat = false;
	           if (isDebug){
	        	   System.out.println("The "+i+" round char "+chars[i]+" with longest "+longest);
	           }
	    	}
	    System.out.println("count:" + count);
           return longest;
     
    	}

}
