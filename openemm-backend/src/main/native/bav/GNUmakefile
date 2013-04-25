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
CFLAGS	= -I../lib $(shell sa-config --cflags) -pthread
LDFLAGS	= -L../lib $(shell sa-config --ldflags) -pthread
# required for Ubuntu 9.10 by user request
LDFLAGS	:= $(LDFLAGS) -L/usr/lib/libmilter
#
LIBS	= -lagn -lmilter $(shell sa-config --libs)
SRCBAV	= bav.c cfg.c
SRCWRP	= bavwrap.c store.c
OBJBAV	= $(SRCBAV:%.c=%.o)
OBJWRP	= $(SRCWRP:%.c=%.o)

all:	sacheck bav bavwrap

sacheck:
	@sa-config --version >/dev/null || ( echo "Library SA is missing, please fetch and install it from: ftp://ftp.ossp.org/pkg/lib/sa/sa-1.2.6.tar.gz"; exit 1 )

bav:	$(OBJBAV)
	$(CC) $(LDFLAGS) $(OBJBAV) -o $@ $(LIBS)

bavwrap:	$(OBJWRP)
	$(CC) $(LDFLAGS) $(OBJWRP) -o $@ $(LIBS)

clean:
	rm -f bav bavwrap *.o *~
