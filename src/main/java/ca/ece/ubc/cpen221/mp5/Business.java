package ca.ece.ubc.cpen221.mp5;

public class Business {
	private String url;
	private String name;
	private String business_id;
	
	private double longitude;
	private double lattitude;
	
	public Business (String url, String name, String business_id, double longitude, double lattitude) {
		this.url = url;
		this.name = name;
		this.business_id = business_id;
		
		this.longitude = longitude;
		this.lattitude = lattitude;
	}
	
	public double[] getCoordinates() {
		double[] coordinates = new double[2];
		coordinates[0] = lattitude;
		coordinates[1] = longitude;
		
		return coordinates;
	}
}
