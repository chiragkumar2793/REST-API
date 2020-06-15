# REST-API
Insider API Automation Assignment

/Insider/src/test/java/test/MoviesTestCases.java
This file contains Test cases


/Insider/src/test/java/test/NegativeTestCases.java
In this class i am not using response provided by Test URL. I am fetching  JSON response from here /Insider/src/main/java/resource/Payload.java
I have amended response to delibertaley fail test cases.

for instance:
 "Rambo" and "Bhool Bhulaiya 2" have same paytmMovieCode "O9RCCT"
 
 Changed release date in past for "Mr. Lele"
 
 Changed poster format for "Toofan"
 
 Also, for point in API automation 5. No movie code should have more than 1 language format.
 
 I can see in JSON response the way it is structured it will always have only one movie "language". So if we want to perform above validation I believe language field should have an array.
