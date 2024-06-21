import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// This class is a thread that sorts an array and calculates its average
class SortThread extends Thread {
    private double[] array;
    private double average;

    // Constructor that takes an array to sort
    public SortThread(double[] array) {
        this.array = array;
    }

    // The run method is called when the thread is started
    public void run() {
        Arrays.sort(array); // Sort the array
        average = Arrays.stream(array).average().orElse(0); // Calculate the average
    }

    // Getter for the sorted array
    public double[] getArray() {
        return array;
    }

    // Getter for the average of the array
    public double getAverage() {
        return average;
    }
}

// This class is a thread that merges two sorted arrays and calculates the overall average
class MergeThread extends Thread {
    private double[] array1;
    private double[] array2;
    private double average1;
    private double average2;
    private double[] mergedArray;
    private double overallAverage;

    // Constructor that takes two sorted arrays and their averages
    public MergeThread(double[] array1, double[] array2, double average1, double average2) {
        this.array1 = array1;
        this.array2 = array2;
        this.average1 = average1;
        this.average2 = average2;
    }

    // The run method is called when the thread is started
    public void run() {
        mergedArray = new double[array1.length + array2.length]; // Initialize the merged array
        // Merge the two sorted arrays into one
        int i = 0, j = 0, k = 0;
        while (i < array1.length && j < array2.length) {
            if (array1[i] <= array2[j]) {
                mergedArray[k++] = array1[i++];
            } else {
                mergedArray[k++] = array2[j++];
            }
        }
        while (i < array1.length) {
            mergedArray[k++] = array1[i++];
        }
        while (j < array2.length) {
            mergedArray[k++] = array2[j++];
        }
        overallAverage = (average1 + average2) / 2; // Calculate the overall average
    }

    // Getter for the merged array
    public double[] getMergedArray() {
        return mergedArray;
    }

    // Getter for the overall average
    public double getOverallAverage() {
        return overallAverage;
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int n = Integer.parseInt(args[0]); // Parse the size of the array from the command line arguments
        double[] array = new double[n]; // Initialize the array
        Random random = new Random(); // Create a random number generator
        // Fill the array with random numbers
        for (int i = 0; i < n; i++) {
            array[i] = 1.0 + random.nextDouble() * 999.0;
        }

        // Sort the array using one thread
        double[] array1 = Arrays.copyOf(array, n);
        SortThread sortThread1 = new SortThread(array1);
        long start1 = System.nanoTime(); // Start the timer
        sortThread1.start(); // Start the sorting thread
        sortThread1.join(); // Wait for the sorting thread to finish
        long end1 = System.nanoTime(); // Stop the timer
        System.out.println("Sorting is done in " + TimeUnit.NANOSECONDS.toMillis(end1 - start1) + "ms when one thread is used");

        // Sort the array using two threads
        double[] array2 = Arrays.copyOfRange(array, 0, n / 2);
        double[] array3 = Arrays.copyOfRange(array, n / 2, n);
        SortThread sortThread2 = new SortThread(array2);
        SortThread sortThread3 = new SortThread(array3);
        long start2 = System.nanoTime(); // Start the timer
        sortThread2.start(); // Start the first sorting thread
        sortThread3.start(); // Start the second sorting thread
        sortThread2.join(); // Wait for the first sorting thread to finish
        sortThread3.join(); // Wait for the second sorting thread to finish
        // Merge the two sorted halves and calculate the overall average
        MergeThread mergeThread = new MergeThread(sortThread2.getArray(), sortThread3.getArray(), sortThread2.getAverage(), sortThread3.getAverage());
        mergeThread.start(); // Start the merging thread
        mergeThread.join(); // Wait for the merging thread to finish
        long end2 = System.nanoTime(); // Stop the timer
        System.out.println("Sorting is done in " + TimeUnit.NANOSECONDS.toMillis(end2 - start2) + "ms when two threads are used");
    }
}