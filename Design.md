
MP5 DESIGN
===

#####**DATABASE INTERFACE: MP5DB**  

* **CLASS** YELPDB implements MP5DB
    * Attributes 
        * Set of T objects - businesses
        * Set of USER objects
        * Set of REVIEW objects
    * Methods 
        * kMeansClusters
            * Rep invariant: businesses is never null; 
            * Precondition: Requires k is not null and > 0 and <= T.size
            * Postcondition: nothing modified
        * getPredictorsFunction
            * Rep invariant: businesses is never null, 
            * Precondition: Requires the user to have made at least one review, 
            * Postcondition: nothing modified
        * getMatches 
            * Rep invariant: Size of returned set is not larger than total T objects
            * Precondition: query string is not null
            * Throws IllegalArgumentException
            * Postcondition: nothing modified
    * **CLASS** BUSINESSES (supertype)
        * Attributes
            * Name 
            * Location (x,y)
            * Neighbourhoods 
        * Methods 
        	   * getName
        	   * getLocation
        	   * moveLocation
        	   * getNeighbourhoods
    * **CLASS** RESTAURANTS extends BUSINESS 
        * Attributes:
            * Location in (x,y) coordinates - Double, Double
            * Name - String 
            * Neighbourhoods - Set<String>
            * URL - String
            * Price - Integer
            * Rating - Integer
            * Schools - Set<String>
         * Methods 
            * Getter function for all attributes - return copy so original is not tainted 
    * **CLASS** USER
        * Attributes:
            * Name - String
            * URL - String
            * Votes - Integer
            * Average Rating - Double
            * Number of Reviews - Integer
            * User ID - String - **final**
            
    * **CLASS** REVIEWS
	    * Attributes:
	        * USER - **final**
	        * Date - String