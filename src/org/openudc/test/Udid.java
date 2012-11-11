package org.openudc.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openudc.test.lib.Udid2;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class Udid extends Activity {

	private EditText et_last_name, et_first_name, et_location;
	private DatePicker dp_birth_date;
	private TextView tv_udid, tv_udid_2;
	private int addressSelected;
	private List<Address> listAdd;
	private String last_name;
	private String first_name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_udid);

		et_last_name = (EditText) findViewById(R.id.editText1);
		et_first_name = (EditText) findViewById(R.id.editText2);
		et_location = (EditText) findViewById(R.id.editText3);
		dp_birth_date = (DatePicker) findViewById(R.id.datePicker1);
		tv_udid = (TextView) findViewById(R.id.textView1);
		tv_udid_2 = (TextView) findViewById(R.id.textView2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_udid, menu);
		return true;
	}

	public void onClick(View v){
		last_name = et_last_name.getText().toString();
		first_name = et_first_name.getText().toString();
		String location_string = et_location.getText().toString();

		if( last_name.length() == 0 || first_name.length() == 0 || location_string.length() == 0 ){
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(Udid.this);

			// 2. Chain together various setter methods to set the dialog characteristics
			builder.setMessage(R.string.dialog_creation_error_message)
			.setTitle(R.string.dialog_creation_error_title);

			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();

			dialog.show();
		}else{

			Geocoder geo = new Geocoder(getApplicationContext());
			try {
				listAdd = geo.getFromLocationName(location_string, 7);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if( listAdd == null || listAdd.size() == 0){
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(Udid.this);

				// 2. Chain together various setter methods to set the dialog characteristics
				builder.setMessage(R.string.dialog_creation_error_adresse_message)
				.setTitle(R.string.dialog_creation_error_title);

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();

				dialog.show();
			}else{
				ArrayList<String> listAddress = new ArrayList<String>();
				for( Address address : listAdd){
					listAddress.add(address.getLocality() + "  " + address.getCountryName());
				}
				
				CharSequence[] csAddress = listAddress.toArray(new CharSequence[listAddress.size()]);

				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder2 = new AlertDialog.Builder(Udid.this);

				// 2. Chain together various setter methods to set the dialog characteristics
				builder2.setTitle(R.string.dialog_choose_city)
						.setItems(csAddress, dialogOnClickListener);
				

				// 3. Get the AlertDialog from create()
				AlertDialog dialog_address = builder2.create();

				dialog_address.show();


			}
		}

	}
	
	private OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	createUdid(which);
        }
	};
	
	private void createUdid(int which_address){
		Location loca = new Location("user");
		loca.setLatitude(listAdd.get(addressSelected).getLatitude());
		loca.setLongitude(listAdd.get(addressSelected).getLongitude());


		Calendar calendar = Calendar.getInstance();
		calendar.set(dp_birth_date.getYear(), dp_birth_date.getMonth(), dp_birth_date.getDayOfMonth());
		try {
			Udid2 udid2 = new Udid2(last_name, first_name, calendar, loca, 0);
			String udid2_clear = udid2.getUdid2_clear();
			tv_udid.setText(udid2_clear);
			String udid2_hashed = udid2.getUdid2_hashed();
			tv_udid_2.setText(udid2_hashed);
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        SharedPreferences.Editor editor=prefs.edit();
	        editor.putString("udid2_c", udid2_clear);
	        editor.putString("udid2_h", udid2_hashed);

	        editor.commit();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
