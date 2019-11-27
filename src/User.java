import java.util.ArrayList;

public class User {

	private String id;
	private ArrayList<User> linkedUsers;
	private ArrayList<Tweet> tweets;
	
	public User(String _id)
	{
		id = _id;
		linkedUsers = new ArrayList<User>();
		tweets = new ArrayList<Tweet>();
	}
	
	public void addLinkedUser(User _user)
	{
		if(!isLinkedTo(_user))
		{
			linkedUsers.add(_user);
		}
	}
	
	public void addTweet(Tweet _tweet)
	{
		if(!tweets.contains(_tweet))
		{
			tweets.add(_tweet);
		}
	}
	
	public boolean isLinkedTo(User _user)
	{
		return linkedUsers.contains(_user);
	}
	
	public String getId()
	{
		return id;
	}
	
	public ArrayList<User> getLinkedUsers()
	{
		return linkedUsers;
	}
	
	public ArrayList<Tweet> getTweets()
	{
		return tweets;
	}
}
