/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
# include	<stdlib.h>
# include	<ctype.h>
# include	"xmlback.h"


static struct { /*{{{*/
	const char	*tname;		/* tagname to be parsed		*/
	/*}}}*/
}	parseable[] = { /*{{{*/
	{	"agnSYSINFO"		}
	/*}}}*/
};
enum PType { /*{{{*/
	P_Sysinfo
	/*}}}*/
};
static int	psize = sizeof (parseable) / sizeof (parseable[0]);

tag_t *
tag_alloc (void) /*{{{*/
{
	tag_t	*t;
	
	if (t = (tag_t *) malloc (sizeof (tag_t))) {
		t -> name = xmlBufferCreate ();
		t -> cname = NULL;
		t -> hash = 0;
		t -> type = NULL;
		t -> topt = NULL;
		t -> value = xmlBufferCreate ();
		t -> parm = NULL;
		t -> used = false;
		t -> next = NULL;
		if ((! t -> name) || (! t -> value))
			t = tag_free (t);
	}
	return t;
}/*}}}*/
tag_t *
tag_free (tag_t *t) /*{{{*/
{
	if (t) {
		if (t -> name)
			xmlBufferFree (t -> name);
		if (t -> cname)
			free (t -> cname);
		if (t -> type)
			free (t -> type);
		if (t -> value)
			xmlBufferFree (t -> value);
		if (t -> parm)
			var_free_all (t -> parm);
		free (t);
	}
	return NULL;
}/*}}}*/
tag_t *
tag_free_all (tag_t *t) /*{{{*/
{
	tag_t	*tmp;
	
	while (tmp = t) {
		t = t -> next;
		tag_free (tmp);
	}
	return NULL;
}/*}}}*/
static void
xmlSkip (xmlChar **ptr, int *len) /*{{{*/
{
	int	n;
	
	while (*len > 0) {
		n = xmlCharLength (**ptr);
		if ((n == 1) && isspace (**ptr)) {
			*(*ptr)++ = '\0';
			*len -= 1;
			while ((*len > 0) && (xmlCharLength (**ptr) == 1) && isspace (**ptr))
				++(*ptr);
			break;
		} else {
			*ptr += n;
			*len -= n;
		}
	}
}/*}}}*/
static int
mkRFCdate (char *dbuf, size_t dlen) /*{{{*/
{
	time_t          now;
	struct tm       *tt;

	time (& now);
	if (tt = gmtime (& now)) {
		const char	*weekday[] = {
			"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
		},		*month[] = {
			"Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
		};
		return sprintf (dbuf, "%s, %2d %s %d %02d:%02d:%02d GMT",
				weekday[tt -> tm_wday], tt -> tm_mday, month[tt -> tm_mon], tt -> tm_year + 1900,
				tt -> tm_hour, tt -> tm_min, tt -> tm_sec) > 0;
	}
	return 0;
}/*}}}*/
static const char *
find_default (tag_t *t) /*{{{*/
{
	const char	*dflt = NULL;
	var_t		*run;

	for (run = t -> parm; run; run = run -> next)
		if (! strcmp (run -> var, "default")) {
			dflt = run -> val;
			break;
		}
	return dflt;
}/*}}}*/
void
tag_parse (tag_t *t, blockmail_t *blockmail) /*{{{*/
{
	xmlBufferPtr	temp;
	
	if (t -> name && (xmlBufferLength (t -> name) > 0) && (temp = xmlBufferCreateSize (xmlBufferLength (t -> name) + 1))) {
		xmlChar	*ptr;
		xmlChar	*name;
		int	len;
		int	tid;
		
		xmlBufferAdd (temp, xmlBufferContent (t -> name), xmlBufferLength (t -> name));
		ptr = (xmlChar *) xmlBufferContent (temp);
		len = xmlBufferLength (temp);
		if ((xmlCharLength (*ptr) == 1) && (*ptr == '[')) {
			++ptr;
			--len;
			if ((len > 0) && (xmlStrictCharLength (*(ptr + len - 1)) == 1) && (*(ptr + len - 1) == ']')) {
				--len;
				if ((len > 0) && (xmlStrictCharLength (*(ptr + len - 1)) == 1) && (*(ptr + len - 1) == '/'))
					--len;
				ptr[len] = '\0';
			}
		}
		name = ptr;
		xmlSkip (& ptr, & len);
		if (t -> type) {
			for (tid = 0; tid < psize; ++tid)
				if (! strcmp (t -> type, parseable[tid].tname))
					break;
		} else
			tid = psize;
		if (tid == psize)
			for (tid = 0; tid < psize; ++tid)
				if (! xmlstrcmp (name, parseable[tid].tname))
					break;
		if (tid < psize) {
			var_t	*cur, *prev;
			xmlChar	*var, *val;
			int	n;
			
			for (prev = t -> parm; prev && prev -> next; prev = prev -> next)
				;
			while (len > 0) {
				var = ptr;
				while (len > 0) {
					n = xmlCharLength (*ptr);
					if ((n == 1) && (*ptr == '=')) {
						*ptr++ = '\0';
						len -= 1;
						break;
					} else {
						ptr += n;
						len -= n;
					}
				}
				if (len > 0) {
					if ((xmlCharLength (*ptr) == 1) && (*ptr == '"')) {
						++ptr;
						--len;
						val = ptr;
						while (len > 0) {
							n = xmlCharLength (*ptr);
							if ((n == 1) && (*ptr == '"')) {
								*ptr++ = '\0';
								len -= 1;
								xmlSkip (& ptr, & len);
								break;
							} else {
								ptr += n;
								len -= n;
							}
						}
					} else {
						val = ptr;
						xmlSkip (& ptr, & len);
					}
					if (cur = var_alloc (xml2char (var), xml2char (val))) {
						if (prev)
							prev -> next = cur;
						else
							t -> parm = cur;
						prev = cur;
					}
				}
			}
			switch ((enum PType) tid) {
			case P_Sysinfo:
				for (cur = t -> parm; cur; cur = cur -> next)
					if (! strcmp (cur -> var, "name")) {
						if (! strcmp (cur -> val, "FQDN")) {
							const char	*dflt;
						
							if (blockmail -> fqdn) {
								xmlBufferEmpty (t -> value);
								xmlBufferCCat (t -> value, blockmail -> fqdn);
							} else if (dflt = find_default (t)) {
								xmlBufferEmpty (t -> value);
								xmlBufferCCat (t -> value, dflt);
							}
						} else if (! strcmp (cur -> val, "RFCDATE")) {
							char            dbuf[128];

							if (mkRFCdate (dbuf, sizeof (dbuf))) {
								xmlBufferEmpty (t -> value);
								xmlBufferCCat (t -> value, dbuf);
							}
						} else if (! strcmp (cur -> val, "EPOCH")) {
							time_t		now;
							char		dbuf[64];
							
							time (& now);
							sprintf (dbuf, "%ld", (long) now);
							xmlBufferEmpty (t -> value);
							xmlBufferCCat (t -> value, dbuf);
						}
					}
				break;
			}
		}
		xmlBufferFree (temp);
	}
}/*}}}*/
bool_t
tag_match (tag_t *t, const xmlChar *name, int nlen) /*{{{*/
{
	const xmlChar	*ptr;
	int		len;
	
	len = xmlBufferLength (t -> name);
	if (len == nlen) {
		ptr = xmlBufferContent (t -> name);
		if (! memcmp (name, ptr, len))
			return true;
	}
	return false;
}/*}}}*/
const xmlChar *
tag_content (tag_t *t, blockmail_t *blockmail, receiver_t *rec, int *length) /*{{{*/
{
	*length = xmlBufferLength (t -> value);
	return xmlBufferContent (t -> value);
}/*}}}*/
