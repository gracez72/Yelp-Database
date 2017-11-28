package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;

/**
 * Restaurant - a datatype that represents a Restaurant
 *
 */

public class Restaurant extends Business{
	
	/**
	 * Restaurant constructor initializes all characteristics of a restaurant.
	 * 
	 * @param url of restaurant website
	 * @param name of restaurant
	 * @param restaurant id
	 * @param longitude
	 * @param latitude
	 * @param price [1, 4]
	 * @param photo url of restaurant
	 * @param open?
	 * @param list of neighbourhoods
	 * @param list of categories of restaurant
	 * @param state
	 * @param number of stars [0, 5]
	 * @param city
	 * @param number of reviews
	 * @param type of restaurant
	 * @param number of stars
	 * 
	 */
	
	 
	public Restaurant (String url, String name, String business_id, double longitude, double latitude, int price,
	         String photo_url, int review_count, ArrayList<String> schools, String state, String full_address,
	         boolean open, ArrayList<String> neighbourhoods, String city, String type, ArrayList<String> categories, double stars) {
		
		super(url, name, business_id,longitude, latitude, price, photo_url,review_count, schools, state, full_address,
		      open, neighbourhoods,  city, type, categories, stars);
	}

}
