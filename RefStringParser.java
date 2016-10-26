package v2;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Cory W. Cordell on 4/8/16.
 */
public class RefStringParser {
    private boolean audit = false;
    private int pageFaults;
    private int residentSetSize;
    private int[] refString;
    private int[] residentSet;

    public RefStringParser(int[] refString, int residentSetSize) {
        super();
        this.residentSetSize = residentSetSize;
        this.refString = refString;
        this.residentSet = new int[residentSetSize];
        this.pageFaults = residentSetSize;
    }

    public int optimal() {
        int refStringPointer = 0;
        pageFaults = 0;
        int[] residentSetQueue = new int[residentSet.length];

        // for testing purposes only - turn off with audit class variable
        audit(residentSetQueue, refStringPointer);

        // initialize the resident set by setting its values to resemble a clean state
        initializeResidentSet(residentSetQueue);

        // populate the resident set fully
        int i = 0;
        while(i < residentSet.length && refStringPointer < refString.length) {
            if(!contains(refString[refStringPointer])) {
                residentSet[i] = refString[refStringPointer];
                ++pageFaults;
                ++i;
            }
            ++refStringPointer;

            // for testing purposes only - turn off with audit class variable
            audit(residentSetQueue, refStringPointer);
        }

        updateresidentSetQueue(residentSetQueue, residentSet, refString, refStringPointer);

        // for testing purposes only - turn off with audit class variable
        if(audit) System.out.println("*********** Initialization Done ***********\n");
        audit(residentSetQueue, refStringPointer);

        // run through the rest of the refString
        int index;
        while(refStringPointer < refString.length) {
            if(!contains(refString[refStringPointer])) {
                index = getMaxIndex(residentSetQueue, refString.length);
                residentSet[index] = refString[refStringPointer];

                ++pageFaults;
            }
            updateresidentSetQueue(residentSetQueue, residentSet, refString, refStringPointer);
            ++refStringPointer;

            // for testing purposes only - turn off with audit class variable
            audit(residentSetQueue, refStringPointer);
        }

        // for testing purposes only - turn off with audit class variable
        if(audit) System.out.println("Page Faults: " + pageFaults);
        if(audit) System.out.println("Done!");
        return pageFaults;
    }

    // audit for optimal
    private void audit(int[] residentSetQueue, int refStringPointer) {
        if(audit) {
            printArray("RefString: \t", refString);
            printArray("ResSet: \t", residentSet);
            printArray("ResSetQ: \t", residentSetQueue);
            System.out.println("Faults: \t" + pageFaults);
            System.out.println("Pointer: \t" + refStringPointer);
            if(refStringPointer < refString.length)
                System.out.println("Next Tok: \t" + refString[refStringPointer]);
            System.out.println();
        }
    }

    // audit for other than optimal
    private void audit(int refStringPointer) {
        if(audit) {
            printArray("RefString: \t", refString);
            printArray("ResSet: \t", residentSet);
            System.out.println("Faults: \t" + pageFaults);
            System.out.println("Pointer: \t" + refStringPointer);
            if(refStringPointer < refString.length)
                System.out.println("Next Tok: \t" + refString[refStringPointer]);
            System.out.println();
        }
    }

    private static void updateresidentSetQueue(int[] residentSetQueue, int[] residentSet, int[] refString, int pointer) {
        for(int i = 0; i < residentSet.length; ++i) {
            residentSetQueue[i] = getNextInstance(residentSet[i], refString, pointer);
        }
    }

    private static int getNextInstance(int val, int[] refString, int pointer) {
        ++pointer;
        while(pointer < refString.length && val != refString[pointer]) {
            ++pointer;
        }
        return pointer;
    }

    private static int getMaxIndex(int[] residentSetQueue, int limit) {
        int max = 0;
        for(int i = 0; i < residentSetQueue.length; ++i) {
            if(residentSetQueue[i] >= limit) return i;
            if(residentSetQueue[i] > residentSetQueue[max]) max = i;
        }
        return max;
    }

    private void initializeResidentSet(int[] residentSetQueue) {
        for(int i = 0; i < residentSet.length; ++i) {
            residentSet[i] = -1;
            residentSetQueue[i] = refString.length + 1;
        }
    }

    private void initializeResidentSet() {
        for(int i = 0; i < residentSet.length; ++i) {
            residentSet[i] = -1;
        }
    }

    public static void printArray(String string, int[] array) {
        System.out.print(string);
        for(int i = 0; i < array.length; ++i) {
            System.out.print(array[i]);
            if ((array.length - 1) > i) {
                if(i != 0 && (i+1)%10 == 0)
                    System.out.print(" | ");
                else
                    System.out.print(", ");
            }
        }

        System.out.println();
    }

    private boolean contains( int value ) {
        for ( int resident : residentSet )
            if ( resident == value )
                return true;

        return false;
    }

    public int leastRecentlyUsed() {
        LinkedList<Integer> list = new LinkedList<>();
        pageFaults = 0;
        int residentSpace = 0;
        int pointer = 0;
        int page;

        // set values in resident set to resemble a clean state
        initializeResidentSet();

        while ( pointer < refString.length ) {
            page = refString[pointer];

            //Resident set has not filled up yet
            if (residentSpace < residentSet.length && !contains(page)) {
                residentSet[residentSpace] = page;
                residentSpace++;
                pageFaults++;
                list.add(page);
            }
            //Replace a page in the Resident Set
            else if (!contains(page)) {
                int replacedPage = list.getFirst();
                int position = 0;

                while ( position < residentSet.length - 1 && residentSet[position] != replacedPage ) {
                    position++;
                }

                residentSet[position] = page;
                pageFaults++;
                list.removeFirst();
                list.add(page);
            }
            //Page is already present, update its position as MRU
            else {
                list.remove(Integer.valueOf(page));
                list.add(page);
            }
            ++pointer;

            // audit is for testing purposes only - turn off with audit class variable
            audit(pointer);
        }
        return pageFaults;
    }

    public int random() {
        Random rand = new Random();
        pageFaults = 0;
        int residentSpace = 0;
        int pointer = 0;
        int page;

        // set values in resident set to resemble a clean state
        initializeResidentSet();

        while ( pointer < refString.length ) {
            page = refString[pointer];

            //Resident set has not filled up yet
            if ( residentSpace < residentSet.length && !contains(page)) {
                residentSet[residentSpace] = page;
                residentSpace++;
                pageFaults++;
            }
            //Replace a page in the Resident Set
            else if (!contains(page)) {
                residentSet[rand.nextInt(residentSet.length)] = page;
                pageFaults++;
            }

            //Else: Do nothing, set already contains the page
            ++pointer;

            // audit is for testing purposes only - turn off with audit class variable
            audit(pointer);
        }

        return pageFaults;
    }

    private static void printResults( String[][] chart ) {
        for (String[] line : chart)
            System.out.printf("%10s%32s%32s\n", line[0], line[1], line[2]);
    }

    public void setAudit(boolean val)
    {
        audit = val;
    }

    // for testing purposes only
    public static void main(String[] args) {
        RefStringParser rsp = new RefStringParser(new int[]{2,1,12,3,7,22,1,8,0,2,1,5,34,19,0,84,99,1,9,19}, 5);
        rsp.setAudit(true);

        System.out.println("*********** Optimal ************");
        rsp.optimal();

        System.out.println("\n\n*********** LRU ************");
        rsp.leastRecentlyUsed();

        System.out.println("\n\n*********** Random ************");
        rsp.random();
    }
}
