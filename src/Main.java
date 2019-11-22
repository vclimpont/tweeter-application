import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		BufferedReader csvReader;
		String row;
		int i = 1;
		try {
			csvReader = new BufferedReader(new FileReader("./Data/Foot.csv"));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split("\t");
			    System.out.println(i + " - " + data[1]);
			    i++;
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
