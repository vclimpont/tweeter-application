import java.util.ArrayList;
import java.util.HashMap;

public class User {

	private String id;
	private HashMap<String,User> externalLinks;
	private HashMap<String,User> internalLinks;
	private ArrayList<Tweet> tweets;
	private String centrality;
	private LNode lnode;
	private Community community;
	private boolean hidden;
	
	public User(String _id)
	{
		id = _id;
		externalLinks = new HashMap<String,User>();
		internalLinks = new HashMap<String,User>();
		tweets = new ArrayList<Tweet>();
		hidden = false;
		centrality = "blue";
		community = null; 
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

	public String getCentrality() {
		return centrality;
	}

	public void setCentrality(String centrality) {
		this.centrality = centrality;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public boolean isHidden()
	{
		return hidden;
	}
	
	public void setHidden(boolean h)
	{
		hidden = h;
	}

	public LNode getLNode() {
		return lnode;
	}

	public void setLNode(LNode lnode) {
		this.lnode = lnode;
	}
}
