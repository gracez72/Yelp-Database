package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


import org.junit.Test;

import com.google.gson.Gson;

import ca.ece.ubc.cpen221.mp5.Business;
import ca.ece.ubc.cpen221.mp5.Restaurant;
import ca.ece.ubc.cpen221.mp5.Review;
import ca.ece.ubc.cpen221.mp5.User;
import ca.ece.ubc.cpen221.mp5.YelpDB;

public class DatabaseTests {
	@Test
	public void test0() throws IOException {
		URL filename = new URL(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
		URLConnection fn = filename.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(fn.getInputStream()));
		String line;

		while ((line = br.readLine()) != null) {
			System.out.println(line);
			Gson gson = new Gson();
			Review review= gson.fromJson(line, Review.class);
			System.out.println(review.getVotes());
		}

		br.close();

	}

	@Test
	public void testKCluster() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>("https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
	             "https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
		db.kMeansClusters_json(20);
	}
	
	@Test
	public void test1() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
		System.out.println(db.getRestaurant("2ciUQ05DREauhBC3xiA4qw"));
		String line = "{\"open\": true, \"url\": \"http://www.yelp.com/biz/cafe-3-berkeley\", \"longitude\": -122.260408, \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"], \"business_id\": \"gclB3ED6uk6viWlolSb_uA\", \"name\": \"Cafe 3\", \"categories\": [\"Cafes\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"stars\": 2.0, \"city\": \"Berkeley\", \"full_address\": \"2400 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\", \"review_count\": 9, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/bphoto/AaHq1UzXiT6zDBUYrJ2NKA/ms.jpg\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.867417, \"price\": 1}";
		System.out.println(db.addRestaurant(line));
		

	}
	
	@Test
	public void test2() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
		System.out.println(db.getRestaurant("2ciUQ05DREauhBC3xiA4qw"));
		String line = "{\"open\": true, \"url\": \"http://www.yelp.com/biz/cafe-3-berkeley\", \"longitude\": -122.260408, \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"], \"business_id\": \"gclB3ED6uk6viWlolSb_uA\", \"name\": \"Cafe 3\", \"categories\": [\"Cafes\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"stars\": 2.0, \"city\": \"Berkeley\", \"full_address\": \"2400 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\", \"review_count\": 9, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/bphoto/AaHq1UzXiT6zDBUYrJ2NKA/ms.jpg\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.867417, \"price\": 1}";
		System.out.println(db.addRestaurant(line));
		System.out.println(db.addUser("{\"name\": \"Sathish G.\"}"));
		String test = "GETRESTAURANT " + "gclB3ED6uk6viWlolSb_uA";
		String[] split = test.toString().split("\\s+");
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < split.length; i++) {
			  builder.append(split[i] + " ");
		}
		split[1] = builder.toString().trim();
		if (split[0].equals("GETRESTAURANT")) System.out.println(db.getRestaurant(split[1]));
		else if (split[0].equals("ADDUSER")) System.out.println(db.addUser(split[1]));
		else if (split[0] == "ADDRESTAURANT") System.out.println(db.addRestaurant(split[1]));
		else if (split[0] == "ADDREVIEW") System.out.println(db.addReview(split[1]));

		String addTest = "ADDREVIEW {}";
		String[] split2 = addTest.toString().split("\\s+");
		if (split2.length > 1) {
			StringBuilder builder2 = new StringBuilder();
			for (int i = 1; i < split2.length; i++) {
				  builder2.append(split2[i] + " ");
			}
			split2[1] = builder2.toString().trim();
		}
		if (split2[0].equals("GETRESTAURANT")) System.out.println(db.getRestaurant(split2[1]));
		else if (split2[0].equals("ADDUSER")) System.out.println(db.addUser(split2[1]));
		else if (split2[0] == "ADDRESTAURANT") System.out.println(db.addRestaurant(split2[1]));
		else if (split2[0].equals("ADDREVIEW")) System.out.println(db.addReview(split2[1]));
	}

}
