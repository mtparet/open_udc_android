package org.openudc.test.lib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	
	public static String get_sha1(String clear) throws NoSuchAlgorithmException{	    
	    MessageDigest md = MessageDigest.getInstance("SHA1");
	 
	    byte[] mdbytes = md.digest(clear.getBytes());
	 
	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < mdbytes.length; i++) {
	    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    return sb.toString();
	}

}
