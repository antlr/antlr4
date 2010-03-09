dfa(startState, states, edges, rankdir, decisionRanks, useBox) ::= <<
digraph NFA {
<if(rankdir)>rankdir=<rankdir>;<endif>
<decisionRanks; separator="\n">
<states; separator="\n">
<edges; separator="\n">
}
>>
