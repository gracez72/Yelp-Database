package ca.ece.ubc.cpen221.mp5;

public class Restaurant extends Business{

	private int priceRange;
	
	public Restaurant (String url, String name, String restaurant_id, double longitude, double lattitude) {
		super(url, name, restaurant_id, longitude, lattitude);
	}
	
	public int getPrice() {
		return priceRange;
	}
}
