import java.util.ArrayList;
import java.util.Date;

public class Tweet {
	
	private User author;
	private Date date;
	private String text;
	private ArrayList<User> retweets;
	
	public Tweet(User _author, Date _date, String _text)
	{
		author = _author;
		date = _date;
		text = _text; 
		retweets = new ArrayList<User>();
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<User> getRetweets() {
		return retweets;
	}

	public void setRetweets(ArrayList<User> retweets) {
		this.retweets = retweets;
	}
	
	
}
