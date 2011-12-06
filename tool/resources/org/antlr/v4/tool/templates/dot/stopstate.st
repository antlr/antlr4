stopstate(name,label,actionIndex,useBox) ::= <<
<name>[fontsize=11, label="<label><if(actionIndex)>,\naction:<actionIndex><endif>", <if(useBox)>shape=polygon,sides=4,peripheries=2,fixedsize=false<else>shape=doublecircle, fixedsize=true, width=.6<endif>];
>>
