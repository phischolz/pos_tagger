package com.rapidminer.extension.ioobjects;

public enum PennTag implements Tagset {
	S("S", false, false),
	NP("NP", false, false), PP("PP", false, false), VP("VP", false, false), ADVP("ADVP", false, false), ADJP("ADJP", false, false),
	SBAR("SBAR", false, false), PRT("PRT", false, false), INTJ("INTJ", false, false), PNP("PNP", false, false), //Chunck Tags
	CC("CC", false, true), CD("CD", false, true), DT("DT", false, true), EX("EX", false, true), FW("FW", false, true), IN("IN", false, true),
	JJ("JJ", false, true), JJR("JJR", false, true), JJS("JJS", false, true), LS("LS", false, true), 
	MD("MD", false, true), NN("NN", false, true), NNS("NNS", false, true), NNP("NNP", false, true), NNPS("NNPS", false, true), PDT("PDT", false, true),
	POS("POS", false, true), PRP("PRP", false, true), PRP$("PRP$", false, true), RB("RB", false, true), 
	RBR("RBR", false, true), RBS("RBS", false, true), RP("RP", false, true),
	SYM("SYM", false, true), TO("TO", false, true), UH("UH", false, true),
	VB("VB", false, true), VBZ("VBZ", false, true), VBP("VBP", false, true), VBD("VBD", false, true), 
	VBN("VBN", false, true), VBG("VBG", false, true), WDT("WDT", false, true), DP("DP", false, true),
	DP$("DP$", false, true), WRB("WRB", false, true), //POS Tags
	A1("A1", false, false), P1("P1", false, false), //Anchor Tags
	Stop(".", true, true), DoubleStop("..", false, true), TripleStop("...", false, true)  , Comma(",", false, true), Semicolon(";", true, true),
	Colon(":", true, true), OpeningMark("``", false, true), QuestionMark("?", false, true), ExclMark("!", false, true),
	ClosingMark("''", false, true), OpeningBracket("(", false, true), ClosingBracket(")", false, true),  //Satzzeichen
	None("", false, false)
	;
	private final String text;
	private final boolean lineseparator;
	private final boolean isPOS;
	
	PennTag(String text, boolean lineseparator, boolean isPOS) {
		this.text = text;
		this.lineseparator=lineseparator;
		this.isPOS = isPOS;
		
	}
	@Override
	public Tagset stringToTagset(String s) {
		
		return findTag(s);
	}
	
	@Override
	public Tagset stringToTagsetStrict(String s) {
		
		return findTagStrict(s);
	}
	@Override
	public boolean isSeparator(){
		
		return lineseparator;
	}
	
	@Override
	public boolean isPOS(){
		return isPOS;
	}


	public static PennTag findTag(String s){
		PennTag[] pennTags = PennTag.values();
		for( PennTag tag: pennTags) {
			if(tag.text.equalsIgnoreCase(s)) {
				return tag;
			}
		}
		return None; 
	}
	
	public static PennTag findTagStrict(String s){
		PennTag[] pennTags = PennTag.values();
		for( PennTag tag: pennTags) {
			if(tag.text.equals(s)) {
				return tag;
			}
		}
		return None; 
	}
}
