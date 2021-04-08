
;**************************************************************
;*
;*             C P / M   version   2 . 2
;*
;*   Reconstructed from memory image on February 27, 1981
;*
;*                by Clark A. Calkins
;*
;**************************************************************
;
;   Set memory limit here. This is the amount of contigeous
; ram starting from 0000. CP/M will reside at the end of this space.
;
MEM	EQU	62	;for a 62k system (TS802 TEST - WORKS OK).
;
IOBYTE	EQU	3	;i/o definition byte.
TDRIVE	EQU	4	;current drive name and user number.
ENTRY	EQU	5	;entry point for the cp/m bdos.
TFCB	EQU	5CH	;default file control block.
TBUFF	EQU	80H	;i/o buffer and command line storage.
TBASE	EQU	100H	;transiant program storage area.
;
;   Set control character equates.
;
CNTRLC	EQU	3	;control-c
CNTRLE	EQU	05H	;control-e
BS	EQU	08H	;backspace
TAB	EQU	09H	;tab
LF	EQU	0AH	;line feed
FF	EQU	0CH	;form feed
CR	EQU	0DH	;carriage return
CNTRLP	EQU	10H	;control-p
CNTRLR	EQU	12H	;control-r
CNTRLS	EQU	13H	;control-s
CNTRLU	EQU	15H	;control-u
CNTRLX	EQU	18H	;control-x
CNTRLZ	EQU	1AH	;control-z (end-of-file mark)
DEL	EQU	7FH	;rubout
;
;   Set origin for CP/M
;
	ORG	(MEM-7)*1024
;
CBASE	JMP	COMMAND	;execute command processor (ccp).
	JMP	CLEARBUF	;entry to empty input buffer before starting ccp.

;
;   Standard cp/m ccp input buffer. Format is (max length),
; (actual length), (char #1), (char #2), (char #3), etc.
;
INBUFF	DB	127	;length of input buffer.
	DB	0	;current length of contents.
	DB	'Copyright'
	DB	' 1979 (c) by Digital Research      '
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
INPOINT	DW	INBUFF+2;input line pointer
NAMEPNT	DW	0	;input line pointer used for error message. Points to
;			;start of name in error.
;
;   Routine to print (A) on the console. All registers used.
;
PRINT	MOV	E,A	;setup bdos call.
	MVI	C,2
	JMP	ENTRY
;
;   Routine to print (A) on the console and to save (BC).
;
PRINTB	PUSH	B
	CALL	PRINT
	POP	B
	RET
;
;   Routine to send a carriage return, line feed combination
; to the console.
;
CRLF	MVI	A,CR
	CALL	PRINTB
	MVI	A,LF
	JMP	PRINTB
;
;   Routine to send one space to the console and save (BC).
;
SPACE	MVI	A,' '
	JMP	PRINTB
;
;   Routine to print character string pointed to be (BC) on the
; console. It must terminate with a null byte.
;
PLINE	PUSH	B
	CALL	CRLF
	POP	H
PLINE2	MOV	A,M
	ORA	A
	RZ
	INX	H
	PUSH	H
	CALL	PRINT
	POP	H
	JMP	PLINE2
;
;   Routine to reset the disk system.
;
RESDSK	MVI	C,13
	JMP	ENTRY
;
;   Routine to select disk (A).
;
DSKSEL	MOV	E,A
	MVI	C,14
	JMP	ENTRY
;
;   Routine to call bdos and save the return code. The zero
; flag is set on a return of 0ffh.
;
ENTRY1	CALL	ENTRY
	STA	RTNCODE	;save return code.
	INR	A	;set zero if 0ffh returned.
	RET
;
;   Routine to open a file. (DE) must point to the FCB.
;
OPEN	MVI	C,15
	JMP	ENTRY1
;
;   Routine to open file at (FCB).
;
OPENFCB	XRA	A	;clear the record number byte at fcb+32
	STA	FCB+32
	LXI	D,FCB
	JMP	OPEN
;
;   Routine to close a file. (DE) points to FCB.
;
CLOSE	MVI	C,16
	JMP	ENTRY1
;
;   Routine to search for the first file with ambigueous name
; (DE).
;
SRCHFST	MVI	C,17
	JMP	ENTRY1
;
;   Search for the next ambigeous file name.
;
SRCHNXT	MVI	C,18
	JMP	ENTRY1
;
;   Search for file at (FCB).
;
SRCHFCB	LXI	D,FCB
	JMP	SRCHFST
;
;   Routine to delete a file pointed to by (DE).
;
DELETE	MVI	C,19
	JMP	ENTRY
;
;   Routine to call the bdos and set the zero flag if a zero
; status is returned.
;
ENTRY2	CALL	ENTRY
	ORA	A	;set zero flag if appropriate.
	RET
;
;   Routine to read the next record from a sequential file.
; (DE) points to the FCB.
;
RDREC	MVI	C,20
	JMP	ENTRY2
;
;   Routine to read file at (FCB).
;
READFCB	LXI	D,FCB
	JMP	RDREC
;
;   Routine to write the next record of a sequential file.
; (DE) points to the FCB.
;
WRTREC	MVI	C,21
	JMP	ENTRY2
;
;   Routine to create the file pointed to by (DE).
;
CREATE	MVI	C,22
	JMP	ENTRY1
;
;   Routine to rename the file pointed to by (DE). Note that
; the new name starts at (DE+16).
;
RENAM	MVI	C,23
	JMP	ENTRY
;
;   Get the current user code.
;
GETUSR	MVI	E,0FFH
;
;   Routne to get or set the current user code.
; If (E) is FF then this is a GET, else it is a SET.
;
GETSETUC:MVI	C,32
	JMP	ENTRY
;
;   Routine to set the current drive byte at (TDRIVE).
;
SETCDRV	CALL	GETUSR	;get user number
	ADD	A	;and shift into the upper 4 bits.
	ADD	A
	ADD	A
	ADD	A
	LXI	H,CDRIVE;now add in the current drive number.
	ORA	M
	STA	TDRIVE	;and save.
	RET
;
;   Move currently active drive down to (TDRIVE).
;
MOVECD	LDA	CDRIVE
	STA	TDRIVE
	RET
;
;   Routine to convert (A) into upper case ascii. Only letters
; are affected.
;
UPPER	CPI	'a'	;check for letters in the range of 'a' to 'z'.
	RC
	CPI	'{'
	RNC
	ANI	5FH	;convert it if found.
	RET
;
;   Routine to get a line of input. We must check to see if the
; user is in (BATCH) mode. If so, then read the input from file
; ($$$.SUB). At the end, reset to console input.
;
GETINP	LDA	BATCH	;if =0, then use console input.
	ORA	A
	JZ	GETINP1
;
;   Use the submit file ($$$.sub) which is prepared by a
; SUBMIT run. It must be on drive (A) and it will be deleted
; if and error occures (like eof).
;
	LDA	CDRIVE	;select drive 0 if need be.
	ORA	A
	MVI	A,0	;always use drive A for submit.
	CNZ	DSKSEL	;select it if required.
	LXI	D,BATCHFCB
	CALL	OPEN	;look for it.
	JZ	GETINP1	;if not there, use normal input.
	LDA	BATCHFCB+15;get last record number+1.
	DCR	A
	STA	BATCHFCB+32
	LXI	D,BATCHFCB
	CALL	RDREC	;read last record.
	JNZ	GETINP1	;quit on end of file.
;
;   Move this record into input buffer.
;
	LXI	D,INBUFF+1
	LXI	H,TBUFF	;data was read into buffer here.
	MVI	B,128	;all 128 characters may be used.
	CALL	HL2DE	;(HL) to (DE), (B) bytes.
	LXI	H,BATCHFCB+14
	MVI	M,0	;zero out the 's2' byte.
	INX	H	;and decrement the record count.
	DCR	M
	LXI	D,BATCHFCB;close the batch file now.
	CALL	CLOSE
	JZ	GETINP1	;quit on an error.
	LDA	CDRIVE	;re-select previous drive if need be.
	ORA	A
	CNZ	DSKSEL	;don't do needless selects.
;
;   Print line just read on console.
;
	LXI	H,INBUFF+2
	CALL	PLINE2
	CALL	CHKCON	;check console, quit on a key.
	JZ	GETINP2	;jump if no key is pressed.
;
;   Terminate the submit job on any keyboard input. Delete this
; file such that it is not re-started and jump to normal keyboard
; input section.
;
	CALL	DELBATCH;delete the batch file.
	JMP	CMMND1	;and restart command input.
;
;   Get here for normal keyboard input. Delete the submit file
; incase there was one.
;
GETINP1	CALL	DELBATCH;delete file ($$$.sub).
	CALL	SETCDRV	;reset active disk.
	MVI	C,10	;get line from console device.
	LXI	D,INBUFF
	CALL	ENTRY
	CALL	MOVECD	;reset current drive (again).
;
;   Convert input line to upper case.
;
GETINP2	LXI	H,INBUFF+1
	MOV	B,M	;(B)=character counter.
GETINP3	INX	H
	MOV	A,B	;end of the line?
	ORA	A
	JZ	GETINP4
	MOV	A,M	;convert to upper case.
	CALL	UPPER
	MOV	M,A
	DCR	B	;adjust character count.
	JMP	GETINP3
GETINP4	MOV	M,A	;add trailing null.
	LXI	H,INBUFF+2
	SHLD	INPOINT	;reset input line pointer.
	RET
;
;   Routine to check the console for a key pressed. The zero
; flag is set is none, else the character is returned in (A).
;
CHKCON	MVI	C,11	;check console.
	CALL	ENTRY
	ORA	A
	RZ		;return if nothing.
	MVI	C,1	;else get character.
	CALL	ENTRY
	ORA	A	;clear zero flag and return.
	RET
;
;   Routine to get the currently active drive number.
;
GETDSK	MVI	C,25
	JMP	ENTRY
;
;   Set the stabdard dma address.
;
STDDMA	LXI	D,TBUFF
;
;   Routine to set the dma address to (DE).
;
DMASET	MVI	C,26
	JMP	ENTRY
;
;  Delete the batch file created by SUBMIT.
;
DELBATCH:LXI	H,BATCH	;is batch active?
	MOV	A,M
	ORA	A
	RZ
	MVI	M,0	;yes, de-activate it.
	XRA	A
	CALL	DSKSEL	;select drive 0 for sure.
	LXI	D,BATCHFCB;and delete this file.
	CALL	DELETE
	LDA	CDRIVE	;reset current drive.
	JMP	DSKSEL
;
;   Check to two strings at (PATTRN1) and (PATTRN2). They must be
; the same or we halt....
;
VERIFY	LXI	D,PATTRN1;these are the serial number bytes.
	LXI	H,PATTRN2;ditto, but how could they be different?
	MVI	B,6	;6 bytes each.
VERIFY1	LDAX	D
	CMP	M
	JNZ	HALT	;jump to halt routine.
	INX	D
	INX	H
	DCR	B
	JNZ	VERIFY1
	RET
;
;   Print back file name with a '?' to indicate a syntax error.
;
SYNERR	CALL	CRLF	;end current line.
	LHLD	NAMEPNT	;this points to name in error.
SYNERR1	MOV	A,M	;print it until a space or null is found.
	CPI	' '
	JZ	SYNERR2
	ORA	A
	JZ	SYNERR2
	PUSH	H
	CALL	PRINT
	POP	H
	INX	H
	JMP	SYNERR1
SYNERR2	MVI	A,'?'	;add trailing '?'.
	CALL	PRINT
	CALL	CRLF
	CALL	DELBATCH;delete any batch file.
	JMP	CMMND1	;and restart from console input.
;
;   Check character at (DE) for legal command input. Note that the
; zero flag is set if the character is a delimiter.
;
CHECK	LDAX	D
	ORA	A
	RZ
	CPI	' '	;control characters are not legal here.
	JC	SYNERR
	RZ		;check for valid delimiter.
	CPI	'='
	RZ
	CPI	'_'
	RZ
	CPI	'.'
	RZ
	CPI	':'
	RZ
	CPI	';'
	RZ
	CPI	'<'
	RZ
	CPI	'>'
	RZ
	RET
;
;   Get the next non-blank character from (DE).
;
NONBLANK:LDAX	D
	ORA	A	;string ends with a null.
	RZ
	CPI	' '
	RNZ
	INX	D
	JMP	NONBLANK
;
;   Add (HL)=(HL)+(A)
;
ADDHL	ADD	L
	MOV	L,A
	RNC	;take care of any carry.
	INR	H
	RET
;
;   Convert the first name in (FCB).
;
CONVFST	MVI	A,0
;
;   Format a file name (convert * to '?', etc.). On return,
; (A)=0 is an unambigeous name was specified. Enter with (A) equal to
; the position within the fcb for the name (either 0 or 16).
;
CONVERT	LXI	H,FCB
	CALL	ADDHL
	PUSH	H
	PUSH	H
	XRA	A
	STA	CHGDRV	;initialize drive change flag.
	LHLD	INPOINT	;set (HL) as pointer into input line.
	XCHG
	CALL	NONBLANK;get next non-blank character.
	XCHG
	SHLD	NAMEPNT	;save pointer here for any error message.
	XCHG
	POP	H
	LDAX	D	;get first character.
	ORA	A
	JZ	CONVRT1
	SBI	'A'-1	;might be a drive name, convert to binary.
	MOV	B,A	;and save.
	INX	D	;check next character for a ':'.
	LDAX	D
	CPI	':'
	JZ	CONVRT2
	DCX	D	;nope, move pointer back to the start of the line.
CONVRT1	LDA	CDRIVE
	MOV	M,A
	JMP	CONVRT3
CONVRT2	MOV	A,B
	STA	CHGDRV	;set change in drives flag.
	MOV	M,B
	INX	D
;
;   Convert the basic file name.
;
CONVRT3	MVI	B,08H
CONVRT4	CALL	CHECK
	JZ	CONVRT8
	INX	H
	CPI	'*'	;note that an '*' will fill the remaining
	JNZ	CONVRT5	;field with '?'.
	MVI	M,'?'
	JMP	CONVRT6
CONVRT5	MOV	M,A
	INX	D
CONVRT6	DCR	B
	JNZ	CONVRT4
CONVRT7	CALL	CHECK	;get next delimiter.
	JZ	GETEXT
	INX	D
	JMP	CONVRT7
CONVRT8	INX	H	;blank fill the file name.
	MVI	M,' '
	DCR	B
	JNZ	CONVRT8
;
;   Get the extension and convert it.
;
GETEXT	MVI	B,03H
	CPI	'.'
	JNZ	GETEXT5
	INX	D
GETEXT1	CALL	CHECK
	JZ	GETEXT5
	INX	H
	CPI	'*'
	JNZ	GETEXT2
	MVI	M,'?'
	JMP	GETEXT3
GETEXT2	MOV	M,A
	INX	D
GETEXT3	DCR	B
	JNZ	GETEXT1
GETEXT4	CALL	CHECK
	JZ	GETEXT6
	INX	D
	JMP	GETEXT4
GETEXT5	INX	H
	MVI	M,' '
	DCR	B
	JNZ	GETEXT5
GETEXT6	MVI	B,3
GETEXT7	INX	H
	MVI	M,0
	DCR	B
	JNZ	GETEXT7
	XCHG
	SHLD	INPOINT	;save input line pointer.
	POP	H
;
;   Check to see if this is an ambigeous file name specification.
; Set the (A) register to non zero if it is.
;
	LXI	B,11	;set name length.
GETEXT8	INX	H
	MOV	A,M
	CPI	'?'	;any question marks?
	JNZ	GETEXT9
	INR	B	;count them.
GETEXT9	DCR	C
	JNZ	GETEXT8
	MOV	A,B
	ORA	A
	RET
;
;   CP/M command table. Note commands can be either 3 or 4 characters long.
;
NUMCMDS	EQU	6	;number of commands
CMDTBL	DB	'DIR '
	DB	'ERA '
	DB	'TYPE'
	DB	'SAVE'
	DB	'REN '
	DB	'USER'
;
;   The following six bytes must agree with those at (PATTRN2)
; or cp/m will HALT. Why?
;
PATTRN1	DB	0,22,0,0,0,0;(* serial number bytes *).
;
;   Search the command table for a match with what has just
; been entered. If a match is found, then we jump to the
; proper section. Else jump to (UNKNOWN).
; On return, the (C) register is set to the command number
; that matched (or NUMCMDS+1 if no match).
;
SEARCH	LXI	H,CMDTBL
	MVI	C,0
SEARCH1	MOV	A,C
	CPI	NUMCMDS	;this commands exists.
	RNC
	LXI	D,FCB+1	;check this one.
	MVI	B,4	;max command length.
SEARCH2	LDAX	D
	CMP	M
	JNZ	SEARCH3	;not a match.
	INX	D
	INX	H
	DCR	B
	JNZ	SEARCH2
	LDAX	D	;allow a 3 character command to match.
	CPI	' '
	JNZ	SEARCH4
	MOV	A,C	;set return register for this command.
	RET
SEARCH3	INX	H
	DCR	B
	JNZ	SEARCH3
SEARCH4	INR	C
	JMP	SEARCH1
;
;   Set the input buffer to empty and then start the command
; processor (ccp).
;
CLEARBUF:XRA	A
	STA	INBUFF+1;second byte is actual length.
;
;**************************************************************
;*
;*
;* C C P  -   C o n s o l e   C o m m a n d   P r o c e s s o r
;*
;**************************************************************
;*
COMMAND	LXI	SP,CCPSTACK;setup stack area.
	PUSH	B	;note that (C) should be equal to:
	MOV	A,C	;(uuuudddd) where 'uuuu' is the user number
	RAR		;and 'dddd' is the drive number.
	RAR
	RAR
	RAR
	ANI	0FH	;isolate the user number.
	MOV	E,A
	CALL	GETSETUC;and set it.
	CALL	RESDSK	;reset the disk system.
	STA	BATCH	;clear batch mode flag.
	POP	B
	MOV	A,C
	ANI	0FH	;isolate the drive number.
	STA	CDRIVE	;and save.
	CALL	DSKSEL	;...and select.
	LDA	INBUFF+1
	ORA	A	;anything in input buffer already?
	JNZ	CMMND2	;yes, we just process it.
;
;   Entry point to get a command line from the console.
;
CMMND1	LXI	SP,CCPSTACK;set stack straight.
	CALL	CRLF	;start a new line on the screen.
	CALL	GETDSK	;get current drive.
	ADI	'a'
	CALL	PRINT	;print current drive.
	MVI	A,'>'
	CALL	PRINT	;and add prompt.
	CALL	GETINP	;get line from user.
;
;   Process command line here.
;
CMMND2	LXI	D,TBUFF
	CALL	DMASET	;set standard dma address.
	CALL	GETDSK
	STA	CDRIVE	;set current drive.
	CALL	CONVFST	;convert name typed in.
	CNZ	SYNERR	;wild cards are not allowed.
	LDA	CHGDRV	;if a change in drives was indicated,
	ORA	A	;then treat this as an unknown command
	JNZ	UNKNOWN	;which gets executed.
	CALL	SEARCH	;else search command table for a match.
;
;   Note that an unknown command returns
; with (A) pointing to the last address
; in our table which is (UNKNOWN).
;
	LXI	H,CMDADR;now, look thru our address table for command (A).
	MOV	E,A	;set (DE) to command number.
	MVI	D,0
	DAD	D
	DAD	D	;(HL)=(CMDADR)+2*(command number).
	MOV	A,M	;now pick out this address.
	INX	H
	MOV	H,M
	MOV	L,A
	PCHL		;now execute it.
;
;   CP/M command address table.
;
CMDADR	DW	DIRECT,ERASE,TYPE,SAVE
	DW	RENAME,USER,UNKNOWN
;
;   Halt the system. Reason for this is unknown at present.
;
HALT	LXI	H,76F3H	;'DI HLT' instructions.
	SHLD	CBASE
	LXI	H,CBASE
	PCHL
;
;   Read error while TYPEing a file.
;
RDERROR	LXI	B,RDERR
	JMP	PLINE
RDERR	DB	'Read error',0
;
;   Required file was not located.
;
NONE	LXI	B,NOFILE
	JMP	PLINE
NOFILE	DB	'No file',0
;
;   Decode a command of the form 'A>filename number{ filename}.
; Note that a drive specifier is not allowed on the first file
; name. On return, the number is in register (A). Any error
; causes 'filename?' to be printed and the command is aborted.
;
DECODE	CALL	CONVFST	;convert filename.
	LDA	CHGDRV	;do not allow a drive to be specified.
	ORA	A
	JNZ	SYNERR
	LXI	H,FCB+1	;convert number now.
	LXI	B,11	;(B)=sum register, (C)=max digit count.
DECODE1	MOV	A,M
	CPI	' '	;a space terminates the numeral.
	JZ	DECODE3
	INX	H
	SUI	'0'	;make binary from ascii.
	CPI	10	;legal digit?
	JNC	SYNERR
	MOV	D,A	;yes, save it in (D).
	MOV	A,B	;compute (B)=(B)*10 and check for overflow.
	ANI	0E0H
	JNZ	SYNERR
	MOV	A,B
	RLC
	RLC
	RLC	;(A)=(B)*8
	ADD	B	;.......*9
	JC	SYNERR
	ADD	B	;.......*10
	JC	SYNERR
	ADD	D	;add in new digit now.
DECODE2	JC	SYNERR
	MOV	B,A	;and save result.
	DCR	C	;only look at 11 digits.
	JNZ	DECODE1
	RET
DECODE3	MOV	A,M	;spaces must follow (why?).
	CPI	' '
	JNZ	SYNERR
	INX	H
DECODE4	DCR	C
	JNZ	DECODE3
	MOV	A,B	;set (A)=the numeric value entered.
	RET
;
;   Move 3 bytes from (HL) to (DE). Note that there is only
; one reference to this at (A2D5h).
;
MOVE3	MVI	B,3
;
;   Move (B) bytes from (HL) to (DE).
;
HL2DE	MOV	A,M
	STAX	D
	INX	H
	INX	D
	DCR	B
	JNZ	HL2DE
	RET
;
;   Compute (HL)=(TBUFF)+(A)+(C) and get the byte that's here.
;
EXTRACT	LXI	H,TBUFF
	ADD	C
	CALL	ADDHL
	MOV	A,M
	RET
;
;  Check drive specified. If it means a change, then the new
; drive will be selected. In any case, the drive byte of the
; fcb will be set to null (means use current drive).
;
DSELECT	XRA	A	;null out first byte of fcb.
	STA	FCB
	LDA	CHGDRV	;a drive change indicated?
	ORA	A
	RZ
	DCR	A	;yes, is it the same as the current drive?
	LXI	H,CDRIVE
	CMP	M
	RZ
	JMP	DSKSEL	;no. Select it then.
;
;   Check the drive selection and reset it to the previous
; drive if it was changed for the preceeding command.
;
RESETDR	LDA	CHGDRV	;drive change indicated?
	ORA	A
	RZ
	DCR	A	;yes, was it a different drive?
	LXI	H,CDRIVE
	CMP	M
	RZ
	LDA	CDRIVE	;yes, re-select our old drive.
	JMP	DSKSEL
;
;**************************************************************
;*
;*           D I R E C T O R Y   C O M M A N D
;*
;**************************************************************
;
DIRECT	CALL	CONVFST	;convert file name.
	CALL	DSELECT	;select indicated drive.
	LXI	H,FCB+1	;was any file indicated?
	MOV	A,M
	CPI	' '
	JNZ	DIRECT2
	MVI	B,11	;no. Fill field with '?' - same as *.*.
DIRECT1	MVI	M,'?'
	INX	H
	DCR	B
	JNZ	DIRECT1
DIRECT2	MVI	E,0	;set initial cursor position.
	PUSH	D
	CALL	SRCHFCB	;get first file name.
	CZ	NONE	;none found at all?
DIRECT3	JZ	DIRECT9	;terminate if no more names.
	LDA	RTNCODE	;get file's position in segment (0-3).
	RRC
	RRC
	RRC
	ANI	60H	;(A)=position*32
	MOV	C,A
	MVI	A,10
	CALL	EXTRACT	;extract the tenth entry in fcb.
	RAL		;check system file status bit.
	JC	DIRECT8	;we don't list them.
	POP	D
	MOV	A,E	;bump name count.
	INR	E
	PUSH	D
	ANI	03H	;at end of line?
	PUSH	PSW
	JNZ	DIRECT4
	CALL	CRLF	;yes, end this line and start another.
	PUSH	B
	CALL	GETDSK	;start line with ('A:').
	POP	B
	ADI	'A'
	CALL	PRINTB
	MVI	A,':'
	CALL	PRINTB
	JMP	DIRECT5
DIRECT4	CALL	SPACE	;add seperator between file names.
	MVI	A,':'
	CALL	PRINTB
DIRECT5	CALL	SPACE
	MVI	B,1	;'extract' each file name character at a time.
DIRECT6	MOV	A,B
	CALL	EXTRACT
	ANI	7FH	;strip bit 7 (status bit).
	CPI	' '	;are we at the end of the name?
	JNZ	DRECT65
	POP	PSW	;yes, don't print spaces at the end of a line.
	PUSH	PSW
	CPI	3
	JNZ	DRECT63
	MVI	A,9	;first check for no extension.
	CALL	EXTRACT
	ANI	7FH
	CPI	' '
	JZ	DIRECT7	;don't print spaces.
DRECT63	MVI	A,' '	;else print them.
DRECT65	CALL	PRINTB
	INR	B	;bump to next character psoition.
	MOV	A,B
	CPI	12	;end of the name?
	JNC	DIRECT7
	CPI	9	;nope, starting extension?
	JNZ	DIRECT6
	CALL	SPACE	;yes, add seperating space.
	JMP	DIRECT6
DIRECT7	POP	PSW	;get the next file name.
DIRECT8	CALL	CHKCON	;first check console, quit on anything.
	JNZ	DIRECT9
	CALL	SRCHNXT	;get next name.
	JMP	DIRECT3	;and continue with our list.
DIRECT9	POP	D	;restore the stack and return to command level.
	JMP	GETBACK
;
;**************************************************************
;*
;*                E R A S E   C O M M A N D
;*
;**************************************************************
;
ERASE	CALL	CONVFST	;convert file name.
	CPI	11	;was '*.*' entered?
	JNZ	ERASE1
	LXI	B,YESNO	;yes, ask for confirmation.
	CALL	PLINE
	CALL	GETINP
	LXI	H,INBUFF+1
	DCR	M	;must be exactly 'y'.
	JNZ	CMMND1
	INX	H
	MOV	A,M
	CPI	'Y'
	JNZ	CMMND1
	INX	H
	SHLD	INPOINT	;save input line pointer.
ERASE1	CALL	DSELECT	;select desired disk.
	LXI	D,FCB
	CALL	DELETE	;delete the file.
	INR	A
	CZ	NONE	;not there?
	JMP	GETBACK	;return to command level now.
YESNO	DB	'All (y/n)?',0
;
;**************************************************************
;*
;*            T Y P E   C O M M A N D
;*
;**************************************************************
;
TYPE	CALL	CONVFST	;convert file name.
	JNZ	SYNERR	;wild cards not allowed.
	CALL	DSELECT	;select indicated drive.
	CALL	OPENFCB	;open the file.
	JZ	TYPE5	;not there?
	CALL	CRLF	;ok, start a new line on the screen.
	LXI	H,NBYTES;initialize byte counter.
	MVI	M,0FFH	;set to read first sector.
TYPE1	LXI	H,NBYTES
TYPE2	MOV	A,M	;have we written the entire sector?
	CPI	128
	JC	TYPE3
	PUSH	H	;yes, read in the next one.
	CALL	READFCB
	POP	H
	JNZ	TYPE4	;end or error?
	XRA	A	;ok, clear byte counter.
	MOV	M,A
TYPE3	INR	M	;count this byte.
	LXI	H,TBUFF	;and get the (A)th one from the buffer (TBUFF).
	CALL	ADDHL
	MOV	A,M
	CPI	CNTRLZ	;end of file mark?
	JZ	GETBACK
	CALL	PRINT	;no, print it.
	CALL	CHKCON	;check console, quit if anything ready.
	JNZ	GETBACK
	JMP	TYPE1
;
;   Get here on an end of file or read error.
;
TYPE4	DCR	A	;read error?
	JZ	GETBACK
	CALL	RDERROR	;yes, print message.
TYPE5	CALL	RESETDR	;and reset proper drive
	JMP	SYNERR	;now print file name with problem.
;
;**************************************************************
;*
;*            S A V E   C O M M A N D
;*
;**************************************************************
;
SAVE	CALL	DECODE	;get numeric number that follows SAVE.
	PUSH	PSW	;save number of pages to write.
	CALL	CONVFST	;convert file name.
	JNZ	SYNERR	;wild cards not allowed.
	CALL	DSELECT	;select specified drive.
	LXI	D,FCB	;now delete this file.
	PUSH	D
	CALL	DELETE
	POP	D
	CALL	CREATE	;and create it again.
	JZ	SAVE3	;can't create?
	XRA	A	;clear record number byte.
	STA	FCB+32
	POP	PSW	;convert pages to sectors.
	MOV	L,A
	MVI	H,0
	DAD	H	;(HL)=number of sectors to write.
	LXI	D,TBASE	;and we start from here.
SAVE1	MOV	A,H	;done yet?
	ORA	L
	JZ	SAVE2
	DCX	H	;nope, count this and compute the start
	PUSH	H	;of the next 128 byte sector.
	LXI	H,128
	DAD	D
	PUSH	H	;save it and set the transfer address.
	CALL	DMASET
	LXI	D,FCB	;write out this sector now.
	CALL	WRTREC
	POP	D	;reset (DE) to the start of the last sector.
	POP	H	;restore sector count.
	JNZ	SAVE3	;write error?
	JMP	SAVE1
;
;   Get here after writing all of the file.
;
SAVE2	LXI	D,FCB	;now close the file.
	CALL	CLOSE
	INR	A	;did it close ok?
	JNZ	SAVE4
;
;   Print out error message (no space).
;
SAVE3	LXI	B,NOSPACE
	CALL	PLINE
SAVE4	CALL	STDDMA	;reset the standard dma address.
	JMP	GETBACK
NOSPACE	DB	'No space',0
;
;**************************************************************
;*
;*           R E N A M E   C O M M A N D
;*
;**************************************************************
;
RENAME	CALL	CONVFST	;convert first file name.
	JNZ	SYNERR	;wild cards not allowed.
	LDA	CHGDRV	;remember any change in drives specified.
	PUSH	PSW
	CALL	DSELECT	;and select this drive.
	CALL	SRCHFCB	;is this file present?
	JNZ	RENAME6	;yes, print error message.
	LXI	H,FCB	;yes, move this name into second slot.
	LXI	D,FCB+16
	MVI	B,16
	CALL	HL2DE
	LHLD	INPOINT	;get input pointer.
	XCHG
	CALL	NONBLANK;get next non blank character.
	CPI	'='	;only allow an '=' or '_' seperator.
	JZ	RENAME1
	CPI	'_'
	JNZ	RENAME5
RENAME1	XCHG
	INX	H	;ok, skip seperator.
	SHLD	INPOINT	;save input line pointer.
	CALL	CONVFST	;convert this second file name now.
	JNZ	RENAME5	;again, no wild cards.
	POP	PSW	;if a drive was specified, then it
	MOV	B,A	;must be the same as before.
	LXI	H,CHGDRV
	MOV	A,M
	ORA	A
	JZ	RENAME2
	CMP	B
	MOV	M,B
	JNZ	RENAME5	;they were different, error.
RENAME2	MOV	M,B;	reset as per the first file specification.
	XRA	A
	STA	FCB	;clear the drive byte of the fcb.
RENAME3	CALL	SRCHFCB	;and go look for second file.
	JZ	RENAME4	;doesn't exist?
	LXI	D,FCB
	CALL	RENAM	;ok, rename the file.
	JMP	GETBACK
;
;   Process rename errors here.
;
RENAME4	CALL	NONE	;file not there.
	JMP	GETBACK
RENAME5	CALL	RESETDR	;bad command format.
	JMP	SYNERR
RENAME6	LXI	B,EXISTS;destination file already exists.
	CALL	PLINE
	JMP	GETBACK
EXISTS	DB	'File exists',0
;
;**************************************************************
;*
;*             U S E R   C O M M A N D
;*
;**************************************************************
;
USER	CALL	DECODE	;get numeric value following command.
	CPI	16	;legal user number?
	JNC	SYNERR
	MOV	E,A	;yes but is there anything else?
	LDA	FCB+1
	CPI	' '
	JZ	SYNERR	;yes, that is not allowed.
	CALL	GETSETUC;ok, set user code.
	JMP	GETBACK1
;
;**************************************************************
;*
;*        T R A N S I A N T   P R O G R A M   C O M M A N D
;*
;**************************************************************
;
UNKNOWN	CALL	VERIFY	;check for valid system (why?).
	LDA	FCB+1	;anything to execute?
	CPI	' '
	JNZ	UNKWN1
	LDA	CHGDRV	;nope, only a drive change?
	ORA	A
	JZ	GETBACK1;neither???
	DCR	A
	STA	CDRIVE	;ok, store new drive.
	CALL	MOVECD	;set (TDRIVE) also.
	CALL	DSKSEL	;and select this drive.
	JMP	GETBACK1;then return.
;
;   Here a file name was typed. Prepare to execute it.
;
UNKWN1	LXI	D,FCB+9	;an extension specified?
	LDAX	D
	CPI	' '
	JNZ	SYNERR	;yes, not allowed.
UNKWN2	PUSH	D
	CALL	DSELECT	;select specified drive.
	POP	D
	LXI	H,COMFILE	;set the extension to 'COM'.
	CALL	MOVE3
	CALL	OPENFCB	;and open this file.
	JZ	UNKWN9	;not present?
;
;   Load in the program.
;
	LXI	H,TBASE	;store the program starting here.
UNKWN3	PUSH	H
	XCHG
	CALL	DMASET	;set transfer address.
	LXI	D,FCB	;and read the next record.
	CALL	RDREC
	JNZ	UNKWN4	;end of file or read error?
	POP	H	;nope, bump pointer for next sector.
	LXI	D,128
	DAD	D
	LXI	D,CBASE	;enough room for the whole file?
	MOV	A,L
	SUB	E
	MOV	A,H
	SBB	D
	JNC	UNKWN0	;no, it can't fit.
	JMP	UNKWN3
;
;   Get here after finished reading.
;
UNKWN4	POP	H
	DCR	A	;normal end of file?
	JNZ	UNKWN0
	CALL	RESETDR	;yes, reset previous drive.
	CALL	CONVFST	;convert the first file name that follows
	LXI	H,CHGDRV;command name.
	PUSH	H
	MOV	A,M	;set drive code in default fcb.
	STA	FCB
	MVI	A,16	;put second name 16 bytes later.
	CALL	CONVERT	;convert second file name.
	POP	H
	MOV	A,M	;and set the drive for this second file.
	STA	FCB+16
	XRA	A	;clear record byte in fcb.
	STA	FCB+32
	LXI	D,TFCB	;move it into place at(005Ch).
	LXI	H,FCB
	MVI	B,33
	CALL	HL2DE
	LXI	H,INBUFF+2;now move the remainder of the input
UNKWN5	MOV	A,M	;line down to (0080h). Look for a non blank.
	ORA	A	;or a null.
	JZ	UNKWN6
	CPI	' '
	JZ	UNKWN6
	INX	H
	JMP	UNKWN5
;
;   Do the line move now. It ends in a null byte.
;
UNKWN6	MVI	B,0	;keep a character count.
	LXI	D,TBUFF+1;data gets put here.
UNKWN7	MOV	A,M	;move it now.
	STAX	D
	ORA	A
	JZ	UNKWN8
	INR	B
	INX	H
	INX	D
	JMP	UNKWN7
UNKWN8	MOV	A,B	;now store the character count.
	STA	TBUFF
	CALL	CRLF	;clean up the screen.
	CALL	STDDMA	;set standard transfer address.
	CALL	SETCDRV	;reset current drive.
	CALL	TBASE	;and execute the program.
;
;   Transiant programs return here (or reboot).
;
	LXI	SP,BATCH	;set stack first off.
	CALL	MOVECD	;move current drive into place (TDRIVE).
	CALL	DSKSEL	;and reselect it.
	JMP	CMMND1	;back to comand mode.
;
;   Get here if some error occured.
;
UNKWN9	CALL	RESETDR	;inproper format.
	JMP	SYNERR
UNKWN0	LXI	B,BADLOAD;read error or won't fit.
	CALL	PLINE
	JMP	GETBACK
BADLOAD	DB	'Bad load',0
COMFILE	DB	'COM'	;command file extension.
;
;   Get here to return to command level. We will reset the
; previous active drive and then either return to command
; level directly or print error message and then return.
;
GETBACK	CALL	RESETDR	;reset previous drive.
GETBACK1:CALL	CONVFST	;convert first name in (FCB).
	LDA	FCB+1	;if this was just a drive change request,
	SUI	' '	;make sure it was valid.
	LXI	H,CHGDRV
	ORA	M
	JNZ	SYNERR
	JMP	CMMND1	;ok, return to command level.
;
;   ccp stack area.
;
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
CCPSTACK:EQU	$	;end of ccp stack area.
;
;   Batch (or SUBMIT) processing information storage.
;
BATCH	DB	0	;batch mode flag (0=not active).
BATCHFCB:DB	0,'$$$     SUB',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;
;   File control block setup by the CCP.
;
FCB	DB	0,'           ',0,0,0,0,0,'           ',0,0,0,0,0
RTNCODE	DB	0	;status returned from bdos call.
CDRIVE	DB	0	;currently active drive.
CHGDRV	DB	0	;change in drives flag (0=no change).
NBYTES	DW	0	;byte counter used by TYPE.
;
;   Room for expansion?
;
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0
;
;   Note that the following six bytes must match those at
; (PATTRN1) or cp/m will HALT. Why?
;
PATTRN2	DB	0,22,0,0,0,0;(* serial number bytes *).
;
;**************************************************************
;*
;*                    B D O S   E N T R Y
;*
;**************************************************************
;
FBASE	JMP	FBASE1
;
;   Bdos error table.
;
BADSCTR	DW	ERROR1	;bad sector on read or write.
BADSLCT	DW	ERROR2	;bad disk select.
RODISK	DW	ERROR3	;disk is read only.
ROFILE	DW	ERROR4	;file is read only.
;
;   Entry into bdos. (DE) or (E) are the parameters passed. The
; function number desired is in register (C).
;
FBASE1	XCHG		;save the (DE) parameters.
	SHLD	PARAMS
	XCHG
	MOV	A,E	;and save register (E) in particular.
	STA	EPARAM
	LXI	H,0
	SHLD	STATUS	;clear return status.
	DAD	SP
	SHLD	USRSTACK;save users stack pointer.
	LXI	SP,STKAREA;and set our own.
	XRA	A	;clear auto select storage space.
	STA	AUTOFLAG
	STA	AUTO
	LXI	H,GOBACK;set return address.
	PUSH	H
	MOV	A,C	;get function number.
	CPI	NFUNCTS	;valid function number?
	RNC
	MOV	C,E	;keep single register function here.
	LXI	H,FUNCTNS;now look thru the function table.
	MOV	E,A
	MVI	D,0	;(DE)=function number.
	DAD	D
	DAD	D	;(HL)=(start of table)+2*(function number).
	MOV	E,M
	INX	H
	MOV	D,M	;now (DE)=address for this function.
	LHLD	PARAMS	;retrieve parameters.
	XCHG		;now (DE) has the original parameters.
	PCHL		;execute desired function.
;
;   BDOS function jump table.
;
NFUNCTS	EQU	41	;number of functions in followin table.
;
FUNCTNS	DW	WBOOT,GETCON,OUTCON,GETRDR,PUNCH,LIST,DIRCIO,GETIOB
	DW	SETIOB,PRTSTR,RDBUFF,GETCSTS,GETVER,RSTDSK,SETDSK,OPENFIL
	DW	CLOSEFIL,GETFST,GETNXT,DELFILE,READSEQ,WRTSEQ,FCREATE
	DW	RENFILE,GETLOG,GETCRNT,PUTDMA,GETALOC,WRTPRTD,GETROV,SETATTR
	DW	GETPARM,GETUSER,RDRANDOM,WTRANDOM,FILESIZE,SETRAN,LOGOFF,RTN
	DW	RTN,WTSPECL
;
;   Bdos error message section.
;
ERROR1	LXI	H,BADSEC	;bad sector message.
	CALL	PRTERR	;print it and get a 1 char responce.
	CPI	CNTRLC	;re-boot request (control-c)?
	JZ	0	;yes.
	RET		;no, return to retry i/o function.
;
ERROR2	LXI	H,BADSEL	;bad drive selected.
	JMP	ERROR5
;
ERROR3	LXI	H,DISKRO	;disk is read only.
	JMP	ERROR5
;
ERROR4	LXI	H,FILERO	;file is read only.
;
ERROR5	CALL	PRTERR
	JMP	0	;always reboot on these errors.
;
BDOSERR	DB	'Bdos Err On '
BDOSDRV	DB	' : $'
BADSEC	DB	'Bad Sector$'
BADSEL	DB	'Select$'
FILERO	DB	'File '
DISKRO	DB	'R/O$'
;
;   Print bdos error message.
;
PRTERR	PUSH	H	;save second message pointer.
	CALL	OUTCRLF	;send (cr)(lf).
	LDA	ACTIVE	;get active drive.
	ADI	'A'	;make ascii.
	STA	BDOSDRV	;and put in message.
	LXI	B,BDOSERR;and print it.
	CALL	PRTMESG
	POP	B	;print second message line now.
	CALL	PRTMESG
;
;   Get an input character. We will check our 1 character
; buffer first. This may be set by the console status routine.
;
GETCHAR	LXI	H,CHARBUF;check character buffer.
	MOV	A,M	;anything present already?
	MVI	M,0	;...either case clear it.
	ORA	A
	RNZ		;yes, use it.
	JMP	CONIN	;nope, go get a character responce.
;
;   Input and echo a character.
;
GETECHO	CALL	GETCHAR	;input a character.
	CALL	CHKCHAR	;carriage control?
	RC		;no, a regular control char so don't echo.
	PUSH	PSW	;ok, save character now.
	MOV	C,A
	CALL	OUTCON	;and echo it.
	POP	PSW	;get character and return.
	RET
;
;   Check character in (A). Set the zero flag on a carriage
; control character and the carry flag on any other control
; character.
;
CHKCHAR	CPI	CR	;check for carriage return, line feed, backspace,
	RZ		;or a tab.
	CPI	LF
	RZ
	CPI	TAB
	RZ
	CPI	BS
	RZ
	CPI	' '	;other control char? Set carry flag.
	RET
;
;   Check the console during output. Halt on a control-s, then
; reboot on a control-c. If anything else is ready, clear the
; zero flag and return (the calling routine may want to do
; something).
;
CKCONSOL:LDA	CHARBUF	;check buffer.
	ORA	A	;if anything, just return without checking.
	JNZ	CKCON2
	CALL	CONST	;nothing in buffer. Check console.
	ANI	01H	;look at bit 0.
	RZ		;return if nothing.
	CALL	CONIN	;ok, get it.
	CPI	CNTRLS	;if not control-s, return with zero cleared.
	JNZ	CKCON1
	CALL	CONIN	;halt processing until another char
	CPI	CNTRLC	;is typed. Control-c?
	JZ	0	;yes, reboot now.
	XRA	A	;no, just pretend nothing was ever ready.
	RET
CKCON1	STA	CHARBUF	;save character in buffer for later processing.
CKCON2	MVI	A,1	;set (A) to non zero to mean something is ready.
	RET
;
;   Output (C) to the screen. If the printer flip-flop flag
; is set, we will send character to printer also. The console
; will be checked in the process.
;
OUTCHAR	LDA	OUTFLAG	;check output flag.
	ORA	A	;anything and we won't generate output.
	JNZ	OUTCHR1
	PUSH	B
	CALL	CKCONSOL;check console (we don't care whats there).
	POP	B
	PUSH	B
	CALL	CONOUT	;output (C) to the screen.
	POP	B
	PUSH	B
	LDA	PRTFLAG	;check printer flip-flop flag.
	ORA	A
	CNZ	LIST	;print it also if non-zero.
	POP	B
OUTCHR1	MOV	A,C	;update cursors position.
	LXI	H,CURPOS
	CPI	DEL	;rubouts don't do anything here.
	RZ
	INR	M	;bump line pointer.
	CPI	' '	;and return if a normal character.
	RNC
	DCR	M	;restore and check for the start of the line.
	MOV	A,M
	ORA	A
	RZ		;ingnore control characters at the start of the line.
	MOV	A,C
	CPI	BS	;is it a backspace?
	JNZ	OUTCHR2
	DCR	M	;yes, backup pointer.
	RET
OUTCHR2	CPI	LF	;is it a line feed?
	RNZ		;ignore anything else.
	MVI	M,0	;reset pointer to start of line.
	RET
;
;   Output (A) to the screen. If it is a control character
; (other than carriage control), use ^x format.
;
SHOWIT	MOV	A,C
	CALL	CHKCHAR	;check character.
	JNC	OUTCON	;not a control, use normal output.
	PUSH	PSW
	MVI	C,'^'	;for a control character, preceed it with '^'.
	CALL	OUTCHAR
	POP	PSW
	ORI	'@'	;and then use the letter equivelant.
	MOV	C,A
;
;   Function to output (C) to the console device and expand tabs
; if necessary.
;
OUTCON	MOV	A,C
	CPI	TAB	;is it a tab?
	JNZ	OUTCHAR	;use regular output.
OUTCON1	MVI	C,' '	;yes it is, use spaces instead.
	CALL	OUTCHAR
	LDA	CURPOS	;go until the cursor is at a multiple of 8

	ANI	07H	;position.
	JNZ	OUTCON1
	RET
;
;   Echo a backspace character. Erase the prevoius character
; on the screen.
;
BACKUP	CALL	BACKUP1	;backup the screen 1 place.
	MVI	C,' '	;then blank that character.
	CALL	CONOUT
BACKUP1	MVI	C,BS	;then back space once more.
	JMP	CONOUT
;
;   Signal a deleted line. Print a '#' at the end and start
; over.
;
NEWLINE	MVI	C,'#'
	CALL	OUTCHAR	;print this.
	CALL	OUTCRLF	;start new line.
NEWLN1	LDA	CURPOS	;move the cursor to the starting position.
	LXI	H,STARTING
	CMP	M
	RNC		;there yet?
	MVI	C,' '
	CALL	OUTCHAR	;nope, keep going.
	JMP	NEWLN1
;
;   Output a (cr) (lf) to the console device (screen).
;
OUTCRLF	MVI	C,CR
	CALL	OUTCHAR
	MVI	C,LF
	JMP	OUTCHAR
;
;   Print message pointed to by (BC). It will end with a '$'.
;
PRTMESG	LDAX	B	;check for terminating character.
	CPI	'$'
	RZ
	INX	B
	PUSH	B	;otherwise, bump pointer and print it.
	MOV	C,A
	CALL	OUTCON
	POP	B
	JMP	PRTMESG
;
;   Function to execute a buffered read.
;
RDBUFF	LDA	CURPOS	;use present location as starting one.
	STA	STARTING
	LHLD	PARAMS	;get the maximum buffer space.
	MOV	C,M
	INX	H	;point to first available space.
	PUSH	H	;and save.
	MVI	B,0	;keep a character count.
RDBUF1	PUSH	B
	PUSH	H
RDBUF2	CALL	GETCHAR	;get the next input character.
	ANI	7FH	;strip bit 7.
	POP	H	;reset registers.
	POP	B
	CPI	CR	;en of the line?
	JZ	RDBUF17
	CPI	LF
	JZ	RDBUF17
	CPI	BS	;how about a backspace?
	JNZ	RDBUF3
	MOV	A,B	;yes, but ignore at the beginning of the line.
	ORA	A
	JZ	RDBUF1
	DCR	B	;ok, update counter.
	LDA	CURPOS	;if we backspace to the start of the line,
	STA	OUTFLAG	;treat as a cancel (control-x).
	JMP	RDBUF10
RDBUF3	CPI	DEL	;user typed a rubout?
	JNZ	RDBUF4
	MOV	A,B	;ignore at the start of the line.
	ORA	A
	JZ	RDBUF1
	MOV	A,M	;ok, echo the prevoius character.
	DCR	B	;and reset pointers (counters).
	DCX	H
	JMP	RDBUF15
RDBUF4	CPI	CNTRLE	;physical end of line?
	JNZ	RDBUF5
	PUSH	B	;yes, do it.
	PUSH	H
	CALL	OUTCRLF
	XRA	A	;and update starting position.
	STA	STARTING
	JMP	RDBUF2
RDBUF5	CPI	CNTRLP	;control-p?
	JNZ	RDBUF6
	PUSH	H	;yes, flip the print flag filp-flop byte.
	LXI	H,PRTFLAG
	MVI	A,1	;PRTFLAG=1-PRTFLAG
	SUB	M
	MOV	M,A
	POP	H
	JMP	RDBUF1
RDBUF6	CPI	CNTRLX	;control-x (cancel)?
	JNZ	RDBUF8
	POP	H
RDBUF7	LDA	STARTING;yes, backup the cursor to here.
	LXI	H,CURPOS
	CMP	M
	JNC	RDBUFF	;done yet?
	DCR	M	;no, decrement pointer and output back up one space.
	CALL	BACKUP
	JMP	RDBUF7
RDBUF8	CPI	CNTRLU	;cntrol-u (cancel line)?
	JNZ	RDBUF9
	CALL	NEWLINE	;start a new line.
	POP	H
	JMP	RDBUFF
RDBUF9	CPI	CNTRLR	;control-r?
	JNZ	RDBUF14
RDBUF10	PUSH	B	;yes, start a new line and retype the old one.
	CALL	NEWLINE
	POP	B
	POP	H
	PUSH	H
	PUSH	B
RDBUF11	MOV	A,B	;done whole line yet?
	ORA	A
	JZ	RDBUF12
	INX	H	;nope, get next character.
	MOV	C,M
	DCR	B	;count it.
	PUSH	B
	PUSH	H
	CALL	SHOWIT	;and display it.
	POP	H
	POP	B
	JMP	RDBUF11
RDBUF12	PUSH	H	;done with line. If we were displaying
	LDA	OUTFLAG	;then update cursor position.
	ORA	A
	JZ	RDBUF2
	LXI	H,CURPOS;because this line is shorter, we must
	SUB	M	;back up the cursor (not the screen however)
	STA	OUTFLAG	;some number of positions.
RDBUF13	CALL	BACKUP	;note that as long as (OUTFLAG) is non
	LXI	H,OUTFLAG;zero, the screen will not be changed.
	DCR	M
	JNZ	RDBUF13
	JMP	RDBUF2	;now just get the next character.
;
;   Just a normal character, put this in our buffer and echo.
;
RDBUF14	INX	H
	MOV	M,A	;store character.
	INR	B	;and count it.
RDBUF15	PUSH	B
	PUSH	H
	MOV	C,A	;echo it now.
	CALL	SHOWIT
	POP	H
	POP	B
	MOV	A,M	;was it an abort request?
	CPI	CNTRLC	;control-c abort?
	MOV	A,B
	JNZ	RDBUF16
	CPI	1	;only if at start of line.
	JZ	0
RDBUF16	CMP	C	;nope, have we filled the buffer?
	JC	RDBUF1
RDBUF17	POP	H	;yes end the line and return.
	MOV	M,B
	MVI	C,CR
	JMP	OUTCHAR	;output (cr) and return.
;
;   Function to get a character from the console device.
;
GETCON	CALL	GETECHO	;get and echo.
	JMP	SETSTAT	;save status and return.
;
;   Function to get a character from the tape reader device.
;
GETRDR	CALL	READER	;get a character from reader, set status and return.
	JMP	SETSTAT
;
;  Function to perform direct console i/o. If (C) contains (FF)
; then this is an input request. If (C) contains (FE) then
; this is a status request. Otherwise we are to output (C).
;
DIRCIO	MOV	A,C	;test for (FF).
	INR	A
	JZ	DIRC1
	INR	A	;test for (FE).
	JZ	CONST
	JMP	CONOUT	;just output (C).
DIRC1	CALL	CONST	;this is an input request.
	ORA	A
	JZ	GOBACK1	;not ready? Just return (directly).
	CALL	CONIN	;yes, get character.
	JMP	SETSTAT	;set status and return.
;
;   Function to return the i/o byte.
;
GETIOB	LDA	IOBYTE
	JMP	SETSTAT
;
;   Function to set the i/o byte.
;
SETIOB	LXI	H,IOBYTE
	MOV	M,C
	RET
;
;   Function to print the character string pointed to by (DE)
; on the console device. The string ends with a '$'.
;
PRTSTR	XCHG
	MOV	C,L
	MOV	B,H	;now (BC) points to it.
	JMP	PRTMESG
;
;   Function to interigate the console device.
;
GETCSTS	CALL	CKCONSOL
;
;   Get here to set the status and return to the cleanup
; section. Then back to the user.
;
SETSTAT	STA	STATUS
RTN	RET
;
;   Set the status to 1 (read or write error code).
;
IOERR1	MVI	A,1
	JMP	SETSTAT
;
OUTFLAG	DB	0	;output flag (non zero means no output).
STARTING:DB	2	;starting position for cursor.
CURPOS	DB	0	;cursor position (0=start of line).
PRTFLAG	DB	0	;printer flag (control-p toggle). List if non zero.
CHARBUF	DB	0	;single input character buffer.
;
;   Stack area for BDOS calls.
;
USRSTACK:DW	0	;save users stack pointer here.
;
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
STKAREA	EQU	$	;end of stack area.
;
USERNO	DB	0	;current user number.
ACTIVE	DB	0	;currently active drive.
PARAMS	DW	0	;save (DE) parameters here on entry.
STATUS	DW	0	;status returned from bdos function.
;
;   Select error occured, jump to error routine.
;
SLCTERR	LXI	H,BADSLCT
;
;   Jump to (HL) indirectly.
;
JUMPHL	MOV	E,M
	INX	H
	MOV	D,M	;now (DE) contain the desired address.
	XCHG
	PCHL
;
;   Block move. (DE) to (HL), (C) bytes total.
;
DE2HL	INR	C	;is count down to zero?
DE2HL1	DCR	C
	RZ		;yes, we are done.
	LDAX	D	;no, move one more byte.
	MOV	M,A
	INX	D
	INX	H
	JMP	DE2HL1	;and repeat.
;
;   Select the desired drive.
;
SELECT	LDA	ACTIVE	;get active disk.
	MOV	C,A
	CALL	SELDSK	;select it.
	MOV	A,H	;valid drive?
	ORA	L	;valid drive?
	RZ		;return if not.
;
;   Here, the BIOS returned the address of the parameter block
; in (HL). We will extract the necessary pointers and save them.
;
	MOV	E,M	;yes, get address of translation table into (DE).
	INX	H
	MOV	D,M
	INX	H
	SHLD	SCRATCH1	;save pointers to scratch areas.
	INX	H
	INX	H
	SHLD	SCRATCH2	;ditto.
	INX	H
	INX	H
	SHLD	SCRATCH3	;ditto.
	INX	H
	INX	H
	XCHG		;now save the translation table address.
	SHLD	XLATE
	LXI	H,DIRBUF	;put the next 8 bytes here.
	MVI	C,8	;they consist of the directory buffer
	CALL	DE2HL	;pointer, parameter block pointer,
	LHLD	DISKPB	;check and allocation vectors.
	XCHG
	LXI	H,SECTORS	;move parameter block into our ram.
	MVI	C,15	;it is 15 bytes long.
	CALL	DE2HL
	LHLD	DSKSIZE	;check disk size.
	MOV	A,H	;more than 256 blocks on this?
	LXI	H,BIGDISK
	MVI	M,0FFH	;set to samll.
	ORA	A
	JZ	SELECT1
	MVI	M,0	;wrong, set to large.
SELECT1	MVI	A,0FFH	;clear the zero flag.
	ORA	A
	RET
;
;   Routine to home the disk track head and clear pointers.
;
HOMEDRV	CALL	HOME	;home the head.
	XRA	A
	LHLD	SCRATCH2;set our track pointer also.
	MOV	M,A
	INX	H
	MOV	M,A
	LHLD	SCRATCH3;and our sector pointer.
	MOV	M,A
	INX	H
	MOV	M,A
	RET
;
;   Do the actual disk read and check the error return status.
;
DOREAD	CALL	READ
	JMP	IORET
;
;   Do the actual disk write and handle any bios error.
;
DOWRITE	CALL	WRITE
IORET	ORA	A
	RZ		;return unless an error occured.
	LXI	H,BADSCTR;bad read/write on this sector.
	JMP	JUMPHL
;
;   Routine to select the track and sector that the desired
; block number falls in.
;
TRKSEC	LHLD	FILEPOS	;get position of last accessed file
	MVI	C,2	;in directory and compute sector #.
	CALL	SHIFTR	;sector #=file-position/4.
	SHLD	BLKNMBR	;save this as the block number of interest.
	SHLD	CKSUMTBL;what's it doing here too?
;
;   if the sector number has already been set (BLKNMBR), enter
; at this point.
;
TRKSEC1	LXI	H,BLKNMBR
	MOV	C,M	;move sector number into (BC).
	INX	H
	MOV	B,M
	LHLD	SCRATCH3;get current sector number and
	MOV	E,M	;move this into (DE).
	INX	H
	MOV	D,M
	LHLD	SCRATCH2;get current track number.
	MOV	A,M	;and this into (HL).
	INX	H
	MOV	H,M
	MOV	L,A
TRKSEC2	MOV	A,C	;is desired sector before current one?
	SUB	E
	MOV	A,B
	SBB	D
	JNC	TRKSEC3
	PUSH	H	;yes, decrement sectors by one track.
	LHLD	SECTORS	;get sectors per track.
	MOV	A,E
	SUB	L
	MOV	E,A
	MOV	A,D
	SBB	H
	MOV	D,A	;now we have backed up one full track.
	POP	H
	DCX	H	;adjust track counter.
	JMP	TRKSEC2
TRKSEC3	PUSH	H	;desired sector is after current one.
	LHLD	SECTORS	;get sectors per track.
	DAD	D	;bump sector pointer to next track.
	JC	TRKSEC4
	MOV	A,C	;is desired sector now before current one?
	SUB	L
	MOV	A,B
	SBB	H
	JC	TRKSEC4
	XCHG		;not yes, increment track counter
	POP	H	;and continue until it is.
	INX	H
	JMP	TRKSEC3
;
;   here we have determined the track number that contains the
; desired sector.
;
TRKSEC4	POP	H	;get track number (HL).
	PUSH	B
	PUSH	D
	PUSH	H
	XCHG
	LHLD	OFFSET	;adjust for first track offset.
	DAD	D
	MOV	B,H
	MOV	C,L
	CALL	SETTRK	;select this track.
	POP	D	;reset current track pointer.
	LHLD	SCRATCH2
	MOV	M,E
	INX	H
	MOV	M,D
	POP	D
	LHLD	SCRATCH3;reset the first sector on this track.
	MOV	M,E
	INX	H
	MOV	M,D
	POP	B
	MOV	A,C	;now subtract the desired one.
	SUB	E	;to make it relative (1-# sectors/track).
	MOV	C,A
	MOV	A,B
	SBB	D
	MOV	B,A
	LHLD	XLATE	;translate this sector according to this table.
	XCHG
	CALL	SECTRN	;let the bios translate it.
	MOV	C,L
	MOV	B,H
	JMP	SETSEC	;and select it.
;
;   Compute block number from record number (SAVNREC) and
; extent number (SAVEXT).
;
GETBLOCK:LXI	H,BLKSHFT;get logical to physical conversion.
	MOV	C,M	;note that this is base 2 log of ratio.
	LDA	SAVNREC	;get record number.
GETBLK1	ORA	A	;compute (A)=(A)/2^BLKSHFT.
	RAR
	DCR	C
	JNZ	GETBLK1
	MOV	B,A	;save result in (B).
	MVI	A,8
	SUB	M
	MOV	C,A	;compute (C)=8-BLKSHFT.
	LDA	SAVEXT
GETBLK2	DCR	C	;compute (A)=SAVEXT*2^(8-BLKSHFT).
	JZ	GETBLK3
	ORA	A
	RAL
	JMP	GETBLK2
GETBLK3	ADD	B
	RET
;
;   Routine to extract the (BC) block byte from the fcb pointed
; to by (PARAMS). If this is a big-disk, then these are 16 bit
; block numbers, else they are 8 bit numbers.
; Number is returned in (HL).
;
EXTBLK	LHLD	PARAMS	;get fcb address.
	LXI	D,16	;block numbers start 16 bytes into fcb.
	DAD	D
	DAD	B
	LDA	BIGDISK	;are we using a big-disk?
	ORA	A
	JZ	EXTBLK1
	MOV	L,M	;no, extract an 8 bit number from the fcb.
	MVI	H,0
	RET
EXTBLK1	DAD	B	;yes, extract a 16 bit number.
	MOV	E,M
	INX	H
	MOV	D,M
	XCHG		;return in (HL).
	RET
;
;   Compute block number.
;
COMBLK	CALL	GETBLOCK
	MOV	C,A
	MVI	B,0
	CALL	EXTBLK
	SHLD	BLKNMBR
	RET
;
;   Check for a zero block number (unused).
;
CHKBLK	LHLD	BLKNMBR
	MOV	A,L	;is it zero?
	ORA	H
	RET
;
;   Adjust physical block (BLKNMBR) and convert to logical
; sector (LOGSECT). This is the starting sector of this block.
; The actual sector of interest is then added to this and the
; resulting sector number is stored back in (BLKNMBR). This
; will still have to be adjusted for the track number.
;
LOGICAL	LDA	BLKSHFT	;get log2(physical/logical sectors).
	LHLD	BLKNMBR	;get physical sector desired.
LOGICL1	DAD	H	;compute logical sector number.
	DCR	A	;note logical sectors are 128 bytes long.
	JNZ	LOGICL1
	SHLD	LOGSECT	;save logical sector.
	LDA	BLKMASK	;get block mask.
	MOV	C,A
	LDA	SAVNREC	;get next sector to access.
	ANA	C	;extract the relative position within physical block.
	ORA	L	;and add it too logical sector.
	MOV	L,A
	SHLD	BLKNMBR	;and store.
	RET
;
;   Set (HL) to point to extent byte in fcb.
;
SETEXT	LHLD	PARAMS
	LXI	D,12	;it is the twelth byte.
	DAD	D
	RET
;
;   Set (HL) to point to record count byte in fcb and (DE) to
; next record number byte.
;
SETHLDE	LHLD	PARAMS
	LXI	D,15	;record count byte (#15).
	DAD	D
	XCHG
	LXI	H,17	;next record number (#32).
	DAD	D
	RET
;
;   Save current file data from fcb.
;
STRDATA	CALL	SETHLDE
	MOV	A,M	;get and store record count byte.
	STA	SAVNREC
	XCHG
	MOV	A,M	;get and store next record number byte.
	STA	SAVNXT
	CALL	SETEXT	;point to extent byte.
	LDA	EXTMASK	;get extent mask.
	ANA	M
	STA	SAVEXT	;and save extent here.
	RET
;
;   Set the next record to access. If (MODE) is set to 2, then
; the last record byte (SAVNREC) has the correct number to access.
; For sequential access, (MODE) will be equal to 1.
;
SETNREC	CALL	SETHLDE
	LDA	MODE	;get sequential flag (=1).
	CPI	2	;a 2 indicates that no adder is needed.
	JNZ	STNREC1
	XRA	A	;clear adder (random access?).
STNREC1	MOV	C,A
	LDA	SAVNREC	;get last record number.
	ADD	C	;increment record count.
	MOV	M,A	;and set fcb's next record byte.
	XCHG
	LDA	SAVNXT	;get next record byte from storage.
	MOV	M,A	;and put this into fcb as number of records used.
	RET
;
;   Shift (HL) right (C) bits.
;
SHIFTR	INR	C
SHIFTR1	DCR	C
	RZ
	MOV	A,H
	ORA	A
	RAR
	MOV	H,A
	MOV	A,L
	RAR
	MOV	L,A
	JMP	SHIFTR1
;
;   Compute the check-sum for the directory buffer. Return
; integer sum in (A).
;
CHECKSUM:MVI	C,128	;length of buffer.
	LHLD	DIRBUF	;get its location.
	XRA	A	;clear summation byte.
CHKSUM1	ADD	M	;and compute sum ignoring carries.
	INX	H
	DCR	C
	JNZ	CHKSUM1
	RET
;
;   Shift (HL) left (C) bits.
;
SHIFTL	INR	C
SHIFTL1	DCR	C
	RZ
	DAD	H	;shift left 1 bit.
	JMP	SHIFTL1
;
;   Routine to set a bit in a 16 bit value contained in (BC).
; The bit set depends on the current drive selection.
;
SETBIT	PUSH	B	;save 16 bit word.
	LDA	ACTIVE	;get active drive.
	MOV	C,A
	LXI	H,1
	CALL	SHIFTL	;shift bit 0 into place.
	POP	B	;now 'or' this with the original word.
	MOV	A,C
	ORA	L
	MOV	L,A	;low byte done, do high byte.
	MOV	A,B
	ORA	H
	MOV	H,A
	RET
;
;   Extract the write protect status bit for the current drive.
; The result is returned in (A), bit 0.
;
GETWPRT	LHLD	WRTPRT	;get status bytes.
	LDA	ACTIVE	;which drive is current?
	MOV	C,A
	CALL	SHIFTR	;shift status such that bit 0 is the
	MOV	A,L	;one of interest for this drive.
	ANI	01H	;and isolate it.
	RET
;
;   Function to write protect the current disk.
;
WRTPRTD	LXI	H,WRTPRT;point to status word.
	MOV	C,M	;set (BC) equal to the status.
	INX	H
	MOV	B,M
	CALL	SETBIT	;and set this bit according to current drive.
	SHLD	WRTPRT	;then save.
	LHLD	DIRSIZE	;now save directory size limit.
	INX	H	;remember the last one.
	XCHG
	LHLD	SCRATCH1;and store it here.
	MOV	M,E	;put low byte.
	INX	H
	MOV	M,D	;then high byte.
	RET
;
;   Check for a read only file.
;
CHKROFL	CALL	FCB2HL	;set (HL) to file entry in directory buffer.
CKROF1	LXI	D,9	;look at bit 7 of the ninth byte.
	DAD	D
	MOV	A,M
	RAL
	RNC		;return if ok.
	LXI	H,ROFILE;else, print error message and terminate.
	JMP	JUMPHL
;
;   Check the write protect status of the active disk.
;
CHKWPRT	CALL	GETWPRT
	RZ		;return if ok.
	LXI	H,RODISK;else print message and terminate.
	JMP	JUMPHL
;
;   Routine to set (HL) pointing to the proper entry in the
; directory buffer.
;
FCB2HL	LHLD	DIRBUF	;get address of buffer.
	LDA	FCBPOS	;relative position of file.
;
;   Routine to add (A) to (HL).
;
ADDA2HL	ADD	L
	MOV	L,A
	RNC
	INR	H	;take care of any carry.
	RET
;
;   Routine to get the 's2' byte from the fcb supplied in
; the initial parameter specification.
;
GETS2	LHLD	PARAMS	;get address of fcb.
	LXI	D,14	;relative position of 's2'.
	DAD	D
	MOV	A,M	;extract this byte.
	RET
;
;   Clear the 's2' byte in the fcb.
;
CLEARS2	CALL	GETS2	;this sets (HL) pointing to it.
	MVI	M,0	;now clear it.
	RET
;
;   Set bit 7 in the 's2' byte of the fcb.
;
SETS2B7	CALL	GETS2	;get the byte.
	ORI	80H	;and set bit 7.
	MOV	M,A	;then store.
	RET
;
;   Compare (FILEPOS) with (SCRATCH1) and set flags based on
; the difference. This checks to see if there are more file
; names in the directory. We are at (FILEPOS) and there are
; (SCRATCH1) of them to check.
;
MOREFLS	LHLD	FILEPOS	;we are here.
	XCHG
	LHLD	SCRATCH1;and don't go past here.
	MOV	A,E	;compute difference but don't keep.
	SUB	M
	INX	H
	MOV	A,D
	SBB	M	;set carry if no more names.
	RET
;
;   Call this routine to prevent (SCRATCH1) from being greater
; than (FILEPOS).
;
CHKNMBR	CALL	MOREFLS	;SCRATCH1 too big?
	RC
	INX	D	;yes, reset it to (FILEPOS).
	MOV	M,D
	DCX	H
	MOV	M,E
	RET
;
;   Compute (HL)=(DE)-(HL)
;
SUBHL	MOV	A,E	;compute difference.
	SUB	L
	MOV	L,A	;store low byte.
	MOV	A,D
	SBB	H
	MOV	H,A	;and then high byte.
	RET
;
;   Set the directory checksum byte.
;
SETDIR	MVI	C,0FFH
;
;   Routine to set or compare the directory checksum byte. If
; (C)=0ffh, then this will set the checksum byte. Else the byte
; will be checked. If the check fails (the disk has been changed),
; then this disk will be write protected.
;
CHECKDIR:LHLD	CKSUMTBL
	XCHG
	LHLD	ALLOC1
	CALL	SUBHL
	RNC		;ok if (CKSUMTBL) > (ALLOC1), so return.
	PUSH	B
	CALL	CHECKSUM;else compute checksum.
	LHLD	CHKVECT	;get address of checksum table.
	XCHG
	LHLD	CKSUMTBL
	DAD	D	;set (HL) to point to byte for this drive.
	POP	B
	INR	C	;set or check ?
	JZ	CHKDIR1
	CMP	M	;check them.
	RZ		;return if they are the same.
	CALL	MOREFLS	;not the same, do we care?
	RNC
	CALL	WRTPRTD	;yes, mark this as write protected.
	RET
CHKDIR1	MOV	M,A	;just set the byte.
	RET
;
;   Do a write to the directory of the current disk.
;
DIRWRITE:CALL	SETDIR	;set checksum byte.
	CALL	DIRDMA	;set directory dma address.
	MVI	C,1	;tell the bios to actually write.
	CALL	DOWRITE	;then do the write.
	JMP	DEFDMA
;
;   Read from the directory.
;
DIRREAD	CALL	DIRDMA	;set the directory dma address.
	CALL	DOREAD	;and read it.
;
;   Routine to set the dma address to the users choice.
;
DEFDMA	LXI	H,USERDMA;reset the default dma address and return.
	JMP	DIRDMA1
;
;   Routine to set the dma address for directory work.
;
DIRDMA	LXI	H,DIRBUF
;
;   Set the dma address. On entry, (HL) points to
; word containing the desired dma address.
;
DIRDMA1	MOV	C,M
	INX	H
	MOV	B,M	;setup (BC) and go to the bios to set it.
	JMP	SETDMA
;
;   Move the directory buffer into user's dma space.
;
MOVEDIR	LHLD	DIRBUF	;buffer is located here, and
	XCHG
	LHLD	USERDMA; put it here.
	MVI	C,128	;this is its length.
	JMP	DE2HL	;move it now and return.
;
;   Check (FILEPOS) and set the zero flag if it equals 0ffffh.
;
CKFILPOS:LXI	H,FILEPOS
	MOV	A,M
	INX	H
	CMP	M	;are both bytes the same?
	RNZ
	INR	A	;yes, but are they each 0ffh?
	RET
;
;   Set location (FILEPOS) to 0ffffh.
;
STFILPOS:LXI	H,0FFFFH
	SHLD	FILEPOS
	RET
;
;   Move on to the next file position within the current
; directory buffer. If no more exist, set pointer to 0ffffh
; and the calling routine will check for this. Enter with (C)
; equal to 0ffh to cause the checksum byte to be set, else we
; will check this disk and set write protect if checksums are
; not the same (applies only if another directory sector must
; be read).
;
NXENTRY	LHLD	DIRSIZE	;get directory entry size limit.
	XCHG
	LHLD	FILEPOS	;get current count.
	INX	H	;go on to the next one.
	SHLD	FILEPOS
	CALL	SUBHL	;(HL)=(DIRSIZE)-(FILEPOS)
	JNC	NXENT1	;is there more room left?
	JMP	STFILPOS;no. Set this flag and return.
NXENT1	LDA	FILEPOS	;get file position within directory.
	ANI	03H	;only look within this sector (only 4 entries fit).
	MVI	B,5	;convert to relative position (32 bytes each).
NXENT2	ADD	A	;note that this is not efficient code.
	DCR	B	;5 'ADD A's would be better.
	JNZ	NXENT2
	STA	FCBPOS	;save it as position of fcb.
	ORA	A
	RNZ		;return if we are within buffer.
	PUSH	B
	CALL	TRKSEC	;we need the next directory sector.
	CALL	DIRREAD
	POP	B
	JMP	CHECKDIR
;
;   Routine to to get a bit from the disk space allocation
; map. It is returned in (A), bit position 0. On entry to here,
; set (BC) to the block number on the disk to check.
; On return, (D) will contain the original bit position for
; this block number and (HL) will point to the address for it.
;
CKBITMAP:MOV	A,C	;determine bit number of interest.
	ANI	07H	;compute (D)=(E)=(C and 7)+1.
	INR	A
	MOV	E,A	;save particular bit number.
	MOV	D,A
;
;   compute (BC)=(BC)/8.
;
	MOV	A,C
	RRC		;now shift right 3 bits.
	RRC
	RRC
	ANI	1FH	;and clear bits 7,6,5.
	MOV	C,A
	MOV	A,B
	ADD	A	;now shift (B) into bits 7,6,5.
	ADD	A
	ADD	A
	ADD	A
	ADD	A
	ORA	C	;and add in (C).
	MOV	C,A	;ok, (C) ha been completed.
	MOV	A,B	;is there a better way of doing this?
	RRC
	RRC
	RRC
	ANI	1FH
	MOV	B,A	;and now (B) is completed.
;
;   use this as an offset into the disk space allocation
; table.
;
	LHLD	ALOCVECT
	DAD	B
	MOV	A,M	;now get correct byte.
CKBMAP1	RLC		;get correct bit into position 0.
	DCR	E
	JNZ	CKBMAP1
	RET
;
;   Set or clear the bit map such that block number (BC) will be marked
; as used. On entry, if (E)=0 then this bit will be cleared, if it equals
; 1 then it will be set (don't use anyother values).
;
STBITMAP:PUSH	D
	CALL	CKBITMAP;get the byte of interest.
	ANI	0FEH	;clear the affected bit.
	POP	B
	ORA	C	;and now set it acording to (C).
;
;  entry to restore the original bit position and then store
; in table. (A) contains the value, (D) contains the bit
; position (1-8), and (HL) points to the address within the
; space allocation table for this byte.
;
STBMAP1	RRC		;restore original bit position.
	DCR	D
	JNZ	STBMAP1
	MOV	M,A	;and stor byte in table.
	RET
;
;   Set/clear space used bits in allocation map for this file.
; On entry, (C)=1 to set the map and (C)=0 to clear it.
;
SETFILE	CALL	FCB2HL	;get address of fcb
	LXI	D,16
	DAD	D	;get to block number bytes.
	PUSH	B
	MVI	C,17	;check all 17 bytes (max) of table.
SETFL1	POP	D
	DCR	C	;done all bytes yet?
	RZ
	PUSH	D
	LDA	BIGDISK	;check disk size for 16 bit block numbers.
	ORA	A
	JZ	SETFL2
	PUSH	B	;only 8 bit numbers. set (BC) to this one.
	PUSH	H
	MOV	C,M	;get low byte from table, always
	MVI	B,0	;set high byte to zero.
	JMP	SETFL3
SETFL2	DCR	C	;for 16 bit block numbers, adjust counter.
	PUSH	B
	MOV	C,M	;now get both the low and high bytes.
	INX	H
	MOV	B,M
	PUSH	H
SETFL3	MOV	A,C	;block used?
	ORA	B
	JZ	SETFL4
	LHLD	DSKSIZE	;is this block number within the
	MOV	A,L	;space on the disk?
	SUB	C
	MOV	A,H
	SBB	B
	CNC	STBITMAP;yes, set the proper bit.
SETFL4	POP	H	;point to next block number in fcb.
	INX	H
	POP	B
	JMP	SETFL1
;
;   Construct the space used allocation bit map for the active
; drive. If a file name starts with '$' and it is under the
; current user number, then (STATUS) is set to minus 1. Otherwise
; it is not set at all.
;
BITMAP	LHLD	DSKSIZE	;compute size of allocation table.
	MVI	C,3
	CALL	SHIFTR	;(HL)=(HL)/8.
	INX	H	;at lease 1 byte.
	MOV	B,H
	MOV	C,L	;set (BC) to the allocation table length.
;
;   Initialize the bitmap for this drive. Right now, the first
; two bytes are specified by the disk parameter block. However
; a patch could be entered here if it were necessary to setup
; this table in a special mannor. For example, the bios could
; determine locations of 'bad blocks' and set them as already
; 'used' in the map.
;
	LHLD	ALOCVECT;now zero out the table now.
BITMAP1	MVI	M,0
	INX	H
	DCX	B
	MOV	A,B
	ORA	C
	JNZ	BITMAP1
	LHLD	ALLOC0	;get initial space used by directory.
	XCHG
	LHLD	ALOCVECT;and put this into map.
	MOV	M,E
	INX	H
	MOV	M,D
;
;   End of initialization portion.
;
	CALL	HOMEDRV	;now home the drive.
	LHLD	SCRATCH1
	MVI	M,3	;force next directory request to read
	INX	H	;in a sector.
	MVI	M,0
	CALL	STFILPOS;clear initial file position also.
BITMAP2	MVI	C,0FFH	;read next file name in directory
	CALL	NXENTRY	;and set checksum byte.
	CALL	CKFILPOS;is there another file?
	RZ
	CALL	FCB2HL	;yes, get its address.
	MVI	A,0E5H
	CMP	M	;empty file entry?
	JZ	BITMAP2
	LDA	USERNO	;no, correct user number?
	CMP	M
	JNZ	BITMAP3
	INX	H
	MOV	A,M	;yes, does name start with a '$'?
	SUI	'$'
	JNZ	BITMAP3
	DCR	A	;yes, set atatus to minus one.
	STA	STATUS
BITMAP3	MVI	C,1	;now set this file's space as used in bit map.
	CALL	SETFILE
	CALL	CHKNMBR	;keep (SCRATCH1) in bounds.
	JMP	BITMAP2
;
;   Set the status (STATUS) and return.
;
STSTATUS:LDA	FNDSTAT
	JMP	SETSTAT
;
;   Check extents in (A) and (C). Set the zero flag if they
; are the same. The number of 16k chunks of disk space that
; the directory extent covers is expressad is (EXTMASK+1).
; No registers are modified.
;
SAMEXT	PUSH	B
	PUSH	PSW
	LDA	EXTMASK	;get extent mask and use it to
	CMA		;to compare both extent numbers.
	MOV	B,A	;save resulting mask here.
	MOV	A,C	;mask first extent and save in (C).
	ANA	B
	MOV	C,A
	POP	PSW	;now mask second extent and compare
	ANA	B	;with the first one.
	SUB	C
	ANI	1FH	;(* only check buts 0-4 *)
	POP	B	;the zero flag is set if they are the same.
	RET		;restore (BC) and return.
;
;   Search for the first occurence of a file name. On entry,
; register (C) should contain the number of bytes of the fcb
; that must match.
;
FINDFST	MVI	A,0FFH
	STA	FNDSTAT
	LXI	H,COUNTER;save character count.
	MOV	M,C
	LHLD	PARAMS	;get filename to match.
	SHLD	SAVEFCB	;and save.
	CALL	STFILPOS;clear initial file position (set to 0ffffh).
	CALL	HOMEDRV	;home the drive.
;
;   Entry to locate the next occurence of a filename within the
; directory. The disk is not expected to have been changed. If
; it was, then it will be write protected.
;
FINDNXT	MVI	C,0	;write protect the disk if changed.
	CALL	NXENTRY	;get next filename entry in directory.
	CALL	CKFILPOS;is file position = 0ffffh?
	JZ	FNDNXT6	;yes, exit now then.
	LHLD	SAVEFCB	;set (DE) pointing to filename to match.
	XCHG
	LDAX	D
	CPI	0E5H	;empty directory entry?
	JZ	FNDNXT1	;(* are we trying to reserect erased entries? *)
	PUSH	D
	CALL	MOREFLS	;more files in directory?
	POP	D
	JNC	FNDNXT6	;no more. Exit now.
FNDNXT1	CALL	FCB2HL	;get address of this fcb in directory.
	LDA	COUNTER	;get number of bytes (characters) to check.
	MOV	C,A
	MVI	B,0	;initialize byte position counter.
FNDNXT2	MOV	A,C	;are we done with the compare?
	ORA	A
	JZ	FNDNXT5
	LDAX	D	;no, check next byte.
	CPI	'?'	;don't care about this character?
	JZ	FNDNXT4
	MOV	A,B	;get bytes position in fcb.
	CPI	13	;don't care about the thirteenth byte either.
	JZ	FNDNXT4
	CPI	12	;extent byte?
	LDAX	D
	JZ	FNDNXT3
	SUB	M	;otherwise compare characters.
	ANI	7FH
	JNZ	FINDNXT	;not the same, check next entry.
	JMP	FNDNXT4	;so far so good, keep checking.
FNDNXT3	PUSH	B	;check the extent byte here.
	MOV	C,M
	CALL	SAMEXT
	POP	B
	JNZ	FINDNXT	;not the same, look some more.
;
;   So far the names compare. Bump pointers to the next byte
; and continue until all (C) characters have been checked.
;
FNDNXT4	INX	D	;bump pointers.
	INX	H
	INR	B
	DCR	C	;adjust character counter.
	JMP	FNDNXT2
FNDNXT5	LDA	FILEPOS	;return the position of this entry.
	ANI	03H
	STA	STATUS
	LXI	H,FNDSTAT
	MOV	A,M
	RAL
	RNC
	XRA	A
	MOV	M,A
	RET
;
;   Filename was not found. Set appropriate status.
;
FNDNXT6	CALL	STFILPOS;set (FILEPOS) to 0ffffh.
	MVI	A,0FFH	;say not located.
	JMP	SETSTAT
;
;   Erase files from the directory. Only the first byte of the
; fcb will be affected. It is set to (E5).
;
ERAFILE	CALL	CHKWPRT	;is disk write protected?
	MVI	C,12	;only compare file names.
	CALL	FINDFST	;get first file name.
ERAFIL1	CALL	CKFILPOS;any found?
	RZ		;nope, we must be done.
	CALL	CHKROFL	;is file read only?
	CALL	FCB2HL	;nope, get address of fcb and
	MVI	M,0E5H	;set first byte to 'empty'.
	MVI	C,0	;clear the space from the bit map.
	CALL	SETFILE
	CALL	DIRWRITE;now write the directory sector back out.
	CALL	FINDNXT	;find the next file name.
	JMP	ERAFIL1	;and repeat process.
;
;   Look through the space allocation map (bit map) for the
; next available block. Start searching at block number (BC-1).
; The search procedure is to look for an empty block that is
; before the starting block. If not empty, look at a later
; block number. In this way, we return the closest empty block
; on either side of the 'target' block number. This will speed
; access on random devices. For serial devices, this should be
; changed to look in the forward direction first and then start
; at the front and search some more.
;
;   On return, (DE)= block number that is empty and (HL) =0
; if no empry block was found.
;
FNDSPACE:MOV	D,B	;set (DE) as the block that is checked.
	MOV	E,C
;
;   Look before target block. Registers (BC) are used as the lower
; pointer and (DE) as the upper pointer.
;
FNDSPA1	MOV	A,C	;is block 0 specified?
	ORA	B
	JZ	FNDSPA2
	DCX	B	;nope, check previous block.
	PUSH	D
	PUSH	B
	CALL	CKBITMAP
	RAR		;is this block empty?
	JNC	FNDSPA3	;yes. use this.
;
;   Note that the above logic gets the first block that it finds
; that is empty. Thus a file could be written 'backward' making
; it very slow to access. This could be changed to look for the
; first empty block and then continue until the start of this
; empty space is located and then used that starting block.
; This should help speed up access to some files especially on
; a well used disk with lots of fairly small 'holes'.
;
	POP	B	;nope, check some more.
	POP	D
;
;   Now look after target block.
;
FNDSPA2	LHLD	DSKSIZE	;is block (DE) within disk limits?
	MOV	A,E
	SUB	L
	MOV	A,D
	SBB	H
	JNC	FNDSPA4
	INX	D	;yes, move on to next one.
	PUSH	B
	PUSH	D
	MOV	B,D
	MOV	C,E
	CALL	CKBITMAP;check it.
	RAR		;empty?
	JNC	FNDSPA3
	POP	D	;nope, continue searching.
	POP	B
	JMP	FNDSPA1
;
;   Empty block found. Set it as used and return with (HL)
; pointing to it (true?).
;
FNDSPA3	RAL		;reset byte.
	INR	A	;and set bit 0.
	CALL	STBMAP1	;update bit map.
	POP	H	;set return registers.
	POP	D
	RET
;
;   Free block was not found. If (BC) is not zero, then we have
; not checked all of the disk space.
;
FNDSPA4	MOV	A,C
	ORA	B
	JNZ	FNDSPA1
	LXI	H,0	;set 'not found' status.
	RET
;
;   Move a complete fcb entry into the directory and write it.
;
FCBSET	MVI	C,0
	MVI	E,32	;length of each entry.
;
;   Move (E) bytes from the fcb pointed to by (PARAMS) into
; fcb in directory starting at relative byte (C). This updated
; directory buffer is then written to the disk.
;
UPDATE	PUSH	D
	MVI	B,0	;set (BC) to relative byte position.
	LHLD	PARAMS	;get address of fcb.
	DAD	B	;compute starting byte.
	XCHG
	CALL	FCB2HL	;get address of fcb to update in directory.
	POP	B	;set (C) to number of bytes to change.
	CALL	DE2HL
UPDATE1	CALL	TRKSEC	;determine the track and sector affected.
	JMP	DIRWRITE	;then write this sector out.
;
;   Routine to change the name of all files on the disk with a
; specified name. The fcb contains the current name as the
; first 12 characters and the new name 16 bytes into the fcb.
;
CHGNAMES:CALL	CHKWPRT	;check for a write protected disk.
	MVI	C,12	;match first 12 bytes of fcb only.
	CALL	FINDFST	;get first name.
	LHLD	PARAMS	;get address of fcb.
	MOV	A,M	;get user number.
	LXI	D,16	;move over to desired name.
	DAD	D
	MOV	M,A	;keep same user number.
CHGNAM1	CALL	CKFILPOS;any matching file found?
	RZ		;no, we must be done.
	CALL	CHKROFL	;check for read only file.
	MVI	C,16	;start 16 bytes into fcb.
	MVI	E,12	;and update the first 12 bytes of directory.
	CALL	UPDATE
	CALL	FINDNXT	;get te next file name.
	JMP	CHGNAM1	;and continue.
;
;   Update a files attributes. The procedure is to search for
; every file with the same name as shown in fcb (ignoring bit 7)
; and then to update it (which includes bit 7). No other changes
; are made.
;
SAVEATTR:MVI	C,12	;match first 12 bytes.
	CALL	FINDFST	;look for first filename.
SAVATR1	CALL	CKFILPOS;was one found?
	RZ		;nope, we must be done.
	MVI	C,0	;yes, update the first 12 bytes now.
	MVI	E,12
	CALL	UPDATE	;update filename and write directory.
	CALL	FINDNXT	;and get the next file.
	JMP	SAVATR1	;then continue until done.
;
;  Open a file (name specified in fcb).
;
OPENIT	MVI	C,15	;compare the first 15 bytes.
	CALL	FINDFST	;get the first one in directory.
	CALL	CKFILPOS;any at all?
	RZ
OPENIT1	CALL	SETEXT	;point to extent byte within users fcb.
	MOV	A,M	;and get it.
	PUSH	PSW	;save it and address.
	PUSH	H
	CALL	FCB2HL	;point to fcb in directory.
	XCHG
	LHLD	PARAMS	;this is the users copy.
	MVI	C,32	;move it into users space.
	PUSH	D
	CALL	DE2HL
	CALL	SETS2B7	;set bit 7 in 's2' byte (unmodified).
	POP	D	;now get the extent byte from this fcb.
	LXI	H,12
	DAD	D
	MOV	C,M	;into (C).
	LXI	H,15	;now get the record count byte into (B).
	DAD	D
	MOV	B,M
	POP	H	;keep the same extent as the user had originally.
	POP	PSW
	MOV	M,A
	MOV	A,C	;is it the same as in the directory fcb?
	CMP	M
	MOV	A,B	;if yes, then use the same record count.
	JZ	OPENIT2
	MVI	A,0	;if the user specified an extent greater than
	JC	OPENIT2	;the one in the directory, then set record count to 0.
	MVI	A,128	;otherwise set to maximum.
OPENIT2	LHLD	PARAMS	;set record count in users fcb to (A).
	LXI	D,15
	DAD	D	;compute relative position.
	MOV	M,A	;and set the record count.
	RET
;
;   Move two bytes from (DE) to (HL) if (and only if) (HL)
; point to a zero value (16 bit).
;   Return with zero flag set it (DE) was moved. Registers (DE)
; and (HL) are not changed. However (A) is.
;
MOVEWORD:MOV	A,M	;check for a zero word.
	INX	H
	ORA	M	;both bytes zero?
	DCX	H
	RNZ		;nope, just return.
	LDAX	D	;yes, move two bytes from (DE) into
	MOV	M,A	;this zero space.
	INX	D
	INX	H
	LDAX	D
	MOV	M,A
	DCX	D	;don't disturb these registers.
	DCX	H
	RET
;
;   Get here to close a file specified by (fcb).
;
CLOSEIT	XRA	A	;clear status and file position bytes.
	STA	STATUS
	STA	FILEPOS
	STA	FILEPOS+1
	CALL	GETWPRT	;get write protect bit for this drive.
	RNZ		;just return if it is set.
	CALL	GETS2	;else get the 's2' byte.
	ANI	80H	;and look at bit 7 (file unmodified?).
	RNZ		;just return if set.
	MVI	C,15	;else look up this file in directory.
	CALL	FINDFST
	CALL	CKFILPOS;was it found?
	RZ		;just return if not.
	LXI	B,16	;set (HL) pointing to records used section.
	CALL	FCB2HL
	DAD	B
	XCHG
	LHLD	PARAMS	;do the same for users specified fcb.
	DAD	B
	MVI	C,16	;this many bytes are present in this extent.
CLOSEIT1:LDA	BIGDISK	;8 or 16 bit record numbers?
	ORA	A
	JZ	CLOSEIT4
	MOV	A,M	;just 8 bit. Get one from users fcb.
	ORA	A
	LDAX	D	;now get one from directory fcb.
	JNZ	CLOSEIT2
	MOV	M,A	;users byte was zero. Update from directory.
CLOSEIT2:ORA	A
	JNZ	CLOSEIT3
	MOV	A,M	;directories byte was zero, update from users fcb.
	STAX	D
CLOSEIT3:CMP	M	;if neither one of these bytes were zero,
	JNZ	CLOSEIT7	;then close error if they are not the same.
	JMP	CLOSEIT5	;ok so far, get to next byte in fcbs.
CLOSEIT4:CALL	MOVEWORD;update users fcb if it is zero.
	XCHG
	CALL	MOVEWORD;update directories fcb if it is zero.
	XCHG
	LDAX	D	;if these two values are no different,
	CMP	M	;then a close error occured.
	JNZ	CLOSEIT7
	INX	D	;check second byte.
	INX	H
	LDAX	D
	CMP	M
	JNZ	CLOSEIT7
	DCR	C	;remember 16 bit values.
CLOSEIT5:INX	D	;bump to next item in table.
	INX	H
	DCR	C	;there are 16 entries only.
	JNZ	CLOSEIT1;continue if more to do.
	LXI	B,0FFECH;backup 20 places (extent byte).
	DAD	B
	XCHG
	DAD	B
	LDAX	D
	CMP	M	;directory's extent already greater than the
	JC	CLOSEIT6	;users extent?
	MOV	M,A	;no, update directory extent.
	LXI	B,3	;and update the record count byte in
	DAD	B	;directories fcb.
	XCHG
	DAD	B
	MOV	A,M	;get from user.
	STAX	D	;and put in directory.
CLOSEIT6:MVI	A,0FFH	;set 'was open and is now closed' byte.
	STA	CLOSEFLG
	JMP	UPDATE1	;update the directory now.
CLOSEIT7:LXI	H,STATUS;set return status and then return.
	DCR	M
	RET
;
;   Routine to get the next empty space in the directory. It
; will then be cleared for use.
;
GETEMPTY:CALL	CHKWPRT	;make sure disk is not write protected.
	LHLD	PARAMS	;save current parameters (fcb).
	PUSH	H
	LXI	H,EMPTYFCB;use special one for empty space.
	SHLD	PARAMS
	MVI	C,1	;search for first empty spot in directory.
	CALL	FINDFST	;(* only check first byte *)
	CALL	CKFILPOS;none?
	POP	H
	SHLD	PARAMS	;restore original fcb address.
	RZ		;return if no more space.
	XCHG
	LXI	H,15	;point to number of records for this file.
	DAD	D
	MVI	C,17	;and clear all of this space.
	XRA	A
GETMT1	MOV	M,A
	INX	H
	DCR	C
	JNZ	GETMT1
	LXI	H,13	;clear the 's1' byte also.
	DAD	D
	MOV	M,A
	CALL	CHKNMBR	;keep (SCRATCH1) within bounds.
	CALL	FCBSET	;write out this fcb entry to directory.
	JMP	SETS2B7	;set 's2' byte bit 7 (unmodified at present).
;
;   Routine to close the current extent and open the next one
; for reading.
;
GETNEXT	XRA	A
	STA	CLOSEFLG;clear close flag.
	CALL	CLOSEIT	;close this extent.
	CALL	CKFILPOS
	RZ		;not there???
	LHLD	PARAMS	;get extent byte.
	LXI	B,12
	DAD	B
	MOV	A,M	;and increment it.
	INR	A
	ANI	1FH	;keep within range 0-31.
	MOV	M,A
	JZ	GTNEXT1	;overflow?
	MOV	B,A	;mask extent byte.
	LDA	EXTMASK
	ANA	B
	LXI	H,CLOSEFLG;check close flag (0ffh is ok).
	ANA	M
	JZ	GTNEXT2	;if zero, we must read in next extent.
	JMP	GTNEXT3	;else, it is already in memory.
GTNEXT1	LXI	B,2	;Point to the 's2' byte.
	DAD	B
	INR	M	;and bump it.
	MOV	A,M	;too many extents?
	ANI	0FH
	JZ	GTNEXT5	;yes, set error code.
;
;   Get here to open the next extent.
;
GTNEXT2	MVI	C,15	;set to check first 15 bytes of fcb.
	CALL	FINDFST	;find the first one.
	CALL	CKFILPOS;none available?
	JNZ	GTNEXT3
	LDA	RDWRTFLG;no extent present. Can we open an empty one?
	INR	A	;0ffh means reading (so not possible).
	JZ	GTNEXT5	;or an error.
	CALL	GETEMPTY;we are writing, get an empty entry.
	CALL	CKFILPOS;none?
	JZ	GTNEXT5	;error if true.
	JMP	GTNEXT4	;else we are almost done.
GTNEXT3	CALL	OPENIT1	;open this extent.
GTNEXT4	CALL	STRDATA	;move in updated data (rec #, extent #, etc.)
	XRA	A	;clear status and return.
	JMP	SETSTAT
;
;   Error in extending the file. Too many extents were needed
; or not enough space on the disk.
;
GTNEXT5	CALL	IOERR1	;set error code, clear bit 7 of 's2'
	JMP	SETS2B7	;so this is not written on a close.
;
;   Read a sequential file.
;
RDSEQ	MVI	A,1	;set sequential access mode.
	STA	MODE
RDSEQ1	MVI	A,0FFH	;don't allow reading unwritten space.
	STA	RDWRTFLG
	CALL	STRDATA	;put rec# and ext# into fcb.
	LDA	SAVNREC	;get next record to read.
	LXI	H,SAVNXT;get number of records in extent.
	CMP	M	;within this extent?
	JC	RDSEQ2
	CPI	128	;no. Is this extent fully used?
	JNZ	RDSEQ3	;no. End-of-file.
	CALL	GETNEXT	;yes, open the next one.
	XRA	A	;reset next record to read.
	STA	SAVNREC
	LDA	STATUS	;check on open, successful?
	ORA	A
	JNZ	RDSEQ3	;no, error.
RDSEQ2	CALL	COMBLK	;ok. compute block number to read.
	CALL	CHKBLK	;check it. Within bounds?
	JZ	RDSEQ3	;no, error.
	CALL	LOGICAL	;convert (BLKNMBR) to logical sector (128 byte).
	CALL	TRKSEC1	;set the track and sector for this block #.
	CALL	DOREAD	;and read it.
	JMP	SETNREC	;and set the next record to be accessed.
;
;   Read error occured. Set status and return.
;
RDSEQ3	JMP	IOERR1
;
;   Write the next sequential record.
;
WTSEQ	MVI	A,1	;set sequential access mode.
	STA	MODE
WTSEQ1	MVI	A,0	;allow an addition empty extent to be opened.
	STA	RDWRTFLG
	CALL	CHKWPRT	;check write protect status.
	LHLD	PARAMS
	CALL	CKROF1	;check for read only file, (HL) already set to fcb.
	CALL	STRDATA	;put updated data into fcb.
	LDA	SAVNREC	;get record number to write.
	CPI	128	;within range?
	JNC	IOERR1	;no, error(?).
	CALL	COMBLK	;compute block number.
	CALL	CHKBLK	;check number.
	MVI	C,0	;is there one to write to?
	JNZ	WTSEQ6	;yes, go do it.
	CALL	GETBLOCK;get next block number within fcb to use.
	STA	RELBLOCK;and save.
	LXI	B,0	;start looking for space from the start
	ORA	A	;if none allocated as yet.
	JZ	WTSEQ2
	MOV	C,A	;extract previous block number from fcb
	DCX	B	;so we can be closest to it.
	CALL	EXTBLK
	MOV	B,H
	MOV	C,L
WTSEQ2	CALL	FNDSPACE;find the next empty block nearest number (BC).
	MOV	A,L	;check for a zero number.
	ORA	H
	JNZ	WTSEQ3
	MVI	A,2	;no more space?
	JMP	SETSTAT
WTSEQ3	SHLD	BLKNMBR	;save block number to access.
	XCHG		;put block number into (DE).
	LHLD	PARAMS	;now we must update the fcb for this
	LXI	B,16	;newly allocated block.
	DAD	B
	LDA	BIGDISK	;8 or 16 bit block numbers?
	ORA	A
	LDA	RELBLOCK	;(* update this entry *)
	JZ	WTSEQ4	;zero means 16 bit ones.
	CALL	ADDA2HL	;(HL)=(HL)+(A)
	MOV	M,E	;store new block number.
	JMP	WTSEQ5
WTSEQ4	MOV	C,A	;compute spot in this 16 bit table.
	MVI	B,0
	DAD	B
	DAD	B
	MOV	M,E	;stuff block number (DE) there.
	INX	H
	MOV	M,D
WTSEQ5	MVI	C,2	;set (C) to indicate writing to un-used disk space.
WTSEQ6	LDA	STATUS	;are we ok so far?
	ORA	A
	RNZ
	PUSH	B	;yes, save write flag for bios (register C).
	CALL	LOGICAL	;convert (BLKNMBR) over to loical sectors.
	LDA	MODE	;get access mode flag (1=sequential,
	DCR	A	;0=random, 2=special?).
	DCR	A
	JNZ	WTSEQ9
;
;   Special random i/o from function #40. Maybe for M/PM, but the
; current block, if it has not been written to, will be zeroed
; out and then written (reason?).
;
	POP	B
	PUSH	B
	MOV	A,C	;get write status flag (2=writing unused space).
	DCR	A
	DCR	A
	JNZ	WTSEQ9
	PUSH	H
	LHLD	DIRBUF	;zero out the directory buffer.
	MOV	D,A	;note that (A) is zero here.
WTSEQ7	MOV	M,A
	INX	H
	INR	D	;do 128 bytes.
	JP	WTSEQ7
	CALL	DIRDMA	;tell the bios the dma address for directory access.
	LHLD	LOGSECT	;get sector that starts current block.
	MVI	C,2	;set 'writing to unused space' flag.
WTSEQ8	SHLD	BLKNMBR	;save sector to write.
	PUSH	B
	CALL	TRKSEC1	;determine its track and sector numbers.
	POP	B
	CALL	DOWRITE	;now write out 128 bytes of zeros.
	LHLD	BLKNMBR	;get sector number.
	MVI	C,0	;set normal write flag.
	LDA	BLKMASK	;determine if we have written the entire
	MOV	B,A	;physical block.
	ANA	L
	CMP	B
	INX	H	;prepare for the next one.
	JNZ	WTSEQ8	;continue until (BLKMASK+1) sectors written.
	POP	H	;reset next sector number.
	SHLD	BLKNMBR
	CALL	DEFDMA	;and reset dma address.
;
;   Normal disk write. Set the desired track and sector then
; do the actual write.
;
WTSEQ9	CALL	TRKSEC1	;determine track and sector for this write.
	POP	B	;get write status flag.
	PUSH	B
	CALL	DOWRITE	;and write this out.
	POP	B
	LDA	SAVNREC	;get number of records in file.
	LXI	H,SAVNXT;get last record written.
	CMP	M
	JC	WTSEQ10
	MOV	M,A	;we have to update record count.
	INR	M
	MVI	C,2
;
;*   This area has been patched to correct disk update problem
;* when using blocking and de-blocking in the BIOS.
;
WTSEQ10	NOP		;was 'dcr c'
	NOP		;was 'dcr c'
	LXI	H,0	;was 'jnz wtseq99'
;
; *   End of patch.
;
	PUSH	PSW
	CALL	GETS2	;set 'extent written to' flag.
	ANI	7FH	;(* clear bit 7 *)
	MOV	M,A
	POP	PSW	;get record count for this extent.
WTSEQ99	CPI	127	;is it full?
	JNZ	WTSEQ12
	LDA	MODE	;yes, are we in sequential mode?
	CPI	1
	JNZ	WTSEQ12
	CALL	SETNREC	;yes, set next record number.
	CALL	GETNEXT	;and get next empty space in directory.
	LXI	H,STATUS;ok?
	MOV	A,M
	ORA	A
	JNZ	WTSEQ11
	DCR	A	;yes, set record count to -1.
	STA	SAVNREC
WTSEQ11	MVI	M,0	;clear status.
WTSEQ12	JMP	SETNREC	;set next record to access.
;
;   For random i/o, set the fcb for the desired record number
; based on the 'r0,r1,r2' bytes. These bytes in the fcb are
; used as follows:
;
;       fcb+35            fcb+34            fcb+33
;  |     'r-2'      |      'r-1'      |      'r-0'     |
;  |7             0 | 7             0 | 7             0|
;  |0 0 0 0 0 0 0 0 | 0 0 0 0 0 0 0 0 | 0 0 0 0 0 0 0 0|
;  |    overflow   | | extra |  extent   |   record #  |
;  | ______________| |_extent|__number___|_____________|
;                     also 's2'
;
;   On entry, register (C) contains 0ffh if this is a read
; and thus we can not access unwritten disk space. Otherwise,
; another extent will be opened (for writing) if required.
;
POSITION:XRA	A	;set random i/o flag.
	STA	MODE
;
;   Special entry (function #40). M/PM ?
;
POSITN1	PUSH	B	;save read/write flag.
	LHLD	PARAMS	;get address of fcb.
	XCHG
	LXI	H,33	;now get byte 'r0'.
	DAD	D
	MOV	A,M
	ANI	7FH	;keep bits 0-6 for the record number to access.
	PUSH	PSW
	MOV	A,M	;now get bit 7 of 'r0' and bits 0-3 of 'r1'.
	RAL
	INX	H
	MOV	A,M
	RAL
	ANI	1FH	;and save this in bits 0-4 of (C).
	MOV	C,A	;this is the extent byte.
	MOV	A,M	;now get the extra extent byte.
	RAR
	RAR
	RAR
	RAR
	ANI	0FH
	MOV	B,A	;and save it in (B).
	POP	PSW	;get record number back to (A).
	INX	H	;check overflow byte 'r2'.
	MOV	L,M
	INR	L
	DCR	L
	MVI	L,6	;prepare for error.
	JNZ	POSITN5	;out of disk space error.
	LXI	H,32	;store record number into fcb.
	DAD	D
	MOV	M,A
	LXI	H,12	;and now check the extent byte.
	DAD	D
	MOV	A,C
	SUB	M	;same extent as before?
	JNZ	POSITN2
	LXI	H,14	;yes, check extra extent byte 's2' also.
	DAD	D
	MOV	A,B
	SUB	M
	ANI	7FH
	JZ	POSITN3;same, we are almost done then.
;
;  Get here when another extent is required.
;
POSITN2	PUSH	B
	PUSH	D
	CALL	CLOSEIT	;close current extent.
	POP	D
	POP	B
	MVI	L,3	;prepare for error.
	LDA	STATUS
	INR	A
	JZ	POSITN4	;close error.
	LXI	H,12	;put desired extent into fcb now.
	DAD	D
	MOV	M,C
	LXI	H,14	;and store extra extent byte 's2'.
	DAD	D
	MOV	M,B
	CALL	OPENIT	;try and get this extent.
	LDA	STATUS	;was it there?
	INR	A
	JNZ	POSITN3
	POP	B	;no. can we create a new one (writing?).
	PUSH	B
	MVI	L,4	;prepare for error.
	INR	C
	JZ	POSITN4	;nope, reading unwritten space error.
	CALL	GETEMPTY;yes we can, try to find space.
	MVI	L,5	;prepare for error.
	LDA	STATUS
	INR	A
	JZ	POSITN4	;out of space?
;
;   Normal return location. Clear error code and return.
;
POSITN3	POP	B	;restore stack.
	XRA	A	;and clear error code byte.
	JMP	SETSTAT
;
;   Error. Set the 's2' byte to indicate this (why?).
;
POSITN4	PUSH	H
	CALL	GETS2
	MVI	M,0C0H
	POP	H
;
;   Return with error code (presently in L).
;
POSITN5	POP	B
	MOV	A,L	;get error code.
	STA	STATUS
	JMP	SETS2B7
;
;   Read a random record.
;
READRAN	MVI	C,0FFH	;set 'read' status.
	CALL	POSITION;position the file to proper record.
	CZ	RDSEQ1	;and read it as usual (if no errors).
	RET
;
;   Write to a random record.
;
WRITERAN:MVI	C,0	;set 'writing' flag.
	CALL	POSITION;position the file to proper record.
	CZ	WTSEQ1	;and write as usual (if no errors).
	RET
;
;   Compute the random record number. Enter with (HL) pointing
; to a fcb an (DE) contains a relative location of a record
; number. On exit, (C) contains the 'r0' byte, (B) the 'r1'
; byte, and (A) the 'r2' byte.
;
;   On return, the zero flag is set if the record is within
; bounds. Otherwise, an overflow occured.
;
COMPRAND:XCHG		;save fcb pointer in (DE).
	DAD	D	;compute relative position of record #.
	MOV	C,M	;get record number into (BC).
	MVI	B,0
	LXI	H,12	;now get extent.
	DAD	D
	MOV	A,M	;compute (BC)=(record #)+(extent)*128.
	RRC		;move lower bit into bit 7.
	ANI	80H	;and ignore all other bits.
	ADD	C	;add to our record number.
	MOV	C,A
	MVI	A,0	;take care of any carry.
	ADC	B
	MOV	B,A
	MOV	A,M	;now get the upper bits of extent into
	RRC		;bit positions 0-3.
	ANI	0FH	;and ignore all others.
	ADD	B	;add this in to 'r1' byte.
	MOV	B,A
	LXI	H,14	;get the 's2' byte (extra extent).
	DAD	D
	MOV	A,M
	ADD	A	;and shift it left 4 bits (bits 4-7).
	ADD	A
	ADD	A
	ADD	A
	PUSH	PSW	;save carry flag (bit 0 of flag byte).
	ADD	B	;now add extra extent into 'r1'.
	MOV	B,A
	PUSH	PSW	;and save carry (overflow byte 'r2').
	POP	H	;bit 0 of (L) is the overflow indicator.
	MOV	A,L
	POP	H	;and same for first carry flag.
	ORA	L	;either one of these set?
	ANI	01H	;only check the carry flags.
	RET
;
;   Routine to setup the fcb (bytes 'r0', 'r1', 'r2') to
; reflect the last record used for a random (or other) file.
; This reads the directory and looks at all extents computing
; the largerst record number for each and keeping the maximum
; value only. Then 'r0', 'r1', and 'r2' will reflect this
; maximum record number. This is used to compute the space used
; by a random file.
;
RANSIZE	MVI	C,12	;look thru directory for first entry with
	CALL	FINDFST	;this name.
	LHLD	PARAMS	;zero out the 'r0, r1, r2' bytes.
	LXI	D,33
	DAD	D
	PUSH	H
	MOV	M,D	;note that (D)=0.
	INX	H
	MOV	M,D
	INX	H
	MOV	M,D
RANSIZ1	CALL	CKFILPOS;is there an extent to process?
	JZ	RANSIZ3	;no, we are done.
	CALL	FCB2HL	;set (HL) pointing to proper fcb in dir.
	LXI	D,15	;point to last record in extent.
	CALL	COMPRAND;and compute random parameters.
	POP	H
	PUSH	H	;now check these values against those
	MOV	E,A	;already in fcb.
	MOV	A,C	;the carry flag will be set if those
	SUB	M	;in the fcb represent a larger size than
	INX	H	;this extent does.
	MOV	A,B
	SBB	M
	INX	H
	MOV	A,E
	SBB	M
	JC	RANSIZ2
	MOV	M,E	;we found a larger (in size) extent.
	DCX	H	;stuff these values into fcb.
	MOV	M,B
	DCX	H
	MOV	M,C
RANSIZ2	CALL	FINDNXT	;now get the next extent.
	JMP	RANSIZ1	;continue til all done.
RANSIZ3	POP	H	;we are done, restore the stack and
	RET		;return.
;
;   Function to return the random record position of a given
; file which has been read in sequential mode up to now.
;
SETRAN	LHLD	PARAMS	;point to fcb.
	LXI	D,32	;and to last used record.
	CALL	COMPRAND;compute random position.
	LXI	H,33	;now stuff these values into fcb.
	DAD	D
	MOV	M,C	;move 'r0'.
	INX	H
	MOV	M,B	;and 'r1'.
	INX	H
	MOV	M,A	;and lastly 'r2'.
	RET
;
;   This routine select the drive specified in (ACTIVE) and
; update the login vector and bitmap table if this drive was
; not already active.
;
LOGINDRV:LHLD	LOGIN	;get the login vector.
	LDA	ACTIVE	;get the default drive.
	MOV	C,A
	CALL	SHIFTR	;position active bit for this drive
	PUSH	H	;into bit 0.
	XCHG
	CALL	SELECT	;select this drive.
	POP	H
	CZ	SLCTERR	;valid drive?
	MOV	A,L	;is this a newly activated drive?
	RAR
	RC
	LHLD	LOGIN	;yes, update the login vector.
	MOV	C,L
	MOV	B,H
	CALL	SETBIT
	SHLD	LOGIN	;and save.
	JMP	BITMAP	;now update the bitmap.
;
;   Function to set the active disk number.
;
SETDSK	LDA	EPARAM	;get parameter passed and see if this
	LXI	H,ACTIVE;represents a change in drives.
	CMP	M
	RZ
	MOV	M,A	;yes it does, log it in.
	JMP	LOGINDRV
;
;   This is the 'auto disk select' routine. The firsst byte
; of the fcb is examined for a drive specification. If non
; zero then the drive will be selected and loged in.
;
AUTOSEL	MVI	A,0FFH	;say 'auto-select activated'.
	STA	AUTO
	LHLD	PARAMS	;get drive specified.
	MOV	A,M
	ANI	1FH	;look at lower 5 bits.
	DCR	A	;adjust for (1=A, 2=B) etc.
	STA	EPARAM	;and save for the select routine.
	CPI	1EH	;check for 'no change' condition.
	JNC	AUTOSL1	;yes, don't change.
	LDA	ACTIVE	;we must change, save currently active
	STA	OLDDRV	;drive.
	MOV	A,M	;and save first byte of fcb also.
	STA	AUTOFLAG;this must be non-zero.
	ANI	0E0H	;whats this for (bits 6,7 are used for
	MOV	M,A	;something)?
	CALL	SETDSK	;select and log in this drive.
AUTOSL1	LDA	USERNO	;move user number into fcb.
	LHLD	PARAMS	;(* upper half of first byte *)
	ORA	M
	MOV	M,A
	RET		;and return (all done).
;
;   Function to return the current cp/m version number.
;
GETVER	MVI	A,022h	;version 2.2
	JMP	SETSTAT
;
;   Function to reset the disk system.
;
RSTDSK	LXI	H,0	;clear write protect status and log
	SHLD	WRTPRT	;in vector.
	SHLD	LOGIN
	XRA	A	;select drive 'A'.
	STA	ACTIVE
	LXI	H,TBUFF	;setup default dma address.
	SHLD	USERDMA
	CALL	DEFDMA
	JMP	LOGINDRV;now log in drive 'A'.
;
;   Function to open a specified file.
;
OPENFIL	CALL	CLEARS2	;clear 's2' byte.
	CALL	AUTOSEL	;select proper disk.
	JMP	OPENIT	;and open the file.
;
;   Function to close a specified file.
;
CLOSEFIL:CALL	AUTOSEL	;select proper disk.
	JMP	CLOSEIT	;and close the file.
;
;   Function to return the first occurence of a specified file
; name. If the first byte of the fcb is '?' then the name will
; not be checked (get the first entry no matter what).
;
GETFST	MVI	C,0	;prepare for special search.
	XCHG
	MOV	A,M	;is first byte a '?'?
	CPI	'?'
	JZ	GETFST1	;yes, just get very first entry (zero length match).
	CALL	SETEXT	;get the extension byte from fcb.
	MOV	A,M	;is it '?'? if yes, then we want
	CPI	'?'	;an entry with a specific 's2' byte.
	CNZ	CLEARS2	;otherwise, look for a zero 's2' byte.
	CALL	AUTOSEL	;select proper drive.
	MVI	C,15	;compare bytes 0-14 in fcb (12&13 excluded).
GETFST1	CALL	FINDFST	;find an entry and then move it into
	JMP	MOVEDIR	;the users dma space.
;
;   Function to return the next occurence of a file name.
;
GETNXT	LHLD	SAVEFCB	;restore pointers. note that no
	SHLD	PARAMS	;other dbos calls are allowed.
	CALL	AUTOSEL	;no error will be returned, but the
	CALL	FINDNXT	;results will be wrong.
	JMP	MOVEDIR
;
;   Function to delete a file by name.
;
DELFILE	CALL	AUTOSEL	;select proper drive.
	CALL	ERAFILE	;erase the file.
	JMP	STSTATUS;set status and return.
;
;   Function to execute a sequential read of the specified
; record number.
;
READSEQ	CALL	AUTOSEL	;select proper drive then read.
	JMP	RDSEQ
;
;   Function to write the net sequential record.
;
WRTSEQ	CALL	AUTOSEL	;select proper drive then write.
	JMP	WTSEQ
;
;   Create a file function.
;
FCREATE	CALL	CLEARS2	;clear the 's2' byte on all creates.
	CALL	AUTOSEL	;select proper drive and get the next
	JMP	GETEMPTY;empty directory space.
;
;   Function to rename a file.
;
RENFILE	CALL	AUTOSEL	;select proper drive and then switch
	CALL	CHGNAMES;file names.
	JMP	STSTATUS
;
;   Function to return the login vector.
;
GETLOG	LHLD	LOGIN
	JMP	GETPRM1
;
;   Function to return the current disk assignment.
;
GETCRNT	LDA	ACTIVE
	JMP	SETSTAT
;
;   Function to set the dma address.
;
PUTDMA	XCHG
	SHLD	USERDMA	;save in our space and then get to
	JMP	DEFDMA	;the bios with this also.
;
;   Function to return the allocation vector.
;
GETALOC	LHLD	ALOCVECT
	JMP	GETPRM1
;
;   Function to return the read-only status vector.
;
GETROV	LHLD	WRTPRT
	JMP	GETPRM1
;
;   Function to set the file attributes (read-only, system).
;
SETATTR	CALL	AUTOSEL	;select proper drive then save attributes.
	CALL	SAVEATTR
	JMP	STSTATUS
;
;   Function to return the address of the disk parameter block
; for the current drive.
;
GETPARM	LHLD	DISKPB
GETPRM1	SHLD	STATUS
	RET
;
;   Function to get or set the user number. If (E) was (FF)
; then this is a request to return the current user number.
; Else set the user number from (E).
;
GETUSER	LDA	EPARAM	;get parameter.
	CPI	0FFH	;get user number?
	JNZ	SETUSER
	LDA	USERNO	;yes, just do it.
	JMP	SETSTAT
SETUSER	ANI	1FH	;no, we should set it instead. keep low
	STA	USERNO	;bits (0-4) only.
	RET
;
;   Function to read a random record from a file.
;
RDRANDOM:CALL	AUTOSEL	;select proper drive and read.
	JMP	READRAN
;
;   Function to compute the file size for random files.
;
WTRANDOM:CALL	AUTOSEL	;select proper drive and write.
	JMP	WRITERAN
;
;   Function to compute the size of a random file.
;
FILESIZE:CALL	AUTOSEL	;select proper drive and check file length
	JMP	RANSIZE
;
;   Function #37. This allows a program to log off any drives.
; On entry, set (DE) to contain a word with bits set for those
; drives that are to be logged off. The log-in vector and the
; write protect vector will be updated. This must be a M/PM
; special function.
;
LOGOFF	LHLD	PARAMS	;get drives to log off.
	MOV	A,L	;for each bit that is set, we want
	CMA		;to clear that bit in (LOGIN)
	MOV	E,A	;and (WRTPRT).
	MOV	A,H
	CMA
	LHLD	LOGIN	;reset the login vector.
	ANA	H
	MOV	D,A
	MOV	A,L
	ANA	E
	MOV	E,A
	LHLD	WRTPRT
	XCHG
	SHLD	LOGIN	;and save.
	MOV	A,L	;now do the write protect vector.
	ANA	E
	MOV	L,A
	MOV	A,H
	ANA	D
	MOV	H,A
	SHLD	WRTPRT	;and save. all done.
	RET
;
;   Get here to return to the user.
;
GOBACK	LDA	AUTO	;was auto select activated?
	ORA	A
	JZ	GOBACK1
	LHLD	PARAMS	;yes, but was a change made?
	MVI	M,0	;(* reset first byte of fcb *)
	LDA	AUTOFLAG
	ORA	A
	JZ	GOBACK1
	MOV	M,A	;yes, reset first byte properly.
	LDA	OLDDRV	;and get the old drive and select it.
	STA	EPARAM
	CALL	SETDSK
GOBACK1	LHLD	USRSTACK;reset the users stack pointer.
	SPHL
	LHLD	STATUS	;get return status.
	MOV	A,L	;force version 1.4 compatability.
	MOV	B,H
	RET		;and go back to user.
;
;   Function #40. This is a special entry to do random i/o.
; For the case where we are writing to unused disk space, this
; space will be zeroed out first. This must be a M/PM special
; purpose function, because why would any normal program even
; care about the previous contents of a sector about to be
; written over.
;
WTSPECL	CALL	AUTOSEL	;select proper drive.
	MVI	A,2	;use special write mode.
	STA	MODE
	MVI	C,0	;set write indicator.
	CALL	POSITN1	;position the file.
	CZ	WTSEQ1	;and write (if no errors).
	RET
;
;**************************************************************
;*
;*     BDOS data storage pool.
;*
;**************************************************************
;
EMPTYFCB:DB	0E5H	;empty directory segment indicator.
WRTPRT	DW	0	;write protect status for all 16 drives.
LOGIN	DW	0	;drive active word (1 bit per drive).
USERDMA	DW	080H	;user's dma address (defaults to 80h).
;
;   Scratch areas from parameter block.
;
SCRATCH1:DW	0	;relative position within dir segment for file (0-3).
SCRATCH2:DW	0	;last selected track number.
SCRATCH3:DW	0	;last selected sector number.
;
;   Disk storage areas from parameter block.
;
DIRBUF	DW	0	;address of directory buffer to use.
DISKPB	DW	0	;contains address of disk parameter block.
CHKVECT	DW	0	;address of check vector.
ALOCVECT:DW	0	;address of allocation vector (bit map).
;
;   Parameter block returned from the bios.
;
SECTORS	DW	0	;sectors per track from bios.
BLKSHFT	DB	0	;block shift.
BLKMASK	DB	0	;block mask.
EXTMASK	DB	0	;extent mask.
DSKSIZE	DW	0	;disk size from bios (number of blocks-1).
DIRSIZE	DW	0	;directory size.
ALLOC0	DW	0	;storage for first bytes of bit map (dir space used).
ALLOC1	DW	0
OFFSET	DW	0	;first usable track number.
XLATE	DW	0	;sector translation table address.
;
;
CLOSEFLG:DB	0	;close flag (=0ffh is extent written ok).
RDWRTFLG:DB	0	;read/write flag (0ffh=read, 0=write).
FNDSTAT	DB	0	;filename found status (0=found first entry).
MODE	DB	0	;I/o mode select (0=random, 1=sequential, 2=special random).
EPARAM	DB	0	;storage for register (E) on entry to bdos.
RELBLOCK:DB	0	;relative position within fcb of block number written.
COUNTER	DB	0	;byte counter for directory name searches.
SAVEFCB	DW	0,0	;save space for address of fcb (for directory searches).
BIGDISK	DB	0	;if =0 then disk is > 256 blocks long.
AUTO	DB	0	;if non-zero, then auto select activated.
OLDDRV	DB	0	;on auto select, storage for previous drive.
AUTOFLAG:DB	0	;if non-zero, then auto select changed drives.
SAVNXT	DB	0	;storage for next record number to access.
SAVEXT	DB	0	;storage for extent number of file.
SAVNREC	DW	0	;storage for number of records in file.
BLKNMBR	DW	0	;block number (physical sector) used within a file or logical sector.
LOGSECT	DW	0	;starting logical (128 byte) sector of block (physical sector).
FCBPOS	DB	0	;relative position within buffer for fcb of file of interest.
FILEPOS	DW	0	;files position within directory (0 to max entries -1).
;
;   Disk directory buffer checksum bytes. One for each of the
; 16 possible drives.
;
CKSUMTBL:DB	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;
;   Extra space ?
;
	DB	0,0,0,0
;
;**************************************************************
;*
;*        B I O S   J U M P   T A B L E
;*
;**************************************************************
;
BOOT	JMP	0	;NOTE WE USE FAKE DESTINATIONS
WBOOT	JMP	0
CONST	JMP	0
CONIN	JMP	0
CONOUT	JMP	0
LIST	JMP	0
PUNCH	JMP	0
READER	JMP	0
HOME	JMP	0
SELDSK	JMP	0
SETTRK	JMP	0
SETSEC	JMP	0
SETDMA	JMP	0
READ	JMP	0
WRITE	JMP	0
PRSTAT	JMP	0
SECTRN	JMP	0
;
;*
;******************   E N D   O F   C P / M   *****************
;*

