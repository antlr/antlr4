import ATNState from "../state/ATNState";
import ATN from "./ATN";
import IntervalSet from '../misc/IntervalSet';
import DFA from "../dfa/DFA";

declare class LL1Analyzer {

  decisionToDFA: DFA[];

  constructor(atn: ATN);
  getDecisionLookahead(state: ATNState): IntervalSet[];

}

export default LL1Analyzer;
