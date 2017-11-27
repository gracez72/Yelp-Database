package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;

/**
 * Business - a datatype that represents a business.
 *
 */

public class Business {
	
	private String url;
	private String name;
	
	private final String business_id;
	
	private double longitude;
	private double latitude;
	
	private int price;
	private int stars;
	
	private boolean open;
	private ArrayList<String> neighbourhoods;
	private String type;
	private String state;
	private String city;
	private String full_address;
	private String photo_url;
	private ArrayList<String> schools;
	private int review_count;
	private ArrayList<String> categories;
	
	/**
	 * Business constructor initializes all characteristics of a business.
	 * 
	 * @param url of business website
	 * @param name of business
	 * @param business id
	 * @param longitude
	 * @param latitude
	 * @param price [1, 4]
	 * @param photo url of business
	 * @param open?
	 * @param list of neighbourhoods
	 * @param list of categories of business
	 * @param state
	 * @param number of stars [0, 5]
	 * @param city
	 * @param number of reviews
	 * @param type of business
	 * 
	 */
	public Business (String url, String name, String business_id, double longitude, double latitude, int price,
			         String photo_url, int review_count, ArrayList<String> schools, String state, String full_address,
			         boolean open, ArrayList<String> neighbourhoods, String city, String type, ArrayList<String> categories, int stars) {
		this.url = url;
		this.name = name;
		this.business_id = business_id;
		
		this.longitude = longitude;
		this.latitude = latitude;
		this.price = price;
		this.type = "Business";
		this.photo_url = photo_url;
		this.review_count = review_count;
		this.schools = schools;
		this.state = state;
		this.full_address = full_address;
		this.open = open;
		this.neighbourhoods = neighbourhoods;
		this.city = city;
		this.type = type;
		this.categories = categories;
		this.stars = stars;
	}
	
	/**
	 * Returns longitude and latitude of business.
	 * 
	 * @return double array with coordinates[0] as latitude 
	 *                       and coordinates[1] as longitude
	 */
	
	public double[] getCoordinates() {
		double[] coordinates = new double[2];
		coordinates[0] = latitude;
		coordinates[1] = longitude;
		
		return coordinates;
	}
	
	/**
	 * Returns price range of business.
	 * 
	 * @return price range [0, 4]
	 */
	public int getPrice() {
		return price;
	}
 
	/**
	 * Returns name of business.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns true if business is open, false otherwise.
	 * 
	 * @return open?
	 */
	public boolean isOpen() {
		return this.open;
	}
	
	/**
	 * Returns url of business.
	 * 
	 * @return url
	 */
	public String getURL() {
		return this.url;
	}
	
	/**
	 * Returns neighbourhoods of business.
	 * 
	 * @return neighbourhoods
	 */
	public ArrayList<String> getNeighbourhoods() {
		return this.neighbourhoods;
	}
	
	/**
	 * Returns business id.
	 * 
	 * @return business id
	 */
	public String getBusinessID() {
		return this.business_id;
	}
	
	/**
	 * Returns categories business falls under
	 * 
	 * @return categories
	 */
	public ArrayList<String> getCategories() {
		return this.categories;
	}
	
	/**
	 * Returns state business is located in.
	 * 
	 * @return state
	 */
	public String getState() {
		return this.state;
	}
	
	/**
	 * Returns type of business.
	 * 
	 * @return type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Returns address of business.
	 * 
	 * @return address
	 */
	public String getFullAddress() {
		return this.full_address;
	}
	
	/**
	 * Returns review count of business.
	 * 
	 * @return review count
	 */
	public int getReviewCount() {
		return this.review_count;
	}
	
	/**
	 * Returns photo url of business.
	 * 
	 * @return photo url
	 */
	public String getPhotoURL() {
		return this.photo_url;
	}
	
	/**
	 * Returns city of business.
	 * 
	 * @return city
	 */
	public String getCity() {
		return this.city;
	}
	
	/**
	 * Returns schools near business.
	 * 
	 * @return list of schools
	 */
	public ArrayList<String> getSchools() {
		return this.schools;
	}
	
	/**
	 * Returns business' rating in stars.
	 * 
	 * @return stars
	 */
	public int getStars() {
		return this.stars;
	}
}
