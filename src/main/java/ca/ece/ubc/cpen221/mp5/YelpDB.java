package ca.ece.ubc.cpen221.mp5;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;

import javax.json.*;

public class YelpDB<T> implements MP5Db<T>{
	
	private Set<Business> businesses;
	private Set<Review> reviews;
	private Set<User> users;
	
	//Yelp DB Constructor
	public YelpDB(T[] businesses, T[] reviews, T[] users){
	}
	
	/**
	 * Perform a structured query and return the set of objects that matches the
	 * query
	 * 
	 * @param queryString
	 * @return the set of objects that matches the query
	 */
	@Override
	public Set<T> getMatches(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Cluster objects into k clusters using k-means clustering
	 * 
	 * @param k
	 *            number of clusters to create (0 < k <= number of objects)
	 * @return a String, in JSON format, that represents the clusters
	 */
	@Override
	public String kMeansClusters_json(int k) {
		/*1. Given a set of restaurant objects, loop through set and set
		 *   the first k restaurants to each be their own cluster.
		 *		
		 * 
		 */
		List<HashSet<T>> kMeansClusters = new ArrayList<HashSet<T>> ();
		
		int tracker = 0;
		ArrayList<Business> centroids = new ArrayList<Business> ();
		HashMap<Business, ArrayList<Double>> centroidCoord = new HashMap<Business, ArrayList<Double>> ();
		for (Business object: this.businesses) {
			if (tracker == k)
				break;
	
			centroids.add(object);
			ArrayList<Double> coordinates = new ArrayList<Double> ();
			coordinates.add(object.getCoordinates()[0]);
			coordinates.add(object.getCoordinates()[1]);
			centroidCoord.put(object, coordinates);
			
			tracker++;
		}
	
		HashMap<Business, Business> clustering = new HashMap<Business, Business> ();
		for (Business object: this.businesses) {
			double currentDistance = -1.0;
			double minDistance = euclideanDistance(object.getCoordinates()[0], object.getCoordinates()[1], centroids.get(0).getCoordinates()[0], centroids.get(0).getCoordinates()[1]);
			Business closest = null;
			
			for (Business centers: centroids) {
				if (object.equals(centers))
					break;
				currentDistance = euclideanDistance(object.getCoordinates()[0], object.getCoordinates()[1], centers.getCoordinates()[0], centers.getCoordinates()[1]);
				if (currentDistance < minDistance)
					closest = centers;
			}
			
			clustering.put(object, closest);	
		}
	
		for (Business centers: centroids) {
			double xValue = centroidCoord.get(centers).get(0);
			double yValue = centroidCoord.get(centers).get(1);
			int counter = 1;
			
			for (Business object: clustering.keySet()) {
				if (clustering.get(object).equals(centers)) {
					xValue += object.getCoordinates()[0];
					yValue += object.getCoordinates()[1];
					counter++;
				}
			}
			
			double meanX = xValue/counter;
			double meanY = yValue/counter;
		}
		
		
		return null;
	}
	
	private double euclideanDistance (double x1, double y1, double x2, double y2) {
		return Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2);
	}

	/**
	 * 
	 * @param user
	 *            represents a user_id in the database
	 * @return a function that predicts the user's ratings for objects (of type
	 *         T) in the database of type MP5Db<T>. The function that is
	 *         returned takes two arguments: one is the database and other
	 *         is a String that represents the id of an object of type T.
	 */
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
