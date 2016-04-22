package com.kwondeveloper.rxpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView test;
    private static String productionAppID = "generala-comadiho-PRD-438ccaf50-460fbf19";
    private static String walmartLookupUpc = "http://api.walmartlabs.com/v1/items?apiKey=jcpk6chshjwn5nbq2khnrvm9&upc=";
    private static String walmartReviewById1 = "http://api.walmartlabs.com/v1/reviews/";
    private static String walmartReviewById2 = "?format=json&apiKey=jcpk6chshjwn5nbq2khnrvm9";
    private static String walmartLookupKeyword = "http://api.walmartlabs.com/v1/search?query=";
    private static final String walmart_api_key ="&format=json&apiKey=5hbnkvrdvq3dafvfax34meez";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (TextView)findViewById(R.id.testTextView);

        //example 1 (more Java oriented)==========================================================
        hello("Ben", "George");


        //example 2 (more Android oriented)=======================================================
        Observable<String> myObservable = Observable.just("Hello"); // Emits "Hello"
        Observer<String> myObserver = new Observer<String>() {
            @Override
            public void onCompleted() {
                // Called when the observable has no more data to emit
            }
            @Override
            public void onError(Throwable e) {
                // Called when the observable encounters an error
            }
            @Override
            public void onNext(String s) {
                // Called each time the observable emits data
                Log.d("MY OBSERVER", s);
            }
        };
        Subscription subscription1 = myObservable.subscribe(myObserver);
        Action1<String> myAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d("My Action", s);
            }
        };
        Subscription subscription2 = myObservable.subscribe(myAction);
        subscription1.unsubscribe();
        subscription2.unsubscribe();

        //example 3 (operators)==================================================================

        Observable<Integer> myArrayObservable
                = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6}); // Emits each item of the array, one at a time
        myArrayObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                Log.d("My Action", String.valueOf(i)); // Prints the number received
            }
        });

        //map operator returns a new Observable, rather than change the original
        myArrayObservable = myArrayObservable.map(new Func1<Integer, Integer>() { // Input and Output are both Integer
            @Override
            public Integer call(Integer integer) {
                return integer * integer; // Square the number
            }
        });

        //operators can also be chained, where the following code block uses the skip operator
        // to skip the first two numbers, and then the filter operator to ignore odd numbers
        myArrayObservable
                .skip(2) // Skip the first two items
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) { // Ignores any item that returns false
                        return integer % 2 == 0;
                    }
                });

        //multi-threading example creates a custom Observable using the create operator.
        // When you create an Observable in this manner, you have to implement the
        // Observable.OnSubscribe interface and control what it emits by calling the
        // onNext, onError, and onCompleted methods yourself
        Observable<String> requestDataFromInternet = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String data = fetchData("toothpaste"); //hypothetical network call to pull a string from a URL
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                }catch(Exception e){
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });
        //When the Observable is ready, you can use subscribeOn and observeOn to specify the threads it should use and subscribe to it.
        requestDataFromInternet
                .subscribeOn(Schedulers.newThread()) // Create a new Thread
                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        test.setText(test.getText() + "\n" + s); // Change a View
                    }
                });
    }

    
    //fetch data makes a network call to Walmart for a product price on the UI thread (so
    // unless method is called on a separate thread, Android will crash
    private String fetchData(String keywordSearch){
        String data ="";
        String price="";
        try {
            URL url = new URL(walmartLookupKeyword + keywordSearch+walmart_api_key);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((data = bufferedReader.readLine()) != null) {
                stringBuilder.append(data);
            }
            bufferedReader.close();
            data = stringBuilder.toString();
        } catch (Throwable thr) {
            thr.fillInStackTrace();
        }
        try {
            JSONObject dataObject = new JSONObject(data);
            JSONArray priceArray = dataObject.optJSONArray("items");
            JSONObject item = priceArray.optJSONObject(0);
            price = item.optString("salePrice", "Product Unavailable");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return price;
    }

    public static void hello(String... names) {
        Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("Hello " + s + "!");
            }
        });
    }
}
