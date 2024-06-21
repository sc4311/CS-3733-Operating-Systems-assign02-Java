import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


class SortThread extends Thread {
    private double[] array;
    private double average;

    public SortThread(double[] array) {
        this.array = array;
    }

    public void run() {
        Arrays.sort(array);
        average = Arrays.stream(array).average().orElse(0);
    }

    public double[] getArray() {
        return array;
    }

    public double getAverage() {
        return average;
    }
}

class MergeThread extends Thread {
    private double[] array1;
    private double[] array2;
    private double average1;
    private double average2;
    private double[] mergedArray;
    private double overallAverage;

    public MergeThread(double[] array1, double[] array2, double average1, double average2) {
        this.array1 = array1;
        this.array2 = array2;
        this.average1 = average1;
        this.average2 = average2;
    }

    public void run() {
        mergedArray = new double[array1.length + array2.length];
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
        overallAverage = (average1 + average2) / 2;
    }

    public double[] getMergedArray() {
        return mergedArray;
    }

    public double getOverallAverage() {
        return overallAverage;
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int n = Integer.parseInt(args[0]);
        double[] array = new double[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            array[i] = 1.0 + random.nextDouble() * 999.0;
        }

        double[] array1 = Arrays.copyOf(array, n);
        SortThread sortThread1 = new SortThread(array1);
        long start1 = System.nanoTime();
        sortThread1.start();
        sortThread1.join();
        long end1 = System.nanoTime();
        System.out.println("Sorting is done in " + TimeUnit.NANOSECONDS.toMillis(end1 - start1) + "ms when one thread is used");

        double[] array2 = Arrays.copyOfRange(array, 0, n / 2);
        double[] array3 = Arrays.copyOfRange(array, n / 2, n);
        SortThread sortThread2 = new SortThread(array2);
        SortThread sortThread3 = new SortThread(array3);
        long start2 = System.nanoTime();
        sortThread2.start();
        sortThread3.start();
        sortThread2.join();
        sortThread3.join();
        MergeThread mergeThread = new MergeThread(sortThread2.getArray(), sortThread3.getArray(), sortThread2.getAverage(), sortThread3.getAverage());
        mergeThread.start();
        mergeThread.join();
        long end2 = System.nanoTime();
        System.out.println("Sorting is done in " + TimeUnit.NANOSECONDS.toMillis(end2 - start2) + "ms when two threads are used");
    }
}