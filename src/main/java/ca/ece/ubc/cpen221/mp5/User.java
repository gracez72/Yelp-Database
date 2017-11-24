package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class User {
	private String url;
	private int review_count;
	private String type;
	private static final ArrayList<String> categories = new ArrayList<String>(Arrays.asList("funny", "useful", "cool"));
	private HashMap<String, Integer> votes;
	// votes 
	private String user_id;
	private String name;
	private double average_stars;
	
	//New User
	public User(String name, String url, String user_id) {
		this.name = name;
		this.url = url;
		this.user_id = user_id;
		
		this.review_count = 0;
		this.type = "user";
		this.average_stars = 0;
	}
	
	//Constructor with all fields  - need type?
	public User(String name, int review_count, String user_id, String url, int average_stars) {
		this.url = url;
		this.user_id = user_id;
		this.review_count = review_count;
		this.average_stars = average_stars;
		this.type = "user";
		
		
	}
	
	public void addReview() {
		
	}
}
