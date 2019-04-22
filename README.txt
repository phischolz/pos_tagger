Part-of-Speech-Tagging Extension, including Evaluation System.
Philipp Scholz, Uni Bayreuth 2019
#########################################
#
#	INSTALLATION
#
#########################################
1. Get RapidMiner Studio: https://rapidminer.com/products/studio/
	NOTE: Without a LARGE License, there might still be issues with this build.
	The Operators FastTag and Evaluator as well as the background Structures should work, though.
2. Install the "Text Processing" Extension from Rapidminer Marketplace (Extensions > Marketplace)

3. Locate your RapidMiner Folder (possibly Path/to/Program Files/RapidMiner)

4. Copy the File from the Project folder pos_tagger/external/text-8.1.0.jar into [RapidMiner]/RapidMiner Studio/lib. Overwrite/Replace if neccessary.
	NOTE: When starting RM, Text Processing will tell you it has an Error. Press ignore, nothing is broken AFAIK.

5. Open Powershell or any other CLI. 

6. navigate to pos_tagger/extension

7. execute "gradlew installExtension"
	(in Powersehell: ./gradlew installExtension)

8. Restart RM Studio

Done!
	

#########################################
#
#	USE
#
#########################################

READING:
	1. Use "Read Document" by Text Processing and specify the file.
TOKENIZATION:
	2. Send the File into the "Tokenize" Operator by Text Processing. Choose Parameter mode = linguistic tokens
TAGGING: 
	3. Send the Tokenized Document to any POS-Tagger
EVALUATING:
	// Files to test Evaluation: pos_tagger/external/textfiles. en-tok for tokenized files (read in for the tagger),
	// en-mrg for Treebank (read in for Gold-Input of the Evaluator).
	4. Send Tagged Object (preferably TagString) to Res-Input of the Evaluator
	5. Read in your Gold Standard and send it to the Gol-Input of the Evaluator.
	6. Configure the Evaluator (If you use the TagString-Tagger-Output and the test-files:
		 Gold-Tagset = PENN, Gold-Format = PARENTHESIS (Tag word), ignore Brackets. )

- LingPipe-Tagger: Only use the GENIA Model if you haven't implemented other Tagsets. the other ones do not follow Penn Treebank standard.
- NLP4J-Tagger: leave Parameters as is if you don't have different configs lying around

#########################################
#
#	STRUCTURE
#
#########################################

To make sense of this Structure, i recommend loading the folder extension into an IDE as existing Gradle project.
	If you use Eclipse, install Gradle IDE pack first.

src/main/java:
	com.rapidminer.extension
		Plugin Initializer (not used)
	com.rapidminer.extension.ioobjects
		TagString and TagToken Objects
		Tagset Interface and implementing PennTag enum
		TagsetType enum
	com.rapidminer.extension.operator
		Tagging-Operators (including Stanford Tagger, which doesn't work due to Permission errors)
		Evaluator
	fasttag.src.com.knowledgebooks.nlp.*
		Implementation of FastTag Tagger
src/main/resources:
	com.rapidminer.extension.resources.*
		config Files for Operators etc.
	fasttag:
		lexica and some github artifacts
	lingpipe:
		Hidden Markov Models (pre-trained)
	penn:
		Stanford Tagger models
	META-INF: 
		Extension Icon
lib
	contains some working files and jars i used, might be useful for later changes
build.gradle
	all import information etc.

#########################################
#
#	ADDING COMPONENTS
#
#########################################

ADDING TAGSETS:
	1. create enum implementing tagset, see Penntag for reference.
	2. add a Constant representing your tagset in TagsetType
	3. map the constant  to the enum class in the static methods of Tagset (expand the switch/case statements)

ADDING TAGGERS:
	1. to read in the tokenized Text created by "tokenize", create a documentinput and use this:
		String in = (Document)documentInput.getData(IOObject.class).getTokenText();
		String[] tokenized = in.split("\\s+")
	2. make sure that -if possible- the tagger accepts Lists/arrays instead of strings.

	Output (Tagstring):
	Create a new TagString([how many tags are output per token, 1 per default],[TagsetType constant corresponding to your tagset])
	use TagString#add(token, tag[s]) to add Tags one by one (in the same order as the Tokens are ordered, of corse)