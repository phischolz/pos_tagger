package com.rapidminer.pos_tagger.ioobjects;
import com.rapidminer.operator.ResultObjectAdapter;

public class resultobj extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159859797569345L;
	private String[] content;
	private String taggerType;
	
	public resultobj(String[] st, String type) {
		content = st;
		taggerType = type;
	}
	
	public void append(String[] st, String type) {
		if (taggerType == type){String [] newcontent = new String[content.length + st.length];
		int clen = content.length;
		int stlen = st.length;
		for (int i = 0; i < clen; i++){
			newcontent[i] = content[i];
		}
		for (int i = 0; i < stlen; i++){
			newcontent [clen + i] = st[i];
		}
		content = newcontent;}
	}
	
	public String[] getContent() {
		return content;
	}
	
	public String getType(){
		return taggerType;
	}
	
	@Override
    public String toString() {
        return "Type: " + taggerType + System.getProperty("line.separator") + content.toString();
    }
}
