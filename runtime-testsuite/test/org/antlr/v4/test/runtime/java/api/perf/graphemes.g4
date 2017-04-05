grammar graphemes;

Extend: [\p{Grapheme_Cluster_Break=Extend}];
ZWJ: '\u200D';
SpacingMark: [\p{Grapheme_Cluster_Break=SpacingMark}];
fragment VS15: '\uFE0E';
fragment VS16: '\uFE0F';
fragment NonspacingMark: [\p{Nonspacing_Mark}];
fragment TextPresentationCharacter: [\p{EmojiPresentation=TextDefault}];
fragment EmojiPresentationCharacter: [\p{EmojiPresentation=EmojiDefault}];
fragment TextPresentationSequence: EmojiPresentationCharacter VS15;
fragment EmojiPresentationSequence: TextPresentationCharacter VS16;
fragment EmojiModifierSequence:
    [\p{Grapheme_Cluster_Break=E_Base}\p{Grapheme_Cluster_Break=E_Base_GAZ}] [\p{Grapheme_Cluster_Break=E_Modifier}];
fragment EmojiFlagSequence:
    [\p{Grapheme_Cluster_Break=Regional_Indicator}] [\p{Grapheme_Cluster_Break=Regional_Indicator}];
fragment ExtendedPictographic: [\p{Extended_Pictographic}];
fragment EmojiNRK: [\p{EmojiNRK}];
fragment EmojiCombiningSequence:
  (   EmojiPresentationSequence
    | TextPresentationSequence
    | EmojiPresentationCharacter )
  NonspacingMark*;
EmojiCoreSequence:
    EmojiModifierSequence
  | EmojiCombiningSequence
  | EmojiFlagSequence;
fragment EmojiZWJElement:
    EmojiModifierSequence
  | EmojiPresentationSequence
  | EmojiPresentationCharacter
  | ExtendedPictographic
  | EmojiNRK;
EmojiZWJSequence:
    EmojiZWJElement (ZWJ EmojiZWJElement)+;
emoji_sequence:
  (   EmojiZWJSequence
    | EmojiCoreSequence )
  ( Extend | ZWJ | SpacingMark )*;

Prepend: [\p{Grapheme_Cluster_Break=Prepend}];
NonControl: [\P{Grapheme_Cluster_Break=Control}];
CRLF: [\p{Grapheme_Cluster_Break=CR}][\p{Grapheme_Cluster_Break=LF}];
HangulSyllable:
    [\p{Grapheme_Cluster_Break=L}]* [\p{Grapheme_Cluster_Break=V}]+ [\p{Grapheme_Cluster_Break=T}]*
  | [\p{Grapheme_Cluster_Break=L}]* [\p{Grapheme_Cluster_Break=LV}] [\p{Grapheme_Cluster_Break=V}]* [\p{Grapheme_Cluster_Break=T}]*
  | [\p{Grapheme_Cluster_Break=L}]* [\p{Grapheme_Cluster_Break=LVT}] [\p{Grapheme_Cluster_Break=T}]*
  | [\p{Grapheme_Cluster_Break=L}]+
  | [\p{Grapheme_Cluster_Break=T}]+;

grapheme_cluster:
    CRLF
  | Prepend* ( emoji_sequence | HangulSyllable | NonControl ) ( Extend | ZWJ | SpacingMark )*;

graphemes: grapheme_cluster* EOF;