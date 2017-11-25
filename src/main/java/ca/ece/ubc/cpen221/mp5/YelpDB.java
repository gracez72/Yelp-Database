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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.*;

public class YelpDB<T> implements MP5Db<T> {

	private HashMap<String, User> userByID;
	private HashMap<String, Business> businessbyID;
	private HashMap<String, Review> reviewbyID;

	// Yelp DB Constructor

	public YelpDB(String businessFile, String userFile, String reviewFile) {
		try {
			ParseJSON(businessFile);
			ParseJSON(userFile);
			ParseJSON(reviewFile);
			
		} catch (IOException e) {
			System.out.println("ERROR: filenames not found.");
		}

	}
	
	public HashMap<String, Business> getBusinessbyID() {
		return this.businessbyID;
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
		/*
		 * 1. Given a set of restaurant objects, loop through set and set the first k
		 * restaurants to each be their own cluster.
		 * 
		 * 
		 */
		List<HashSet<T>> kMeansClusters = new ArrayList<HashSet<T>>();

		int tracker = 0;
		ArrayList<T> centroids = new ArrayList<T>();
		HashMap<T, ArrayList<Integer>> centroidCoord = new HashMap<T, ArrayList<Integer>>();
		for (T object : this.businesses) {
			if (tracker == k)
				break;

			centroids.add(object);
			centroidCoord.put(object, Arrays.asList(object.getX(), object.getY()));

			tracker++;
		}

		HashMap<T, T> clustering = new HashMap<T, T>();
		for (T object : this.businesses) {
			double currentDistance = -1.0;
			double minDistance = Math.pow(centroids.get(0).getX() - object.getX(), 2)
					+ Math.pow(centroids.get(0).getY() - object.getY(), 2);
			T closest;

			for (T centers : centroids) {
				if (object.equals(centers))
					break;
				currentDistance = Math.pow(centers.getX() - object.getX(), 2)
						+ Math.pow(centers.getY() - object.getY(), 2);
				if (currentDistance < minDistance)
					closest = centers;
			}

			clustering.put(object, closest);
		}

		for (T centers : centroids) {
			double xValue = centroidCoord.get(centers).get(0);
			double yValue = centroidCoord.get(centers).get(1);
			int counter = 1;

			for (T object : clustering.keySet()) {
				if (clustering.get(object).equals(centers)) {
					xValue += object.getX();
					yValue += object.getY();
					counter++;
				}
			}

			double meanX = xValue / counter;
			double meanY = yValue / counter;
		}

		return null;
	}

	/**
	 * 
	 * @param user
	 *            represents a user_id in the database
	 * @return a function that predicts the user's ratings for objects (of type T)
	 *         in the database of type MP5Db<T>. The function that is returned takes
	 *         two arguments: one is the database and other is a String that
	 *         represents the id of an object of type T.
	 */
	@Override
	public ToDoubleBiFunction<MP5Db<T>, String> getPredictorFunction(String user) {
		// ASSUMPTIONS ABOUT REPRESENTATION:
		// DB contains Map with user id as key and user as value
		// User has a list of reviews
		// review has a rating
		// review has a restaurant
		// DB contains Map with restaurant id and restaurant
		// assumes Restaurants contains a integer "priciness"

		// x represents restaurant price
		// y represents users stars
		ArrayList<String> reviewList = userByID.get(user).getReviewList();
		List<Integer> x = reviewList.stream().map(review_id -> reviewbyID.get(review_id))
				                             .map(review -> review.getBusinessID())
				                             .map(business_id -> businessbyID.get(business_id).getPrice())
				                             .collect(Collectors.toList());
		
		List<Integer> y = reviewList.stream().map(review_id -> reviewbyID.get(review_id))
				                             .map(review -> review.getStars()).collect(Collectors.toList());
		

		double x_mean = x.stream().reduce(0, Integer::sum) / reviewList.stream().count();
		double y_mean = y.stream().reduce(0, Integer::sum)/ reviewList.stream().count();
		
		double Sxx = x.stream().map(x_val -> Math.pow(x_val - x_mean,2))
				               .reduce(0.0, Double::sum);
		double Syy = y.stream().map(y_val -> Math.pow(y_val - y_mean, 2))
				               .reduce(0.0, Double::sum);
		
		double Sxy = 0;
		
		for (int i = 0; i < reviewList.stream().count(); i++) {
			Sxy += (x.get(i) - x_mean) * (y.get(i) - y_mean);
		}
		
		double b = Sxy/Sxx;
		double a = y_mean - (b * x_mean);
		double R2 = Math.pow(Sxy, 2) / (Sxx * Syy);
		
		ToDoubleBiFunction<MP5Db<T>, String> fnc = (db, business) -> (a + b * ((Business) db.getBusinessbyID().get(business)).getPrice());
		return fnc;
	    
		
	}

	// JSON Parser
	public static void ParseJSON(String filename) throws IOException {
		// TODO : Throw exception if nullPointerException?
		// Store as attributes for each restaurant
		BufferedReader br = new BufferedReader(new FileReader(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rms0Ocy1zerHj7RBhp3zaupQEJgu8ks5aIiykwA%3D%3D"));
		String line;
		while ((line = br.readLine()) != null) {
			JsonReader jr = Json.createReader(new StringReader(line));
			JsonObject obj = jr.readObject();
			jr.close();

			System.out.println("Name " + obj.getString("name"));
			System.out.println(
					"Longitude and Latitude: " + obj.getJsonNumber("longitude") + "," + obj.getJsonNumber("latitude"));
			System.out.println("Open? " + obj.getBoolean("open"));
			System.out.println("Url: " + obj.getString("url") + "\n");
		}

		br.close();

	}
	
	//Review Parser
	//Add each reviewId to User


}
