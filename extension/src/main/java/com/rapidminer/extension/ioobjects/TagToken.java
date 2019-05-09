package com.rapidminer.extension.ioobjects;

/**
 * This Object safely wraps an Array of Strings (tags) and a token.
 * Tags and Tokens can only be added via constrructor
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
	
	/**
	 * 
	 * @return size of the contained Tag-Array
	 */
	public int getSize(){
		if (tags!=null){
			if (tags.length!=0) return tags.length;
		}
		return 0;
	}
	
	/**
	 * 
	 * @return  contained Token
	 */
	public String getToken(){
		if (token!=null){
			if (token!="") return token;
		}
		return "NONE";
	}
	
	/**
	 * 
	 * @return first tag which is therefore estimaded as most likely
	 */
	public String getFirstTag(){
		if (tags!=null){
			if (tags.length>0) return tags[0];
		}
		return "NONE";
	}
	
	/**
	 * 
	 * @return all Tags, whereas Array-Position 0 is the most likely tag
	 */
	public String[] getTags(){
		String[] none = {"NONE"};
		if (tags==null) return none;
		return tags;
	}
}
