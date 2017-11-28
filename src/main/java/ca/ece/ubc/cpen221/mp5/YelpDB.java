package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;
import com.google.gson.Gson;
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

	// Catch exception later and print "ERR: INVALID_RESTAURANT_STRING"
	public JsonObject getRestaurant(String businessID) throws InvalidBusinessException {
		JsonObject obj = Json.createObjectBuilder().build();

		if (!businessbyID.containsKey(businessID))
			throw new InvalidBusinessException();
		else {
			Business restaurant = businessbyID.get(businessID);
			obj = Json.createObjectBuilder().add("open", restaurant.isOpen()).add("url", restaurant.getURL())
					.add("longitude", restaurant.getCoordinates()[0])
					.add("neighbourhoods", new Gson().toJson(restaurant.getNeighbourhoods()))
					.add("business_id", restaurant.getBusinessID()).add("name", restaurant.getName())
					.add("categories", new Gson().toJson(restaurant.getCategories()))
					.add("state", restaurant.getState()).add("type", restaurant.getType())
					.add("stars", restaurant.getStars()).add("city", restaurant.getCity())
					.add("full_address", restaurant.getFullAddress()).add("review_count", restaurant.getReviewCount())
					.add("photo_url", restaurant.getPhotoURL())
					.add("schools", new Gson().toJson(restaurant.getSchools()))
					.add("latitude", restaurant.getCoordinates()[1]).add("price", restaurant.getPrice()).build();
		}

		return obj;
	}
	
	public JsonObject addUser(JsonObject in) throws InvalidUserException{
		try {
			String name = in.getString("name");
			User user = new User(name, "http://www.yelp.com/" + name.hashCode(), name.hashCode() + name, "user");
			return null;
		} catch (NullPointerException | ClassCastException c) {
			throw new InvalidUserException();
		}
		
	}

	/**
	 * Cluster objects into k clusters using k-means clustering
	 * 
	 * @param k
	 *            number of clusters to create (0 < k <= number of objects)
	 * @return a String, in JSON format, that represents the clusters
	 * @throws IOException 
	 */
	@Override
	public String kMeansClusters_json(int k) {
		List<HashSet<Business>> kMeansClusters = new ArrayList<HashSet<Business>>();
		boolean one = true;
		
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		
		for (Entry<String, Business> someEntry: businessbyID.entrySet()) {
			double currentX = someEntry.getValue().getCoordinates()[0];
			double currentY = someEntry.getValue().getCoordinates()[1];
			
			if(currentX < minX)
				minX = currentX;
			
			if(currentX > maxX)
				maxX = currentX;
			
			if (currentY < minY)
				minY = currentY;
			
			if(currentY > maxY)
				maxY = currentY;
		}
		
		ArrayList<double[]> centroids = new ArrayList<double[]> ();
		for(int index = 0; index < k; index++) {
			double[] randCoordinates = new double[2];
			Random r = new Random ();
			randCoordinates[0] = minX + (maxX - minX)*r.nextDouble();
			randCoordinates[1] = minY + (maxY - minY)*r.nextDouble();

			centroids.add(index, randCoordinates);
		}
		

		while (true) {
			List<HashSet<Business>> tempClusters = new ArrayList<HashSet<Business>>();
			tempClusters = clustering(centroids); //make concurrent later
			
			if (one) {
				kMeansClusters = tempClusters;
				one = false;
			}
		
			ArrayList<double[]> newcentroids = new ArrayList<double[]>();
			
			for (HashSet<Business> oneCluster : tempClusters) { //make concurrent later
				double xValue = 0;
				double yValue = 0;
				int counter = 0;

				for (Business object : oneCluster) {
					xValue += object.getCoordinates()[0];
					yValue += object.getCoordinates()[1];
					counter++;
				}
				double[] newCenter = new double[2];
				newCenter[0] = xValue / counter;
				newCenter[1] = yValue / counter;

				newcentroids.add(newCenter);
			}
			
			if(!one & kMeansClusters.equals(tempClusters))
				break;
			
			kMeansClusters.clear();
			kMeansClusters = tempClusters;
		}

		String json = "[";
		int cluster = 0;
		double weight = 5.0;
		
		for(HashSet<Business> oneCluster: kMeansClusters) {
			for (Business object: oneCluster) {
				json = json.concat("{\"x\": " + Double.toString(object.getCoordinates()[0]) + ", ");
				json = json.concat("\"y\": " + Double.toString(object.getCoordinates()[1]) + ", ");
				
				json = json.concat("\"name\": \"" + object.getName() + "\", ");
				
				json = json.concat("\"cluster\": " + Integer.toString(cluster) + ", ");
				json = json.concat("\"weight\": " + Double.toString(weight) + "}, ");
			}
			cluster++;
		}
		
		json = json.substring(0, json.length() -2).concat("]");
		
		BufferedWriter write;
		try {
			write = new BufferedWriter(new FileWriter("voronoi.json"));
			write.write(json);
			write.close();
		} catch (IOException e1) {
		}
		
		return json;
	}
	
	private ArrayList<HashSet<Business>> clustering (ArrayList<double[]> centroids) {
		ArrayList<HashSet<Business>> clusters = new ArrayList<HashSet<Business>>();
		for (int index = 0; index < centroids.size(); index++) {
			clusters.add(index, new HashSet<Business>());
		}
		
		for (Entry<String, Business> someEntry: businessbyID.entrySet()) { //clustering everything
			Business current = someEntry.getValue();
			
			double currentDistance = -1.0;
			double minDistance = Double.MAX_VALUE;
			double[] closest = new double[2];
			
			for (double[] centers: centroids) {
				currentDistance = euclideanDistance(current.getCoordinates()[0], current.getCoordinates()[1], centers[0], centers[1]);
				
				if (currentDistance < minDistance) {
					closest = centers;
					minDistance = currentDistance;
				}
			}
		
			
			clusters.get(centroids.indexOf(closest)).add(current);
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
			JsonReader jr = Json.createReader(new StringReader(line));
			JsonObject obj = jr.readObject();
			jr.close();

			if (objtype.equals("user")) {

				Type type = new TypeToken<HashMap<String, Integer>>() {
				}.getType();
				HashMap<String, Integer> votes = new Gson().fromJson(obj.getJsonObject("votes").toString(), type);

				userbyID.put(obj.getString("user_id"),
						new User(obj.getString("name"), obj.getInt("review_count"), obj.getString("user_id"),
								obj.getString("url"), obj.getJsonNumber("average_stars").doubleValue(),
								obj.getString("type"), votes));

			} else if (objtype.equals("review")) {

				Type type = new TypeToken<HashMap<String, Integer>>() {
				}.getType();
				HashMap<String, Integer> votes = new Gson().fromJson(obj.getJsonObject("votes").toString(), type);

				reviewbyID.put(obj.getString("review_id"),
						new Review(obj.getString("business_id"), obj.getInt("stars"), obj.getString("review_id"),
								obj.getString("type"), votes, obj.getString("text"), obj.getString("date"),
								obj.getString("user_id")));

				userbyID.get(obj.getString("user_id")).addReview(obj.getString("review_id"));

			} else {

				businessbyID.put(obj.getString("business_id"), new Business(obj.getString("url"), obj.getString("name"),
						obj.getString("business_id"), obj.getJsonNumber("longitude").doubleValue(),
						obj.getJsonNumber("latitude").doubleValue(), obj.getInt("price"), obj.getString("photo_url"),
						obj.getInt("review_count"),
						obj.getJsonArray("schools").stream().map(school -> school.toString())
								.collect(Collectors.toCollection(ArrayList::new)),
						obj.getString("state"), obj.getString("full_address"), obj.getBoolean("open"),
						obj.getJsonArray("neighborhoods").stream().map(neighborhood -> neighborhood.toString()).collect(
								Collectors.toCollection(ArrayList::new)),
						obj.getString("city"), obj.getString("type"), obj.getJsonArray("categories").stream()
								.map(category -> category.toString()).collect(Collectors.toCollection(ArrayList::new)),
						obj.getInt("stars")));
			}

		}

		br.close();

	}

}
