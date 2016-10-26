package v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by cwcordell on 4/8/16.
 */
public class RunTest {
    public RunTest() {
        super();
    }

    public static int[] run(int[] refString) {
        int[] results = new int[2];
        long startTime;
        startTime = System.nanoTime();
//        results[0] = rsp.optimal();
        results[1] = (int)(System.nanoTime() - startTime);
        return results;
    }

    // get a reference string array from an existing file
    public static int[] getRefStringFromFile(String path) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        ArrayList<Integer> list = new ArrayList<>();

        while(scan.hasNext()) {
            list.add(scan.nextInt());
        }

        return RandomRefString.listToArray(list);
    }

    public static void publishRefStringToFile(String filename, int[] array) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename,"UTF-8");
        for(int i : array)
            writer.println(i);
        writer.close();
    }

    public static void publishRefStringDataToFile(String filename, String refStringFilename, String data) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename,"UTF-8");
        writer.println("Reference String Filename: " + refStringFilename);
        writer.print(data);
        writer.close();
    }

    public static int[] getRefStream(int min, int max, int amount, int percent, String refStringFilename, String dataFilename) throws FileNotFoundException, UnsupportedEncodingException {
        int[] refString = RandomRefString.randomRefString(min, max, amount, percent);
        publishRefStringToFile(refStringFilename, refString);
        publishRefStringDataToFile(dataFilename, refStringFilename, RandomRefString.analyzeRefStringToString(RandomRefString.analyzeRefString(refString)));
        return refString;
    }

    public static void main(String[] args) {
        // newRefString = true will generate a new ref. string using v2.RandomRefString.randomRefString()
        // otherwise a file with the prefix will be used
        boolean newRefString = false;
        String prefix = "Random_Non-Repeating";
        RefStringParser rsp;
        int[] refString;
        long startTime;

        // the resident set sizes to test for the algorithms
        int[] residentSetSize = {1, 5, 10, 25, 50, 75, 100};

        // if generating a new ref. string the prefix will be a time-date stamp if prefix is empty
        if(newRefString && prefix.equals(""))
            prefix = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // suffixes for the output files
        String resultFilename = String.format("%s_Results.csv", prefix);
        String dataFilename = String.format("%s_RefStringData.txt", prefix);

        // suffixes for the input file
        String refStringFilename = String.format("%s_ReferenceString.txt", prefix);


        try {
            if(newRefString) {
                // get a new reference stream
                refString = getRefStream(10, 100, 10000, 90, refStringFilename, dataFilename);

                // run the analyzer on the reference string and output to the data file
            }
            else {
                // get an array from a reference string file
                refString = getRefStringFromFile(refStringFilename);
            }
            publishRefStringDataToFile(dataFilename, refStringFilename,
                    RandomRefString.analyzeRefStringToString(RandomRefString.analyzeRefString(refString)));

            // get a new file writer object
            PrintWriter writer = new PrintWriter(resultFilename,"UTF-8");

            // write the file header
            writer.println("Filename,\t\t\t\t\t" + resultFilename);
            writer.println("Reference String File,\t\t" + refStringFilename);
            writer.println("Reference String Data File,\t" + dataFilename);
            writer.println();

            // write the column headings
            writer.println("Algorithm,\tRes. Set Size,\tPage Faults,\tExecution Time");

            // execute each resident set size and write the results to the file
            for(int i : residentSetSize) {
                // instantiate the RefStringParser object
                rsp = new RefStringParser(refString, i);

                // optimal
                startTime = System.nanoTime();
                writer.println("Optimal,\t" + i + ",\t\t\t\t" + rsp.optimal()
                        + ",\t\t\t\t" + (System.nanoTime() - startTime));

                // LRU
                startTime = System.nanoTime();
                writer.println("LRU,\t\t" + i + ",\t\t\t\t" + rsp.leastRecentlyUsed()
                        + ",\t\t\t\t" + (System.nanoTime() - startTime));

                // Random
                startTime = System.nanoTime();
                writer.println("Random,\t\t" + i + ",\t\t\t\t" + rsp.random()
                        + ",\t\t\t\t" + (System.nanoTime() - startTime));
            }

            // close the file writer
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
