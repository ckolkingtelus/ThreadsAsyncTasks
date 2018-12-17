package com.example.ckolking.appthreadtiming;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv; //for class wide reference to update status
    Button bt; // for class wide reference to update button text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the references to on screen items
        tv = (TextView) findViewById(R.id.textView);
        bt = (Button) findViewById(R.id.button);
        //handle button presses
        Log.d("CEK-onCreate", "setOnClickListener");
        findViewById(R.id.button).setOnClickListener(new doButtonClick());
    }

    class doButtonClick implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("CEK-doButtonClick", "UI message please wait");
            tv.setText("Processing, please wait.");
            Log.d("CEK-doButtonClick", "CPU busy doing ThisTakesAWhile");
            // BAD:  calls a function that runs CPU intensive stuff while the UI has to wait:
            // BAD: BAD_ThisTakesAWhile();
            // GOOD:  call the Async functionality...
            // TODO: call the Async stuff!
            ThisTakesAWhile ttaw = new ThisTakesAWhile();
            ttaw.execute();
            Log.d("CEK-doButtonClick", "UI message finished");
            // 'finished' only made sense before the Async task stuff
            // tv.setText("Finished.");
            // instead with the Async 'executed' in the background, then we can say that we have started: 
            tv.setText("We have begun...");
        }
    }

    private void BAD_ThisTakesAWhile() {
        //mimic long running code
        int count = 0;
        do {
            // this call is the CPU hog!!!
            Log.d("CEK-ThisTakesAWhile", "Starting CPU hog...");
            SystemClock.sleep(1000);
            Log.d("CEK-ThisTakesAWhile", "... finished CPU hog.");
            count++;
            Log.d("CEK-ThisTakesAWhile", "UI message processed count " + count);
            tv.setText("Processed " + count + " of 10.");
            Log.d("CEK-ThisTakesAWhile", "crash before here?");
        } while (count < 10);
    }

    class ThisTakesAWhile extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            // fails because calling a class member method is disliked by the OS:
            // FAILS: BAD_ThisTakesAWhile();
            // so instead, implement the code inside here directly:
            //private void BAD_ThisTakesAWhile() {
                //mimic long running code
                int count = 0;
                do {
                    // this call is the CPU hog!!!
                    Log.d("CEK-ThisTakesAWhile", "Starting CPU hog...");
                    SystemClock.sleep(1000);
                    Log.d("CEK-ThisTakesAWhile", "... finished CPU hog.");
                    count++;
                    Log.d("CEK-ThisTakesAWhile", "UI message processed count " + count);
                    // FAILS here: tv.setText("Processed " + count + " of 10.");
                    // instead, change UX elements in 'onProgressUpdate' method of the Async class:
                    publishProgress(count);
                    // yes this log works AFTER I put the 'textView' UX update in the Asynch 'progress update' method
                    // Log.d("CEK-ThisTakesAWhile", "crash before here?");
                } while (count < 10);
            //}
            return count;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // FAILS here: tv.setText("Processed " + count + " of 10.");
            // fix:
            tv.setText("Processed " + values[0] + " of 10.");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}