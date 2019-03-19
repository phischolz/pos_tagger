package com.rapidminer.pos_tagger.ioobjects;

import java.util.ArrayList;
import java.util.List;

/*
 * This Class resembles a Database of Tagsets, where every Tagset
 */

public class TagsetCollection {
	private List<Tagset> collection ;
	
	public TagsetCollection(){
		collection = new ArrayList<Tagset>();
	}
	
	/*	acts as a factory for Tagset, so that they can be used for evaluations.
	 * 
	 * 	to add a Tagset, add it in @TagsetType, add a case for it here, where a new @Treebank must be created
	 * 	with the parameters ([Enum Name],[Array of all Labels],[Array of Labels which you want to split Rows in @resultobj])
	 */
	public void register(TagsetType type){
		if (containsTagset(type)) return;
		
		switch(type){
			case UNDEFINED:
				String[] lb_undef = {"."};
				add(new Tagset(TagsetType.UNDEFINED, null, lb_undef));
					break;
			case PENN_TREEBANK:
				//all Tags:
				String[] arr= {"CC","CD","DT","EX","FW","IN",
						"JJ","JJR","JJS","LS","MD","NN","NNS","NNP","NNPS",
						"PDT","POS","PRP","PRP$","RB","RBR","RBS",
						"RP","SYM","TO","UH","VB","VBD","VBG",
						"VBN","VBP","VBZ","WDT",
						"WP","WP$","WRB","#","(",")",",",":","."};
				//Line-Breaking Tags:
				String[] lb = {".", ":", "SYM"};
				add(new Tagset(TagsetType.PENN_TREEBANK, arr, lb));
					break;
					
			default:
				String[] lb_def = {"."};
				add(new Tagset(TagsetType.UNDEFINED, null, lb_def));
					break;
		}
	}
	
	private void add(Tagset t){
		collection.add(t);
	}
	
	public boolean containsTagset(TagsetType type){
		if (collection.isEmpty() == false){
			for (Tagset t: collection){
				if (t.getType()==type) return true;
			}
		}
		return false;
	}
	
	public List<Tagset> getCollection(){
		return collection;
	}
	
	public int getCollectionSize(){
		return collection.size();
	}
	
	//Use this to work with the Sets once created
	public Tagset getElement(TagsetType type){
		if (collection.isEmpty() == false){
			for (Tagset t: collection){
				if (t.getType()==type) return t;
			}
		}
		return null;
	}
}
