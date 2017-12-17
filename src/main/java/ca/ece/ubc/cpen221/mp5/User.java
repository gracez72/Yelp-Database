package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;
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
	private HashMap<String, Integer> votes;
	@Expose
	private int review_count;
	@Expose
	private String type;
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
	
	/**
	 * Constructor that initializes a user with only the user's name
	 * 
	 * @param name
	 */
	public User (String name) {
		this.name = name;
		this.url = "http://www.yelp.com/user_details?userid=" + name.hashCode() + name;
		this.user_id = name.hashCode() + name;
		this.type = "user";
		
		this.review_count = 0;
		this.average_stars = 0;
		this.reviewList = new ArrayList<String>();
		
		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		this.votes = votes;
	}
	
	/**
	 * Empty user constructor.
	 * 
	 */
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
		this.type = type;
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

	/**
	 * Adds review ID to reviewList.
	 * 
	 * @modifies user_id
	 * @param reviewID
	 */
	public void setuserid() {
		
		this.user_id = name.hashCode() + name;
	}

	/**
	 * Returns unique hashcode for each user.
	 * User objects that are equal are guaranteed to 
	 * have the same hashcode. 
	 * 
	 * @return hashcode
	 */
	public int hashCode() {
		return (this.user_id.hashCode() + this.name.hashCode()); 
	}
	
	/**
	 * Returns true if user ids are the same, false otherwise.
	 * Assumes that each unique user has a unique user_id
	 * 
	 * @param User u
	 * @return users equal?
	 */
	public boolean equals(User u) {
		return this.user_id.equals(u.getUserID());
	}
}
