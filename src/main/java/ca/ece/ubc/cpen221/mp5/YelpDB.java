package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;

import javax.json.*;

public class YelpDB<T> implements MP5Db<T> {

	private HashMap<String, User> userbyID;
	private HashMap<String, Business> businessbyID;
	private HashMap<String, Review> reviewbyID;

	// Yelp DB Constructor

	public YelpDB(String businessFile, String userFile, String reviewFile) {
		try {
			userbyID = new HashMap<String, User>();
			businessbyID = new HashMap<String, Business>();
			reviewbyID = new HashMap<String, Review>();
			
			ParseJSON(businessFile, "business");
			ParseJSON(userFile, "user");
			ParseJSON(reviewFile, "review");
			
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

		ArrayList<String> reviewList = userbyID.get(user).getReviewList();
		List<Integer> x = reviewList.stream().map(review_id -> reviewbyID.get(review_id))
				.map(review -> review.getBusinessID()).map(business_id -> businessbyID.get(business_id).getPrice())
				.collect(Collectors.toList());

		List<Integer> y = reviewList.stream().map(review_id -> reviewbyID.get(review_id))
				.map(review -> review.getStars()).collect(Collectors.toList());

		double x_mean = x.stream().reduce(0, Integer::sum) / reviewList.stream().count();
		double y_mean = y.stream().reduce(0, Integer::sum) / reviewList.stream().count();

		double Sxx = x.stream().map(x_val -> Math.pow(x_val - x_mean, 2)).reduce(0.0, Double::sum);
		double Syy = y.stream().map(y_val -> Math.pow(y_val - y_mean, 2)).reduce(0.0, Double::sum);

		double Sxy = 0;

		for (int i = 0; i < reviewList.stream().count(); i++) {
			Sxy += (x.get(i) - x_mean) * (y.get(i) - y_mean);
		}

		double b = Sxy / Sxx;
		double a = y_mean - (b * x_mean);
		double R2 = Math.pow(Sxy, 2) / (Sxx * Syy);

		ToDoubleBiFunction<MP5Db<T>, String> fnc = (db,
				business) -> (a + b * ((Business) db.getBusinessbyID().get(business)).getPrice());
		return fnc;

	}



	// JSON Parser 
	public void ParseJSON(String url, String objtype) throws IOException {
		URL filename = new URL(url);
		URLConnection fn = filename.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(fn.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			JsonReader jr = Json.createReader(new StringReader(line));
			JsonObject obj = jr.readObject();
			jr.close();

			if (objtype.equals("user")) {

				Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
				HashMap<String, Integer> votes = new Gson().fromJson(obj.getJsonObject("votes").toString(), type);
				
				userbyID.put(obj.getString("user_id"), new User(obj.getString("name"), obj.getInt("review_count"), obj.getString("user_id"),
						obj.getString("url"), obj.getJsonNumber("average_stars").doubleValue(),
						obj.getString("type"), votes));
				
			} else if (objtype.equals("review")) {
				
				Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
				HashMap<String, Integer> votes = new Gson().fromJson(obj.getJsonObject("votes").toString(), type);

				reviewbyID.put(obj.getString("review_id"),
						new Review(obj.getString("business_id"), obj.getInt("stars"), obj.getString("review_id"),
								obj.getString("type"), votes, obj.getString("text"), obj.getString("date"),
								obj.getString("user_id")));

				userbyID.get(obj.getString("user_id")).addReview(obj.getString("review_id"));
				
			} else {
				
				businessbyID.put(obj.getString("business_id"), 
						         new Business(obj.getString("url"), obj.getString("name"),
				                 obj.getString("business_id"),obj.getJsonNumber("longitude").doubleValue(), 
				                 obj.getJsonNumber("latitude").doubleValue(), obj.getInt("price"), obj.getString("photo_url"),
				                 obj.getInt("review_count"), obj.getJsonArray("schools").stream().map(school -> school.toString()).collect(Collectors.toCollection(ArrayList::new)), 
				                 obj.getString("state"), obj.getString("full_address"), obj.getBoolean("open"),
				                 obj.getJsonArray("neighborhoods").stream().map(neighborhood -> neighborhood.toString()).collect(Collectors.toCollection(ArrayList::new)),
				                 obj.getString("city"), obj.getString("type"), obj.getJsonArray("categories").stream().map(category -> category.toString()).collect(Collectors.toCollection(ArrayList::new))));
			}

		}

		br.close();

	}

}
