package test;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import resource.Payload;

public class MoviesTest {
	public static void main(String[] args) throws ParseException {

		RestAssured.baseURI = "https://apiproxy.paytm.com/v2/movies";

		/*
		 * String response = given().log().all().header("Content-Type",
		 * "application/json")
		 * .when().get("/upcoming").then().log().all().assertThat().statusCode(200).
		 * extract().asString();
		 * 
		 * 
		 * JsonPath js= new JsonPath(response);
		 */

		//RequestSpecification httpRequest = RestAssured.given().log().all();

		//Response response = httpRequest.request(Method.GET, "/upcoming");

		// Assert response code is 200
		// response.then().log().all().assertThat().statusCode(200);

		// Movies release date should not be before today's date

		//String resData = response.asString();
		JsonPath js = new JsonPath(Payload.upcomingMovies());

		int noofmovies = js.getInt("upcomingMovieData.size()");
		System.out.println(noofmovies);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date todayDate = new Date();
		System.out.println(todayDate);

		// Date date;

		for (int i = 0; i < noofmovies - 1; i++) {
			String relDate = js.getString("upcomingMovieData[" + i + "].releaseDate");
			Date dates = formatter.parse(relDate);
			// System.out.println(dates);
			
			boolean result = dates.before(todayDate);
			if (result == true) {
				System.out.println("**** RELEASE DATE IS BEFORE TODAYS DATE ****");
			} else {
				System.out.println("**** RELEASE DATE IS NOT BEFORE TODAYS DATE ****");
			}

		}

		// should have only .jpg

		for (int i = 0; i < noofmovies; i++) {
			String posterType = js.getString("upcomingMovieData[" + i + "].moviePosterUrl");

			boolean b = posterType.endsWith(".jpg"); // System.out.println(posterType);
			if (b == true) {
				System.out.println("****  Movie Poster format is .jpg  ****");

			} else {
				System.out.println("****  Movie Poster format is not .jpg ****");
			}

		}

		// convert date in to long
		// get current date convert in to long

		// use date class

		// use add method in Hashset

		// Paytm movie code is unique
		//List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		
		for (int i = 0; i < noofmovies; i++) {
			String pmcode = js.getString("upcomingMovieData[" + i + "].paytmMovieCode");
			boolean flag =set.add(pmcode);
			
		
			if(flag==false) {
			System.out.println("**** "+pmcode+" Paytm code is not unique **** ");
			}
			else {
				System.out.println("**** Paytm code is unique ****");
			}

		}
		//System.out.println(set.size());
		
		
		//List of movies whose content available is 0
		List<String> movieList= new ArrayList<String>();

		for (int i = 0; i < noofmovies; i++) {
			int flag = js.get("upcomingMovieData[" + i + "].isContentAvailable");
			
			//System.out.println(flag);
			
			if(flag==0)
			{
				String movieName=js.getString("upcomingMovieData[" + i + "].provider_moviename");
				movieList.add(movieName);
				//System.out.println(movieName);
			}
			
			 // Blank workbook 
	       
				
				
				
			
		}
		
		System.out.println(movieList.size());
		
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
	                    cell.setCellValue((String)key); 
	              //  else if (obj instanceof Integer) 
	              //      cell.setCellValue((Integer)obj); 
	            } 
	      //  } 
	        try { 
	            // this Writes the workbook gfgcontribute 
	            FileOutputStream out = new FileOutputStream(new File("Insider_Movies.xlsx")); 
	            workbook.write(out); 
	            out.close(); 
	            System.out.println("Insider_Movies.xlsx written successfully."); 
	        } 
	        catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
			
				
		
			}

	}



