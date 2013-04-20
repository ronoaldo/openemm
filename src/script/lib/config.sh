#!/bin/sh
#	-*- sh -*-

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
#
# Global configuration file for shell scripts on all production
# machines, including also several utility functions
#
# A.) Configuration
#
verbose=1
system="`uname -s`"
host="`uname -n | cut -d. -f1`"
#
# Set the base for the whole system ..
if [ ! "$BASE" ] ; then
	BASE="$HOME"
fi
export BASE
optbase="$OPENEMM_SW"
# .. and for java ..
LC_ALL=C
LANG=en_US.ISO8859_1
NLS_LANG=american_america.UTF8
export LC_ALL LANG NLS_LANG
if [ ! "$JBASE" ] ; then
	JBASE="$BASE/JAVA"
fi
if [ ! "$JAVAHOME" ] ; then
	for java in "$optbase/java"; do
		if [ -d $java ] ; then
			for sdk in $java/*sdk* ; do
				if [ -d $sdk ] ; then
					JAVAHOME=$sdk
					break
				fi
			done
			if [ ! "$JAVAHOME" ] ; then
				JAVAHOME=$java
				break
			fi
		fi
	done
fi
if [ "$JAVAHOME" ] ; then
	PATH="$JAVAHOME/bin:$PATH"
	export PATH JAVAHOME
fi
if [ "$JBASE" ] && [ -d $JBASE ] ; then
	cp="$JBASE"
	for jar in $JBASE/*.jar $JBASE/*.zip ; do
		if [ -f $jar ] ; then
			cp="$cp:$jar"
		fi
	done
	if [ "$CLASSPATH" ] ; then
		CLASSPATH="$CLASSPATH:$cp"
	else
		CLASSPATH="$cp"
	fi
fi
# .. and for others ..
for other in python perl sqlite ; do
	path="$optbase/$other"
	if [ -d $path/bin ] ; then
		PATH="$path/bin:$PATH"
	fi
done
export PATH
#
# Logging
#
if [ "$LOG_HOME" ] ; then
	logpath="$LOG_HOME"
else
	logpath="$BASE/var/log"
fi
loghost="`uname -n | cut -d. -f1`"
logname="`basename -- $0`"
loglast=0
#
# Sendmail location
#
sendmail="$HOME/bin/smctrl"
if [ ! -x $sendmail ]; then
	sendmail="/usr/sbin/sendmail"
	if [ ! -x $sendmail ] ; then
		sendmail="/usr/lib/sendmail"
	fi
fi
#
# B.) Routine collection
#
messagen() {
	if [ $verbose -gt 0 ] ; then
		case "$system" in
		SunOS|HP-UX)
			echo "$*\c"
			;;
		*)	echo -n "$*"
			;;
		esac
	fi
}
message() {
	if [ $verbose -gt 0 ] ; then
		echo "$*"
	fi
}
error() {
	echo "$*" 1>&2
}
epoch() {
		python -c "
import	time

print int (time.time ())
"
}
log() {
	__fname="$logpath/`date +%Y%m%d`-${loghost}-${logname}.log"
	echo "[`date '+%d.%m.%Y  %H:%M:%S'`] $$ $*" >> $__fname
	loglast="`epoch`"
}
mark() {
	if [ $# -eq 1 ] ; then
		__dur=`expr $1 \* 60`
	else
		__dur=3600
	fi
	__now="`epoch`"
	if [ `expr $loglast + $__dur` -lt $__now ] ; then
		log "-- MARK --"
	fi
}
elog() {
	log "$*"
	error "$*"
}
mlog() {
	log "$*"
	message "$*"
}
die() {
	if [ $# -gt 0 ] ; then
		elog "$*"
	fi
	exit 1
}
mstart() {
	messagen "$* "
}
mproceed() {
	if [ $# -eq 0 ] ; then
		messagen "."
	else
		messagen " $* "
	fi
}
mend() {
	message " $*."
}
msleep() {
	if [ $# -ne 1 ] ; then
		__end=1
	else
		__end=$1
	fi
	__cur=0
	while [ $__cur -lt $__end ] ; do
		mproceed
		sleep 1
		__cur=`expr $__cur + 1`
	done
}
#

__dbformat="YYYY-MM-DD:HH24:MI:SS"
dbdate() {
	date '+%Y-%m-%d:%H:%M:%S'
}
dbtodate() {
	if [ "$1" ] ; then
		echo "to_date ('$1', '$__dbformat')"
	else
		echo "to_date ('`dbdate`', '$__dbformat')"
	fi
}
dbtochar() {
	echo "to_char ('$1', '$__dbformat')"
}
#
uid() {
	__uid="`id | tr ' ' '\n' | egrep '^uid=' | tr -cd '[0-9]'`"
	if [ ! "$__uid" ] ; then
		__uid="-1"
	fi
	echo "$__uid"
}
#
call() {
	if [ $# -eq 0 ] ; then
		echo "Usage: $0 <program> [<parm>]" 1>&2
		__rc=1
	else
		__tmp=/var/tmp/call.$$
		"$@" > $__tmp 2>&1
		__rc=$?
		cat $__tmp
		rm $__tmp
	fi
	return $__rc
}
#
pathstrip() {
	if [ $# -ne 1 ] ; then
		echo "Usage: $0 <path>" 1>&2
	else
		python -c "
import	string
def pathstrip (s):
	rc = []
	for e in s.split (':'):
		if not e in rc:
			rc.append (e)
	return string.join (rc, ':')
print pathstrip (\"$1\")
"
	fi
}
#
filecount() {
	if [ $# -ne 2 ] ; then
		echo "Usage: $0 <dir> <pattern>" 1>&2
	else
		python -c "
import	os, sre
def filecount (directory, pattern):
	n = 0
	pat = sre.compile (pattern)
	for file in os.listdir (directory):
		mtch = pat.search (file)
		if not mtch is None:
			n += 1
	return n
print filecount (\"$1\", \"$2\")
"
	fi
}
#
pathto() {
	__path=""
	if [ $# -ne 1 ] ; then
		echo "Usage: $0 <program>" 1>&2
	else
		__chk="$BASE/bin:/usr/local/bin:$PATH"
		while [ ! "$__path" ] && [ "$__chk" ] ; do
			__cur="`echo $__chk | cut -d: -f1 -s`"
			if [ ! "$__cur" ] ; then
				__cur="$__chk"
			fi
			__chk="`echo $__chk | cut -d: -f2- -s`"
			if [ -x "$__cur/$1" ] ; then
				__path="$__cur/$1"
			fi
		done
	fi
	echo "$__path"
}
#
diskusage1() {
	if [ $# -eq 1 ] ; then
		df -k $1 | egrep -v '^Filesystem' | tr '\n' ' ' | awk '{ print $5 }' | grep '%' | tr -d '%'
	fi
}
diskusage() {
	__df=""
	if [ $# -gt 0 ] ; then
		while [ $# -gt 0 ] ; do
			__temp="`diskusage1 $1`"
			if [ "$__temp" ] ; then
				if [ "$__df" ] ; then
					__df="$__df;$1=$__temp"
				else
					__df="$1=$__temp"
				fi
			fi
			shift
		done
	fi
	if [ "$__df" ] ; then
		echo "$__df"
	fi
}
diskusageall() {
	diskusage `df -k | egrep -v '^(Filesystem|[ 	])' | awk '{ print $6 }'`
}
#
mailsend() {
	if [ $# -ne 4 ] ; then
		echo "Usage: $0 <sender> <receiver> <subject> <message>"
		return 1
	fi
	__send="$1"
	__recv="$2"
	__subj="$3"
	__msg="$4"

	$sendmail `echo $__recv | tr ',' ' '` << __EOF__
Subject: $__subj
From: $__send

$__msg
__EOF__
	__rc=$?
	if [ $__rc -ne 0 ] ; then
		log "ERROR: failed to send mail to $__recv"
	fi
	return $__rc
}
panicsend() {
	__subj="PANIC: on $host"
	__body="System: $host, Current time: `date`"
	if [ $# -gt 0 ] ; then
		__subj="$1"
		shift
	fi
	mailsend "postmaster" "postmaster" \
		"$__subj" "$__body
$*"
	elog "PANIC: $__subj - $*"
}
#
terminator() {
	while [ $# -gt 0 ] ; do
		__pat="$1"
		shift
		if [ "$__pat" ] ; then
			for sig in 15 9 ; do
				__run="`ps -ef | grep -- \"$__pat\" | grep -v grep | awk '{ print $2 }'`"
				if [ "$__run" ] ; then
					messagen "Stop $__pat program with signal $sig .. "
					kill -$sig $__run >/dev/null 2>&1
					sleep 2
					message "done."
				fi
			done
		fi
	done
}
#
softterm() {
	while [ $# -gt 0 ] ; do
		__pat="$1"
		shift
		if [ "$__pat" ] ; then
			for sv in 2 4 6 8 10 ; do
				repeat="on"
				while [ $repeat = "on" ]; do
					repeat="off"
					__run="`ps -ef | grep -- \"$__pat\" | grep -v grep | awk '{ print $2 }'`"
					if [ "$__run" ] ; then
						messagen "Stop $__pat program  .. "
						kill -15 $__run >/dev/null 2>&1
						sleep 1
						__run="`ps -ef | grep -- \"$__pat\" | grep -v grep | awk '{ print $2 }'`"
						if [ "$__run" ]; then
							messagen "delaying $sv seconds .. "
							sleep `expr $sv - 1`
							if [ $sv -eq 10 ]; then
								repeat="on"
							fi
						fi
						message "done."
					fi
				done
			done
		fi
	done
}
#
mestopper() {
	while [ $# -gt 1 ] ; do
		__fn="$1"
		__pat="$2"
		shift
		shift
		if [ "$__fn" ] && [ "$__pat" ] ; then
			messagen "Creating $__fn, waiting for $__pat: "
			touch "$__fn"
			while true ; do
				__run="`ps -ef | grep -- \"$__pat\" | grep -v grep | awk '{ print $2 }'`"
				if [ ! "$__run" ] ; then
					break
				fi
				messagen "."
				sleep 1
			done
			message "stopped."
			rm -f "$__fn"
		fi
	done
}
#
starter() {
	messagen "Start $* .. "
	(
		nohup "$@" > /dev/null 2>&1 &
	)
	message "done."
}
#
if [ "$LD_LIBRARY_PATH" ] ; then
	LD_LIBRARY_PATH="$BASE/lib:$LD_LIBRARY_PATH"
else
	LD_LIBRARY_PATH="$BASE/lib"
fi
export LD_LIBRARY_PATH
LD_LIBRARY_PATH="`pathstrip \"$LD_LIBRARY_PATH\"`"
export LD_LIBRARY_PATH
#
if [ "$PATH" ] ; then
	PATH="$BASE/bin:$PATH"
else
	PATH="$BASE/bin"
fi
if [ "`uid`" = "0" ] && [ -d "$BASE/sbin" ]; then
	PATH="$BASE/sbin:$PATH"
fi
if [ -d "$BASE/lbin" ]; then
	PATH="$BASE/lbin:$PATH"
fi
PATH="`pathstrip \"$PATH\"`"
export PATH
#
if [ "$CLASSPATH" ] ; then
	CLASSPATH="`pathstrip \"$CLASSPATH\"`"
	export CLASSPATH
fi
#
if [ "$PYTHONPATH" ] ; then
	PYTHONPATH="$BASE/lib:$PYTHONPATH"
else
	PYTHONPATH="$BASE/lib"
fi

PYTHONPATH="$BASE/bin/scripts:$PYTHONPATH"
PYTHONPATH="`pathstrip \"$PYTHONPATH\"`"
export PYTHONPATH
#
