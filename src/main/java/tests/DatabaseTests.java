package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.junit.Test;

import com.google.gson.Gson;

import ca.ece.ubc.cpen221.mp5.Business;
import ca.ece.ubc.cpen221.mp5.Restaurant;
import ca.ece.ubc.cpen221.mp5.Review;
import ca.ece.ubc.cpen221.mp5.User;
import ca.ece.ubc.cpen221.mp5.YelpDB;
import ca.ece.ubc.cpen221.mp5.YelpDBClient;
import ca.ece.ubc.cpen221.mp5.YelpDBServer;

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
			Review review = gson.fromJson(line, Review.class);
			System.out.println(review.getVotes());
		}

		br.close();

	}

	@Test
	public void testKCluster() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
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
		String userTest = "ADDUSER {\"name\" : \"Grace\"}";

		System.out.println(db.addUser("{\"name\": \"Sathish G.\"}"));
		
		String restTest= "ADDRESTAURANT {\"open\": true, \"url\": \"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\", \"longitude\": -122.2580151, \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"], \"name\": \"Bear and Cat's Ramen House\", \"categories\": [\"Korean\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"city\": \"Berkeley\", \"full_address\": \"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\", \"review_count\": 225, \"photo_url\": \"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8680531, \"price\": 2}";
		String[] splitT = restTest.toString().split("\\s+");
		StringBuilder builderrest = new StringBuilder();
		for (int i = 1; i < splitT.length; i++) {
			builderrest.append(splitT[i] + " ");
		}
		splitT[1] = builderrest.toString().trim();
		System.out.println("1:" + splitT[1]);
		if (splitT[0].equals("GETRESTAURANT"))
			System.out.println(db.getRestaurant(splitT[1]));
		else if (splitT[0].equals("ADDUSER"))
			System.out.println(db.addUser(splitT[1]));
		else if (splitT[0].equals("ADDRESTAURANT"))
			System.out.println("result: " + db.addRestaurant(splitT[1]));
		else if (splitT[0] == "ADDREVIEW")
			System.out.println(db.addReview(splitT[1]));
		
//		String test = "GETRESTAURANT " + "gclB3ED6uk6viWlolSb_uA";
//		String[] split = userTest.toString().split("\\s+");
//		StringBuilder builder = new StringBuilder();
//		for (int i = 1; i < split.length; i++) {
//			builder.append(split[i] + " ");
//		}
//		split[1] = builder.toString().trim();
//		System.out.println("1:" + split[1]);
//		if (split[0].equals("GETRESTAURANT"))
//			System.out.println(db.getRestaurant(split[1]));
//		else if (split[0].equals("ADDUSER"))
//			System.out.println(db.addUser(split[1]));
//		else if (split[0] == "ADDRESTAURANT")
//			System.out.println(db.addRestaurant(split[1]));
//		else if (split[0] == "ADDREVIEW")
//			System.out.println(db.addReview(split[1]));
//
//		String addTest = "ADDREVIEW {}";
//		String[] split2 = addTest.toString().split("\\s+");
//		if (split2.length > 1) {
//			StringBuilder builder2 = new StringBuilder();
//			for (int i = 1; i < split2.length; i++) {
//				builder2.append(split2[i] + " ");
//			}
//			split2[1] = builder2.toString().trim();
//		}
//		if (split2[0].equals("GETRESTAURANT"))
//			System.out.println(db.getRestaurant(split2[1]));
//		else if (split2[0].equals("ADDUSER"))
//			System.out.println(db.addUser(split2[1]));
//		else if (split2[0] == "ADDRESTAURANT")
//			System.out.println(db.addRestaurant(split2[1]));
//		else if (split2[0].equals("ADDREVIEW"))
//	     	System.out.println(db.addReview(split2[1]));
	}

	// USER TESTS
	@Test
	public void test3() throws IOException {
		User u1 = new User("Bob");
		assertTrue(u1.getName().equals("Bob"));
		assertTrue(u1.getAverageStars() == 0);
		assertTrue(u1.getReviewCount() == 0);

		User u2 = new User("Jean", "https://google.com", "Jean132456tyfc", "user");
		assertTrue(u2.getUrl().equals("https://google.com"));
		assertTrue(u2.getReviewList().isEmpty());
		assertTrue(u2.getVotes().isEmpty());

		assertFalse(u1.equals(u2));

		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		votes.put("cool", 2);
		User u3 = new User("Sally", 5, "SAOUWEA5231bg", "https://random.com", 3.7, "user", votes);
		assertTrue(u3.getVotes().containsKey("cool"));
		assertTrue(u3.getReviewCount() == 5);
	}

	@Test
	public void test4() throws IOException {
		ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		String[] start = { "4949" };

		new Thread(new Runnable() {
			public void run() {
				YelpDBServer.main(start);
			}
		}).start();

		Runnable client = new Runnable() {
			public void run() {
				try {
					YelpDBClient client;
					client = new YelpDBClient("localhost", 4949);
					client.sendRequest("end");

					String y = client.getReply();
					String result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: Closing client..."));
					
					
					client.sendRequest("ADDUSER {\"name\": \"Grace\"}");
					y = client.getReply();
					result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: {\"review_count\":0,\"votes\":{},\"user_id\":\"69062552Grace\",\"name\":\"Grace\",\"average_stars\":0.0,\"reviewList\":[]}"));
					
					
					client.sendRequest("ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client.getReply();
					result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}"));
					client.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		Thread clientT = new Thread(client);
		clientT.start();

		Runnable client2 = new Runnable() {
			public void run() {
				try {
					String y;
					String result;
					YelpDBClient client2;
					client2 = new YelpDBClient("localhost", 4949);
					
					
					client2.sendRequest("ADDUSER {\"name\": \"Grace\"}");
					y = client2.getReply();
					result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: {\"review_count\":0,\"votes\":{},\"user_id\":\"69062552Grace\",\"name\":\"Grace\",\"average_stars\":0.0,\"reviewList\":[]}"));
					
					
					client2.sendRequest("ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}"));
					
					
					client2.sendRequest("end");
					y = client2.getReply();
					result = "Reply: " + y;
					System.out.println(result);
					assertTrue(result.equals("Reply: Closing client..."));
					
					client2.close();
					
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		Thread clientT2 = new Thread(client2);
		clientT2.start();
		
		try {
			clientT.join();
			clientT2.join();
		} catch (InterruptedException e) {
			System.out.println("Error: client thread interrupted.");
		}

	}


	@Test
	public void test5() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
		//System.out.println(db.getPredictorFunction("_NH7Cpq3qZkByP5xR4gXog").applyAsDouble(db, "fcdjnsgO8Z5LthXUx3y-lA"));
		//assertTrue(db.getPredictorFunction("_NH7Cpq3qZkByP5xR4gXog").applyAsDouble(db, "fcdjnsgO8Z5LthXUx3y-lA") == 4.0);
		
		System.out.println(db.getPredictorFunction("KM272ChoRWl9Jvmv2N08ZQ").applyAsDouble(db, "AfJCoI9vYrj1lnhlNhZdfw")); //price is 2
	}
	

}
