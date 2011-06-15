atn(startState, states, edges, rankdir, decisionRanks, useBox) ::= <<
digraph ATN {
rankdir=LR;
<decisionRanks; separator="\n">
<states; separator="\n">
<edges; separator="\n">
}
>>
