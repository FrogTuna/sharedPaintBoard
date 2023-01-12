package Manager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	
	//field
    public static JSONParser parser = new JSONParser();
    public static List<String> allData = new ArrayList<String>();

    
    
    //parse json object line by line
    public static JSONObject parse(String line) {
    	
        try {
        	
            return (JSONObject) parser.parse(line);
            
        } catch (ParseException e) {
        	
            System.out.println("Failed to parse undefined json format in Tools class.");
        }
        
        return null;
    }

    
    
    //add shape's information in json format to list
    public static void addData(String line) {
        allData.add(line);
    }

    
    //remove all list data 
    public static void newCanvas() {
        allData.clear();
    }

    
    
    //save all the json data line by line using printwriter
    public static void saveCanvas() {
        try {
        	
            PrintWriter printWriter = new PrintWriter("canvas.txt");
            for (String line : allData) {
            	printWriter.println(line);
            }
            printWriter.flush();
            
        } catch (FileNotFoundException e) {
        	
            System.out.println("this .txt file does not exist in the system.");
        }

    }

    
    //open the file that previously saved by uer 
    public static void openCanvas(File file) {
        List<String> tmpData = new ArrayList<>();
        try {
        	
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bufferReader.readLine()) != null) {
                tmpData.add(line.trim());
            } 
            allData = tmpData;
            
        } catch (IOException e) {
        	
            System.out.println("Could not open invalided file.");
        }
    }

    public static void systemOver() {
        try {	
            Thread.sleep(1000);
            
        } catch (InterruptedException e) {
        	
            System.out.println("Trigger a interrupted exception in the tool class.");
        }
        System.exit(0);
    }

}