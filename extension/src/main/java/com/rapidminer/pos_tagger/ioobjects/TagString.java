package com.rapidminer.pos_tagger.ioobjects;
import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.ResultObjectAdapter;

public class TagString extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159859797569345L;
	private List<List<String>> content = new ArrayList<List<String>>();
	private TagsetType type = TagsetType.UNDEFINED;
	private int unregistered_elements = 0;
	
	public TagString() {
	}
	
	public void appendRow(List<String> row) {
		content.add(row);
	}
	
	public void addTag(String newTag){
		if (content.size()==0)
			newLine();
		List<String> row= content.get(content.size() - 1);
		row.add(newTag);
		if (Tagset.isSeparator(type, newTag)) newLine();		
	}
	
	
	
	public void newLine(){
		content.add(new ArrayList<String>());
	}
	
	public List<List<String>> getContent() {
		return content;
	}
	
	public int countRows(){
		return content.size();
	}
	
	public int countTags(){
		int size = 0;
		for(List<String> list: content){
			size += list.size();
		}
		return size;
	}
	
	public void setType(TagsetType type){
		this.type = type;
	}
	
	public TagsetType getType(){
		return type;
	}
	
	@Override
    public String toString() {
		String result= null;
		
        for (List<String> row: content){
        	for (String tag: row){
        		result += tag + " ";
        	}
        	result += "\n";
        }
        result = "FAULTY/UNKNOWN TAGS: " + unregistered_elements + "/" + (unregistered_elements/countTags()) + "\n" + 
        	result;
        		
        return result;
    }
}
