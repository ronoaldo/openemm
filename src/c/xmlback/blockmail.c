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
# include	<stdarg.h>
# include	<string.h>
# include	"xmlback.h"

# define	SYNC_POSTFIX		".SYNC"

static bool_t
open_syncfile (blockmail_t *b) /*{{{*/
{
	bool_t		rc;
	int		oflen, nflen;
	char		fname[PATH_MAX + 1];
	char		*ptr;
	int		flen;
	
	rc = false;
	oflen = strlen (b -> fname);
# ifdef		WIN32
	if (oflen < sizeof (fname)) {
		strcpy (fname, b -> fname);
		rc = true;
	}
# else		/* WIN32 */	
	if (b -> fname[0] == '/') {
		if (oflen < sizeof (fname)) {
			strcpy (fname, b -> fname);
			rc = true;
		}
	} else if (getcwd (fname, sizeof (fname) - 1)) {
		nflen = strlen (fname);
		if (oflen + nflen + 1 < sizeof (fname)) {
			fname[nflen++] = '/';
			strcpy (fname + nflen, b -> fname);
			rc = true;
		}
	}
# endif		/* WIN32 */
	if (rc) {
		if (ptr = strrchr (fname, '/'))
			++ptr;
		else
			ptr = fname;
		if (ptr = strchr (ptr, '.'))
			flen = ptr - fname;
		else
			flen = strlen (fname);
		if (flen + sizeof (SYNC_POSTFIX) <= sizeof (b -> syfname)) {
			strncpy (b -> syfname, fname, flen);
			strcpy (b -> syfname + flen, SYNC_POSTFIX);
			if ((! (b -> syfp = fopen (b -> syfname, "a+"))) || (fseek (b -> syfp, 0, SEEK_SET) == -1))
				rc = false;
		} else
			rc = false;
	}
	return rc;
}/*}}}*/
blockmail_t *
blockmail_alloc (const char *fname, bool_t syncfile, log_t *lg) /*{{{*/
{
	blockmail_t	*b;

	if (b = (blockmail_t *) malloc (sizeof (blockmail_t))) {
		b -> fname = fname;

		b -> syfname[0] = '\0';
		b -> syfp = NULL;
		b -> syeof = false;
		b -> in = NULL;
		b -> out = NULL;
		b -> translate = NULL;
		b -> lg = lg;
		b -> eval = NULL;

		b -> usecrlf = false;
		b -> raw = false;
		b -> output = NULL;
		b -> outputdata = NULL;
		b -> counter = NULL;
		b -> active = false;
		b -> head = NULL;
		b -> body = NULL;
		b -> rblocks = NULL;

		b -> company_id = -1;
		b -> mailinglist_id = -1;
		b -> mailing_id = -1;
		b -> mailing_name = NULL;
		b -> maildrop_status_id = -1;
		b -> status_field = '\0';
		
		b -> email.subject = NULL;
		b -> email.from = NULL;
		b -> profile_url = NULL;
		b -> unsubscribe_url = NULL;
		b -> auto_url = NULL;
		b -> onepixel_url = NULL;
		b -> password = NULL;
		b -> total_subscribers = 0;

		b -> blocknr = 0;
		b -> innerboundary = NULL;
		b -> outerboundary = NULL;
		b -> attachboundary = NULL;
		
		DO_ZERO (b, block);
		DO_ZERO (b, media);
		DO_ZERO (b, mailtype);

		b -> ltag = NULL;
		b -> taglist_count = 0;

		b -> gtag = NULL;
		b -> globaltag_count = 0;


		b -> dyn = NULL;
		b -> dynamic_count = 0;
		
		DO_ZERO (b, url);
		b -> url_usage = 0;

		DO_ZERO (b, field);
		b -> mailtype_index = -1;
		b -> mtbuf[0] = NULL;
		b -> mtbuf[1] = NULL;

		b -> receiver_count = 0;

		b -> fqdn = NULL;
		b -> xconv = NULL;

		if ((syncfile && (! open_syncfile (b))) ||
		    (! (b -> in = xmlBufferCreate ())) ||
		    (! (b -> out = xmlBufferCreate ())) ||
		    (! (b -> eval = eval_alloc (b))) ||
		    (! (b -> head = buffer_alloc (4096))) ||
		    (! (b -> body = buffer_alloc (65536))) ||
		    (! (b -> mtbuf[0] = xmlBufferCreate ())) ||
		    (! (b -> mtbuf[1] = xmlBufferCreate ())) ||
		    (! (b -> xconv = xconv_alloc (250))))
			b = blockmail_free (b);
		else {
			b -> head -> spare = 1024;
			b -> body -> spare = 8192;
			xmlBufferCCat (b -> mtbuf[0], "0");
			xmlBufferCCat (b -> mtbuf[1], "1");
		}
	}
	return b;
}/*}}}*/
blockmail_t *
blockmail_free (blockmail_t *b) /*{{{*/
{
	if (b) {
		if (b -> syfp)
			fclose (b -> syfp);
		if (b -> in)
			xmlBufferFree (b -> in);
		if (b -> out)
			xmlBufferFree (b -> out);
		if (b -> translate)
			xmlCharEncCloseFunc (b -> translate);
		if (b -> eval)
			eval_free (b -> eval);
		if (b -> counter)
			counter_free_all (b -> counter);
		if (b -> head)
			buffer_free (b -> head);
		if (b -> body)
			buffer_free (b -> body);
		if (b -> rblocks)
			rblock_free_all (b -> rblocks);
		if (b -> mtbuf[0])
			xmlBufferFree (b -> mtbuf[0]);
		if (b -> mtbuf[1])
			xmlBufferFree (b -> mtbuf[1]);
		if (b -> xconv)
			xconv_free (b -> xconv);
		if (b -> mailing_name)
			xmlBufferFree (b -> mailing_name);

		if (b -> email.subject)
			xmlBufferFree (b -> email.subject);
		if (b -> email.from)
			xmlBufferFree (b -> email.from);
		if (b -> profile_url)
			xmlBufferFree (b -> profile_url);
		if (b -> unsubscribe_url)
			xmlBufferFree (b -> unsubscribe_url);
		if (b -> auto_url)
			xmlBufferFree (b -> auto_url);
		if (b -> onepixel_url)
			xmlBufferFree (b -> onepixel_url);
		if (b -> password)
			xmlBufferFree (b -> password);
		if (b -> innerboundary)
			free (b -> innerboundary);
		if (b -> outerboundary)
			free (b -> outerboundary);
		if (b -> attachboundary)
			free (b -> attachboundary);

		DO_FREE (b, block);
		DO_FREE (b, media);
		DO_FREE (b, mailtype);

		if (b -> ltag)
			tag_free_all (b -> ltag);
		if (b -> gtag)
			tag_free_all (b -> gtag);
		if (b -> dyn)
			dyn_free_all (b -> dyn);
		
		DO_FREE (b, url);
		
		DO_FREE (b, field);

		free (b);
	}
	return NULL;
}/*}}}*/
bool_t
blockmail_count (blockmail_t *b, const char *mediatype, int subtype, long bytes) /*{{{*/
{
	counter_t	*run, *prv;
	
	for (run = b -> counter, prv = NULL; run; run = run -> next)
		if ((! strcmp (run -> mediatype, mediatype)) && (run -> subtype == subtype))
			break;
		else
			prv = run;
	if (run) {
		if (prv) {
			prv -> next = run -> next;
			run -> next = b -> counter;
			b -> counter = run;
		}
	} else if (run = counter_alloc (mediatype, subtype)) {
		run -> next = b -> counter;
		b -> counter = run;
	}
	if (run) {
		run -> unitcount++;
		run -> bytecount += bytes;
	}
	return run ? true : false;
}/*}}}*/
void
blockmail_count_sort (blockmail_t *b) /*{{{*/
{
	if (b -> counter && b -> counter -> next) {
		counter_t	*run, *prv, *tmp, *tpv;
		int		cmp;
		
		for (run = b -> counter, prv = NULL; run; ) {
			for (tmp = run -> next, tpv = run; tmp; tmp = tmp -> next) {
				cmp = strcmp (run -> mediatype, tmp -> mediatype);
				if (cmp == 0)
					cmp = run -> subtype - tmp -> subtype;
				if (cmp > 0)
					break;
				else
					tpv = tmp;
			}
			if (tmp) {
				tpv -> next = tmp -> next;
				tmp -> next = run;
				if (prv)
					prv -> next = tmp;
				else
					b -> counter = tmp;
				run = tmp;
			} else {
				prv = run;
				run = run -> next;
			}
		}
	}
}/*}}}*/
/*
 * each line in the syncfile has three fields, separated by semicolon:
 * - the customer number
 * - the size of the created mail for this customer
 * - the mediatype
 * - the subtype
 */
void
blockmail_unsync (blockmail_t *b) /*{{{*/
{
	if (b -> syfp) {
		fclose (b -> syfp);
		b -> syfp = NULL;
		unlink (b -> syfname);
	}
}/*}}}*/
bool_t
blockmail_insync (blockmail_t *b, int cid, const char *mediatype, int subtype) /*{{{*/
{
	bool_t	rc;
	
	rc = false;
	if (b -> syfp && (! b -> syeof)) {
		char	*inp;
		char	buf[512];
		char	*ptr;
		char	*size, *mtyp, *temp;
		int	styp;
		int	ncid;
		
		while (inp = fgets (buf, sizeof (buf) - 1, b -> syfp)) {
			ncid = atoi (buf);
			mtyp = NULL;
			styp = 0;
			if (ptr = strchr (buf, ';')) {
				*ptr++ = '\0';
				size = ptr;
				if (ptr = strchr (ptr, ';')) {
					*ptr++ = '\0';
					mtyp = ptr;
					if (ptr = strchr (ptr, ';')) {
						*ptr++ = '\0';
						temp = ptr;
						if (ptr = strchr (ptr, '\n'))
							*ptr = '\0';
						styp = atoi (temp);
						if (ncid)
							blockmail_count (b, mtyp, styp, atol (size));
					}
				}
			}
			if (mtyp && (cid == ncid) &&
			    (! strcmp (mtyp, mediatype)) && (styp == subtype)) {
				rc = true;
				break;
			}
		}
		if ((! inp) || feof (b -> syfp))
			b -> syeof = true;
	}
	return rc;
}/*}}}*/
bool_t
blockmail_tosync (blockmail_t *b, int cid, const char *mediatype, int subtype) /*{{{*/
{
	bool_t	rc;
	long	size;
	
	rc = true;
	size = b -> head -> length + 2 + b -> body -> length;
	if (b -> syfp) {
		long	pos;
		
		if (! b -> syeof) {
			pos = ftell (b -> syfp);
			if ((pos == -1) || (fseek (b -> syfp, 0, SEEK_END) == -1))
				rc = false;
		} else
			pos = -1;
		if (rc) {
			if ((fprintf (b -> syfp, "%d;%ld;%s;%d\n", cid, size, mediatype, subtype) == -1) ||
			    (fflush (b -> syfp) == -1))
				rc = false;
			if (rc && (pos != -1) && (fseek (b -> syfp, pos, SEEK_SET) == -1))
				rc = false;
		}
	}
	if (rc && cid && b -> active)
		rc = blockmail_count (b, mediatype, subtype, size);
	return rc;
}/*}}}*/

/*
 * extract the relevant informations from the media types and add it
 * to the current variables
 */
static void
replace (xmlBufferPtr *buf, media_t *m, const char *mid) /*{{{*/
{
	parm_t		*p;
	xmlBufferPtr	nbuf;

	if (p = media_find_parameter (m, mid))
		if (nbuf = parm_valuecat (p, ",")) {
			if (*buf)
				xmlBufferFree (*buf);
			*buf = nbuf;
		}
}/*}}}*/
static inline void
cat (xmlBufferPtr buf, const char *what, ...) /*{{{*/
{
	va_list	par;
	void	*ptr;
	int	n;
	
	va_start (par, what);
	for (n = 0; what[n]; ++n)
		if (ptr = va_arg (par, void *))
			if (what[n] == 's')
				xmlBufferCCat (buf, (const char *) ptr);
			else if (what[n] == 'b')
				xmlBufferAdd (buf, xmlBufferContent ((xmlBufferPtr) ptr), xmlBufferLength ((xmlBufferPtr) ptr));
	va_end (par);
}/*}}}*/
bool_t
blockmail_extract_mediatypes (blockmail_t *b) /*{{{*/
{
	int	n;
	bool_t	st;
	st = true;
	for (n = 0; n < b -> media_count; ++n) {
		media_t	*m = b -> media[n];
		
		switch (m -> type) {
		case MT_EMail:
			replace (& b -> email.subject, m, "subject");
			replace (& b -> email.from, m, "from");
			break;
		}
	}
	return st;
}/*}}}*/
