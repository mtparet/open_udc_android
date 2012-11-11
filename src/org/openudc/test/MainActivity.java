package org.openudc.test;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
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
