package com.rapidminer.extension.ioobjects;

import java.util.ArrayList;
import java.util.List;

public interface Tagset {
	//####################################
	//Demanded methods:
	//####################################
	
	//maps Strings to corresponding Tag (recommended: use equalsIgnoreCase() for the token comparison)
	public Tagset stringToTagset(String s, boolean strict);
	public String key();	
	
	//returns whether the Tag is a POS tag
	public boolean isPOS();
	
	
	
	
	
	//####################################
	// Methods to operate on generic Tagset:
	//####################################
	
	
	public static String[] values(TagsetType t){
		String[] failure = {};
		switch (t){
		case PENN_TREEBANK:
			return values(PennTag.class);
		case UNDEFINED: return failure;
		default: return failure;
		
		}		
	}
		
	
	
	public static boolean isPOS(TagsetType t, String s, boolean strict){
		switch(t){
		case PENN_TREEBANK:
			return isPOS(PennTag.class, s, strict);
		case UNDEFINED: 
			return false;
		default: return false;
		}
	}
	
	
	public static <T extends Enum<T> & Tagset> boolean isPOS(Class<T> clazz, String s, boolean strict){
		 //TODO
		for (Tagset t: clazz.getEnumConstants()){
			if (t==t.stringToTagset(s, strict) && t.isPOS()) return true;
		}
		 return false;
	 }
	
	public static <T extends Enum<T> & Tagset> String[] values(Class<T> clazz){
		List<String> values = new ArrayList<String>();
		for (Tagset t:clazz.getEnumConstants()){
			if (t.isPOS()) values.add(t.key());
		}
		
		String[] val = new String[values.size()];
		for (int i=0; i<values.size(); i++){
			val[i] = values.get(i);
		}
		
		return val;
		
	}
	
}
