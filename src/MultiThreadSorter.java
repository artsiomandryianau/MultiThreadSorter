import java.util.Random;
import java.util.Scanner;

public class MultiThreadSorter {

    public static int numThreads;

    public static void main(String[] args) {
        System.out.println("Enter number of threads");
        Scanner scanner = new Scanner(System.in);
        numThreads = scanner.nextInt();

        /* generate data */
        Integer[] ints = new Integer[1000000];
        Random randGen = new Random();
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = new Integer(randGen.nextInt(10000));
        }

        long startTime = System.currentTimeMillis();
        ints = sort(ints, numThreads);

        // -----------------> to chek if its sorted <-----------------
        /*for (int i = 0; i < ints.length; ++i) {
            System.out.println(ints[i]);
        }*/
        long endTime = System.currentTimeMillis();

        System.out.println(
                "Parallel mergesort of " + ints.length + " random integers" + " with " + numThreads + " threads");

        if (isSorted(ints)) {
            System.out.println("Sort succeeded!!!");
        } else {
            System.out.println("Sort failed:(");
        }

        System.out.println("Time " + ((double) (endTime - startTime) / 1000) + " seconds");
    }

    /* check whether array is sorted */
    public static boolean isSorted(Integer[] data) {
        for (int i = 0; i < data.length - 1; ++i) {
            if (data[i].compareTo(data[i + 1]) > 0)
                return false;
        }
        return true;
    }

    public static Integer[] sort(Integer[] data, int numThreads) {
        Integer[] temp = mergesort(data, 0, data.length - 1, numThreads);
        return temp;
    }

    private static Integer[] mergesort(Integer[] data, int firstIndex, int lastIndex, int numThreads) {
        if (firstIndex > lastIndex) {
            return new Integer[0];
        } else if (firstIndex == lastIndex) {
            return new Integer[] { data[firstIndex] };
        }
        int midIndex = (firstIndex + lastIndex) / 2;

        Integer[] temp1 = null;
        Thread otherThread = null;
        CodeForThread other = null;

        if (numThreads > 1) {
            other = new CodeForThread(data, firstIndex, midIndex, numThreads / 2);
            otherThread = new Thread(other);
            otherThread.start();
        } else {
            temp1 = mergesort(data, firstIndex, midIndex, numThreads / 2);
        }
        Integer[] temp2 = mergesort(data, midIndex + 1, lastIndex, numThreads - (numThreads / 2));

        if (numThreads > 1) {
            try {
                otherThread.join();
            } catch (InterruptedException e) {
                System.out.println("this should not happen");
            }
            temp1 = other.getResult();
        }

        return merge(temp1, temp2);
    }

    private static Integer[] merge(Integer[] a1, Integer[] a2) {
        Integer[] result = new Integer[a1.length + a2.length];

        int i1 = 0; /* index into a1 */
        int i2 = 0; /* index into a2 */

        for (int j = 0; j < result.length; ++j) {
            if (i2 >= a2.length) {
                result[j] = a1[i1++];
            } else if (i1 >= a1.length) {
                result[j] = a2[i2++];
            } else if (a1[i1].compareTo(a2[i2]) <= 0) {
                result[j] = a1[i1++];
            } else {
                result[j] = a2[i2++];
            }
        }
        return result;
    }

    private static class CodeForThread implements Runnable {

        private Integer[] data;
        private int firstIndex;
        private int lastIndex;
        int numThreads;
        private Integer[] result;

        public CodeForThread(Integer[] d, int first, int last, int nThreads) {
            data = d;
            firstIndex = first;
            lastIndex = last;
            numThreads = nThreads;
        }

        public void run() {

            result = mergesort(data, firstIndex, lastIndex, numThreads);
        }

        public Integer[] getResult() {

            return result;
        }
    }
}