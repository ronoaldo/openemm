##################################################################################
# The contents of this file are subject to the Common Public Attribution
# License Version 1.0 (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.openemm.org/cpal1.html. The License is based on the Mozilla
# Public License Version 1.1 but Sections 14 and 15 have been added to cover
# use of software over a computer network and provide for limited attribution
# for the Original Developer. In addition, Exhibit A has been modified to be
# consistent with Exhibit B.
# Software distributed under the License is distributed on an "AS IS" basis,
# WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
# the specific language governing rights and limitations under the License.
# 
# The Original Code is OpenEMM.
# The Original Developer is the Initial Developer.
# The Initial Developer of the Original Code is AGNITAS AG. All portions of
# the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
# Reserved.
# 
# Contributor(s): AGNITAS AG. 
##################################################################################
 
CC	= gcc
CFLAGS	= -I. -I../lib -I$(HOME)/include $(shell xml2-config --cflags) -I/usr/include/slang
LDFLAGS	= -L$(HOME)/lib
LIBS	= grammar/libgrammar.a -lslang -L../lib -lagn $(shell xml2-config --libs) -ldl
SRCS	= append.c \
	  block.c \
	  blockmail.c \
	  blockspec.c \
	  codec.c \
	  convert.c \
	  count.c \
	  counter.c \
	  create.c \
	  data.c \
	  dcache.c \
	  dyn.c \
	  eval.c \
	  field.c \
	  fix.c \
	  generate.c \
	  links.c \
	  mailtype.c \
	  media.c \
	  misc.c \
	  modify.c \
	  none.c \
	  parse.c \
	  postfix.c \
	  preview.c \
	  rblock.c \
	  receiver.c \
	  replace.c \
	  sqllike.c \
	  tag.c \
	  tagpos.c \
	  url.c \
	  xmlback.c
OBJS	= $(SRCS:%.c=%.o)

all::
	@$(MAKE) -C ../lib $@

all clean::
	make -C grammar $@

all::	xmlback

xmlback:	$(OBJS) grammar/libgrammar.a
	$(CC) $(LDFLAGS) $(OBJS) -o $@ $(LIBS)

grammar/libgrammar.a:
	make -C grammar

clean::
	rm -f xmlback *.o *~
