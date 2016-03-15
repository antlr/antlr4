# [The "BSD license"]
#  Copyright (c) 2015 Dan McLaughlin
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

LINUXROT:= `pwd` # doh
RUNTIME := ../org/antlr/v4/runtime
OBJDIR  := obj

# Stop on any error or warning, comment out if you want to blast through
NUMERRORS := -Werror

# Another possibility
# NUMERRORS := -fmax-errors=5 

CC      := ./g++
CFLAGS  := -fPIC -shared -g -O3 -Wall -std=c++11 $(NUMERRORS)
LDFLAGS :=
IFLAGS  :=-I../antlrcpp -I. -I $(RUNTIME) -I $(RUNTIME)/atn -I $(RUNTIME)/dfa \
	  -I $(RUNTIME)/misc -I $(RUNTIME)/tree -I $(RUNTIME)/tree/pattern \
          -I $(RUNTIME)/tree/xpath 
NAME    := ANTLR4
MAJOR   := 0
MINOR   := 1
VERSION := $(MAJOR).$(MINOR)

DFA     := $(RUNTIME)/dfa
MISC    := $(RUNTIME)/misc
TREE    := $(RUNTIME)/tree
TREEPAT := $(TREE)/pattern
XPATH   := $(TREE)/xpath

RUNTIMESRC := $(wildcard $(RUNTIME)/*.cpp)
RUNTIMEOBJ := $(patsubst %.cpp,%.o,$(RUNTIMESRC))
DFASRC     := $(wildcard $(DFA)/*.cpp)
DFAOBJ     := $(patsubst %.cpp,%.o,$(DFASRC))
MISCSRC    := $(wildcard $(MISC)/*.cpp)
MISCOBJ    := $(patsubst %.cpp,%.o,$(MISCSRC))
TREESRC    := $(wildcard $(TREE)/*.cpp)
TREEOBJ    := $(patsubst %.cpp,%.o,$(TREESRC))
TREEPATSRC := $(wildcard $(TREEPAT)/*.cpp)
TREEPATOBJ := $(patsubst %.cpp,%.o,$(TREEPATSRC))
XPATHSRC   := $(wildcard $(XPATH)/*.cpp)
XPATHOBJ   := $(patsubst %.cpp,%.o,$(XPATHSRC))

OBJS       := $(notdir $(RUNTIMEOBJ) $(DFAOBJ) $(MISCOBJ) $(TREEOBJ) \
                       $(TREEPATOBJ) $(XPATHOBJ) )

OBJECTS    := $(addprefix $(OBJDIR)/,$(OBJS))

SOURCES := $(RUNTIMESRC) $(DFASRC) $(MISCSRC) $(TREESRC) $(TREEPATSRC) $(XPATHSRC) 

LIB     := lib$(NAME).so.$(VERSION)

all: $(LIB)

$(LIB): $(OBJECTS) 
	$(CC) -shared -Wl,-soname,lib$(NAME).so.$(MAJOR) $^ -o $@

# pull in dependency info for *existing* .o files
-include $(OBJECTS:.o=.d)

# TODO: Is there a way to collapse all these into one line? It doesn't 
# work if I do so
$(OBJDIR)/%.o : $(RUNTIME)/%.cpp 
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c  $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#       This is the GNU makefile canonical way of adding the dependency file generation 
#       to include regenerating when any of the dependencies change. However in our
#       solution the dependency is always generated whenever a compilation occurs (as it should)
#       so this is un-necessary
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJDIR)/%.o : $(DFA)/%.cpp 
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJDIR)/%.o : $(MISC)/%.cpp 
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJDIR)/%.o : $(TREE)/%.cpp
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJDIR)/%.o : $(TREEPAT)/%.cpp
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJDIR)/%.o : $(XPATH)/%.cpp
	@set -e; rm -f $@; \
	$(CC) $(IFLAGS) $(CFLAGS) -o $@ -c $<	
	$(CC) $(IFLAGS) $(CFLAGS) -o $(subst .o,.d,$@) -MM $<
	sed -i '1s/^/obj\//' $(subst .o,.d,$@)
#	sed -i 's,\($*\)\.o[ :]*,\1.o $(subst .o,.d,$@) : ,g' $(subst .o,.d,$@) 

$(OBJECTS): | $(OBJDIR)/

$(OBJDIR): FORCE
	mkdir -p $(OBJDIR)

FORCE:

clean:
	$(RM) $(NAME)_test *.so* -fr obj
