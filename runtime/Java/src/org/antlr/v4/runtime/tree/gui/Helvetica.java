/*
 [The "BSD license"]
  Copyright (c) 2011 Udo Borkowski and Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree.gui;

// actually from Arial but seems to work
public class Helvetica extends BasicFontMetrics {
	{
		maxCharHeight = 896;
        widths[32] = 277; // space
        widths[33] = 277; // exclam
        widths[34] = 354; // quotedbl
        widths[35] = 556; // numbersign
        widths[36] = 556; // dollar
        widths[37] = 889; // percent
        widths[38] = 666; // ampersand
        widths[39] = 190; // quotesingle
        widths[40] = 333; // parenleft
        widths[41] = 333; // parenright
        widths[42] = 389; // asterisk
        widths[43] = 583; // plus
        widths[44] = 277; // comma
        widths[45] = 333; // hyphen
        widths[46] = 277; // period
        widths[47] = 277; // slash
        widths[48] = 556; // zero
        widths[49] = 556; // one
        widths[50] = 556; // two
        widths[51] = 556; // three
        widths[52] = 556; // four
        widths[53] = 556; // five
        widths[54] = 556; // six
        widths[55] = 556; // seven
        widths[56] = 556; // eight
        widths[57] = 556; // nine
        widths[58] = 277; // colon
        widths[59] = 277; // semicolon
        widths[60] = 583; // less
        widths[61] = 583; // equal
        widths[62] = 583; // greater
        widths[63] = 556; // question
        widths[64] = 1015; // at
        widths[65] = 666; // A
        widths[66] = 666; // B
        widths[67] = 722; // C
        widths[68] = 722; // D
        widths[69] = 666; // E
        widths[70] = 610; // F
        widths[71] = 777; // G
        widths[72] = 722; // H
        widths[73] = 277; // I
        widths[74] = 500; // J
        widths[75] = 666; // K
        widths[76] = 556; // L
        widths[77] = 833; // M
        widths[78] = 722; // N
        widths[79] = 777; // O
        widths[80] = 666; // P
        widths[81] = 777; // Q
        widths[82] = 722; // R
        widths[83] = 666; // S
        widths[84] = 610; // T
        widths[85] = 722; // U
        widths[86] = 666; // V
        widths[87] = 943; // W
        widths[88] = 666; // X
        widths[89] = 666; // Y
        widths[90] = 610; // Z
        widths[91] = 277; // bracketleft
        widths[92] = 277; // backslash
        widths[93] = 277; // bracketright
        widths[94] = 469; // asciicircum
        widths[95] = 556; // underscore
        widths[96] = 333; // grave
        widths[97] = 556; // a
        widths[98] = 556; // b
        widths[99] = 500; // c
        widths[100] = 556; // d
        widths[101] = 556; // e
        widths[102] = 277; // f
        widths[103] = 556; // g
        widths[104] = 556; // h
        widths[105] = 222; // i
        widths[106] = 222; // j
        widths[107] = 500; // k
        widths[108] = 222; // l
        widths[109] = 833; // m
        widths[110] = 556; // n
        widths[111] = 556; // o
        widths[112] = 556; // p
        widths[113] = 556; // q
        widths[114] = 333; // r
        widths[115] = 500; // s
        widths[116] = 277; // t
        widths[117] = 556; // u
        widths[118] = 500; // v
        widths[119] = 722; // w
        widths[120] = 500; // x
        widths[121] = 500; // y
        widths[122] = 500; // z
        widths[123] = 333; // braceleft
        widths[124] = 259; // bar
        widths[125] = 333; // braceright
        widths[126] = 583; // asciitilde
		widths[160] = 277; // nbspace
		widths[161] = 333; // exclamdown
		widths[162] = 556; // cent
		widths[163] = 556; // sterling
		widths[164] = 556; // currency
		widths[165] = 556; // yen
		widths[166] = 259; // brokenbar
		widths[167] = 556; // section
		widths[168] = 333; // dieresis
		widths[169] = 736; // copyright
		widths[170] = 370; // ordfeminine
		widths[171] = 556; // guillemotleft
		widths[172] = 583; // logicalnot
		widths[173] = 333; // sfthyphen
		widths[174] = 736; // registered
		widths[175] = 552; // overscore
		widths[176] = 399; // degree
		widths[177] = 548; // plusminus
		widths[178] = 333; // twosuperior
		widths[179] = 333; // threesuperior
		widths[180] = 333; // acute
		widths[181] = 576; // mu1
		widths[182] = 537; // paragraph
		widths[183] = 333; // middot
		widths[184] = 333; // cedilla
		widths[185] = 333; // onesuperior
		widths[186] = 365; // ordmasculine
		widths[187] = 556; // guillemotright
		widths[188] = 833; // onequarter
		widths[189] = 833; // onehalf
		widths[190] = 833; // threequarters
		widths[191] = 610; // questiondown
		widths[192] = 666; // Agrave
		widths[193] = 666; // Aacute
		widths[194] = 666; // Acircumflex
		widths[195] = 666; // Atilde
		widths[196] = 666; // Adieresis
		widths[197] = 666; // Aring
		widths[198] = 1000; // AE
		widths[199] = 722; // Ccedilla
		widths[200] = 666; // Egrave
		widths[201] = 666; // Eacute
		widths[202] = 666; // Ecircumflex
		widths[203] = 666; // Edieresis
		widths[204] = 277; // Igrave
		widths[205] = 277; // Iacute
		widths[206] = 277; // Icircumflex
		widths[207] = 277; // Idieresis
		widths[208] = 722; // Eth
		widths[209] = 722; // Ntilde
		widths[210] = 777; // Ograve
		widths[211] = 777; // Oacute
		widths[212] = 777; // Ocircumflex
		widths[213] = 777; // Otilde
		widths[214] = 777; // Odieresis
		widths[215] = 583; // multiply
		widths[216] = 777; // Oslash
		widths[217] = 722; // Ugrave
		widths[218] = 722; // Uacute
		widths[219] = 722; // Ucircumflex
		widths[220] = 722; // Udieresis
		widths[221] = 666; // Yacute
		widths[222] = 666; // Thorn
		widths[223] = 610; // germandbls
		widths[224] = 556; // agrave
		widths[225] = 556; // aacute
		widths[226] = 556; // acircumflex
		widths[227] = 556; // atilde
		widths[228] = 556; // adieresis
		widths[229] = 556; // aring
		widths[230] = 889; // ae
		widths[231] = 500; // ccedilla
		widths[232] = 556; // egrave
		widths[233] = 556; // eacute
		widths[234] = 556; // ecircumflex
		widths[235] = 556; // edieresis
		widths[236] = 277; // igrave
		widths[237] = 277; // iacute
		widths[238] = 277; // icircumflex
		widths[239] = 277; // idieresis
		widths[240] = 556; // eth
		widths[241] = 556; // ntilde
		widths[242] = 556; // ograve
		widths[243] = 556; // oacute
		widths[244] = 556; // ocircumflex
		widths[245] = 556; // otilde
		widths[246] = 556; // odieresis
		widths[247] = 548; // divide
		widths[248] = 610; // oslash
		widths[249] = 556; // ugrave
		widths[250] = 556; // uacute
		widths[251] = 556; // ucircumflex
		widths[252] = 556; // udieresis
		widths[253] = 500; // yacute
		widths[254] = 556; // thorn
		widths[255] = 500; // ydieresis
    }
}
