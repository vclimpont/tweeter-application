import java.util.ArrayList;


public class UsersBase {
	
	private ArrayList<User> users;
	private int maxLinks;
	
	public UsersBase()
	{
		users = new ArrayList<User>();
		maxLinks = 0;
	}
	
	public void rowDataToUser(String[] data)
	{
		if(data.length == 4)
		{
			User u = getUser(data[1]);	
			if(u == null) // Add user in the base if he does not exist
			{
				u = new User(data[1]);
				addUser(u);
			}			
			
			Tweet t = u.getTweet(data[3]);
			if(t == null) // Add tweet to the user-author if it does not exist
			{
				t = new Tweet(u, data[2], data[3]);
				u.addTweet(t);
			}
		}
		else if(data.length == 5) // It's a retweet
		{
			User rtu = getUser(data[1]);	
			if(rtu == null) // Add the user who RT'd in the base
			{
				rtu = new User(data[1]);
				addUser(rtu);
			}
			
			User u = getUser(data[4]);	
			if(u == null) // Add user in the base if he does not exist
			{
				u = new User(data[4]);
				addUser(u);
			}			
			
			Tweet t = u.getTweet(data[3]);
			if(t == null) // Add tweet to the user-author if it does not exist
			{
				t = new Tweet(u, data[2], data[3]);
				u.addTweet(t);
			}
			
			rtu.addExternalLink(u);
		}
		else
		{
			System.out.println("There is a problem on this row length : " + data[4]);
		}
	}
	
	public void addUser(User _user)
	{
		users.add(_user);
		System.out.println("User " + _user.getId() + " added.");
	}
		
	public User getUser(String id)
	{
		for(User u : users)
		{
			if(u.getId().equals(id))
			{
				return u;
			}
		}
		System.out.println("User introuvable");
		return null;
	}
	
	public void setMaxLinks()
	{
		for(User u : users)
		{
			if(maxLinks < u.getInternalLinksNumber())
			{
				maxLinks = u.getInternalLinksNumber();
			}
		}
	}
	
	public int getMaxLinks()
	{
		return maxLinks;
	}
	
	public ArrayList<User> getUsers()
	{
		return users;
	}
}
