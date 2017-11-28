package ca.ece.ubc.cpen221.mp5;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.annotations.Expose;


/**
 * User - a datatype that represents a user.
 *
 */

public class User {
	@Expose
	private String url;
	@Expose
	private int review_count;
	@Expose
	private String type;
	@Expose
	private static final ArrayList<String> categories = new ArrayList<String>(Arrays.asList("funny", "useful", "cool"));
	@Expose
	private HashMap<String, Integer> votes;
	@Expose
	private String user_id;
	@Expose
	private String name;
	@Expose
	private double average_stars;
	@Expose
	private ArrayList<String> reviewList;
	
	/**
	 * User constructor that initializes a new User.
	 * 			User has no reviews
	 * 			User's average stars is 0
	 * 			User's reviewList is empty
	 * 			User's votes has three categories with 0 each
	 * 
	 * @param url
	 * @param user_id
	 * @param type
	 * @param name
	 * 
	 */
	public User(String name, String url, String user_id, String type) {
		this.name = name;
		this.url = url;
		this.user_id = user_id;
		this.type = type;
		
		this.review_count = 0;
		this.average_stars = 0;
		this.reviewList = new ArrayList<String>();
		
		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		this.votes = votes;
		
	}
	public User() {
		this.reviewList = new ArrayList<String>();
		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		this.votes = votes;
	}
	
	/**
	 * User constructor that initializes an existing User.
	 * 
	 * @param url
	 * @param user_id
	 * @param type
	 * @param name
	 * @param average stars
	 * @param votes
	 * @param review count
	 * 
	 */
	public User(String name, int review_count, String user_id, String url, double average_stars,
				String type, HashMap<String, Integer> votes) {
		this.url = url;
		this.user_id = user_id;
		this.review_count = review_count;
		this.average_stars = average_stars;
		this.type = "user";
		this.votes = votes;
		this.name = name;
		this.reviewList = new ArrayList<String>();

	}
	
	/**
	 * Returns list of Review IDs
	 * 
	 * @return reviewList
	 */
	public ArrayList<String> getReviewList() {
		return this.reviewList;
	}
	
	/**
	 * Returns url 
	 * 
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Returns hashmap of votes with category as key
	 * 
	 * @return votes
	 */
	public HashMap<String, Integer> getVotes() {
		return this.votes;
	}
	
	/**
	 * Returns user id
	 * 
	 * @return user id 
	 */
	public String getUserID() {
		return this.user_id;
	}
	
	/**
	 * Returns number of reviews made by user
	 * 
	 * @return review count
	 */
	public int getReviewCount() {
		return this.review_count;
	}
	
	/**
	 * Returns name of user
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns average star rating given by user
	 * 
	 * @return average stars
	 */
	public double getAverageStars() {
		return this.average_stars;
	}
	
	
	/**
	 * Adds review ID to reviewList.
	 * 
	 * @param reviewID
	 */
	public void addReview(String reviewID) {
		reviewList.add(reviewID);
	}

}
