package com.rapidminer.extension.ioobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines all Enums representing Tagset, also maps them to their respective TagsetType.
 * Lets you call Tagsets with only their Type and the Parameters
 * 
 * @author Philipp Scholz, Uni Bayreuth
 *
 */
public interface Tagset {
	
	
	/**
	 * maps Strings to corresponding Tag (recommended: use equalsIgnoreCase() for the token comparison)
	 * @param s: String to find
	 * @param strict: false -> ignore case
	 * @return Tag's Enum Value
	 */
	public Tagset stringToTagset(String s, boolean strict);
	
	/**
	 * 
	 * @return actual Tag (Some constants are not identical to their Tag, like "OpeningPBracket" for "(" )
	 */
	public String key();	
	
	/**
	 * returns whether the Tag is a POS tag
	 * @return
	 */
	public boolean isPOS();
	
	
	
	
	
	//####################################
	// Methods to operate on generic Tagset:
	//####################################
	
	
	/**
	 * maps TagsetType to Enum and calls the other values() method
	 * @param t
	 * @return
	 */
	public static String[] values(TagsetType t){
		String[] failure = {};
		switch (t){
		case PENN_TREEBANK:
			return values(PennTag.class);
		case UNDEFINED: return failure;
		default: return failure;
		
		}		
	}
		
	
	/**
	 * maps TagsetType to Enum and calls the respective Enum
	 * @param t tagsetType
	 * @param s tag
	 * @return true, if the Enum has a Tag with the key s
	 */
	public static boolean isPOS(TagsetType t, String s, boolean strict){
		switch(t){
		case PENN_TREEBANK:
			return isPOS(PennTag.class, s, strict);
		case UNDEFINED: 
			return false;
		default: return false;
		}
	}
	
	
	/**
	 * 
	 * @param clazz Tagset-Enum
	 * @param s Tag
	 * @param strict false -> ignore Case
	 * @return true if the tag s in clazz is POS
	 */
	public static <T extends Enum<T> & Tagset> boolean isPOS(Class<T> clazz, String s, boolean strict){
		 //TODO
		for (Tagset t: clazz.getEnumConstants()){
			if (t==t.stringToTagset(s, strict) && t.isPOS()) return true;
		}
		 return false;
	 }
	
	/**
	 * 
	 * @param clazz
	 * @return all keys (=tags) of Tagset Enum clazz
	 */
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
