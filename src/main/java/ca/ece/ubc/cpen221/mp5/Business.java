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
			         boolean open, ArrayList<String> neighbourhoods, String city, String type) {
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
}
