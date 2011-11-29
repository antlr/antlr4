parser grammar HTMLParser;

options { tokenVocab=HTMLParser; }

file : ( TAG_START (starttag | endtag) | TEXT
{System.out.println("TEXT "+$TEXT);} )+ EOF ;

starttag : ID attr* TAG_STOP ;

attr : ID (EQ (ID|STRING))? ;

endtag
	:	 END_TAG {System.out.println("END tag "+$END_TAG);}
	;
