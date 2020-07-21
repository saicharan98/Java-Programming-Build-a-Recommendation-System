
/**
 * Write a description of FirstRatings here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.util.*;
import edu.duke.*;
import org.apache.commons.csv.*;

public class FirstRatings {
    public ArrayList<Movie> LoadMovies(String fileName){
        ArrayList <Movie> answer = new ArrayList<Movie>();
        FileResource fr = new FileResource(fileName);
        CSVParser parse = fr.getCSVParser();
        for(CSVRecord cr : parse){
            Movie myMovie = new Movie(cr.get("id"),cr.get("title"),cr.get("year"),
                            cr.get("genre"),cr.get("director"),cr.get("country"),cr.get("poster"),Integer.parseInt(cr.get("minutes")));
            answer.add(myMovie);
        }
        return answer;
    }
    
    public void testLoadMovies(){
        String filename = "data/ratedmoviesfull.csv";
        ArrayList <Movie> output = LoadMovies(filename);
        System.out.println(output.size());
        //System.out.println(output);
        int genreCount = 0;
        int exceeds150m = 0;
        HashMap <String,Integer> directorCount = new HashMap <String,Integer>();
        for(Movie m : output){
            if(m.getGenres().contains("Comedy")){
                genreCount++;
            }
            if(m.getMinutes() > 150){
                exceeds150m++;
            }
            String currDir = m.getDirector();
            if(directorCount.containsKey(currDir)){
                directorCount.put(currDir,directorCount.get(currDir)+1);
            }
            else{
                directorCount.put(currDir,1);
            }
            
        }
        
        for(Map.Entry i : directorCount.entrySet()){
            System.out.println(i.getKey() + " " + i.getValue());
        }
        
        int maxDirector = Collections.max(directorCount.values());
        
        ArrayList<String> movieMaxDir = new ArrayList();
        for(String dir:directorCount.keySet()){
            if(directorCount.get(dir) == maxDirector){
                movieMaxDir.add(dir);
            }
        }
        System.out.println(genreCount);
        System.out.println(exceeds150m);
        System.out.println(maxDirector);
        System.out.println(movieMaxDir);
    }
    
    public ArrayList<Rater> LoadRaters(String filename){
        FileResource fr = new FileResource(filename);
        String searchRaterID = "2";
        ArrayList<Rater> raters = new ArrayList<Rater>();
        ArrayList<String> raterIDList = new ArrayList();
        // find the number of ratings for a particular rater
        for(CSVRecord record:fr.getCSVParser()){
            String rater_id = record.get("rater_id");
            String movie_id = record.get("movie_id");
            double rating = Double.parseDouble(record.get("rating"));
            if(raters.size()==0){
                Rater currRater = new Rater(rater_id);
                currRater.addRating(movie_id, rating);
                raters.add(currRater);
            }
            else{
                List<Rater> raterList = new ArrayList<Rater>(raters);
                Iterator<Rater> raterIterator = raterList.iterator();
                while(raterIterator.hasNext()){
                    Rater r = raterIterator.next();
                    if(r.getID().equals(rater_id)){
                        r.addRating(movie_id, rating);
                        break;
                    }
                    else{
                        Rater currRater = new Rater(rater_id);
                        currRater.addRating(movie_id, rating);
                        raters.add(currRater);
                        break;
                    }
                }
            }            
        }        
        return raters;
    }
        
    public void testLoadRaters(){
        // print the total number of raters
        //ArrayList<Rater> loadedRaters = loadRaters("data/ratings_short.csv");
        ArrayList<Rater> loadedRaters = LoadRaters("data/ratings.csv");
        // find the number of ratings for a particular rater
        System.out.println("loadedRaters size: " + loadedRaters.size());
        int numOfRatingsPerRater = getRatingsPerRater(loadedRaters, "193");
        System.out.println("rater_id:193 has " + numOfRatingsPerRater + " ratings");
        ////// Find maximum number of ratings /////////////////
        HashMap<String,Integer> raterWithNumOfMovies= new HashMap<String,Integer>();
        getRaterWithNumOfMovies(loadedRaters,raterWithNumOfMovies);
        int maxValue = Collections.max(raterWithNumOfMovies.values());
        String maxKey = "";
        for(String s:raterWithNumOfMovies.keySet()){
            if(raterWithNumOfMovies.get(s)==maxValue){
                maxKey = s;
            }
        }
        System.out.println("maxKey: "+ maxKey+ ", "+"maxValue: "+ maxValue);
       ////////// Find maximum number of ratings ///////////////////////////
        //getRaterWithNumOfMovies(raters,raterWithNumOfMovies);
        int numOfRatingPerMovie= getNumOfRatingPerMovie(loadedRaters, "1798709");
        System.out.println("1798709 has " + numOfRatingPerMovie + " raters");
        //////// how many different movies have been rated by all these raters///////
        HashMap<String, Integer> movieRatingCounts = new HashMap();
        for(int k=0;k<loadedRaters.size();k++){
            Rater currRater = loadedRaters.get(k);
            for(int i=0;i< currRater.numRatings();i++){
                String currMovieID = currRater.getItemsRated().get(i);
                if(movieRatingCounts.containsKey(currMovieID)){
                    movieRatingCounts.put(currMovieID, movieRatingCounts.get(currMovieID)+1);
                }
                else{
                    movieRatingCounts.put(currMovieID, 1);
                }
            }
        }
        System.out.println("number of movies rated: " + movieRatingCounts.size());
    }
    
    // find the  number o f ratings for a particular rater
    public int getRatingsPerRater(ArrayList<Rater> raters,String rater_id){
        int numOfRatingsPerRater=0;
        for(Rater currRater:raters){
            if(currRater.getID().equals(rater_id)){
               numOfRatingsPerRater +=1;
            }
        }
        return numOfRatingsPerRater;
    }
    
    // find the maximum number of ratings by any rater
    public void getRaterWithNumOfMovies(ArrayList<Rater> raters,HashMap<String,Integer> raterWithNumOfMovies){
        for(Rater rater:raters){
            if(!raterWithNumOfMovies.containsKey(rater.getID())){
                raterWithNumOfMovies.put(rater.getID(),1);
            }
            else{
                int num = raterWithNumOfMovies.get(rater.getID());
                num+=1;
                raterWithNumOfMovies.put(rater.getID(),num);
            }
        }
    }
    
    // find the number of ratings a particular movie has
    public int getNumOfRatingPerMovie(ArrayList<Rater> raters,String movie_id){
        int numOfRatingPerMovie=0;
        for(Rater rater:raters){
            // use method hasRating in class Rater
            if(rater.hasRating(movie_id)){
                numOfRatingPerMovie+=1;
            }
        }
        return numOfRatingPerMovie;
    }
}