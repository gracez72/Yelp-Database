package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ca.ece.ubc.cpen221.mp5.Business;
import ca.ece.ubc.cpen221.mp5.Restaurant;
import ca.ece.ubc.cpen221.mp5.Review;
import ca.ece.ubc.cpen221.mp5.User;
import ca.ece.ubc.cpen221.mp5.YelpDB;
import ca.ece.ubc.cpen221.mp5.YelpDBClient;
import ca.ece.ubc.cpen221.mp5.YelpDBServer;

public class DatabaseTests {
	
	//BUSINESS CLASS TEST
	@Test
	public void test0() throws IOException {
		Business b1 = new Business("https://google.com", "Hi", "135gws", 124.4, 167.3, 4,
				"https://google.com/images", 5, new ArrayList<String>(), "CA", "3rd Frost St.",
		         true, new ArrayList<String>(), "Vancouver", "restaurant", new ArrayList<String>(), 4.6);
		assertTrue(b1.isOpen());
		assertTrue(b1.getURL().equals("https://google.com"));
		assertTrue(b1.getNeighborhoods().isEmpty());
		assertTrue(b1.getCategories().isEmpty());
		assertTrue(b1.getType().equals("restaurant"));
		assertTrue(b1.getReviewCount() == 5);
		assertTrue(b1.getStars() == 4.6);
		assertTrue(b1.getPhotoURL().equals("https://google.com/images"));
		assertTrue(b1.getCity().equals("Vancouver"));
		assertTrue(b1.getSchools().isEmpty());
		
		Restaurant r1 = new Restaurant("https://google.com", "Hi", "135gws", 124.4, 167.3, 4,
				"https://google.com/images", 5, new ArrayList<String>(), "CA", "3rd Frost St.",
		         true, new ArrayList<String>(), "Vancouver", "restaurant", new ArrayList<String>(), 4.6);
		
		assertTrue(b1.equals(r1));
	}

	
	//REVIEW CLASS TEST
	@Test 
	public void test2() throws IOException {
		Review r1 = new Review("aeg9", 3.5, "aeraeg9", "review", new HashMap<String, Integer>(),
				"Not much to say", "12/01/17", "ssaet0ae9"); 
		Review r2 = new Review ("aeg9", 4.5, "aeraegerr9", "review", new HashMap<String, Integer>(),
				"Not much to say", "12/01/17", "ssaetw346sdt0ae9");
		assertTrue(r1.getVotes().isEmpty());
		assertTrue(r1.hashCode() != r2.hashCode());
		assertTrue(!r1.equals(r2));
	}
	

	//USER TESTS
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
		assertTrue(u1.hashCode() != u2.hashCode());
		
		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		votes.put("cool", 2);
		User u3 = new User("Sally", 5, "SAOUWEA5231bg", "https://random.com", 3.7, "user", votes);
		assertTrue(u3.getVotes().containsKey("cool"));
		assertTrue(u3.getReviewCount() == 5);
	}

	
	// Multithreaded YelpDBServer and YelpDBClient Test
	@Test
	public void test4() throws IOException {
		String[] start = { "4949" };

		// STARTS NEW SERVER
		new Thread(new Runnable() {
			public void run() {
				YelpDBServer.main(start);
			}
		}).start();

		// RUNS ONE CLIENT THREAD
		Runnable available = new Runnable() {
			public void run() {
				YelpDBClient.main(start);
			}
		};

		// RUNS SERVER THREAD WITHOUT PORT NUMBER
		String[] args = {};
		YelpDBClient.main(args);

		new Thread(new Runnable() {
			public void run() {
				YelpDBServer.main(args);
			}
		}).start();
		
		
		Thread activeClient = new Thread(available);
		activeClient.start();

		String str1 = "GETRESTAURANT q3etg08";
		InputStream is1 = new ByteArrayInputStream(str1.getBytes());
		System.setIn(is1);

		String str3 = "ADDUSER q55etg08";
		InputStream is3 = new ByteArrayInputStream(str3.getBytes());
		System.setIn(is3);

		String str2 = "end";
		InputStream is2 = new ByteArrayInputStream(str2.getBytes());
		System.setIn(is2);

		Runnable client = new Runnable() {
			public void run() {
				try {
					YelpDBClient client;
					client = new YelpDBClient("localhost", 4949);
					client.sendRequest("end");
					

					String y = client.getReply();
					String result = y;
					assertTrue(("Closing client...").equals(result));

					client.sendRequest("ADDUSER {\"name\": \"Grace\"}");
					y = client.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"votes\":{},\"review_count\":0,\"user_id\":\"69062552Grace\",\"name\":\"Grace\",\"average_stars\":0.0,\"reviewList\":[]}"));

					client.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"open\":true,\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"longitude\":-122.2580151,\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"categories\":[\"Korean\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":0.0,\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"review_count\":225,\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":37.8680531,\"price\":2}"));
					
					client.sendRequest("ADDUSER {}");
					y = client.getReply();
					result = y;
					assertTrue(("ERR: INVALID_USER_STRING").equals(result));

					client.sendRequest("ADDUSER {null}");
					y = client.getReply();
					result = y;
					assertTrue(("ERR: NO_SUCH_USER").equals(result));

					client.sendRequest("ADDUSER {null}");
					y = client.getReply();
					result = y;
					assertTrue(("ERR: NO_SUCH_USER").equals(result));

					client.sendRequest("ADDREVIEW");
					y = client.getReply();
					result = y;
					assertTrue(("ERR: ILLEGAL_REQUEST").equals(result));

					client.sendRequest("ADDREVIEW null");
					y = client.getReply();
					result = y;
					assertTrue(("ERR: NO_SUCH_REVIEW").equals(result));
					

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

					client2.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"open\":true,\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"longitude\":-122.2580151,\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"categories\":[\"Korean\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":0.0,\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"review_count\":225,\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":37.8680531,\"price\":2}"));

					// No full address
					client2.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"name\":\"Bear and Cat\\u0027s Ramen House\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_RESTAURANT_STRING"));

					// No name
					client2.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_RESTAURANT_STRING"));

					// No business id
					client2.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"state\":\"CA\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = y;
					assertTrue(("ERR: INVALID_RESTAURANT_STRING").equals(result));

					// no state
					client2.sendRequest(
							"ADDRESTAURANT {\"url\":\"http://www.yelp.com/biz/bearsandcats-ramen-house-berkeley\",\"business_id\":\"1384591796Bear and Cat\\u0027s Ramen House37.8680531-122.2580151\",\"longitude\":-122.2580151,\"latitude\":37.8680531,\"price\":2,\"stars\":0.0,\"open\":true,\"type\":\"business\",\"city\":\"Berkeley\",\"full_address\":\"2521 Durant Ave\\nTelegraph Ave\\nBerkeley, CA 94701\",\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/BFEn7l4opMgRDeZ6ak7rcQ/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"review_count\":225,\"categories\":[\"Korean\",\"Restaurants\"]}");
					y = client2.getReply();
					result = y;
					assertTrue(("ERR: INVALID_RESTAURANT_STRING").equals(result));

					client2.sendRequest("GETRESTAURANT HXni0_SFPT1jAoH-Sm78Jg");
					y = client2.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"open\":true,\"url\":\"http://www.yelp.com/biz/alborz-berkeley\",\"longitude\":-122.266645,\"neighborhoods\":[\"Downtown Berkeley\",\"UC Campus Area\"],\"business_id\":\"HXni0_SFPT1jAoH-Sm78Jg\",\"name\":\"Alborz\",\"categories\":[\"Persian/Iranian\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":3.5,\"city\":\"Berkeley\",\"full_address\":\"2142 Center St\\nDowntown Berkeley\\nBerkeley, CA 94704\",\"review_count\":172,\"photo_url\":\"http://s3-media2.ak.yelpcdn.com/bphoto/YOmjJWRKPMmgv4ctkNkBoA/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":37.8701999,\"price\":4}"));
					
					client2.sendRequest("end");
					y = client2.getReply();
					result = y;
					assertTrue(("Closing client...").equals(result));

					client2.sendRequest("ADDRESTAURANT NULL");
					y = client2.getReply();
					result = y;
					assertTrue(("ERR: NO_SUCH_RESTAURANT").equals(result));

					client2.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		Thread clientT2 = new Thread(client2);
		clientT2.start();

		Runnable client3 = new Runnable() {
			public void run() {
				try {
					String y;
					String result;
					YelpDBClient client3;
					client3 = new YelpDBClient("localhost", 4949);

					client3.sendRequest("ADDREVIEW {}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_REVIEW_STRING"));

					client3.sendRequest("GETRESTAURANT 13514v51v");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_RESTAURANT_STRING"));

					client3.sendRequest(null);
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: ILLEGAL_REQUEST"));

					client3.sendRequest("hELLO THERE");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: ILLEGAL_REQUEST"));

					client3.sendRequest("ADDRESTAURANT  {rsh}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_RESTAURANT_STRING"));

					client3.sendRequest(
							"ADDRESTAURANT  {\"open\": true, \"url\": \"http://www.yelp.com/biz/cafe-milano-berkeley\", \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"], \"business_id\": \"NGyFcZHghu1uJ0G-pXJxoQ\", \"name\": \"Cafe Milano\", \"categories\": [\"Food\", \"Coffee & Tea\", \"Cafes\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"stars\": 3.5, \"city\": \"Berkeley\", \"full_address\": \"2522 Bancroft Way\\nTelegraph Ave\\nBerkeley, CA 94704\", \"review_count\": 267, \"photo_url\": \"http://s3-media3.ak.yelpcdn.com/bphoto/R7LBk41UPNh3oTljpDbyjg/ms.jpg\", \"schools\": [\"University of California at Berkeley\"], \"price\": 1}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"open\":true,\"url\":\"http://www.yelp.com/biz/cafe-milano-berkeley\",\"longitude\":0.0,\"neighborhoods\":[\"Telegraph Ave\",\"UC Campus Area\"],\"business_id\":\"-1125099147Cafe Milano0.00.0\",\"name\":\"Cafe Milano\",\"categories\":[\"Food\",\"Coffee \\u0026 Tea\",\"Cafes\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":3.5,\"city\":\"Berkeley\",\"full_address\":\"2522 Bancroft Way\\nTelegraph Ave\\nBerkeley, CA 94704\",\"review_count\":267,\"photo_url\":\"http://s3-media3.ak.yelpcdn.com/bphoto/R7LBk41UPNh3oTljpDbyjg/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":0.0,\"price\":1}"));

					client3.sendRequest("ADDREVIEW {\"review_id\": \"135dg\"}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_REVIEW_STRING"));

					client3.sendRequest(
							"ADDREVIEW {\"type\": \"review\", \"business_id\": \"1CBs84C-a-cuA3vncXVSAw\", \"votes\": {\"cool\": 0, \"useful\": 0, \"funny\": 0}, \"text\": \"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\", \"stars\": 4, \"user_id\": \"MY9ht7Fw_ER3dJ7baYjcxw\", \"date\": \"2011-09-28\"}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"type\":\"review\",\"business_id\":\"1CBs84C-a-cuA3vncXVSAw\",\"votes\":{\"cool\":0,\"useful\":0,\"funny\":0},\"review_id\":\"1CBs84C-a-cuA3vncXVSAw4.01032624752\",\"text\":\"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\",\"stars\":4.0,\"user_id\":\"MY9ht7Fw_ER3dJ7baYjcxw\",\"date\":\"2011-09-28\"}"));

					//missing type
					client3.sendRequest(
							"ADDREVIEW {\"business_id\": \"1CBs84C-a-cuA3vncXVSAw\", \"votes\": {\"cool\": 0, \"useful\": 0, \"funny\": 0}, \"review_id\": \"DG8LX-iRbWlrcoiIOVW-Bw\", \"text\": \"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\", \"stars\": 4, \"user_id\": \"MY9ht7Fw_ER3dJ7baYjcxw\", \"date\": \"2011-09-28\"}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"business_id\":\"1CBs84C-a-cuA3vncXVSAw\",\"votes\":{\"cool\":0,\"useful\":0,\"funny\":0},\"review_id\":\"DG8LX-iRbWlrcoiIOVW-Bw\",\"text\":\"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\",\"stars\":4.0,\"user_id\":\"MY9ht7Fw_ER3dJ7baYjcxw\",\"date\":\"2011-09-28\"}")); 

					//missing votes
					client3.sendRequest(
							"ADDREVIEW {\"type\": \"review\", \"business_id\": \"1CBs84C-a-cuA3vncXVSAw\", \"review_id\": \"DG8LX-iRbWlrcoiIOVW-Bw\", \"text\": \"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\", \"stars\": 4, \"user_id\": \"MY9ht7Fw_ER3dJ7baYjcxw\", \"date\": \"2011-09-28\"}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals(
							"Reply: {\"type\":\"review\",\"business_id\":\"1CBs84C-a-cuA3vncXVSAw\",\"review_id\":\"DG8LX-iRbWlrcoiIOVW-Bw\",\"text\":\"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\",\"stars\":4.0,\"user_id\":\"MY9ht7Fw_ER3dJ7baYjcxw\",\"date\":\"2011-09-28\"}"));
					
					client3.sendRequest(
							"ADDREVIEW {\"type\": \"review\", \"votes\": {\"cool\": 0, \"useful\": 0, \"funny\": 0}, \"review_id\": \"DG8LX-iRbWlrcoiIOVW-Bw\", \"text\": \"Extra Large Athenian for pick-up please!!!!\\n\\nI wish they delivered to downtown Oakland.\", \"stars\": 4, \"user_id\": \"MY9ht7Fw_ER3dJ7baYjcxw\", \"date\": \"2011-09-28\"}");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_REVIEW_STRING"));

					//normal query request
					client3.sendRequest("QUERY in(Telegraph Ave) && category(Thai) && price < 2 && rating >= 3");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("Reply: {\"open\":true,\"url\":\"http://www.yelp.com/biz/satay-house-berkeley\",\"longitude\":-122.2580885,\"neighborhoods\":[\"Telegraph Ave\",\"UC Campus Area\"],\"business_id\":\"zzFi7Z-pejLJtyg-SiGI5g\",\"name\":\"Satay House\",\"categories\":[\"Thai\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":4.0,\"city\":\"Berkeley\",\"full_address\":\"2519 Durant Ave\\nSte B\\nTelegraph Ave\\nBerkeley, CA 94704\",\"review_count\":6,\"photo_url\":\"http://s3-media3.ak.yelpcdn.com/bphoto/jRjjXhEPK_sPNQ7N4x4ytw/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":37.868144,\"price\":1}"));
					
					//incorrectly formatted request
					client3.sendRequest("QUERY in(Telegraph Ave) &&& (()category(Thai)3");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: INVALID_QUERY"));
					
					//no match request
					client3.sendRequest("QUERY price > 5");
					y = client3.getReply();
					result = y;
					assertTrue(result.equals("ERR: NO_MATCH"));
					
					client3.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};

		Thread clientT3 = new Thread(client3);
		clientT3.start();
		try {
			clientT.join();
			clientT2.join();
			clientT3.join();
			activeClient.join();
		} catch (InterruptedException e) {
			System.out.println("Error: client thread interrupted.");
		}

	}

	
	//GETPREDICTORFUNCTION TESTS
	@Test
	public void test5() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmjZ_YWpcWIvOzbrOfWXmGoDUlsKyks5aPwj0wA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rml_dvSV_hatl9cu4zlfSDpvhe5p5ks5aPwkFwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmhziGHNofZMAYY1yOet3dxLFUKveks5aPwkWwA%3D%3D");

		assertTrue(
				db.getPredictorFunction("_NH7Cpq3qZkByP5xR4gXog").applyAsDouble(db, "fcdjnsgO8Z5LthXUx3y-lA") == 4.0);

		assertTrue(db.getPredictorFunction("KM272ChoRWl9Jvmv2N08ZQ").applyAsDouble(db,
				"AfJCoI9vYrj1lnhlNhZdfw") == 2.5723542116630673);

		assertTrue(db.getPredictorFunction("kJS3R2N1pzf59APqO5mxXA").applyAsDouble(db,
				"1wz7l5OyVoUlvPDRy59ZMA") == 3.857142857142857);
	}
	
	
	//KCLUSTERS TESTS
		//Testing KClusters with a small set of restaurants
	@Test 
	public void test6() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurantsSmaller.json?token=Ad5rmsSAKcA98t1qPDEwV2OmPOC00UJLks5aP07kwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rml_dvSV_hatl9cu4zlfSDpvhe5p5ks5aPwkFwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmhziGHNofZMAYY1yOet3dxLFUKveks5aPwkWwA%3D%3D");

		db.kMeansClusters_json(2);
		HashMap<String, Integer> answerTwoClusters = new HashMap<String, Integer> ();
		answerTwoClusters = db.getkMeans();
		assertTrue(answerTwoClusters.get("Cafe 3").equals(answerTwoClusters.get("Jasmine Thai")));
		
		db.kMeansClusters_json(4);
		HashMap<String, Integer> answerFourClusters = new HashMap<String, Integer> ();
		answerFourClusters = db.getkMeans();
		assertTrue(answerFourClusters.get("Cafe 3").equals(answerFourClusters.get("Jasmine Thai")));	
	}
	
		//Testing KClusters with a full set of restaurants
	@Test
	public void test7() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmjZ_YWpcWIvOzbrOfWXmGoDUlsKyks5aPwj0wA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rml_dvSV_hatl9cu4zlfSDpvhe5p5ks5aPwkFwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmhziGHNofZMAYY1yOet3dxLFUKveks5aPwkWwA%3D%3D");

		db.kMeansClusters_json(5);
		HashMap<String, Integer> answerFiveClusters = new HashMap<String, Integer> ();
		answerFiveClusters = db.getkMeans();
		
		assertTrue(answerFiveClusters.get("The Melt Berkeley").equals(answerFiveClusters.get("Gordo Taqueria")));
		
		db.kMeansClusters_json(20);
		HashMap<String, Integer> answerTwentyClusters = new HashMap<String, Integer> ();
		answerTwentyClusters = db.getkMeans();
		
		assertTrue(answerTwentyClusters.get("The Melt Berkeley").equals(answerTwentyClusters.get("Gordo Taqueria")));	
	}
	
	
	//STRUCTURED QUERIES TESTS
		//Testing getMatches with a small set of restaurants
	@Test
	public void test8() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurantsSmaller.json?token=Ad5rmsSAKcA98t1qPDEwV2OmPOC00UJLks5aP07kwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rml_dvSV_hatl9cu4zlfSDpvhe5p5ks5aPwkFwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmhziGHNofZMAYY1yOet3dxLFUKveks5aPwkWwA%3D%3D");
	
		String query = "(price < 3 && price > 1)";
		assertEquals(7, db.getMatches(query).size());
		
		query = "in(Telegraph Ave) && category(Indian)";
		assertEquals(0, db.getMatches(query).size());
		
		query = "(category(Indian) || category(Chinese)) || in(UC Campus Area)";
		assertEquals(10, db.getMatches(query).size());
		
		query = "name(Pho K & K)";
		assertEquals(1, db.getMatches(query).size());
		
		query = "(price <= 5 && rating > 3) && in(Telegraph Ave) && category(Chinese)";
		assertEquals(2, db.getMatches(query).size());
		
		query = "rating <= 3 || category(Chinese) || name(Jasmine Thai) || in(Telegraph Ave)";
		assertEquals(9, db.getMatches(query).size());
		
		query = "(rating >= 3 && price >= 2) || category(Chinese)";
		assertEquals(7, db.getMatches(query).size());
		
		query = "price = 3 && (rating=4 || rating < 3)";
		assertEquals(0, db.getMatches(query).size());
		
		query = "price <= 2";
		assertEquals(9, db.getMatches(query).size());
		
		query = "(rating price) < 4";
		try {
			db.getMatches(query).size();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		
		query = "rating < 3 price >5";
		try {
			db.getMatches(query).size();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
		//Testing getMatches with a full set of restaurants
	@Test
	public void test9() throws IOException {
		YelpDB<Restaurant> db = new YelpDB<Restaurant>(
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmjZ_YWpcWIvOzbrOfWXmGoDUlsKyks5aPwj0wA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rml_dvSV_hatl9cu4zlfSDpvhe5p5ks5aPwkFwA%3D%3D",
				"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmhziGHNofZMAYY1yOet3dxLFUKveks5aPwkWwA%3D%3D");
		
		String query = "(price < 3 && price > 1)";
		assertEquals(61, db.getMatches(query).size());
		
		query = "in(Telegraph Ave) && category(Indian)";
		assertEquals(3, db.getMatches(query).size());
		
		query = "(category(Indian) || category(Chinese)) || in(UC Campus Area)";
		assertEquals(135, db.getMatches(query).size());
		
		query = "name(Jasmine Thai)";
		assertEquals(1, db.getMatches(query).size());
		
		query = "(price <= 5 && rating > 3) && in(Telegraph Ave) && category(Chinese)";
		assertEquals(3, db.getMatches(query).size());
		
		query = "rating <= 3 || category(Chinese) || name(Jasmine Thai) || in(Telegraph Ave)";
		assertEquals(97, db.getMatches(query).size());
		
		query = "(rating >= 3 && price >= 2) || category(Chinese)";
		assertEquals(79, db.getMatches(query).size());
		
		query = "price = 3 && (rating=4 || rating < 3)";
		assertEquals(4, db.getMatches(query).size());
		
		query = "price <= 2";
		assertEquals(115, db.getMatches(query).size());
	}
}
