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
/** @file csig.c
 * Signal handling.
 */
# include	<stdlib.h>
# include	<stdarg.h>
# include	"agn.h"

# ifndef	NSIG
#  ifndef	_NSIG
#   define	NSIG		32
#  else		/* _NSIG */
#   define	NSIG		_NSIG
#  endif	/* _NSIG */
# endif		/* NSIG */

/** Information about one signal
 */
typedef struct ssig { /*{{{*/
	int			snr;	/**< signal number		*/
	struct sigaction	set;	/**< current information	*/
	struct sigaction	old;	/**< previous information	*/
	struct ssig		*next;	/**< next signal 		*/
	/*}}}*/
}	ssig_t;

/** Allocate new signal.
 * @param snr the signal number
 * @param hand the signal handler
 * @return new instance on success, NULL otherwise
 */
static ssig_t *
ssig_alloc (int snr, void (*hand) (int)) /*{{{*/
{
	ssig_t	*s;
	
	s = NULL;
	if ((snr < NSIG) && (s = (ssig_t *) malloc (sizeof (ssig_t)))) {
		s -> snr = snr;
		s -> set.sa_handler = hand;
		sigemptyset (& s -> set.sa_mask);
		s -> set.sa_flags = 0;
		s -> next = NULL;
	}
	return s;
}/*}}}*/
/** Frees signal.
 * Returns used resources to the system
 * @param s the signal instance to free
 * @return NULL
 */
static ssig_t *
ssig_free (ssig_t *s) /*{{{*/
{
	if (s) {
		free (s);
	}
	return NULL;
}/*}}}*/

/** Allocate signals.
 * Setup signal handler for signal/handler pairs and returns one
 * control structure for these handlers
 * @param signr Signal number
 * @param ... handler, (signr, handler)*, -1
 * @return newly allocated instance on success, NULL otherwise
 */
csig_t *
csig_alloc (int signr, ...) /*{{{*/
{
	va_list		par;
	void		(*hand) (int);
	csig_t		*c;
	bool_t		st;
	ssig_t		*prv, *cur;

	va_start (par, signr);
	if (c = (csig_t *) malloc (sizeof (csig_t))) {
		c -> base = NULL;
		sigemptyset (& c -> mask);
		c -> isblocked = false;
		st = true;
		prv = NULL;
		while (signr > 0) {
			hand = (void (*) (int)) va_arg (par, void *);
			if (! (cur = ssig_alloc (signr, hand))) {
				st = false;
				break;
			}
			sigaddset (& c -> mask, signr);
			if (prv)
				prv -> next = cur;
			else
				c -> base = cur;
			prv = cur;
			signr = va_arg (par, int);
		}
		if (st)
			for (cur = c -> base; cur && st; cur = cur -> next) {
				cur -> set.sa_mask = c -> mask;
				sigdelset (& cur -> set.sa_mask, cur -> snr);
				if (sigaction (cur -> snr, & cur -> set, & cur -> old) == -1)
					st = false;
			}
		if (! st)
			c = csig_free (c);
	}
	va_end (par);
	return c;
}/*}}}*/
/** Frees signal instance.
 * Returns the signal handlers resources to the system
 * @param c the signal handler instance
 * @return NULL
 */
csig_t *
csig_free (csig_t *c) /*{{{*/
{
	ssig_t	*tmp;
	
	if (c) {
		if (! c -> isblocked)
			csig_block (c);
		while (tmp = c -> base) {
			c -> base = tmp -> next;
			sigaction (tmp -> snr, & tmp -> old, NULL);
			ssig_free (tmp);
		}
		csig_unblock (c);
		free (c);
	}
	return NULL;
}/*}}}*/
/** Block signals.
 * Block the signals parts of this signal set
 * @param c the signal set
 */
void
csig_block (csig_t *c) /*{{{*/
{
	if (! c -> isblocked) {
		sigprocmask (SIG_BLOCK, & c -> mask, & c -> oldmask);
		c -> isblocked = true;
	}
}/*}}}*/
/** Unblock signals.
 * Remove a previous installed block for the signals
 * @param c the signal set
 */
void
csig_unblock (csig_t *c) /*{{{*/
{
	if (c -> isblocked) {
		c -> isblocked = false;
		sigprocmask (SIG_SETMASK, & c -> oldmask, NULL);
	}
}/*}}}*/
