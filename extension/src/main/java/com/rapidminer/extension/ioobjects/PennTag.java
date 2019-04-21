package com.rapidminer.extension.ioobjects;

/**
 * 
 * @author Philipp Scholz, Uni Bayreuth
 *
 */
public enum PennTag implements Tagset {
	S("S", false),
	NP("NP", false), PP("PP", false), VP("VP", false), ADVP("ADVP", false), ADJP("ADJP", false),
	SBAR("SBAR", false), PRT("PRT", false), INTJ("INTJ", false), PNP("PNP", false), //Chunck Tags
	
	CC("CC", true), CD("CD", true), DT("DT", true), EX("EX", true), FW("FW", true), GW("GW", true), HYPH("HYPH", true), IN("IN", true),
	JJ("JJ", true), JJR("JJR", true), JJS("JJS", true), LS("LS", true), 
	MD("MD", true), NFP("NFP", true), NN("NN", true), NNS("NNS", true), NNP("NNP", true), NNPS("NNPS", true), PDT("PDT", true),
	POS("POS", true), PRP("PRP", true), PRP$("PRP$", true), RB("RB", true), 
	RBR("RBR", true), RBS("RBS", true), RP("RP", true),
	SYM("SYM", true), TO("TO", true), UH("UH",  true),
	VB("VB", true), VBZ("VBZ", true), VBP("VBP", true), VBD("VBD", true), 
	VBN("VBN", true), VBG("VBG", true), WDT("WDT",true), DP("DP", true),
	DP$("DP$", true), WRB("WRB", true), WP("WP", true), WP$("WP$", true),//POS Tags
	
	A1("A1", false), P1("P1", false), //Anchor Tags
	
	Dollar("$", true), LRB("-LRB-", true), RRB("-RRB-", true), ADD("ADD", true), AFX("AFX", true), Stop(".", true) , Comma(",",  true), Semicolon(";", true),
	Colon(":", true), OpeningMark("``", true), QuestionMark("?", true), ExclMark("!", true),
	ClosingMark("''", true), OpeningBracket("(", true), ClosingBracket(")", true),  //Satzzeichen
	None("", false), NLP4JFALSE("XX", false)
	;
	
	
	private final String text;
	
	private final boolean isPOS;
	
	
	PennTag(String text, boolean isPOS) {
		this.text = text;
		
		this.isPOS = isPOS;
		
	}
	
	@Override
	public Tagset stringToTagset(String s, boolean strict) {
		
		return findTag(s, strict);
	}
	
	
	
	
	@Override
	public boolean isPOS(){
		return isPOS;
	}

	@Override
	public String key(){
		return text;
	}
	
	/**
	 * finds tags regardless whether they're POS
	 * @param s
	 * @param strict
	 * @return
	 */
	public static PennTag findTag(String s, boolean strict){
		PennTag[] pennTags = PennTag.values();
		for( PennTag tag: pennTags) {
			if((tag.text.equalsIgnoreCase(s) && strict==false) || tag.text.equals(s)) {
				return tag;
			}
		}
		return None; 
	}
	
	
	
	
}
