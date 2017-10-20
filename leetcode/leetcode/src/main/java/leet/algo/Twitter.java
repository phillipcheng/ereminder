package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Twitter {
	private static int tsId=0;
	class TsTweet implements Comparable<TsTweet>{
		int ts;
		int twid;
		int userId;
		public TsTweet(int twid, int userId){
			this.ts = tsId++;
			this.twid = twid;
			this.userId = userId;
		}
		@Override
		public int compareTo(TsTweet tt){
			return tt.ts-this.ts;
		}
		@Override
		public String toString(){
			return String.format("%d,%d,%d", ts, twid, userId);
		}
	}
	class TUser{
		int uid;
		List<Integer> followers = new ArrayList<Integer>();//who uid follows
		List<Integer> followees = new ArrayList<Integer>();//whom uid is followed by
		List<TsTweet> tweets = new ArrayList<TsTweet>();//list of TsTweet
		
		public TUser(int uid){
			this.uid = uid;
		}
	}
	Map<Integer, TUser> userMap = new HashMap<Integer, TUser>();
	
	/** Initialize your data structure here. */
    public Twitter() {
        
    }
    
    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
    	TUser tu = userMap.get(userId);
        if (tu==null){
        	tu = new TUser(userId);
            userMap.put(userId, tu);
        }
        tu.tweets.add(0, new TsTweet(tweetId, userId));
    }
    
    /** Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent. */
    public List<Integer> getNewsFeed(int userId) {
        List<Integer> feeds = new ArrayList<Integer>();
        TUser tu = userMap.get(userId);
        if (tu!=null){
            Map<Integer, TUser> users = new HashMap<Integer, TUser>();//users
            for (int uid:tu.followers){
            	users.put(uid, userMap.get(uid));
            }
            users.put(userId, tu);
            Map<Integer, Integer> tidmap = new HashMap<Integer, Integer>(); //userid to the index of tweet fetched
            PriorityQueue<TsTweet> candidateTweets = new PriorityQueue<TsTweet>();
            for (TUser tu1:users.values()){
            	int idx =0;
            	if (idx<tu1.tweets.size()){
            		TsTweet tt = tu1.tweets.get(idx);
            		candidateTweets.add(tt);
            		tidmap.put(tu1.uid, idx);
            	}
            }
            int cnt=0;
            while(cnt<10 && candidateTweets.size()>0){
            	TsTweet tt = candidateTweets.remove();
            	feeds.add(tt.twid);
            	cnt++;
            	int idx = tidmap.get(tt.userId);
            	idx++;
            	if (users.get(tt.userId).tweets.size()>idx){
            		candidateTweets.add(users.get(tt.userId).tweets.get(idx));
            		tidmap.put(tt.userId, idx);
            	}
            }
            return feeds;
        }else{
            return feeds;
        }
    }
    
    /** Follower follows a followee. If the operation is invalid, it should be a no-op. */
    public void follow(int followerId, int followeeId) {
        TUser follower = userMap.get(followerId);
        if (follower==null){
        	follower = new TUser(followerId);
        	userMap.put(followerId, follower);
        }
        TUser followee = userMap.get(followeeId);
        if (followee==null){
        	followee = new TUser(followeeId);
        	userMap.put(followeeId, followee);
        }
        follower.followers.add(followeeId);
        followee.followees.add(followerId);
    }
    
    /** Follower unfollows a followee. If the operation is invalid, it should be a no-op. */
    public void unfollow(int followerId, int followeeId) {
    	TUser follower = userMap.get(followerId);
    	TUser followee = userMap.get(followeeId);
    	if (follower!=null)
    	    follower.followers.remove(new Integer(followeeId));
    	if (followee!=null)
    	    followee.followees.remove(new Integer(followerId));
    }
    
    public static void main(String[] args){
    	Twitter obj = new Twitter();
    	obj.postTweet(2,5);
    	obj.postTweet(1,3);
    	obj.postTweet(1,101);
    	obj.postTweet(2,13);
    	obj.postTweet(2,10);
    	obj.postTweet(1,2);
    	obj.postTweet(2,94);
    	obj.postTweet(2,505);
    	obj.postTweet(1,333);
    	obj.postTweet(1,22);
    	List<Integer> p = obj.getNewsFeed(2);
    	System.out.println(p);
    	obj.follow(2, 1);
    	p = obj.getNewsFeed(2);
    	System.out.println(p);
    }
}
