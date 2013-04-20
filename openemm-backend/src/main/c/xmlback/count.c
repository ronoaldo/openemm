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
# include	<string.h>
# include	"xmlback.h"

typedef struct { /*{{{*/
	char		*fname;		/* filename to write result to	*/
	/*}}}*/
}	count_t;

void *
count_oinit (blockmail_t *blockmail, var_t *opts) /*{{{*/
{
	count_t	*c;
	
	if (c = (count_t *) malloc (sizeof (count_t))) {
		var_t		*tmp;
		const char	*fname;
		
		fname = NULL;
		for (tmp = opts; tmp; tmp = tmp -> next)
			if ((! tmp -> var) || var_partial_imatch (tmp, "path"))
				fname = tmp -> val;
		c -> fname = NULL;
		if (fname && (! (c -> fname = strdup (fname)))) {
			free (c);
			c = NULL;
		}
	}
	return c;
}/*}}}*/
bool_t
count_odeinit (void *data, blockmail_t *blockmail, bool_t success) /*{{{*/
{
	count_t	*c = (count_t *) data;
	bool_t	st;
	
	st = false;
	if (success && c) {
		FILE		*fp;
		counter_t	*run;
		
		if (c -> fname)
			fp = fopen (c -> fname, "a");
		else
			fp = stdout;
		if (fp) {
			st = true;
			for (run = blockmail -> counter; run && st; run = run -> next) {
				long	mb = (run -> bytecount + 1024 * 1024 - 1) / (1024 * 1024);
				
				log_out (blockmail -> lg, LV_DEBUG, "%s/%d: %8ld Mail%s %8ld MByte%s",
					 run -> mediatype, run -> subtype,
					 run -> unitcount, (run -> unitcount == 1 ? ", " : "s,"),
					 mb, (mb == 1 ? "" : "s"));
				if (fprintf (fp, "%s\t%d\t%ld\t%lld\n",
					     run -> mediatype,
					     run -> subtype,
					     run -> unitcount,
					     run -> bytecount) == EOF) {
					log_out (blockmail -> lg, LV_ERROR, "Hit EOF on writing to %s (%m)", (c -> fname ? c -> fname : "*stdout*"));
					st = false;
				}
			}
			if (fp != stdout) {
				if (fclose (fp) == EOF) {
					log_out (blockmail -> lg, LV_ERROR, "Failed to close %s (%m)", c -> fname);
					st = false;
				}
			} else
				if (fflush (fp) == EOF) {
					log_out (blockmail -> lg, LV_ERROR, "Failed to flush stdout (%m)");
					st = false;
				}
		} else if (c -> fname)
			log_out (blockmail -> lg, LV_ERROR, "Unable to open file %s (%m)", c -> fname);
	} else
		st = true;
	if (c) {
		if (c -> fname)
			free (c -> fname);
		free (c);
	}
	return st;
}/*}}}*/
bool_t
count_owrite (void *data, blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	return true;
}/*}}}*/
