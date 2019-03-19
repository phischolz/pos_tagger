package com.rapidminer.pos_tagger.ioobjects;

import java.util.ArrayList;
import java.util.List;

public class Tagset {
	private TagsetType type = TagsetType.UNDEFINED;
	private String[] tokens = null;
	private List<String> lineBreaker = new ArrayList<String>();
	
	public Tagset(TagsetType type, String[] tokens, String[] linebreakers){
		this.type=type;
		this.tokens=tokens;
		List<String> LBs = new ArrayList<String>();
		for (String token: linebreakers){
			if (hasToken(token)) lineBreaker.add(token);
		}
		
	}
	
	public String[] getTokenList(){
		return tokens;
	}
	public boolean hasToken(String a){
		if (tokens != null){
			for (int i = 0 ; i< tokens.length; i++){
				if (tokens[i].equalsIgnoreCase(a)) return true;
			}
		}
		return false;
	}
	public TagsetType getType(){
		return type;
	}
	public boolean isLineBreaker(String a){
		if (tokens != null){
			for (String s: lineBreaker){
				if (s.equalsIgnoreCase(a)) return true;
			}
		}
		return false;
	}
}
