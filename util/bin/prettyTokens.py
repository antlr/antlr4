#!/usr/bin/python
#  Copyright 2013 Gregory Davis
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#  3. The name of the author may not be used to endorse or promote products
#     derived from this software without specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# prettyTokens.py
# sys.argv[] arguments:
#    none
# Description:
#    Reads from stdin and echoes to stdout, conditionally altering the lines
#    which match a re pattern. The intent is to allow filtering the output of
#    TestRig, aka grun, to reformat any tokens display created when using the
#    option '-tokens'.
#
#    The behaviour of the filter may be altered with a number of
#    environment variables:
#      ANTLR_TOKEN_FORMAT_STRING
#      ANTLR_RE_TOKEN_STRING
#      ANTLR_TARGET_SUFFIXES
#      ANTLR_RE_TOKEN_ENUM
#      ANTLR_CHANNEL_NAME
#      ANTLR_CHANNEL_VALUE
#
#    The first of these, ANTLR_TOKEN_FORMAT_STRING, defines the new format of
#    the token string, where the following fields will be replaced using the
#    Python format string syntax, see:
#      http://docs.python.org/2/library/string.html#string.Formatter.vformat
#    with the parsed values:
#      index:        the token index, incremented for each token
#      start:        the column where the token begins
#      stop:         the column where the token ends
#      text:         the token text
#      type:         the token type, a numeric string
#      channel:      the channel number where the token was sent, '' for parser
#      line:         the line number where the token begins
#      position:     the column where the token begins
#    and the derived values:
#      length:       the length of the token text, derived from start and stop
#      token_name:   the name of the token, derived from type
#      channel_name: the name of the channel, derived from channel
#
#    The balance of the environment variables should allow the tool to be used
#    when many of the assumptions in the code become untrue. See the code for
#    default values, which should suggest ways to override without needing to
#    change the code.

import glob, os, re, sys;

# format string for tokens
try:
    # get format string from environment. bash example:
    #   export ANTLR_TOKEN_FORMAT_STRING=\
    #     "[@{index:>4}, line {line:>3}[{position:>3}:{length:>+4}],\
    #      {token_name:>30}({type:>3}), {text:>23} -> {channel_name}"
    token_format_string = os.environ['ANTLR_TOKEN_FORMAT_STRING']
except KeyError:
    # default format string
    token_format_string = ("[@{index:>4}, "
                           "line {line:>3}[{position:>3}:{length:>+4}],"
                           "{token_name:>30}({type:>3}), "
                           "{text:>23} -> {channel_name}")

# regular expression for tokens output. example targets for default:
#    [@1,8:16='intensive',<165>,1:8]
#    [@3,38:38=',',<171>,1:38]
#    [@4,31:46='// comment text\n',<161>,channel=1,2:10]
#    [@10,105:104='<EOF>',<-1>,2:0]
try:
    # get token string re from environment. bash example:
    #   export ANTLR_RE_TOKEN_STRING=\
    #     "\[@(?P<index>\d+),(?P<start>\d+):(?P<stop>\d+)=(?P<text>'.*'),\
    #      <(?P<type>-?[\d]+)>,(channel=(?P<channel>\d+),)?\
    #      (?P<line>\d+):(?P<position>\d+)\]"
    re_token_string = os.environ['ANTLR_RE_TOKEN_STRING']
except KeyError:
    # default
    re_token_string = (r"\[@"
                       r"(?P<index>\d+),"
                       r"(?P<start>\d+):"
                       r"(?P<stop>\d+)="
                       r"(?P<text>'.*'),<"
                       r"(?P<type>-?[\d]+)>,"
                       r"(channel=(?P<channel>\d+),)?"
                       r"(?P<line>\d+):"
                       r"(?P<position>\d+)\]")
rec_token_string = re.compile(re_token_string)

# whitespace separated list of target language suffixes
try:
    # get the target suffix list from the environment. bash example:
    #   export ANTLR_TARGET_SUFFIXES="java cs cpp cc c lisp py pl tcl"
    target_suffixes = os.environ['ANTLR_TARGET_SUFFIXES']
except:
    target_suffixes = 'java cs cpp cc c lisp py pl tcl'

# regular expression for enums in the tokens file. example targets for default:
#    BEGIN=22
#    'begin'=22
# The default will use the first token name found for the value
try:
    # get token enum string from environment. bash example:
    #   export ANTLR_RE_TOKEN_ENUM="(?P<name>[^=\s]+)\s*=\s*(?P<value>\d+)"
    re_token_enum = os.environ['ANTLR_RE_TOKEN_ENUM']
except KeyError:
    # default
    re_token_enum = (r"(?P<name>[^=\s]+)\s*=\s*"
                     r"(?P<value>\d+)")
rec_token_enum = re.compile(re_token_enum)

# regular expression for identifying channel declarations in generated code, eg:
#    case 3: _channel = COMMENTS;  break;
try:
    # get channel name string from environment. bash example:
    #   export ANTLR_CHANNEL_NAME=".*channel\s*=\s*(?P<name>\w+)"
    re_channel_name = os.environ['ANTLR_CHANNEL_NAME']
except KeyError:
    # default
    re_channel_name = (r".*channel\s*=\s*"
                       r"(?P<name>\w+)")
rec_channel_name = re.compile(re_channel_name)

# regular expression for identifying the channel value in generate code, eg.
#    public static final int COMMENTS = 1;
# don't compile this one, as it's a template that needs to formatted with name
try:
    # get channel value dtring from environment. bash example:
    #   export ANTLR_CHANNEL_VALUE="r\"  \""
    re_channel_value = os.environ['ANTLR_CHANNEL_VALUE']
except KeyError:
    # default
    re_channel_value = (r".*\s"
                        r"{name}\s*=\s*"
                        r"(?P<value>\d+)")

# get the local filename for the lexer code, latest if multiple glob hits
lexer_path = '';
lexer_path_mtime = 0
for suffix in target_suffixes.split():
    lexer_paths = glob.iglob('./*Lexer.{sfx}'.format(sfx=suffix))
    for path in lexer_paths:
        path_mtime = os.path.getctime(path)
        if (path_mtime > lexer_path_mtime):
            lexer_path = path
            lexer_path_mtime = path_mtime

# get channel value map, if a lexer path was identified
channels_by_value = {}
if lexer_path:
    # get the channel names into a dict, for use as Set
    lexer_source = open(lexer_path, 'r')
    true_by_channels = {}
    for line in lexer_source:
        matched = rec_channel_name.match(line)
        if matched:
            fields = matched.groupdict()
            true_by_channels[fields['name']] = True
    lexer_source.close()
    # get the channel values into the dict
    lexer_source = open(lexer_path, 'r')
    for line in lexer_source:
        for channel_name in true_by_channels.keys():
            matched = re.match(re_channel_value.format(name=channel_name), line)
            if matched:
                fields = matched.groupdict()
                channels_by_value[eval(fields['value'])] = channel_name
    lexer_source.close()

# get the local filename for the tokens file, latest if multiple glob hits
tokens_path = '';
tokens_path_mtime = 0
tokens_paths = glob.iglob('./*Lexer.tokens')
for path in tokens_paths:
    path_mtime = os.path.getctime(path)
    if (path_mtime > tokens_path_mtime):
        tokens_path = path
        tokens_path_mtime = path_mtime

# build a dict for the tokens
tokens_by_value = {}
if tokens_path:
    token_enums = open(tokens_path, 'r')
    for line in token_enums:
        matched = rec_token_enum.match(line)
        if matched:
            fields = matched.groupdict()
            if not tokens_by_value.has_key(fields['value']):
                tokens_by_value[eval(fields['value'])] = fields['name']
    token_enums.close()

# substitute formatted token line for each matching token string
for line in sys.stdin:
    matched = rec_token_string.match(line)
    if matched:
        # when the line matches our token string re, get the fields
        fields = matched.groupdict()
        # convert the number strings to numbers
        for key in fields.keys():
            if (key != 'text') and fields[key]:
                fields[key] = eval(fields[key])
        # derive token length
        try:
            length = fields['stop'] - fields['start'] + 1
        except:
            length = ''
        # derive token name
        try:
            token_name = tokens_by_value[fields['type']]
        except:
            token_name = ''
        # derive channel name
        try:
            channel_name = channels_by_value[fields['channel']]
        except:
            if fields['channel'] is '1' and not lexer_path:
                channel_name = 'HIDDEN'
            elif fields['channel']:
                channel_name = fields['channel']
            else:
                channel_name = ''
        # substitute original and derived fields into the format string, print
        print token_format_string.format(index=fields['index'],
                                         start=fields['start'],
                                         stop=fields['stop'],
                                         text=fields['text'],
                                         type=fields['type'],
                                         channel=fields['channel'],
                                         line=fields['line'],
                                         position=fields['position'],
                                         length=length,
                                         token_name=token_name,
                                         channel_name=channel_name)
    else:
        # otherwise, no match on the token string re, so echo the line
        sys.stdout.write(line)

# end
