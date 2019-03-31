package com.rapidminer.pos_tagger.ioobjects;

public interface Tagset {
	public Tagset stringToTagset(String s);
	public boolean isSeparator();
	
}
