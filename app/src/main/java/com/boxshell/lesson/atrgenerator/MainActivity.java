package com.boxshell.lesson.atrgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ATRmain";
    private static Button mBtnSend, mBtnReceive;
    private static EditText mText;
    private static Encoder mEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        init();

    }
    private void init(){
        mBtnSend = (Button) findViewById(R.id.buttonSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send clicked");
                // get content from
                String text;
                text = (String) mText.getText().toString();
                if(text.length() > 0) {
                    Log.d(TAG, text);
                }
                else{
                    return;
                }

                // how to change it to binary
                // english for now
                mEncoder.send(text);
            }
        });

        mBtnReceive = (Button) findViewById(R.id.buttonReceive);
        mBtnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Receive clicked");
                // switch to receive activity
                Intent myIntent = new Intent(MainActivity.this, ReceiveActivity.class);
                startActivity(myIntent);
            }
        });

        mText = (EditText) findViewById(R.id.editInput);
        //mText.setText("Waiting...");

        mEncoder = new Encoder(Freq.SAMPLE_RATE_1*20);// about 10 seconds

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
