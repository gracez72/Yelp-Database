package ca.ece.ubc.cpen221.mp5;

import java.util.HashMap;



/**
 * Review - a datatype that represents a review.
 *
 */

public class Review {
	private String type = "review";
	private final String business_id;
	private HashMap<String, Integer> votes;
	private String review_id;
	private String text;
	private double stars;
	private final String user_id;
	private String date;
	

	/**
	 * Review constructor initializes characteristics of a review.
	 * 
	 * @param business
	 *            id being reviewed
	 * @param user
	 *            id
	 * @param type
	 * @param number
	 *            of stars [0, 5]
	 * @param number
	 *            of votes in each category
	 * @param review
	 *            text
	 * @param date
	 */

	public Review(String business_id, double stars, String review_id, String type, HashMap<String, Integer> votes,
			String text, String date, String user_id) {
		this.business_id = business_id;
		this.stars = stars;
		this.review_id = review_id;
		this.type = type;
		this.votes = votes;
		this.date = date;
		this.user_id = user_id;
		this.text = text;
	}

	/**
	 * Returns date review was written.
	 * 
	 * @return date
	 */

	public String getDate() {
		return this.date;
	}
	
	
	/**
	 * Returns the text written by reviewer.
	 * 
	 * @return review text
	 */

	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns hashmap of votes in each category.
	 * 
	 * @return votes
	 */

	public HashMap<String, Integer> getVotes() {
		return this.votes;
	}
	
	
	
	/**
	 * Returns id of reviewed business.
	 * 
	 * @return business ID
	 */

	public String getBusinessID() {
		return this.business_id;
	}
	
	/**
	 * Returns id of reviewer.
	 * 
	 * @return user ID
	 */

	public String getUserID() {
		return this.user_id;
	}
	
	/**
	 * Returns id of review.
	 * 
	 * @return review ID
	 */

	public String getReviewID() {
		return this.review_id;
	}

	/**
	 * Returns number of stars given in review.
	 * 
	 * @return stars
	 */
	public double getStars() {
		return this.stars;
	}
	
	/**
	 * Returns hashcode for given review object. 
	 * Equal reviews area guaranteed to have the same hashcode.  
	 * 
	 * @return hashcode
	 */
	public int hashCode() {
		return this.review_id.hashCode();
		
	}
	/**
	 * Sets review id.
	 * 
	 * @modifies review_id 
	 */
	public void setReviewID() {
		
		this.review_id = this.business_id + this.getStars() + this.getText().hashCode();
	}
	
	/**
	 * Returns true if reviews are the same.
	 * Assumes that each unique review has a unique review_id
	 * 
	 * @param Review 
	 * @return true if same review object, false otherwise
	 */
	public boolean equals(Review r) {
		return this.review_id.equals(r.getReviewID());
	}
}
