grammar <grammarName>;
prog : stat EOF;
stat : 'x' ('y' | EOF)*?;
