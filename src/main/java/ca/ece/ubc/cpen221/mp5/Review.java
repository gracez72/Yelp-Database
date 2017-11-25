package ca.ece.ubc.cpen221.mp5;

public class Review {
	private Restaurant restaurant;
	private int stars;
	
	public Review(Restaurant restaurant, int stars) {
		this.restaurant = restaurant;
		this.stars = stars;
	}
	
	public Restaurant getRestaurant() {
		return this.restaurant;
	}
	
	public int getStars() {
		return this.stars;
	}
}
