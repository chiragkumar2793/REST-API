package test;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resource.Payload;

public class NegativeTestCases {

	RequestSpecification httpRequest;
	Response response;
	JsonPath js;
	int noofmovies;

	@BeforeTest
	public void payload() {
		js = new JsonPath(Payload.upcomingMovies());

		noofmovies = js.getInt("upcomingMovieData.size()");

	}

	// Validate response code is 200 or not
	@Test(priority = 0, enabled = false)
	public void responseValidation() {

		response.then().assertThat().statusCode(200);
	}

	

	@Test(priority = 1)
	public void releaseDate() throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date todayDate = new Date();
		// System.out.println(todayDate);
		for (int i = 0; i < noofmovies; i++) {
			String relDate = js.getString("upcomingMovieData[" + i + "].releaseDate");
			String movieName = js.getString("upcomingMovieData[" + i + "].movie_name");

			if (relDate != null) {
				Date dates = formatter.parse(relDate);
				// System.out.println(dates);

				boolean result = dates.before(todayDate);
				Assert.assertEquals(result,false,"Release date is in past for "+movieName);
			} else
				System.out.println("Release date is Null for " + movieName);

		}

	}

	// check poster format
	@Test(priority = 2)
	public void posterFormat() {
		for (int i = 0; i < noofmovies; i++) {
			String posterType = js.getString("upcomingMovieData[" + i + "].moviePosterUrl");
			String movieName = js.getString("upcomingMovieData[" + i + "].movie_name");

			boolean b = posterType.endsWith(".jpg"); // System.out.println(posterType);
			
			Assert.assertEquals(b, true," "+movieName+" poster is not ending with .jpg");
			
				}
	}

	// Paytm movie code should be unique
	@Test(priority = 3)
	public void validateMoviecode() {
		Set<String> set = new HashSet<String>();
		String pmcode = "";
		for (int i = 0; i < noofmovies; i++) {
			pmcode = js.getString("upcomingMovieData[" + i + "].paytmMovieCode");
			boolean flag = set.add(pmcode);
			Assert.assertEquals(flag, true," "+pmcode+" is not unique");

		}
	}

	
}
