package org.openudc.test.lib;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.location.Geocoder;
import android.location.Location;

/**
 * 4.1.2. Universal Digital IDentity string version 2.

    The "udid2;" header MUST be followed by a one-char field which
    define if the birth datas are in clear or hashed :
    - 'c' char means the data are not hashed.
    - 'h' char means the data are hashed.

    The hashed version is obtained by applying the SHA1 algorithm on
    the clear version.

    Because now we can hash the udid, we have no more place for
    fuzziness in it, and so the rules of the clear version are very
    strict :

    The clear version "udid2;c;" MUST be followed by 5 fields:

    1- The last last name as written on the birth certificate.
       Only the 26 upper case [A-Z] US-ASCII characters are allowed.
       That means it have to be transposed in a standardized way to
       this US-ASCII characters.

       The transposed name MUST contain at least 1 character and
       MUST not exceed 20 characters. If it reach the 20-chars
       limit, extra characters MUST be ignored.

    2- The first first name as written on the birth certificate.
       Only the 27 upper case [A-Z-] US-ASCII characters are allowed.
       That means it have to be transposed in a standardized way to
       this US-ASCII characters.

       The transposed name MUST contain at least 1 character and
       MUST not exceed 20 characters. If it reach the 20-chars
       limit, extra characters MUST be ignored.

    3- The date of birth as written on the birth certificate, but
       transposed, as specified by the ISO-8601, in the extended
       form: "YYYY-mm-dd".

    4- The location of birth. As long as individuals are born on
       earth, it should be prefixed by a 'e' (lower case) character.
       Then following is defined as terrestrial coordinates.

       The first number MUST be the latitude and MUST be a 4 digits
       number : 2 before the decimal point and 2 after, prefixed by
       '+' if in the Northern hemispheres, and prefixed by '-' if in
       the Southern hemispheres.
       The second number MUST be the longitude and MUST be a 5 digits
       number : 3 before the decimal point and 2 after, prefixed by
       by '+' in the east from the Greenwich meridian, and by '-' in
       the west from the Greenwich meridian.

       Accepted range for latitude is [-90.00;+90.00].
       Accepted range for longitude is [-179.99;+180.00].

       For a given place of birth, only one coordinate is allowed.
       It should correspond to the coordinate of the office which
       write the birth certificate. And so in general to the
       coordinate that indicate almost all map tools (like
       googlemap).

    5- A number wich may increase in the case of more than one
       children born with the same last name and first name at the
       same place at the same date.
       This number should so be equal to 0 for almost every one.

    The hashed version "udid2;h;" MUST be followed by 2 fields:

    1- The SHA1 of the string composed by the fields "1;2;3;4"
       of the clear version (with the three ';' delimiters).

    2- A number wich may increase in the case of more than one
       children born with the same last name and first name at the
       same place at the same date, or if SHA1 collision.
       (2^63 operations are needed to meet an SHA1 collision, and
       there is today less than 2^33 humans on earth...)
       This number should so be equal to 0 for almost every one.

  example: if a individual is registered as
         - first names: François-Xavier-Robert,Lucien
         - last name: DE CLÉREL-DE-TOCQUEVILLE
         - birth date: 14 July 1989
         - birthplace: Paris 15

    its clear udid2 will be :

   udid2;c;TOCQUEVILLE;FRANCOIS-XAVIER-ROBE;1989-07-14;e+48.84+002.30;0;

    and its hashed udid2 will be :

   udid2;h;85bbb64915ae7f2cdbe54618453b3ed107f1f242;0;

   Note1: the SHA1 can be calculated with the sha1sum tool on almost
    all recent Unix systems.

   Note2: to transpose some europeans characters to US-ASCII charset,
     uni2ascii tool may help.
     (http://billposer.org/Software/uni2ascii.html)
 * @author matthieu
 *
 */
public class Udid2 {
	
	private String version = "udid2";
	private byte[] last_name = new byte[20];
	private byte[] first_name = new byte[20];
	private byte[] birth_date = new byte[20];
	private byte[] pre_location = "e".getBytes();
	private byte[] latitude = new byte[6];
	private byte[] longitude = new byte[7];
	private String hashed;
	private int number = 0;
	
	public Udid2(String last_name, String first_name, Calendar birth_date,
			Location birth_location, int number) throws Exception {
		super();
		this.setLast_name(last_name);
		this.setFirst_name(first_name);
		this.setBirth_date(birth_date);
		this.setBirth_location(birth_location);
		this.number = number;
		this.generate_hashed();
	}
	
	/**
	 * Used to created this object from a clear udid2
	 * @param last_name
	 * @param first_name
	 * @param birth_date
	 * @param birth_location
	 * @param number
	 * @throws Exception
	 */
	public Udid2(String last_name, String first_name, String birth_date,
			String birth_location, int number) throws Exception {
		super();
		this.last_name = last_name.getBytes("US-ASCII");
		this.first_name = first_name.getBytes("US-ASCII");
		this.birth_date = birth_date.getBytes("US-ASCII");
		this.setBirth_location(birth_location);
		this.number = number;
		this.generate_hashed();
	}
	
	/**
	 * 
	 * @param udid2
	 * @throws Exception 
	 */
	public Udid2(String udid2) throws Exception{
		String last_name_str = udid2.substring(8);
		String first_name_str = last_name_str.substring(last_name_str.indexOf(";") + 1);
		String birth_date_str = first_name_str.substring(first_name_str.indexOf(";") + 1);
		String birth_location_str = birth_date_str.substring(birth_date_str.indexOf(";") + 1);
		String number_str = birth_location_str.substring(birth_location_str.indexOf(";") + 1);
		
		this.last_name = last_name_str.substring(0, last_name_str.indexOf(";") ).getBytes("US-ASCII");
		this.first_name	= first_name_str.substring(0, first_name_str.indexOf(";") ).getBytes("US-ASCII");
		this.birth_date = birth_date_str.substring(0,birth_date_str.indexOf(";") ).getBytes("US-ASCII");
		this.setBirth_location(birth_location_str.substring(0,birth_location_str.indexOf(";")));
		this.number = Integer.valueOf(number_str.substring(0, 1));
		this.generate_hashed();
		
	}
	
	private String get_clear_hash() throws UnsupportedEncodingException{
		return getFirst_name() + ";" + getLast_name() + ";" + new String(getBirth_date_udid2(),"US-ASCII") + ";" + new String(getBirth_location_udid2(),"US-ASCII");
	}
	
	private void generate_hashed() throws Exception {
		
		this.hashed = Util.get_sha1(get_clear_hash());

	}

	public String getLast_name() throws UnsupportedEncodingException {
		return new String(last_name, "US-ASCII");
	}
	
	public byte[] getLast_name_udid2() {
		return last_name;
	}
	
	/**
	 * @param last_name
	 * @throws Exception 
	 */
	public void setLast_name(String last_name) throws Exception {
		if(last_name.length() < 1){
			throw new Exception("name too short");
		}else{
			if(last_name.length() > 20){
				last_name = last_name.substring(0, 19);
			}
			last_name = last_name.toUpperCase();
			
			this.last_name = last_name.getBytes("US-ASCII");
						
		}
	}
	
	private void setLast_name(byte[] last_name){
		this.last_name = last_name;
	}
	
	public String getFirst_name() throws UnsupportedEncodingException {
		return new String(first_name, "US-ASCII");
	}
	
	public byte[] getFirst_name_udid2() {
		return first_name;
	}
	
	/**
	 * @param first_name
	 * @throws Exception 
	 */
	private void setFirst_name(String first_name) throws Exception {
		if(first_name.length() < 1){
			throw new Exception("name too short");
		}else{
			if(first_name.length() > 20){
				first_name = first_name.substring(0, 19);
			}
			first_name = first_name.toUpperCase();
			
			this.first_name = first_name.getBytes("US-ASCII");
						
		}
	}
	
	private void setFirst_name(byte[] first_name){
		this.first_name = first_name;
	}
	
	/**
	 * 
	 * @return date, objet Calendar
	 */
	public Calendar getBirth_date() {
		String yyyy = new String(birth_date,0,4); 
		String mm = new String(birth_date,5,2); 
		String dd = new String(birth_date,8,2); 
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(yyyy), Integer.valueOf(mm), Integer.valueOf(dd));
		
		return calendar;
	}
	
	
	/**
	 * 
	 * @return birth_date, format udid2
	 */
	public byte[] getBirth_date_udid2(){
		return birth_date;
	}
	
	
	/**
	 * @param birth_date
	 * @throws UnsupportedEncodingException 
	 */
	private void setBirth_date(Calendar birth_date) throws UnsupportedEncodingException {

		this.birth_date = (birth_date.get(Calendar.YEAR) + "-" + birth_date.get(Calendar.MONTH) + "-" + birth_date.get(Calendar.DAY_OF_MONTH)).getBytes("US-ASCII");
	}
	
	private void setBirth_date(byte[] birth_date){
		this.birth_date = birth_date;
	}
	
	
	public Location getBirth_location() {
		Double db_latitude = Double.valueOf(String.valueOf(latitude));
		Double db_longitude = Double.valueOf(String.valueOf(longitude));

		Location loca = new Location("udid2");
		loca.setLatitude(db_latitude);
		loca.setLongitude(db_longitude);
		
		return loca;
	}
	
	public byte[] getBirth_location_udid2() {
		byte[] result = new byte[pre_location.length + latitude.length + longitude.length];
		
		System.arraycopy(pre_location, 0, result, 0, pre_location.length);
		System.arraycopy(latitude, 0, result, pre_location.length, latitude.length);
		System.arraycopy(longitude, 0, result, latitude.length + pre_location.length, longitude.length);

		return result;
	}
	
	/**
	 * @param birth_location
	 */
	private void setBirth_location(Location birth_location) {
		Double latitude = birth_location.getLatitude();
		Double longitude = birth_location.getLongitude();
		
		String sign_latitude = "+";
		String sign_longitude = "+";
		
		if(latitude < 0){
			sign_latitude = "-";
		}
		
		if(longitude < 0){
			sign_longitude = "-";
		}
		
		latitude = Math.abs(latitude);
		longitude =  Math.abs(longitude);
		DecimalFormat df_latitude = new DecimalFormat("00.00");
		DecimalFormat df_longitude = new DecimalFormat("000.00");

		String latitude_string = sign_latitude + df_latitude.format(latitude);
		String longitude_string = sign_longitude + df_longitude.format(longitude);
		
		this.latitude = latitude_string.getBytes();
		this.longitude = longitude_string.getBytes();
	}
	
	private void setBirth_location(String birth_location) throws UnsupportedEncodingException{
		this.latitude = birth_location.substring(1, 7).getBytes("US-ASCII");
		this.longitude = birth_location.substring(7, 14).getBytes("US-ASCII");

	}
	
	
	public int getNumber() {
		return number;
	}
	
	/**
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getUdid2_clear() throws UnsupportedEncodingException{
		return version + ";c;" + getFirst_name() + ";" + getLast_name() + ";" + new String(getBirth_date_udid2(),"US-ASCII") + ";" + new String(getBirth_location_udid2(),"US-ASCII") + ";" + getNumber() + ";";
	}
	
	public String getUdid2_hashed() throws UnsupportedEncodingException{
		return version + ";h;" + hashed + ";" + getNumber() + ";";
	}
}
