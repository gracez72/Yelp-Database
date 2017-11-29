package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.net.URLConnection;

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

	public String getRestaurant(String businessID) {
		if (!businessbyID.containsKey(businessID))
			return "ERR: INVALID_RESTAURANT_STRING";
		else {
			Business restaurant = businessbyID.get(businessID);
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			return gson.toJson(restaurant);
		}

	}

	public String addReview(String line) {
		try {
			Gson gson = new Gson();
			Review review = gson.fromJson(line, Review.class);
			if (review.getBusinessID() == null | review.getDate() == null | review.getUserID() == null) {
				return "ERR: NOT_ENOUGH_REVIEW_INFO";
			} else {
				reviewbyID.put(review.getReviewID(), review);
				userbyID.get(review.getUserID()).addReview(review.getReviewID());
				return gson.toJson(review);
			}
		} catch (NullPointerException | ClassCastException | JsonSyntaxException c) {
			return "ERR: NO_SUCH_REVIEW";
		}

	}

	public String addUser(String line) {
		try {
			Gson gson = new Gson();
			User user = gson.fromJson(line, User.class);
			if (user.getName() == null) return "ERR:NOT_ENOUGH_USER_INFO";
			userbyID.put(user.getUserID(), user);
			return gson.toJson(user);
		} catch (NullPointerException | ClassCastException | JsonSyntaxException c) {
			return "ERR: NO_SUCH_USER";
		}

	}

	public String addRestaurant(String line) {
		try {
			Gson gson = new Gson();
			Restaurant restaurant = gson.fromJson(line, Restaurant.class);
			if (restaurant.getCity() == null | restaurant.getCoordinates() == null |
				restaurant.getName() == null | restaurant.getState() == null) return "ERR:NOT_ENOUGH_RESTAURANT_INFO";
			businessbyID.put(restaurant.getBusinessID(), restaurant);
			return gson.toJson(restaurant);
		} catch (NullPointerException | ClassCastException | JsonSyntaxException c) {
			return "ERR: NO_SUCH_RESTAURANT";
		}

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
		List<HashSet<Business>> kMeansClusters = new ArrayList<HashSet<Business>>();

		ArrayList<Business> centroids = new ArrayList<Business>();
		for (Entry<String, Business> someEntry : businessbyID.entrySet()) {
			centroids.add(someEntry.getValue());

			if (centroids.size() == k)
				break;
		}

		List<HashSet<Business>> tempClusters = new ArrayList<HashSet<Business>>();

		while (true) {
			tempClusters = clustering(centroids); // make concurrent later

			ArrayList<Business> newcentroids = new ArrayList<Business>();

			for (Business centers : centroids) { // make concurrent later
				double xValue = centers.getCoordinates()[0];
				double yValue = centers.getCoordinates()[0];
				int counter = 1;

				HashSet<Business> oneCluster = new HashSet<Business>();
				for (HashSet<Business> theCluster : tempClusters) {
					if (theCluster.contains(centers))
						oneCluster = theCluster;
				}

				for (Business object : oneCluster) {
					xValue += object.getCoordinates()[0];
					yValue += object.getCoordinates()[1];
					counter++;
				}

				double meanX = xValue / counter;
				double meanY = yValue / counter;

				Business closest = null;
				double currentDistance = -1.0;
				double minDistance = euclideanDistance(meanX, meanY, xValue, yValue);

				for (Business object : oneCluster) {
					currentDistance = euclideanDistance(meanX, meanY, object.getCoordinates()[0],
							object.getCoordinates()[1]);

					if (currentDistance < minDistance)
						closest = object;
				}
				newcentroids.add(closest);
			}

			if (centroids.equals(newcentroids))
				break;
			centroids = newcentroids;
		}

		kMeansClusters = tempClusters;
		String json = "";
		int cluster = 0;
		double weight = 1.0;

		for (HashSet<Business> oneCluster : kMeansClusters) {
			for (Business object : oneCluster) {
				json = json.concat("{\"x\": " + Double.toString(object.getCoordinates()[0]) + ", ");
				json = json.concat("\"y\": " + Double.toString(object.getCoordinates()[1]) + ", ");

				json = json.concat("\"name\": " + object.getName() + ", ");

				json = json.concat("\"cluster\": " + Integer.toString(cluster) + ", ");
				json = json.concat("\"weight\": " + Double.toString(weight) + "}, ");
			}
			cluster++;
		}
		return json;
	}

	private ArrayList<HashSet<Business>> clustering(ArrayList<Business> centroids) {
		HashMap<Business, Business> clustering = new HashMap<Business, Business>();
		ArrayList<HashSet<Business>> clusters = new ArrayList<HashSet<Business>>();

		for (Entry<String, Business> someEntry : businessbyID.entrySet()) { // clustering everything
			Business current = someEntry.getValue();

			double currentDistance = -1.0;
			double minDistance = euclideanDistance(current.getCoordinates()[0], current.getCoordinates()[1],
					centroids.get(0).getCoordinates()[0], centroids.get(0).getCoordinates()[1]);
			Business closest = centroids.get(0);

			for (Business centers : centroids) {
				if (current.equals(centers))
					break;
				currentDistance = euclideanDistance(current.getCoordinates()[0], current.getCoordinates()[1],
						centers.getCoordinates()[0], centers.getCoordinates()[1]);

				if (currentDistance < minDistance)
					closest = centers;
			}

			clustering.put(current, closest);
		}

		for (Business centers : centroids) {
			HashSet<Business> oneCluster = new HashSet<Business>();
			oneCluster.add(centers);

			for (Business someBusiness : clustering.keySet()) {
				if (clustering.get(someBusiness).equals(centers))
					oneCluster.add(someBusiness);
			}
			clusters.add(oneCluster);
		}

		return clusters;
	}

	private double euclideanDistance(double x1, double y1, double x2, double y2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
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
			Gson gson = new Gson();
			if (objtype.equals("user")) {

				User user = gson.fromJson(line, User.class);
				userbyID.put(user.getUserID(), user);

			} else if (objtype.equals("review")) {

				Review review = gson.fromJson(line, Review.class);
				reviewbyID.put(review.getReviewID(), review);
				userbyID.get(review.getUserID()).addReview(review.getReviewID());

			} else {
				Business restaurant = gson.fromJson(line, Business.class);
				businessbyID.put(restaurant.getBusinessID(), restaurant);
			}

		}

		br.close();

	}

}
