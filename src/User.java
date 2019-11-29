import java.util.ArrayList;
import java.util.HashMap;

public class User {

	private String id;
	private HashMap<String,User> externalLinks;
	private HashMap<String,User> internalLinks;
	private ArrayList<Tweet> tweets;
	
	public User(String _id)
	{
		id = _id;
		externalLinks = new HashMap<String,User>();
		internalLinks = new HashMap<String,User>();
		tweets = new ArrayList<Tweet>();
	}
	
	public void addExternalLink(User _user)
	{
		if(!isExtLinkedTo(_user))
		{
			externalLinks.put(_user.getId(), _user);
			_user.addInternalLink(this);
		}
	}
	
	public void addInternalLink(User _user)
	{
		if(!isIntLinkedTo(_user))
		{
			internalLinks.put(_user.getId(), _user);
		}
	}
	
	public void addTweet(Tweet _tweet)
	{
		tweets.add(_tweet);
		System.out.println("Added tweet : " + _tweet.getText());
	}
	
	public boolean isExtLinkedTo(User _user)
	{
		return externalLinks.containsKey(_user.getId());
	}
	
	public boolean isIntLinkedTo(User _user)
	{
		return internalLinks.containsKey(_user.getId());
	}
	
	public String getId()
	{
		return id;
	}
	
	public int getInternalLinksNumber()
	{
		return internalLinks.size();
	}
	
	public HashMap<String,User> getInternalLinks()
	{
		return internalLinks;
	}
	
	public int getExternalLinksNumber()
	{
		return externalLinks.size();
	}
	
	public HashMap<String,User> getExternalLinks()
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

	/*@Override
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
	}*/
	
	
}
