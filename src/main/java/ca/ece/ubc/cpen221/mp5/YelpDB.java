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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.net.URLConnection;

public class YelpDB<T> implements MP5Db<T> {
	private ConcurrentHashMap<String, User> userbyID;
	private ConcurrentHashMap<String, Business> businessbyID;
	private ConcurrentHashMap<String, Review> reviewbyID;
	
	private ArrayList<Business> businesses = new ArrayList<Business> ();
	private ConcurrentHashMap<Integer, Set<Business>> filteredByAtom = new ConcurrentHashMap <Integer, Set<Business>> ();
	
	private HashMap<String, Integer> kMeansClusters = new HashMap<String, Integer> ();	
	
	/**
	 * Yelp Database constructor. 
	 * 		Initializes contents of given files into a database.
	 * 
	 * @param businessFile
	 * @param userFile
	 * @param reviewFile
	 */
	public YelpDB(String businessFile, String userFile, String reviewFile) {
		try {
			userbyID = new ConcurrentHashMap<String, User>();
			businessbyID = new ConcurrentHashMap<String, Business>();
			reviewbyID = new ConcurrentHashMap<String, Review>();

			ParseJSON(businessFile, "business");
			ParseJSON(userFile, "user");
			ParseJSON(reviewFile, "review");
		} 
		
		catch (IOException e) {
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
	@SuppressWarnings("unchecked")
	public Set<T> getMatches(String queryString) {
		CharStream stream = new ANTLRInputStream(queryString);
		GrammarLexer lexer = new GrammarLexer(stream);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		parser.removeErrorListeners();
		
		ParseTree tree = parser.query();
		ParseTreeWalker walker = new ParseTreeWalker();
		GrammarListenerGetNodes listener = new GrammarListenerGetNodes();
		
		//If the query is in an incorrect format, then the parse tree will
		//throw an exception.
		try {
			walker.walk(listener, tree);
		} catch (IllegalArgumentException e){
			return null;
		}
		
		//Map of each of the different requests in the query, mapped to a unique integer.
		Map<Integer, String> atoms = listener.getAtoms();
		
		//Concurrrently creates another map (private field) that maps a set of business
		//objects satisfying a condition (atom) to the same integer that that condition is 
		//mapped to.
		filteredByAtom.clear();
		FilterAtoms t = new FilterAtoms (atoms, 0);
		ForkJoinPool.commonPool().invoke(t);
		
		Stack<String> stack = new Stack<String> ();
		stack = listener.getStack();
		
		Stack<Set<Business>> tempFilteredSets = new Stack<Set<Business>> ();
		
		while (!stack.isEmpty()) {
			Set<Business> tempFilteredSet = new HashSet<Business> ();
			
			String operand = "";
			int conditionOne = -1;
			int conditionTwo = -1;
			
			//The next set of conditions combines previous brackets
			if (stack.peek().equals("||") || stack.peek().equals("&&"))
				operand = stack.pop();
			//The next set of conditions is inside brackets
			else { 
				conditionOne = Integer.parseInt(stack.pop());
				conditionTwo = Integer.parseInt(stack.pop());
				operand = stack.pop();
			}
			
			//Depending on the above conditions, the filtered sets are chosen
			//and combined accordingly to form one large filtered set that
			//satisfies the conditions.
			if (operand.equals("||") && conditionOne == -1) {
				tempFilteredSet.addAll(tempFilteredSets.pop());
				tempFilteredSet.addAll(tempFilteredSets.pop());
				tempFilteredSets.push(tempFilteredSet);
			}
			
			else if (operand.equals("||")) {
				tempFilteredSet.addAll(filteredByAtom.get(conditionOne));
				tempFilteredSet.addAll(filteredByAtom.get(conditionTwo));
				tempFilteredSets.push(tempFilteredSet);
			}
			
			else if (operand.equals("&&") && conditionOne == -1) {
				Set<Business> tempOne = new HashSet<Business> ();
				tempOne.addAll(tempFilteredSets.pop());
				Set<Business> tempTwo = new HashSet<Business> ();
				tempTwo.addAll(tempFilteredSets.pop());
				
				tempFilteredSet = tempOne.stream().filter(x -> tempTwo.contains(x)).collect(Collectors.toSet());
				tempFilteredSet.addAll(tempTwo.stream().filter(x -> tempOne.contains(x)).collect(Collectors.toSet()));
				tempFilteredSets.push(tempFilteredSet);
			}
			
			else {
				Set<Business> tempOne = new HashSet<Business> ();
				tempOne.addAll(filteredByAtom.get(conditionOne));
				Set<Business> tempTwo = new HashSet<Business> ();
				tempTwo.addAll(filteredByAtom.get(conditionTwo));
				
				tempFilteredSet = tempOne.stream().filter(x -> tempTwo.contains(x)).collect(Collectors.toSet());
				tempFilteredSet.addAll(tempTwo.stream().filter(x -> tempOne.contains(x)).collect(Collectors.toSet()));
				tempFilteredSets.push(tempFilteredSet);
			}
		}
		
		//Some incorrectly structured queries will get passed by the parser
		//due to the way the grammar is written. This check ensures that
		//the query is correct.
		if (tempFilteredSets.size() > 1)
			return null;
	
		return (Set<T>) tempFilteredSets.pop();
	}
	
	/**
	 * Class uses ForkJoin framework to concurrently filter the businesses
	 * by the atoms in the user's request.
	 */
	@SuppressWarnings("serial")
	private class FilterAtoms extends RecursiveAction{
		private Map<Integer, String> atoms = new HashMap<Integer, String>();
		private int counter;

		private FilterAtoms(Map<Integer, String> atoms, int counter) {
			this.atoms = atoms;
			this.counter = counter;
		}

		protected void compute() {
			if (counter < atoms.size()) {
				FilterAtoms next = new FilterAtoms (this.atoms, counter + 1);
				next.fork();
				
				filterBusinesses(atoms.get(counter), counter);
				next.join();
			}
		}
	}
	
	/**
	 * Takes in a condition for the businesses and filters out the businesses
	 * in the database that satisfy that condition. Class run concurrently, with
	 * amount of threads = amount of atoms in the request.
	 * 
	 *  @param currentAtom - condition to be satisfied, first letter in the
	 *  				string specifies what kind of condition it is
	 *  @param counter - specifies what integer this condition is mapped to
	 *  				so that the filtered set can be mapped to the same integer
	 *  @modifies filteredByAtom - private field that maps a set of business objects
	 *  				to an integer that corresponds to a specific condition
	 */
	private void filterBusinesses (String currentAtom, int counter) {
		String operation = currentAtom.substring(0, 1);
		String condition = currentAtom.substring(1);

		Set<Business> currentFilter = new HashSet<Business> ();
		//Condition is a neighborhood
		if (operation.equals("I"))
			currentFilter = businesses.stream().filter(x -> x.getFormattedNeighborhoods().contains(condition))
					.collect(Collectors.toSet());

		//Condition is a category
		if (operation.equals("C"))
			currentFilter = businesses.stream().filter(x -> x.getFormattedCategories().contains(condition))
					.collect(Collectors.toSet());

		//Condition is a name
		if (operation.equals("N"))
			currentFilter = businesses.stream().filter(x -> x.getFormattedName().contains(condition))
					.collect(Collectors.toSet());

		//Condition is a rating
		if (operation.equals("R")) {
			if (condition.substring(0, 2).equals("<="))
				currentFilter = businesses.stream()
						.filter(x -> x.getStars() <= Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 2).equals(">="))
				currentFilter = businesses.stream()
						.filter(x -> x.getStars() >= Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 1).equals("="))
				currentFilter = businesses.stream()
						.filter(x -> x.getStars() == Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 1).equals("<"))
				currentFilter = businesses.stream()
						.filter(x -> x.getStars() < Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else
				currentFilter = businesses.stream()
						.filter(x -> x.getStars() > Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());
		}

		//Condition is a price
		if (operation.equals("P")) {
			if (condition.substring(0, 2).equals("<="))
				currentFilter = businesses.stream()
						.filter(x -> x.getPrice() <= Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 2).equals(">="))
				currentFilter = businesses.stream()
						.filter(x -> x.getPrice() >= Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 1).equals("="))
				currentFilter = businesses.stream()
						.filter(x -> x.getPrice() == Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else if (condition.substring(0, 1).equals("<"))
				currentFilter = businesses.stream()
						.filter(x -> x.getPrice() < Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());

			else
				currentFilter = businesses.stream()
						.filter(x -> x.getPrice() > Integer.parseInt(condition.substring(condition.length() - 1)))
						.collect(Collectors.toSet());
		}

		filteredByAtom.put(counter, currentFilter);
	}
	
	/**
	 * Takes in a query string of conditions, passes it to the getMatches method,
	 * and analyzes the return of the method to provide a user with a reply.
	 * 
	 * @param query - request for restaurants that satisfy specific conditions
	 * @return a string in JSON format of all the restaurants that
	 * 		satisfy the query, returns ERR: NO_MATCH if there are no
	 * 		restaurants that match, and returns ERR: INVALID_QUERY if
	 * 		the query is not structured correctly
	 */
	public String parseQuery(String query) {
		Set<T> matches = getMatches(query);
		try {
			if (matches.isEmpty())
				return "ERR: NO_MATCH";
			else {
				String reply = "";
				for (T business: matches) {
					Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					reply = reply.concat(gson.toJson(business));
				}
				return "Reply: " + reply;
			}
		} catch (NullPointerException e) {
			return "ERR: INVALID_QUERY";
		}
	}
	
	
	/**
	 * Retrieves business_id -> Business map
	 * 
	 * @return Business ConcurrentHashMap
	 */
	public ConcurrentHashMap<String, Business> getBusinessbyID() {
		return this.businessbyID;
	}

	/**
	 * Retrieves information about a restaurant based on its businessID
	 * 
	 * @param businessID
	 * @return Json formatted string representing restaurant information or error message
	 * 			returns ERR: INVALID_RESTAURANT_STRING if the restaurant doesn't exist
	 */
	public String getRestaurant(String businessID) {
		if (!businessbyID.containsKey(businessID))
			return "ERR: INVALID_RESTAURANT_STRING";
		else {
			Business restaurant = businessbyID.get(businessID);
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			return "Reply: " + gson.toJson(restaurant);
		}
	}

	/**
	 * Adds a review using information given from Client input to database.
	 * 
	 * @param line
	 * @requires review must have a businessID, userID, date, text
	 * @return Json formatted string representing review information or error message
	 * 			returns ERR: INVALID_REVIEW_STRING if information is missing
	 * 			return ERR: NO_SUCH_REVIEW if no information is given
	 */
	public String addReview(String line) {
		try {
			Gson gson = new Gson();
			Review review = gson.fromJson(line, Review.class);
			if (review.getBusinessID() == null |review.getUserID() == null|
				review.getDate() == null | review.getText() == null) return "ERR: INVALID_REVIEW_STRING";
			if (review.getReviewID() == null) review.setReviewID();
			reviewbyID.put(review.getReviewID(), review);
			userbyID.get(review.getUserID()).addReview(review.getReviewID());
			return "Reply: " + gson.toJson(review);

		} catch (NullPointerException | ClassCastException | JsonSyntaxException d) {
			return "ERR: NO_SUCH_REVIEW";
		}
	}
	
	/**
	 * Adds a user using information given from Client input to database.
	 * 
	 * @param line
	 * @return Json formatted string representing user information or error message
	 * 			returns ERR: INVALID_USER_STRING if information is missing
	 * 			return ERR: NO_SUCH_USER if no information is given
	 */
	public String addUser(String line) {
		try {
			Gson gson = new Gson();
			User user = gson.fromJson(line, User.class);
			if (user.getName() == null) return "ERR: INVALID_USER_STRING";
			user.setuserid();
			userbyID.put(user.getUserID(), user);
			return "Reply: " + gson.toJson(user);
		} catch (NullPointerException | ClassCastException | JsonSyntaxException c) {
			return "ERR: NO_SUCH_USER";
		}
	}

	/**
	 * Adds a restaurant using information given from Client input to database.
	 * 
	 * @param line
	 * @requires restaurant to have a name, state, address
	 * 			ASSUMPTIONS: IF NO COORDINATES ARE GIVEN, THE DEFAULT VALUES ARE 0.0
	 * @return Json formatted string representing restaurant information or error message
	 * 			returns ERR: NO_SUCH_RESTAURANT if no information is given
	 * 			returns ERR: INVALID_RESTAURANT_STRING if information is missing
	 */
	public String addRestaurant(String line) {
		try {
			Gson gson = new Gson();
			Restaurant restaurant = gson.fromJson(line, Restaurant.class);
			if (restaurant.getName() == null | restaurant.getState() == null | 
			    restaurant.getFullAddress() == null) return "ERR: INVALID_RESTAURANT_STRING";
			restaurant.setBusinessID();
			businessbyID.put(restaurant.getBusinessID(), restaurant);
			return "Reply: " + gson.toJson(restaurant);
		} catch (NullPointerException | ClassCastException b) {
			return "ERR: NO_SUCH_RESTAURANT";
		} catch (JsonSyntaxException c) {
			return "ERR: INVALID_RESTAURANT_STRING";
		}
	}
	
	/**
	 * Cluster objects into k clusters using k-means clustering			
	 * 
	 * @param k
	 *            number of clusters to create (0 < k <= number of objects)
	 *            requires k is not null 
	 * @requires businessesbyID to not be null, and be already initialized
	 * @return a String, in JSON format, that represents the clusters
	 * @throws IOException 
	 */
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
	 * Returns map of each business to its cluster, used for debugging kClusters
	 * @return HashMap that has each Business (by name) mapped to its
	 * 		cluster number
	 */
	public HashMap<String, Integer> getkMeans () {
		return kMeansClusters;
	}
	
	/**
	 * Implements a least-squares linear regression to approximate
	 * 		the relationship between price and rating of a restaurant.
	 * 
	 * REP INVARIANT: businessbyID is never null
	 * 				  Restaurants, users, and reviews are never modified
	 * 
	 * PRECONDITION: requires used to have made at least one review
	 * 
	 * ASSUMPTIONS: Ratings are [1,5] and prices are [1,5]
	 * 				Filters out ratings and prices outside these ranges
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
				.filter(price -> ((price >= 1 && price <=5))).collect(Collectors.toList());
		
		List<Double> y = reviewList.stream().map(review_id -> reviewbyID.get(review_id))
				.map(review -> review.getStars()).filter(stars -> (stars >= 1 && stars <=5))
				.collect(Collectors.toList());
		
		double x_mean = (double) x.stream().reduce(0, Integer::sum) / (double)(reviewList.stream().count());
		double y_mean = y.stream().reduce(0.0, Double::sum) / (double) reviewList.stream().count();
		double Sxx = x.stream().map(x_val -> Math.pow(x_val - x_mean, 2)).reduce(0.0, Double::sum);
		//double Syy = y.stream().map(y_val -> Math.pow(y_val - y_mean, 2)).reduce(0.0, Double::sum);
		double Sxy = 0;

		for (int i = 0; i < reviewList.stream().count(); i++) {
			Sxy += (x.get(i) - x_mean) * (y.get(i) - y_mean);
		}
		double b;
		if (Sxx != 0.0) {
			b = Sxy / Sxx;
		} else b = 0.0; //throw exception instead?
		double a = y_mean - (b * x_mean);
		//double R2 = Math.pow(Sxy, 2) / (Sxx * Syy);

		ToDoubleBiFunction<MP5Db<T>, String> fnc = (db,
				business) -> (a + b * ((Business) db.getBusinessbyID().get(business)).getPrice());
		return fnc;

	}

	/**
	 * Parses JSON files given string of type of object in JSON format.
	 * 
	 * @param url
	 * @param objtype
	 * @throws IOException if file is not found
	 */
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
				businesses.add(restaurant);
			}

		}

		br.close();
	}
}