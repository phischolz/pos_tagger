package com.rapidminer.extension.ioobjects;

/**
 * This Object safely wraps an Array of Strings (tags) and a token.
 * 
 * @author Philipp Scholz, Uni Bayreuth
 *
 */
public class TagToken {

	private String token;
	private String[] tags;
		
	public TagToken(String[] tags, String token){
		this.tags = tags;
		this.token = token;
		
	}
	
	public int getSize(){
		if (tags!=null){
			if (tags.length!=0) return tags.length;
		}
		return 0;
	}
	
	public String getToken(){
		if (token!=null){
			if (token!="") return token;
		}
		return "NONE";
	}
	
	public String getFirstTag(){
		if (tags!=null){
			if (tags.length>0) return tags[0];
		}
		return "NONE";
	}
	
	public String[] getTags(){
		String[] none = {"NONE"};
		if (tags==null) return none;
		return tags;
	}
}
