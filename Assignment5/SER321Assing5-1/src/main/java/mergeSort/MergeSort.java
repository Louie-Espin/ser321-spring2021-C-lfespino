package mergeSort;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.Instant;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MergeSort {
  /**
   * Thread that declares the lambda and then initiates the work
   */

  public static int message_id = 0;

  public static JSONObject init(int[] array) {
    JSONArray arr = new JSONArray();
    for (var i : array) {
      arr.put(i);
    }
    JSONObject req = new JSONObject();
    req.put("method", "init");
    req.put("data", arr);
    return req;
  }

  public static JSONObject peek() {
    JSONObject req = new JSONObject();
    req.put("method", "peek");
    return req;
  }

  public static JSONObject remove() {
    JSONObject req = new JSONObject();
    req.put("method", "remove");
    return req;
  }
  
  public static void Test(int port, String host) {
    int[] a = { 5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6};
    JSONObject response = NetworkUtils.send(host, port, init(a));
    
    System.out.println(response);
    response = NetworkUtils.send(host, port, peek());
    System.out.println(response);

    while (true) {
      response = NetworkUtils.send(host, port, remove());

      if (response.getBoolean("hasValue")) {
        System.out.println(response);;
 
      } else{
        break;
      }
    }
  }
  
  public static void TestOnes(int port, String host) {
	    int[] a = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	    JSONObject response = NetworkUtils.send(host, port, init(a));
	    
	    System.out.println(response);
	    response = NetworkUtils.send(host, port, peek());
	    System.out.println(response);

	    while (true) {
	      response = NetworkUtils.send(host, port, remove());

	      if (response.getBoolean("hasValue")) {
	        System.out.println(response);;
	 
	      } else{
	        break;
	      }
	    }
  }
  
  public static void TestLong(int port, String host) {
	    int[] a = { 5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6,
	    		5, 1, 6, 2, 3, 4, 10,634,34,23,653, 23,2 ,6};
	    JSONObject response = NetworkUtils.send(host, port, init(a));
	    
	    System.out.println(response);
	    response = NetworkUtils.send(host, port, peek());
	    System.out.println(response);

	    while (true) {
	      response = NetworkUtils.send(host, port, remove());

	      if (response.getBoolean("hasValue")) {
	        System.out.println(response);;
	 
	      } else{
	        break;
	      }
	    }
  }
  
  public static void TestShort(int port, String host) {
	    int[] a = { 5, 1, 6, 2};
	    JSONObject response = NetworkUtils.send(host, port, init(a));
	    
	    System.out.println(response);
	    response = NetworkUtils.send(host, port, peek());
	    System.out.println(response);

	    while (true) {
	      response = NetworkUtils.send(host, port, remove());

	      if (response.getBoolean("hasValue")) {
	        System.out.println(response);;
	 
	      } else{
	        break;
	      }
	    }
	  }

  public static void main(String[] args) {
	  // measure start time
	  Instant start1 = Instant.now();
	  // use the port of one of the branches to test things
	  Test(Integer.valueOf(args[0]), args[1]); // args 7000 args "localhost"
	  // measure end time
	  Instant finish1 = Instant.now();
	  long timeElapsed1 = Duration.between(start1, finish1).toMillis();
	  System.out.println("TIME 1 (given array): " + timeElapsed1 + " milliseconds");
	  
	  // measure start time
	  Instant start2 = Instant.now();
	  // use the port of one of the branches to test things
	  TestOnes(Integer.valueOf(args[0]), args[1]); // args 7000 args "localhost"
	  // measure end time
	  Instant finish2 = Instant.now();
	  long timeElapsed2 = Duration.between(start2, finish2).toMillis();
	  System.out.println("TIME 2 (array of ones): " + timeElapsed2 + " milliseconds");
	  
	  // measure start time
	  Instant start3 = Instant.now();
	  // use the port of one of the branches to test things
	  TestLong(Integer.valueOf(args[0]), args[1]); // args 7000 args "localhost"
	  // measure end time
	  Instant finish3 = Instant.now();
	  long timeElapsed3 = Duration.between(start3, finish3).toMillis();
	  System.out.println("TIME 3 (long array): " + timeElapsed3 + " milliseconds");
	  
	  // measure start time
	  Instant start4 = Instant.now();
	  // use the port of one of the branches to test things
	  TestShort(Integer.valueOf(args[0]), args[1]); // args 7000 args "localhost"
	  // measure end time
	  Instant finish4 = Instant.now();
	  long timeElapsed4 = Duration.between(start4, finish4).toMillis();
	  System.out.println("TIME 3 (long array): " + timeElapsed4 + " milliseconds");
  }
}
