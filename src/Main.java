import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
		UsersBase base = new UsersBase();
		UsersGraph graph = new UsersGraph(base);
		
		for(int i = 0; i < 50; i++)
		{
			base.addUser(new User(""+i));
		}
		
		Random rand = new Random();
		for(User u : base.getUsers())
		{
			int i = rand.nextInt(10 - 0 + 1) + 0;
			while(i > 0)
			{
				int j = rand.nextInt(50);
				u.addLinkedUser(base.getUser(""+j));
				i--;
			}
		}
		
		base.setMaxLinks();
		graph.build();
		graph.displayGraph(); 
	}

}
