package resource;

public class PrimeNumber {

	
	 public static void main(String args[])
	   {
	      
	      int status = 1;
	      int num = 2;
	      System.out.println("First 100 prime numbers are:");   
	      
	      for ( int i = 1 ; i <=100 ;  )
	      {
	    	 
	    	  	    	
	         for ( int j = 2 ; j <=Math.sqrt(num)  ; j++ )
	         {
	            if ( num%j == 0 )
	            {
	               status = 0;
	               break;
	            }
	         }
	         if ( status != 0 )
	         {
	            System.out.println(i+" : "+num);
	            i++;
	         }
	       
	       status = 1;
	         num++;
	      }         
	   }
}