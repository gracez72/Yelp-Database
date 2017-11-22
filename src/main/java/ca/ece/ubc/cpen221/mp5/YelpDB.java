package ca.ece.ubc.cpen221.mp5;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;

import javax.json.*;

public class YelpDB<T> implements MP5Db<T>{
	
	private Set<T> businesses;
	private Set<T> reviews;
	private Set<T> users;
	
	//Yelp DB Constructor
	public YelpDB(String businesses, String reviews, String user){
		
	}
	
	@Override
	public Set<T> getMatches(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String kMeansClusters_json(int k) {
		//Given a set of x y coordinates
		return null;
	}
	

	@Override
	public ToDoubleBiFunction <MP5Db<T>, String> getPredictorFunction(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	
	//JSON Parser
	public static void ParseJSON(String filename) throws IOException{
		//TODO : Throw exception if nullPointerException?
		//       Store as attributes for each restaurant
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Grace\\eclipse-workspace\\f17-mp51-gracez72_andradazoltan\\data\\restaurants.json"));
		String line;
		while ((line = br.readLine()) != null) {
			JsonReader jr = Json.createReader(new StringReader(line));
			JsonObject obj = jr.readObject();
			jr.close();
			
			System.out.println("Name " + obj.getString("name"));
			System.out.println("Longitude and Latitude: " + obj.getJsonNumber("longitude") + "," + obj.getJsonNumber("latitude"));
			System.out.println("Open? " + obj.getBoolean("open"));
			System.out.println("Url: " + obj.getString("url") + "\n");
		}
		
		br.close();
		
	}
	
	public static void main(String[] args) {
		try {
			ParseJSON("what");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
