state(state, label, name, transitions) ::= <<
<name>[fontsize=11, label="<if(rest(transitions))>{<endif><label><if(rest(transitions))>|{<transitions:{t|\<p<i0>\>}; separator="|">}}<endif>", <if(rest(transitions))>shape=record, fixedsize=false<else>shape=circle, fixedsize=true, width=.55<endif>, peripheries=1];
>>