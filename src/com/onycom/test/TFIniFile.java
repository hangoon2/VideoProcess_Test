package com.onycom.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFIniFile {
	
	private Pattern m_section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	private Pattern m_keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
	private Map<String, Map<String, String>> m_entries = new HashMap<>();
	
	private static TFIniFile instance = null;
	
	private TFIniFile() {
		load("Setup.ini");
	}
	
	public static TFIniFile getInstance() {
		if(instance == null) {
			instance = new TFIniFile();
		}
		
		return instance;
	}
	
	private boolean load(String path) {
		try {
			BufferedReader br = new BufferedReader( new FileReader(path) );
			String line = null;
			String section = null;
			while( (line = br.readLine()) != null ) {
				Matcher m = m_section.matcher(line);
				if( m.matches() ) {
					section = m.group(1).trim();
				} else if( section != null ) {
					m = m_keyValue.matcher(line);
					if( m.matches() ) {
						String key = m.group(1).trim();
						String value = m.group(2).trim();
						
						Map<String, String> kv = m_entries.get(section);
						if(kv == null) {
							m_entries.put( section, kv = new HashMap<>() );
						}
						
						kv.put(key, value);
					}
				}
			}
			
			br.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public String getString(String section, String key, String defaultValue) {
		Map<String, String> kv = m_entries.get(section);
		if(kv == null || kv.get(key) == null)
			return defaultValue;
		
		return kv.get(key);
	}
	
	public int getInt(String section, String key, int defaultValue) {
		Map<String, String> kv = m_entries.get(section);
		if(kv == null || kv.get(key) == null)
			return defaultValue;
		
		return Integer.parseInt( kv.get(key) );
	}
	
	public float getFloat(String section, String key, float defaultValue) {
		Map<String, String> kv = m_entries.get(section);
		if(kv == null || kv.get(key) == null)
			return defaultValue;
		
		return Float.parseFloat( kv.get(key) );
	}
	
	public double getDouble(String section, String key, double defaultValue) {
		Map<String, String> kv = m_entries.get(section);
		if(kv == null || kv.get(key) == null)
			return defaultValue;
		
		return Double.parseDouble( kv.get(key) );
	}

}
