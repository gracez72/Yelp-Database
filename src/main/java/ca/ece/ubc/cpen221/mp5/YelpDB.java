package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.tool.Grammar;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.net.URLConnection;

public class YelpDB<T> implements MP5Db<T> {

	private ConcurrentHashMap<String, User> userbyID;
	private static ConcurrentHashMap<String, Business> businessbyID;
	private ConcurrentHashMap<String, Review> reviewbyID;
	
	private HashMap<String, Integer> kMeansClusters = new HashMap<String, Integer> ();

	public static void main(String[] args) {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");

		String query = "in(Terminal Ave) && price < 3";
		parseQuery(query);
	}
	
	
	
	// Yelp DB Constructor
	public YelpDB(String businessFile, String userFile, String reviewFile) {
		try {
			userbyID = new ConcurrentHashMap<String, User>();
			businessbyID = new ConcurrentHashMap<String, Business>();
			reviewbyID = new ConcurrentHashMap<String, Review>();

			ParseJSON(businessFile, "business");
			ParseJSON(userFile, "user");
			ParseJSON(reviewFile, "review");

		} catch (IOException e) {
			System.out.println("ERROR: filenames not found.");
		}

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
		CharStream stream = new ANTLRInputStream(queryString);
		GrammarLexer lexer = new GrammarLexer(stream);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		
		ParseTree tree = parser.query();
		ParseTreeWalker walker = new ParseTreeWalker();
		GrammarListenerGetNodes listener = new GrammarListenerGetNodes();
		walker.walk(listener, tree);
		
		Set<Business> businesses = new HashSet<Business> ();
		for(Entry<String, Business> someentry: businessbyID.entrySet()) {
			businesses.add(someentry.getValue());
		}
		
		Set<Business> filteredBusinesses = businesses.stream().filter(x -> x.getNeighbourhoods().contains(listener.getNeighbourhoods().get(0)))
					.collect(Collectors.toSet());
		
			System.out.println(filteredBusinesses);
		return null;
	}
	
	public static String parseQuery(String query) {
		//Set<T> matches = getMatches(query);

		CharStream stream = new ANTLRInputStream(query);
		GrammarLexer lexer = new GrammarLexer(stream);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		
		ParseTree tree = parser.query();
		ParseTreeWalker walker = new ParseTreeWalker();
		GrammarListenerGetNodes listener = new GrammarListenerGetNodes();
		walker.walk(listener, tree);


		
		return null;
	}
	
	
	
	public ConcurrentHashMap<String, Business> getBusinessbyID() {
		return this.businessbyID;
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
			user.setuser_id();
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
	 * @throws IOException 
	 */
	@Override
	public String kMeansClusters_json(int k) {
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		
		//Find the range of x- and y- coordinates for businesses
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
		
		//Make the first set of centroids a random value (x,y) within the above calculated range
		ArrayList<double[]> centroids = new ArrayList<double[]> ();
		for(int index = 0; index < k; index++) {
			double[] randCoordinates = new double[2];
			Random r = new Random ();
			randCoordinates[0] = minX + (maxX - minX)*r.nextDouble();
			randCoordinates[1] = minY + (maxY - minY)*r.nextDouble();

			centroids.add(index, randCoordinates);
		}

		List<HashSet<Business>> kMeansClusters = new ArrayList<HashSet<Business>>();
		boolean one = true;
		boolean empty = false;
		while (true) {
			List<HashSet<Business>> tempClusters = new ArrayList<HashSet<Business>>();
			tempClusters = clustering(centroids); 
			empty = false;
			
			if (one)
				kMeansClusters = tempClusters;
		
			ArrayList<double[]> newcentroids = new ArrayList<double[]>();
			
			//Calculates the new centroids for each of the k clusters
			for (HashSet<Business> oneCluster : tempClusters) { 
				//If cluster is empty, a new random centroid is assigned as above
				if (oneCluster.isEmpty()) {
					double[] randCoordinates = new double[2];
					Random r = new Random ();
					randCoordinates[0] = minX + (maxX - minX)*r.nextDouble();
					randCoordinates[1] = minY + (maxY - minY)*r.nextDouble();
					newcentroids.add(randCoordinates);
					empty = true;
				}
				
				//If cluster is not empty, mean value of cluster is found and 
				//new centroid is assigned to that point
				else {
					double xValue = 0;
					double yValue = 0;
					int counter = 0;
	
					for (Business object : oneCluster) {
						xValue += object.getCoordinates()[0];
						yValue += object.getCoordinates()[1];
						counter++;
					}
					double[] meanCenter = new double[2];
					meanCenter[0] = xValue / counter;
					meanCenter[1] = yValue / counter;
	
					newcentroids.add(meanCenter);
				}
			}
			
			//Algorithm continues until the clusters no longer change and
			//there are no empty clusters
			if(!one && !empty && kMeansClusters.equals(tempClusters))
				break;
			
			one = false;
			kMeansClusters.clear();
			kMeansClusters = tempClusters;
			centroids = newcentroids;
		}
		
		//Constructs string in JSON format for each cluster
		String json = "[";
		int cluster = 0;
		double weight = 5.0;
		
		for(HashSet<Business> oneCluster: kMeansClusters) {
			for (Business object: oneCluster) {
				this.kMeansClusters.put(object.getName(), cluster);
				json = json.concat("{\"x\": " + Double.toString(object.getCoordinates()[0]) + ", ");
				json = json.concat("\"y\": " + Double.toString(object.getCoordinates()[1]) + ", ");
				
				json = json.concat("\"name\": \"" + object.getName() + "\", ");
				
				json = json.concat("\"cluster\": " + Integer.toString(cluster) + ", ");
				json = json.concat("\"weight\": " + Double.toString(weight) + "}, ");
			}
			cluster++;
		}

		json = json.substring(0, json.length() -2).concat("]");
		
		//Writes this string to a file
		BufferedWriter write;
		try {
			write = new BufferedWriter(new FileWriter("voronoi.json"));
			write.write(json);
			write.close();
		} catch (IOException e1) {
		}
		
		return json;
	}
	
	
	/**
	 * Helper function for the kMeansCluster method. Takes in a list of the
	 * coordinates of k centroids, and clusters the businesses in the database
	 * according to which center they are closest to.
	 * 
	 * @param ArrayList of centroids, with each entry being a size 2 array with
	 * 		  arr[0] = x-coordinate and arr[1] = y-coordinate of the centroid
	 * 			-Size of array list is always equal to k
	 * @return ArrayList of HashSet<Business> where each entry in the array list 
	 * 		   is a different cluster, and each entry contains all the businesses
	 * 		   in that cluster
	 * 			-ArrayList can hold empty HashSets, meaning that a cluster is empty
	 */
	private ArrayList<HashSet<Business>> clustering (ArrayList<double[]> centroids) {
		ArrayList<HashSet<Business>> clusters = new ArrayList<HashSet<Business>>();
		
		//Initializing ArrayList to hold k empty clusters, where k = centroids.size()
		for (int index = 0; index < centroids.size(); index++) {
			clusters.add(index, new HashSet<Business>());
		}
		
		//Loop through set of businesses in database and find its closest centroid
		for (Entry<String, Business> someEntry: businessbyID.entrySet()) { 
			Business current = someEntry.getValue();
			
			double currentDistance = -1.0;
			double minDistance = Double.MAX_VALUE;
			double[] closest = new double[2];
			
			//Loop through the centroids to find the closest centroid to the current business
			for (double[] centers: centroids) {
				currentDistance = euclideanDistance(current.getCoordinates()[0], current.getCoordinates()[1], centers[0], centers[1]);
				
				if (currentDistance < minDistance) {
					closest = centers;
					minDistance = currentDistance;
				}
			}
		
			//Add the business to the ArrayList position corresponding to the
			//same position of its center in the centroids array
			clusters.get(centroids.indexOf(closest)).add(current);
		}

		return clusters;
	}

	/**
	 * Returns map of each business to its cluseter, used for debugging kClusters
	 * @return HashMap that has each Business (by name) mapped to its
	 * 		cluster number
	 */
	public HashMap<String, Integer> getkMeans () {
		return kMeansClusters;
	}
	
	/**
	 * Calculates squared distance between two Cartesian points
	 * @param x1, y1
	 * 				coordinates of a point (x1, y1), values are not null 
	 * 				and can be negative 
	 * @param x2, y2
	 * 				coordinates of a second point (x2, y2), values are not null
	 * 				and can be negative
	 * @return the squared distance between the two points inputted into the function,
	 * 		   returns 0 if the two inputted points are the same
	 */
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
