package com.rapidminer.pos_tagger.ioobjects;

public enum PennTag implements Tagset {
	S("S", false),
	NP("NP", false), PP("PP", false), VP("VP", false), ADVP("ADVP", false), ADJP("ADJP", false),
	SBAR("SBAR", false), PRT("PRT", false), INTJ("INTJ", false), PNP("PNP", false), //Chunck Tags
	CC("CC", false), CD("CD", false), DT("DT", false), EX("EX", false), FW("FW", false), IN("IN", false),
	JJ("JJ", false), JJR("JJR", false), JJS("JJS", false), LS("LS", false), 
	MD("MD", false), NN("NN", false), NNS("NNS", false), NNP("NNP", false), NNPS("NNPS", false), PDT("PDT", false),
	POS("POS", false), PRP("PRP", false), PRP$("PRP$", false), RB("RB", false), 
	RBR("RBR", false), RBS("RBS", false), RP("RP", false), SYM("SYM", false), TO("TO", false), UH("UH", false),
	VB("VB", false), VBZ("VBZ", false), VBP("VBP", false), VBD("VBD", false), 
	VBN("VBN", false), VBG("VBG", false), WDT("WDT", false), DP("DP", false), DP$("DP$", false), WRB("WRB", false), //POS Tags
	A1("A1", false), P1("P1", false), //Anchor Tags
	Stop(".", true), DoubleStop("..", true), TripleStop("...", true)  , Comma(",", false), Semicolon(";", true),
	Colon(":", true), Dollar("$", true), OpeningMark("``", true), QuestionMark("?", true), ExclMark("!", true),
	ClosingMark("''", true), OpeningBracket("(", true), ClosingBracket(")", true),  //Satzzeichen
	None("", false)
	;
	private final String text;
	private final boolean lineseparator;
	
	PennTag(String text, boolean lineseparator) {
		this.text = text;
		this.lineseparator=lineseparator;
		
}
	@Override
	public Tagset stringToTagset(String s) {
		
		return findTag(s);
}
	@Override
	public boolean isSeparator(){
		
		return lineseparator;
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
}
