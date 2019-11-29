import java.util.HashMap;


public class UsersBase {
	
	private HashMap<String,User> users;
	private int maxLinks;
	
	public UsersBase()
	{
		users = new HashMap<String,User>();
		maxLinks = 0;
	}
	
	public void rowDataToUser(String[] data)
	{
		if(data.length == 4)
		{
			User u = users.get(data[1]);
			if(u == null) // Add user in the base if he does not exist
			{
				u = new User(data[1]);
				addUser(u);
			}			
			
			/*Tweet t = u.getTweet(data[3]);
			if(t == null) // Add tweet to the user-author if it does not exist
			{
				t = new Tweet(u, data[2], data[3]);
				u.addTweet(t);
			}*/
		}
		else if(data.length == 5) // It's a retweet
		{
			User rtu = users.get(data[1]);	
			if(rtu == null) // Add the user who RT'd in the base
			{
				rtu = new User(data[1]);
				addUser(rtu);
			}
			
			User u = users.get(data[4]);	
			if(u == null) // Add user in the base if he does not exist
			{
				u = new User(data[4]);
				addUser(u);
			}			
			
			/*Tweet t = u.getTweet(data[3]);
			if(t == null) // Add tweet to the user-author if it does not exist
			{
				t = new Tweet(u, data[2], data[3]);
				u.addTweet(t);
			}*/
			
			rtu.addExternalLink(u);
		}
		else
		{
			System.out.println("There is a problem on this row length : " + data[4]);
		}
	}
	
	public void addUser(User _user)
	{
		users.put(_user.getId(), _user);
		System.out.println("User " + _user.getId() + " added.");
	}
		
	public User getUser(String id)
	{
		return users.get(id);
	}
	
	public void setMaxLinks()
	{
		for(String id : users.keySet())
		{
			if(maxLinks < users.get(id).getInternalLinksNumber())
			{
				maxLinks = users.get(id).getInternalLinksNumber();
			}
		}
	}
	
	public int getMaxLinks()
	{
		return maxLinks;
	}
	
	public HashMap<String,User> getUsers()
	{
		return users;
	}
}
