import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
		UsersBase base = new UsersBase();
		UsersGraph graph = new UsersGraph(base);
		
		// Add some users
		for(int i = 0; i < 10; i++)
		{
			base.addUser(new User(""+i));
		}
		
		// Add some links to users
		Random rand = new Random();
		for(User u : base.getUsers())
		{
			int i = rand.nextInt(10 - 0 + 1) + 0;
			while(i > 0)
			{
				int j = rand.nextInt(10);
				if(u.getId().compareTo(""+j) != 0)
				{
					u.addExternalLink(base.getUser(""+j));
				}
				i--;
			}
		}
		
		// Find the maximum amount of links for 1 user
		base.setMaxLinks();
		// Build nodes and edges
		graph.build();
		// Display the graph
		graph.displayGraph(); 
	}

}
