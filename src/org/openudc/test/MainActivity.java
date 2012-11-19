package org.openudc.test;

import org.thialfihar.android.apg.integration.ApgData;
import org.thialfihar.android.apg.integration.ApgIntentHelper;

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
	
	TextView tvClear,tvHashed, tvKeyId;
    ApgIntentHelper mApgIntentHelper;

    ApgData mApgData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvClear = (TextView) findViewById(R.id.tvClear);
        tvHashed = (TextView) findViewById(R.id.tvHashed);
        tvKeyId = (TextView) findViewById(R.id.tvKeyId);
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String Udid2Clear = prefs.getString("udid2_c", "no stored");
		String Udid2Hashed = prefs.getString("udid2_h", "no stored");
		String signatureUserId = prefs.getString("signatureUserId", null);
		Long signaturekeyId = prefs.getLong("signaturekeyId", 0);

		
		tvHashed.setText("Udid2 hashed : " + Udid2Hashed);
		tvClear.setText("Udid2 clear : " + Udid2Clear);
        tvKeyId.setText("UserId: " + signatureUserId + "keyId" + signaturekeyId);

		
		mApgIntentHelper = new ApgIntentHelper(this);
		mApgData = new ApgData();
		
		if (signatureUserId == null ){
			askKey();
		}
				



        
        
    }

    private void askKey() {
    	mApgIntentHelper.selectSecretKey();
		
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
    		case R.id.test1 : mApgIntentHelper.selectSecretKey();
    		break;
    	}
    		
    }
    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String Udid2Clear = prefs.getString("udid2_c", "no stored");
		String Udid2Hashed = prefs.getString("udid2_h", "no stored");
		
		tvHashed.setText("Udid2 hashed : " + Udid2Hashed);
		tvClear.setText("Udid2 clear : " + Udid2Clear);
	}

	private void startCreateUddi2(){
        Intent i = new Intent(this, Udid.class);
        
        startActivity(i);
    	
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // this updates the mApgData object to the result of the methods
        boolean result = mApgIntentHelper.onActivityResult(requestCode, resultCode, data, mApgData);
        if (result) {
    			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	        SharedPreferences.Editor editor = prefs.edit();
    			editor.putString("signatureUserId", mApgData.getSignatureUserId());
    			editor.putLong("signaturekeyId", mApgData.getSignatureKeyId());

    	        editor.commit();
        }

        // continue with other activity results
        super.onActivityResult(requestCode, resultCode, data);
    }
}
