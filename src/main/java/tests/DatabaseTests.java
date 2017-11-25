package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import ca.ece.ubc.cpen221.mp5.MP5Db;
import ca.ece.ubc.cpen221.mp5.YelpDB;

public class DatabaseTests {

	@Test
	public void test1() {
		MP5Db db = new YelpDB("https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmo9tXwh9lYalidf_muOfIGcyx4H1ks5aIm-HwA%3D%3D",
				             "https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
				             "https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
	}

}
