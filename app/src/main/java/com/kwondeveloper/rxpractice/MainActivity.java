package com.kwondeveloper.rxpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
