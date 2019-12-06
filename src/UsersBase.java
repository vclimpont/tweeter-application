import java.util.ArrayList;


public class UsersBase {
	
	private ArrayList<User> users;
	private int maxLinks;
	
	public UsersBase()
	{
		users = new ArrayList<User>();
		maxLinks = 0;
	}
	
	public void addUser(User _user)
	{
		if(!users.contains(_user))
		{
			users.add(_user);
			//System.out.println("User " + _user.getId() + " added.");
		}
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
