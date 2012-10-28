epsilon-edge(src,label,target,arrowhead,transitionIndex,loopback=false) ::= <<
<src><if(transitionIndex)>:p<transitionIndex><endif> -> <target> [fontname="Times-Italic", label="&epsilon;"<if(loopback)>, style="dashed"<endif>];
>>
