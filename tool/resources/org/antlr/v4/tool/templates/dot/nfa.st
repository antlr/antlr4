nfa(startState, states, edges, rankdir, decisionRanks) ::= <<
digraph NFA {
rankdir=LR;
<decisionRanks; separator="\n">
<states; separator="\n">
<edges; separator="\n">
}
>>
