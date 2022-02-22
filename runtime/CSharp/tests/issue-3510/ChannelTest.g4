grammar ChannelTest;

prog : INT+ EOF;
INT : [0-9]+;
AAA : [a-z]+ -> channel(HIDDEN); // Note--lowercase "channel"
BBB : [A-Z]+ -> Channel(HIDDEN); // Note--uppercase "Channel"
WS : [ \t\r\n] -> skip;
