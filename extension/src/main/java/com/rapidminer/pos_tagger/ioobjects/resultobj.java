package com.rapidminer.pos_tagger.ioobjects;
import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.ResultObjectAdapter;

public class resultobj extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159859797569345L;
	private List<ResultRow> content = new ArrayList<>();
	private TagsetType type;
	private int unregistered_elements = 0;
	
	public resultobj(TagsetType type) {
		this.type = type;
	}
	
	public void appendRow(ResultRow row) {
		content.add(row);
	}
	
	public void addTag(String newTag){
		if (content.size()==0)
			newLine();
		ResultRow row= content.get(content.size() - 1);
		row.append(newTag);
		if (Tagset.isSeparator(type, newTag)) newLine();		
	}
	
	
	
	public void newLine(){
		content.add(new ResultRow());
	}
	
	public List<ResultRow> getContent() {
		return content;
	}
	
	public int countRows(){
		return content.size();
	}
	
	public TagsetType getType(){
		return type;
	}
	
	@Override
    public String toString() {
		String result= null;
        for (ResultRow row: content){
        	result += row.read() + System.getProperty("line.separator");
        }
        result = "FAULTY/UNKNOWN TAGS: " + unregistered_elements + System.getProperty("line.separator") + 
        		System.getProperty("line.separator") + result;
        		
        return result;
    }
}
