action-edge(src,target,label,arrowhead,transitionIndex) ::= <<
<src><if(transitionIndex)>:p<transitionIndex><endif> -> <target> [fontsize=11, fontname="Courier", arrowsize=.7, label = "<label>"<if(arrowhead)>, arrowhead = <arrowhead><endif>];
>>
