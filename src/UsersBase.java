import java.util.HashMap;


public class UsersBase {
	
	private HashMap<String,User> users;
	private int[] nbUsersPerCentrality;

	public UsersBase()
	{
		users = new HashMap<String,User>();
		nbUsersPerCentrality = new int[5];
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
		//System.out.println("User " + _user.getId() + " added.");
	}
		
	public User getUser(String id)
	{
		return users.get(id);
	}
	
	public void setUsersCentrality()
	{	
		for(String id : users.keySet())
		{	
			User u = getUser(id);
			int i = u.getInternalLinksNumber();	
			if(i < 10)
			{
				u.setCentrality("blue");
				nbUsersPerCentrality[0] += 1;
			}
			else if(i < 100)
			{
				u.setCentrality("green");
				nbUsersPerCentrality[1] += 1;
			}
			else if(i < 1000)
			{
				u.setCentrality("yellow");
				nbUsersPerCentrality[2] += 1;
			}
			else if(i < 10000)
			{
				u.setCentrality("orange");
				nbUsersPerCentrality[3] += 1;
			}
			else
			{
				u.setCentrality("red");
				nbUsersPerCentrality[4] += 1;
			}
		}
	}
	
	public int getNumberOfUsers()
	{
		return users.size();
	}
		
	public HashMap<String,User> getUsers()
	{
		return users;
	}
	
	public int[] getNumberOfUsersPerCentrality()
	{
		return nbUsersPerCentrality;
	}
	
}
