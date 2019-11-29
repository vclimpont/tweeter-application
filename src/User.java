import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class User {

	private String id;
	private ArrayList<User> externalLinks;
	private ArrayList<User> internalLinks;
	private ArrayList<Tweet> tweets;
	
	public User(String _id)
	{
		id = _id;
		externalLinks = new ArrayList<User>();
		internalLinks = new ArrayList<User>();
		tweets = new ArrayList<Tweet>();
	}
	
	public void addExternalLink(User _user)
	{
		if(!isExtLinkedTo(_user))
		{
			externalLinks.add(_user);
			_user.addInternalLink(this);
		}
	}
	
	public void addInternalLink(User _user)
	{
		if(!isIntLinkedTo(_user))
		{
			internalLinks.add(_user);
		}
	}
	
	public void addTweet(Tweet _tweet)
	{
		tweets.add(_tweet);
		System.out.println("Added tweet : " + _tweet.getText());
	}
	
	public boolean isExtLinkedTo(User _user)
	{
		return externalLinks.contains(_user);
	}
	
	public boolean isIntLinkedTo(User _user)
	{
		return internalLinks.contains(_user);
	}
	
	public String getId()
	{
		return id;
	}
	
	public int getInternalLinksNumber()
	{
		return internalLinks.size();
	}
	
	public ArrayList<User> getInternalLinks()
	{
		return internalLinks;
	}
	
	public int getExternalLinksNumber()
	{
		return externalLinks.size();
	}
	
	public ArrayList<User> getExternalLinks()
	{
		return externalLinks;
	}
	
	public Tweet getTweet(String text)
	{
		for(Tweet t : tweets)
		{
			if(t.getText().equals(text))
			{
				return t;
			}
		}
		
		System.out.println("Tweet introuvable");
		return null;
	}
	
	public ArrayList<Tweet> getTweets()
	{
		return tweets;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof User)
		{
			return ((User)arg0).getId().equals(this.getId());
		}
		else
		{
			return false;
		}	
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
        try {
			byte[] bytes = getId().getBytes("US-ASCII");
			
			String s = "";
			for(int i=0; i<3; i++)
			{
				s += bytes[i];
			}
			System.out.println(s);
			return Integer.parseInt(s);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return -1;
	}
	
	
}
