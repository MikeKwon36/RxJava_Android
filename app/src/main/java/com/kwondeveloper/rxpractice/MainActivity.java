package com.kwondeveloper.rxpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;

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

        subscription2.unsubscribe();
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
