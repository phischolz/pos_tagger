package com.rapidminer.pos_tagger.ioobjects;

public interface Tagset {
	//####################################
	//Demanded methods:
	//####################################
	
	//maps Strings to corresponding Tag (recommended: use equalsIgnoreCase() for the token comparison)
	public Tagset stringToTagset(String s);
	
	//returns true if the called Tag is supposed to divide lines
	public boolean isSeparator();
	
	
	
	
	//####################################
	// Methods to operate on generic Tagset:
	//####################################
	
		
	public static boolean isSeparator(TagsetType t, String s){
		switch (t){
		case PENN_TREEBANK: 
			if (isTag(PennTag.class, s)) return isSeparator(PennTag.class, s);
		case UNDEFINED:
			return false;
		default:
			return false;
		}
	}
	
	public static boolean isTag(TagsetType t, String s){
		switch(t){
		case PENN_TREEBANK:
			return isTag(PennTag.class, s);
		case UNDEFINED: 
			return false;
		default: return false;
		}
	}
	
	public static <T extends Enum<T> & Tagset> boolean  isSeparator(Class<T> clazz, String s){
		for (Tagset t: clazz.getEnumConstants()){
			if(t==t.stringToTagset(s) && t.isSeparator()) return true;
		}
		return false;
	 }
	
	public static <T extends Enum<T> & Tagset> boolean isTag(Class<T> clazz, String s){
		 //TODO
		for (Tagset t: clazz.getEnumConstants()){
			if (t==t.stringToTagset(s) && t.isSeparator()) return true;
		}
		 return false;
	 }
}
