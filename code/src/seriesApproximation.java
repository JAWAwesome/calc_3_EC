import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Created by Jared on 11/5/2015.
 */
public class seriesApproximation{
    static int n = 10;
    static int numThreads = 1;
    static ExecutorService executorService;
    static ArrayList<Points> ranges = new ArrayList<Points>();
    static ArrayList<Future<Double>> values = new ArrayList();

    public static void main (String args[]) {
        //Get the numbers to test
        ask();
        long startTime = System.nanoTime();
        // Check for errors
        if (n<=1||numThreads<=0) {
            print("Bad thread or number size");
            return;
        }
        // Find the values
        findRanges();
        // Create the places for the threads
        executorService = Executors.newFixedThreadPool(ranges.size());
        print("Using: "+ranges.size()+" threads");
        // Create the threads with the  parameters
        for (int i = 0; i < ranges.size(); i++) {
            Future<Double> rangeSum = executorService.submit(new Calculator(ranges.get(i),n));
            values.add(rangeSum);
        }
        // Output the values
        double sum = 0;
        for (Future<Double> temp: values) {
            try {
                sum = sum + temp.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sum = sum * 1.0/Math.pow(n,2.0);
        long elapsedTime = System.nanoTime() - startTime;
        executorService.shutdown();
        print("\n\n\n\n\n\n");
        print("Sum: " + sum);
        print("Process completed in: " + elapsedTime + " nano seconds, " + TimeUnit.NANOSECONDS.toMicros(elapsedTime) + " micro seconds, " + TimeUnit.NANOSECONDS.toMillis(elapsedTime) + " milli seconds, " + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + " seconds, " + TimeUnit.NANOSECONDS.toMinutes(elapsedTime) + " minutes, " + TimeUnit.NANOSECONDS.toHours(elapsedTime) + " hours");
    }

    // Used to find the ranges of the number and threads
    public static void findRanges() {
        // Check if the value is unreasonable
        if (n < numThreads) {
            numThreads = n;
        }
        // Set the size of the range
        int rangeSize = n/numThreads;
        // Store the last values
        int lastI = 1;
        // Create the ranges and add
        for (int i = rangeSize-1; i < n; i += rangeSize) {
            ranges.add(new Points(lastI,i+1));
            lastI = i + 1;
        }
        // Fix the end value for the test
        ranges.set(ranges.size()-1,new Points(ranges.get(ranges.size()-1).getX(),ranges.get(ranges.size()-1).getY()+1));
        // Check for the upper end being present
        if (n%numThreads!=0) {
            ranges.set(ranges.size()-1,new Points(ranges.get(ranges.size()-1).getX(),n+1));
        }
        // Print the ranges and specs
        print("Range Size: "+rangeSize);
        print("Ranges: "+ranges.toString());
    }

    // Used for printing stuff
    static void print(String s) {
        System.out.println(s);
    }

    // Used to ask for threads and number
    static void ask() {
        Scanner kb = new Scanner(System.in);
        try {
            print("Please enter the number to find the value of");
            n = kb.nextInt();
        } catch (Exception e) {
            e.printStackTrace();
            print("Invalid number, using: " + n);
        }
        try {
            print("Please enter the number of threads");
            numThreads = kb.nextInt();
        } catch (Exception e) {
            e.printStackTrace();
            print("Invalid number of threads: using: " + numThreads);
        }
    }
}

// Used for passing two values around
class Points {
    int x = 0;
    int y = 0;

    // Construct
    public Points(int a, int b) {
        x=a;y=b;
    }

    // Assign x
    public void setX(int a) {
        x=a;
    }

    // Assign y
    public void setY(int b) {
        y=b;
    }

    // Retrieve x
    public int getX() {
        return x;
    }

    // Retrieve y
    public int getY() {
        return y;
    }

    // Nice print
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}

// Used for calculating the values
class Calculator implements Callable<Double> {
    int upper = 0;
    int lower = 0;
    int val = 0;

    // Set the variables
    public Calculator(Points p, int x) {
        upper = p.getY();
        lower = p.getX();
        val = x;
    }

    // Calculate each values
    @Override
    public Double call() throws Exception {
        double sum = 0;
        // loop through the outer sum values
        for (int i = lower; i < upper; i++) {
            // calculate value for each number and add
            sum = sum + getVal(i,val);
        }
        return sum;
    }

    // Create the summation machine for the inner sum
    public double getVal(int i, int x) {
        double sum = 0;
        for (int j = 1; j <= Math.pow(x,2.0); j++) {
            sum = sum + f(i, j, x);
        }
        return sum;
    }

    // Get the function at the values
    public static double f(double i, double j, double x) {
        double temp = Math.pow(x,2.0);
        temp = temp + x*i;
        temp = temp + j;
        temp = Math.sqrt(temp);
        temp = 1.0/temp;
        return temp;
    }
}