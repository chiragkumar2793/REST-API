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

	}

	// Validate response code is 200 or not
	@Test(priority = 0)
	public void responseValidation() {

		response.then().assertThat().statusCode(200);
	}

	@Test(priority = 1)
	public void releaseDate() throws Exception {

		// response.then().assertThat().statusCode(200);
		String resData = response.asString();
		js = new JsonPath(resData);

		noofmovies = js.getInt("upcomingMovieData.size()");
		// System.out.println(noofmovies);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date todayDate = new Date();
		// System.out.println(todayDate);
		for (int i = 0; i < noofmovies - 1; i++) {
			String relDate = js.getString("upcomingMovieData[" + i + "].releaseDate");
			Date dates = formatter.parse(relDate);
			// System.out.println(dates);

			boolean result = dates.before(todayDate);
			Assert.assertEquals(false, result);

		}

	}

	// check poster format
	@Test(priority = 2)
	public void posterFormat() {
		for (int i = 0; i < noofmovies; i++) {
			String posterType = js.getString("upcomingMovieData[" + i + "].moviePosterUrl");

			boolean b = posterType.endsWith(".jpg"); // System.out.println(posterType);
			Assert.assertEquals(true, b);

		}

	}

	// Paytm movie code should be unique
	@Test(priority = 3)
	public void validateMoviecode() {
		Set<String> set = new HashSet<String>();

		for (int i = 0; i < noofmovies; i++) {
			String pmcode = js.getString("upcomingMovieData[" + i + "].paytmMovieCode");
			boolean flag = set.add(pmcode);
			Assert.assertEquals(true, flag);

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
			// Blank workbook

		}

		// System.out.println(movieList.size());
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("MoviesList");

		int rownum = 0;
		for (String key : movieList) {
			// this creates a new row in the sheet
			Row row = sheet.createRow(rownum++);
			// Object[] objArr = movieList.toArray();
			int cellnum = 0;
			// for (Object obj : objArr) {
			// this line creates a cell in the next column of that row
			Cell cell = row.createCell(cellnum++);
			if (key instanceof String)
				cell.setCellValue((String) key);
			// else if (obj instanceof Integer)
			// cell.setCellValue((Integer)obj);
		}
		// }
		try {
			// this Writes the workbook gfgcontribute
			FileOutputStream out = new FileOutputStream(new File("Insider_Movies.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Insider_Movies.xlsx written successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
