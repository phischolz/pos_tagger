package com.rapidminer.pos_tagger.ioobjects;

import java.util.ArrayList;
import java.util.List;

public class ResultRow {
	private List<String> content = new ArrayList<>();
	
	public ResultRow(){
		//nuthin'
	}
	
	public List<String> get(){
		return content;
	}
	public String read(){
		String result= null;
		for (String x: content){
			result += x + "    ";
		}
		return result;
	}
	public void set(List<String> newcontent){
		content = newcontent;
	}
	public void append(List<String> appendix){
		for (String x: appendix){
			content.add(x);
		}
	}
	public void append(String appendix){
		content.add(appendix);
	}
}
