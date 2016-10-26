package v2;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Cory W. Cordell on 4/8/16.
 */
public class RandomRefString {

    public static int[] randomRefString(int min, int max, int amount, int percent) {
        SecureRandom random = new SecureRandom();
        int[] refStream = new int[amount];
        int amountA = amount * percent/100;

        for (int i = 0; i < amountA; i++ ) {
            refStream[i] = random.nextInt(min);
        }

        for (int i = amountA; i < amount; i++ ) {
            refStream[i] = min + random.nextInt(max - min);
        }

        arrayShuffle(amount, refStream);
        return refStream;
    }

    public static ArrayList<Integer>[] analyzeRefString(int[] array) {
        // find the max num in array
        int max = -1;
        for(int i : array)
            if(max < i) max = i;

        ArrayList<Integer>[] numbersArray = new ArrayList[max + 1];

        for(int i = 0; i < numbersArray.length; ++i) {
            numbersArray[i] = new ArrayList<>();
        }

        for(int i = 0; i < array.length; ++i) {
            numbersArray[array[i]].add(i);
        }

        return numbersArray;
    }

    public static ArrayList<Integer>[] analyzeRefString(ArrayList<Integer> list) {
        return analyzeRefString(listToArray(list));
    }

    public static int[] listToArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        Iterator<Integer> iter = list.iterator();
        int i = 0;
        while(iter.hasNext()) {
            array[i] = iter.next();
            ++i;
        }
        return array;
    }

    public static String analyzeRefStringToString(ArrayList<Integer>[] array)
    {
        int quantity = 0;
        int numsLessThan10 = 0;
        int min = array.length;
        int max = -1;
        int next;
        Iterator<Integer> iter;
        String s = "Num:\tCt\t(Occurrence)\n";

        for(int i = 0; i < array.length; ++i) {
            if(array[i].size() > 0) {
                if(min > i) min = i;
                max = i;
                s += i + ":\t\t" + array[i].size() + "\t(";
                quantity += array[i].size();
                iter = array[i].iterator();
                while (iter.hasNext()) {
                    next = iter.next();
                    if(i < 10) ++numsLessThan10;
                    s += next;
                    if(iter.hasNext()) s += ", ";
                }
                s += ")\n";
            }
        }
        s = "Numbers:\n" + s;
        s = "Max: " + max + "\n\n" + s;
        s = "Min: " + min + "\n" + s;
        s = "Ratio: " + (numsLessThan10/(double)quantity * 100) + "%\n" + s;
        s = "Less Than 10: " + numsLessThan10 + "\n" + s;
        s = "Quantity: " + quantity + "\n" + s;
        s = "\nReference String Analysis:\n" + s;
        return s;
    }

    public static void arrayShuffle(int amount, int[] array)
    {
        SecureRandom random = new SecureRandom();
        int newIndex;
        int temp;

        for(int i = 0; i < amount; ++i) {
            for (int j = 0; j < amount; ++j) {
                newIndex = random.nextInt(array.length);
                temp = array[j];
                array[j] = array[newIndex];
                array[newIndex] = temp;
            }
        }
    }

    public static void printArray(int[] array) {
        for(int i = 0; i < array.length; ++i) {
            System.out.print(array[i]);
            if (array.length > i) System.out.print(", ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] rs = randomRefString( 10, 100, 50, 90 );
        printArray(rs);
        String s = analyzeRefStringToString(analyzeRefString(rs));
        System.out.println(s);
    }
}
