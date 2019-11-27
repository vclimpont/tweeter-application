import java.util.ArrayList;


public class UsersBase {
	
	private ArrayList<User> users;
	
	public UsersBase()
	{
		users = new ArrayList<User>();
	}
	
	public void addUser(User _user)
	{
		users.add(_user);
	}
	
	public User getUser(String id)
	{
		for(User u : users)
		{
			if(u.getId() == id)
			{
				return u;
			}
		}
		System.out.println("User introuvable");
		return null;
	}
	
	public ArrayList<User> getUsers()
	{
		return users;
	}
}
