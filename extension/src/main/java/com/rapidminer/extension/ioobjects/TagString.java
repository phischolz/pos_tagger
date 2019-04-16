package com.rapidminer.extension.ioobjects;
import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.ResultObjectAdapter;

public class TagString extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159859797569345L;
	private List<List<String[][]>> content = new ArrayList<List<String[][]>>();
	private TagsetType type = TagsetType.UNDEFINED;
	private int unregistered_elements = 0;
	private int max_nbest = 1;
	
	public TagString() {}
	
	public void appendRow(List<String[][]> row) {
		content.add(row);
		for (String[][] tag: row){
			if (Tagset.isPOS(type, tag[0][0], false) == false) unregistered_elements++;
		}
	}
	
	public void addTag(String token, String newTag){
		String[][] s = new String[1][2];
		s[0][0] = newTag;
		s[0][1] = token;
		addTag(s);
	}
	
	public void addTag(String[][] newTag){
		if (content.size()==0)
			newLine();
		
		List<String[][]> row= content.get(content.size() - 1);
		
		
		
		row.add(newTag);
		if (isSeparator(newTag[0][1])) newLine();	
		if (Tagset.isPOS(type, newTag[0][0], false) == false) unregistered_elements++;
		
	}
	
	public String[][] getLast1(){
		String[][] s = new String[1][2];
		if (content.size() != 0){
			List<String[][]> grab = content.get(content.size()-1);
			if (grab.size() != 0){
				if (grab.get(grab.size()-1).length != 0)
					s[0][0] = grab.get(grab.size()-1)[0][0];
					s[0][1] = grab.get(grab.size()-1)[0][1];
					return s;
			}
			return null;
		
		}
		return null;
	}
	
	public String[][] getLast(){
		if (content.size() != 0){
			List<String[][]> grab = content.get(content.size()-1);
			if (grab.size() != 0){
				return grab.get(grab.size()-1);
			}
			return null;
		}
		return null;
	}
	
	public void newLine(){
		content.add(new ArrayList<String[][]>());
	}
	
	public List<List<String[][]>> getContent() {
		return content;
	}
	
	public List<List<String[][]>> getAsSingularRow() {
		List<List<String[][]>> newContent = new ArrayList<List<String[][]>>();
		List<String[][]> newLine = new ArrayList<String[][]>();
		for (List<String[][]> row : content){
			newLine.addAll(row);
		}
		newContent.add(newLine);
		return newContent;
	}   
	
	public int countRows(){
		return content.size();
	}
	
	public int countTokens(){
		int size = 0;
		for(List<String[][]> list: content){
			size += list.size();
		}
		return size;
	}
	
	public void setNbest(int n){
		this.max_nbest = n;
	}
	
	public int getNbest(){
		return this.max_nbest;
	}
	
	public void setType(TagsetType type){
		this.type = type;
	}
	
	public TagsetType getType(){
		return type;
	}
	
	public boolean isSeparator(String s){
		switch (s.charAt(0)){
		case '.': return true;
		case ':': return true;
		case '?': return true;
		case '!': return true;
		case '-': return true;
		default: return false;
		}
	}
	
	@Override
    public String toString() {
		String result= null;
		
        for (List<String[][]> row: content){
        	for (String[][] tag: row){
        		if (tag != null){
        			if (tag.length != 0)
        				result += tag[0][1] + "\\" + tag[0][0];
        				if (Tagset.isPOS(type, tag[0][0], false) == false) result +="*";
        				result += " ";
        		}
        	}
        	result += "\n";
        }
        result = "FAULTY/UNKNOWN TAGS: " + unregistered_elements + "/" + countTokens() + "\n" + 
        	result;
        		
        return result;
    }
}
