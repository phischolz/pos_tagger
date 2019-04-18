package com.rapidminer.extension.ioobjects;
import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.ResultObjectAdapter;

/**
 * Wraps Lists of Lists of {@link TagToken}. Whenever the inner List's newest added TagToken's token fulfills {@link isSeparator()}
 * a new Line (inner List) is created
 * 
 * @author Philipp Scholz, Uni Bayreuth
 *
 */
public class TagString extends ResultObjectAdapter {
	private static final long serialVersionUID = 1725159859797569345L;
	private List<List<TagToken>> content = new ArrayList<List<TagToken>>();
	private TagsetType type = TagsetType.UNDEFINED;
	private int unregistered_elements = 0;
	private int max_nbest = 1;
	
	/**
	 * Configure the Tagstring by Amount of Tags per Token and Type
	 * @param max_nbest: 1 for first-best, x>1 for x-best tags format
	 * @param type: Corresponding Tagset to tags
	 */
	public TagString(int max_nbest, TagsetType type) {
		this.max_nbest = max_nbest;
		this.type = type;
	}
	
	
	
	/**
	 * Adds an entire row at the end of the List
	 */
	public void appendRow(List<TagToken> row) {
		content.add(row);
		for (TagToken tag: row){
			if (Tagset.isPOS(type, tag.getFirstTag(), false) == false) unregistered_elements++;
		}
	}
	
	/**
	 * Adds non-Array Tags
	 */
	
	public void addTag(String token, String singleTag){
		String[] s = {singleTag};
		addTag(token, s);
	}
	
	/**
	* Adds a tag at end of the Structure. Makes sure that newTag.length is cut/expanded to max_nbest
	*/
	public void addTag(String token, String[] newTag){
		String[] toAdd = new String[max_nbest];
		
		for(int i=0; i<java.lang.Math.min(max_nbest, newTag.length); i++){
			toAdd[i] = newTag[i];
		}
		if (max_nbest>newTag.length){
			for (int i= newTag.length; i<max_nbest; i++){
				toAdd[i] = "NONE";
			}
		}
		
		
		TagToken s = new TagToken(toAdd, token);
		addTag(s);
	}
	
	/**
	 * Utility to add Tags properly and break lines when Separator-Token is inside
	 * @param newTag
	 */
	private void addTag(TagToken newTag){
		if (content.size()==0)
			newLine();
		
		List<TagToken> row= content.get(content.size() - 1);
		
		
		
		row.add(newTag);
		if (isSeparator(newTag.getToken())) newLine();	
		if (Tagset.isPOS(type, newTag.getFirstTag(), false) == false) unregistered_elements++;
		
	}
	
	/**
	 * 
	 * @return amount of Separated Sentences
	 */
	public int getRowCount(){
		return content.size();
	}
	
	/**
	 * 
	 * @param rowNum specify row's index
	 * @return size of specified sentence (in Tokens)
	 */
	public int getRowSize(int rowNum){
		if (content.size()>rowNum && rowNum>0) return content.get(rowNum).size();
		return 0;
	}
	
	
	
	/**
	 * 
	 * @return last TagToken in the entire structure
	 */
	public TagToken getLast(){
		
		if (content.size() != 0){
			List<TagToken> grab = content.get(content.size()-1);
			if (grab.size() != 0){
				return grab.get(grab.size()-1);	
			}
		}
		return dummy();
	}
	
	
	/**
	 * ends the current inner List and starts the next
	 */
	private void newLine(){
		content.add(new ArrayList<TagToken>());
	}
	
	/**
	 * 
	 * @return content without metadata
	 */
	public List<List<TagToken>> getContent() {
		return content;
	}
	
	/**
	 * all Lines are merged into one
	 */
	public void serialize() {
		List<List<TagToken>> oldContent = content;
		content = new ArrayList<List<TagToken>>();
		content.add(new ArrayList<TagToken>());
		
		for (List<TagToken> row: oldContent){
			content.get(0).addAll(row);
		}
	}   
	
	/**
	 * returns TagToken at specified indexes 
	 * @param rowIndex Index at outer List
	 * @param tokenIndex Index at inner List
	 * @return TagToken (may be an empty dummy if index out of bounds)
	 */
	public TagToken getTagToken (int rowIndex, int tokenIndex){
		
		if (rowIndex>0 && content.size()>rowIndex){
			if (tokenIndex>0 && content.get(rowIndex).size()>tokenIndex){
				return content.get(rowIndex).get(tokenIndex);
			}
		}
		
		return dummy();
	}
	
	
	
	/**
	 * 
	 * @return total nr of contained TagTokens
	 */
	public int countTokens(){
		int size = 0;
		for(List<TagToken> list: content){
			size += list.size();
		}
		return size;
	}
	
	/**
	 * 	
	 * @return configured N-best
	 */
	public int getNbest(){
		return this.max_nbest;
	}
	
	/**
	 * 
	 * @return configured TagsetType
	 */
	public TagsetType getType(){
		return type;
	}
	
	/**
	 * 
	 * @param s String to check
	 * @return whether s starts with any of [.:?!-]
	 */
	private boolean isSeparator(String s){
		switch (s.charAt(0)){
		case '.': return true;
		case ':': return true;
		case '?': return true;
		case '!': return true;
		case '-': return true;
		default: return false;
		}
	}
	
	/**
	 * 
	 * @return an empty TagToken to represent an OutOfBounds situation
	 */
	private TagToken dummy(){
		String[] dummy = new String[max_nbest];
		for (int i=0; i< dummy.length; i++){
			dummy[i] = "NONE";
		}
		return new TagToken(dummy, null);
	}
	
	/**
	 * toString as needed by RapidMiner IOObjects
	 */
	@Override
    public String toString() {
		String result= null;
		
        for (List<TagToken> row: content){
        	for (TagToken tag: row){
        		result += tag.getToken() + "\\" + tag.getFirstTag();
        		if (Tagset.isPOS(type, tag.getFirstTag(), false) == false) result +="*";
        		result += " ";
        	}
        	result += "\n";
        }
        result = "FAULTY/UNKNOWN TAGS: " + unregistered_elements + "/" + countTokens() + "\n" + 
        	result;
        		
        return result;
    }
}
