import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		BufferedReader csvReader;
		String row;
		try {
			csvReader = new BufferedReader(new FileReader("./Data/climat.csv"));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split("\t");
			    System.out.println(data.length + " - " + data[1]);
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
