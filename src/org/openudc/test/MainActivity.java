package org.openudc.test;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView tvClear,tvHashed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvClear = (TextView) findViewById(R.id.tvClear);
        tvHashed = (TextView) findViewById(R.id.tvHashed);
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String Udid2Clear = prefs.getString("udid2_c", "no stored");
		String Udid2Hashed = prefs.getString("udid2_h", "no stored");
		
		tvHashed.setText("Udid2 hashed : " + Udid2Hashed);
		tvClear.setText("Udid2 clear : " + Udid2Clear);



        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void onClick(View v){
    	switch (v.getId()){
    		case R.id.create_udid2 : startCreateUddi2();
    		break;
    	}
    		
    }
    
    private void startCreateUddi2(){
        Intent i = new Intent(this, Udid.class);
        
        startActivity(i);
    	
    }
}
