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

public class MoviesTestCases {

	RequestSpecification httpRequest;
	Response response;
	JsonPath js;
	int noofmovies;

	@BeforeTest
	public void getmovies() {
		RestAssured.baseURI = "https://apiproxy.paytm.com/v2/movies";

		httpRequest = RestAssured.given().log().all();

		response = httpRequest.request(Method.GET, "/upcoming");
		response.then().log().all();
		
		String resData = response.asString();
		js = new JsonPath(resData);

		noofmovies = js.getInt("upcomingMovieData.size()");
	}

	// Validate response code is 200 or not
	@Test(priority = 0)
	public void responseValidation() {

		response.then().assertThat().statusCode(200);
	}
	
	//movie's release should not be in Past and NULL
	@Test(priority = 1)
	public void releaseDate() throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date todayDate = new Date();
		// System.out.println(todayDate);
		for (int i = 0; i < noofmovies; i++) {
			String relDate = js.getString("upcomingMovieData[" + i + "].releaseDate");
			String movieName = js.getString("upcomingMovieData[" + i + "].movie_name");
			
			if(relDate!=null)
			{	
			Date dates = formatter.parse(relDate);
			// System.out.println(dates);
			
			boolean result = dates.before(todayDate);
			
			Assert.assertEquals(result,false,"Release date is in past for "+movieName);
			}
			else
				System.out.println("Release date is Null for "+movieName);

		}

	}

	// check poster format
	@Test(priority = 2)
	public void posterFormat() {
		for (int i = 0; i < noofmovies; i++) {
			String posterType = js.getString("upcomingMovieData[" + i + "].moviePosterUrl");

			boolean b = posterType.endsWith(".jpg"); 
			//System.out.println(posterType);
			Assert.assertEquals( b, true);

		}

	}

	// Paytm movie code should be unique
	@Test(priority = 3)
	public void validateMoviecode() {
		Set<String> set = new HashSet<String>();

		for (int i = 0; i < noofmovies; i++) {
			String pmcode = js.getString("upcomingMovieData[" + i + "].paytmMovieCode");
			boolean flag = set.add(pmcode);
			Assert.assertEquals( flag, true);

		}

	}

	// Write movie name to excel where content available is 0
	@Test(priority = 4)
	public void writeMoviestoexcel() {

		List<String> movieList = new ArrayList<String>();

		for (int i = 0; i < noofmovies; i++) {
			int flag = js.get("upcomingMovieData[" + i + "].isContentAvailable");

			// System.out.println(flag);

			if (flag == 0) {
				String movieName = js.getString("upcomingMovieData[" + i + "].provider_moviename");
				movieList.add(movieName);
				//System.out.println(movieName);
			}
			
		}

		// System.out.println(movieList.size());
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("MoviesList");

		int rownum = 0;
		for (String key : movieList) {
			
			Row row = sheet.createRow(rownum++);
			
			int cellnum = 0;
			
			Cell cell = row.createCell(cellnum++);
			if (key instanceof String)
				cell.setCellValue((String) key);
			
		}
	
		try {
			
			FileOutputStream out = new FileOutputStream(new File("Insider_Movies.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Movies added to excel successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
