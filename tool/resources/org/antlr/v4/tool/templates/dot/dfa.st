dfa(name, startState, states, edges, rankdir, decisionRanks, useBox) ::= <<
digraph <name>  {
<if(rankdir)>rankdir=<rankdir>;<endif>
<decisionRanks; separator="\n">
<states; separator="\n">
<edges; separator="\n">
}
>>
