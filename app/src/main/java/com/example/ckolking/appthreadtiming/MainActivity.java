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

import static java.lang.Boolean.*;

public class MainActivity extends AppCompatActivity {
    TextView tv; //for class wide reference to update status
    Button bt; // for class wide reference to update button text
    Integer nBtClicks; // for class wide reference to update number of button clicks
    Boolean btBusy; // for class wide reference to update blocking state of button
    Integer btJustify;
    //TODO: cannot make the "integer" resource work neither here in declaration nor in constructor.
    // int countMax = getResources().getInteger(R.integer.maxNumberToProcess);
    // instead hard-code:
    // Integer countMax = 10;
    Integer countMax;

    public MainActivity() {
        nBtClicks = 0;
        btBusy = FALSE;
        //Log.d("CEK-MainActivity constructor", "countMax is " + countMax);
        Log.d("CEK-MainActivity constructor", "Rid countMax is " + R.integer.maxNumberToProcess);
//        String testString = getString(R.integer.maxNumberToProcess);
//        Log.d("CEK-MainActivity constructor", "did I get this far 1");
//        Log.d("CEK-MainActivity constructor", "String countMax is " + testString + ".");
//        try {
//            countMax = Integer.parseInt(getString(R.integer.maxNumberToProcess));
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
        //Log.d("CEK-MainActivity constructor", "integer countMax is " + String.valueOf(countMax));
        //Log.d("CEK-MainActivity constructor", "log stuff");
        // countMax = Integer.valueOf(getString(R.integer.maxNumberToProcess));
//        countMax = 10;
    }

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
        String testString = getString(R.integer.maxNumberToProcess);
        Log.d("CEK-MainActivity constructor", "did I get this far 1");
        Log.d("CEK-MainActivity constructor", "String countMax is " + testString + ".");
        try {
            countMax = Integer.parseInt(getString(R.integer.maxNumberToProcess));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    class doButtonClick implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("CEK-doButtonClick", "UI message please wait");
            tv.setText("Processing, please wait.");
            // Log.d("CEK-doButtonClick", "CPU busy doing ThisTakesAWhile");
            // BAD:  calls a function that runs CPU intensive stuff while the UI has to wait:
            // BAD: BAD_ThisTakesAWhile();
            // GOOD:  call the Async functionality...
            // TODO: call the Async stuff!
            if (!btBusy) {
                ThisTakesAWhile ttaw = new ThisTakesAWhile();
                ttaw.execute();
                Log.d("CEK-doButtonClick", "UI message finished");
                // 'finished' only made sense before the Async task stuff
                // tv.setText("Finished.");
                // instead with the Async 'executed' in the background, then we can say that we have started:
                tv.setText("We have begun...");
            }
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
            tv.setText("Processed " + count + " of " + getString(R.integer.maxNumberToProcess) + ".");
            //tv.setText("Processed " + count + " of " + countMax + ".");
            Log.d("CEK-ThisTakesAWhile", "crash before here?");
        } while (count < 10);
    }

    class ThisTakesAWhile extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nBtClicks +=1;
            btBusy = TRUE;
            // what alignment # is what?
            // how do I get "set font / style to italics" ?
            btJustify = bt.getTextAlignment();
            bt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); // 2 = Left-just actually "START"
            Log.d("CEK-test", "current is " + bt.getTextAlignment());
            //bt.setText("..busy..");
            bt.setText(R.string.btBusyText);
            Log.d("CEK-onPreEx", "count of button clicks is " + nBtClicks);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.d("CEK-DoInB-start", "count of button clicks is " + nBtClicks);

            // fails because calling a class member method is disliked by the OS:
            // FAILS: BAD_ThisTakesAWhile();
            // so instead, implement the code inside here directly:
            //private void BAD_ThisTakesAWhile() {
                //mimic long running code
                int count = 0;
                do {
                    // this call is the CPU hog!!!
                    Log.d("CEK-DoInB-ThisTakesAWhile", "Starting CPU hog...");
                    SystemClock.sleep(1000);
                    Log.d("CEK-DoInB-ThisTakesAWhile", "... finished CPU hog.");
                    count++;
                    Log.d("CEK-DoInB-ThisTakesAWhile", "UI message processed count " + count);
                    // FAILS here in 'doInBackgr': tv.setText("Processed " + count + " of 10.");
                    // instead, change UX elements in 'onProgressUpdate' method of the Async class:
                    publishProgress(count);
                    // yes this log works AFTER I put the 'textView' UX update in the Asynch 'progress update' method
                    // Log.d("CEK-DoInB-ThisTakesAWhile", "crash before here?");
                } while (count < countMax);
            //}
            return count;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            btBusy = FALSE;
            bt.setTextAlignment(btJustify);
            //bt.setText("GO");
            bt.setText(R.string.btReadyText);
            Log.d("CEK-onPostEx", "count of button clicks is " + nBtClicks);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("CEK-onProgressUpd", "count of button clicks is " + nBtClicks);

            // to fix FAILS here in 'doInBackgr': tv.setText("Processed " + count + " of " + R.integer.maxNumberToProcess + ".");
            // fix:
            tv.setText("Processed " + values[0] + " of " + getString(R.integer.maxNumberToProcess) + ".");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d("CEK-onCancelled", "count of button clicks is " + nBtClicks);
        }
    }
}