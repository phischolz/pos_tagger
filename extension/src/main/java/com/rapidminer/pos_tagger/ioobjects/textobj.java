package com.rapidminer.pos_tagger.ioobjects;
import com.rapidminer.operator.ResultObjectAdapter;

public class textobj extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159059797569345L;
	private String content;
	
	public textobj(String st) {
		content = st;
	}
	
	public void append(String st) {
		content = content + " " + st;
	}
	
	public String getContent() {
		return toString();
	}
	
	@Override
    public String toString() {
        return content;
    }
}
