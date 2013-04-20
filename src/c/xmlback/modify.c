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
# include	<ctype.h>
# include	"xmlback.h"

# define	ST_INITIAL		0
# define	ST_START_FOUND		(-1)
# define	ST_END_FOUND		(-2)
# define	SWAP(bbb)		do { xmlBufferPtr __temp = (bbb) -> in; (bbb) -> in = (bbb) -> out; (bbb) -> out = __temp; } while (0)

static bool_t
modify_urls (blockmail_t *blockmail, receiver_t *rec, block_t *block) /*{{{*/
{
	int		n;
	int		len;
	const xmlChar	*cont;
	int		lstore;
	int		state;
	char		initial;
	bool_t		ishtml;
	char		ch;
	int		start, end;
	int		mask;
	int		clen;
	bool_t		changed;

	xmlBufferEmpty (block -> out);
	len = xmlBufferLength (block -> in);
	cont = xmlBufferContent (block -> in);
	lstore = 0;
	state = ST_INITIAL;
	if (block -> tid == TID_EMail_Text) {
		initial = 'h';
		ishtml = false;
		mask = 1;
	} else if (block -> tid == TID_EMail_HTML) {
		initial = '<';
		ishtml = true;
		mask = 2;
	} else {
		initial = '\0';
		ishtml = false;
		mask = 0;
	}
	start = -1;
	end = -1;
	changed = false;
	for (n = 0; n <= len; ) {
		if (n < len) {
			clen = xmlCharLength (cont[n]);
			if ((clen > 1) || isascii ((char) cont[n])) {
				ch = clen == 1 ? (char) cont[n] : '\0';
				switch (state) {
				case ST_INITIAL:
					if (tolower (ch) == initial) {
						if (! ishtml) {
							state = 1;
							start = n;
						} else {
							state = 100;
						}
					}
					break;
# define	CHK(ccc)	do { if ((ccc) == ch) ++state; else state = ST_INITIAL; } while (0)
# define	CCHK(ccc)	do { if ((ccc) == tolower (ch)) ++state; else state = ST_INITIAL; } while (0)
				case 1:		CCHK ('t');	break;
				case 2:		CCHK ('t');	break;
				case 3:		CCHK ('p');	break;
				case 4:
					++state;
					if (tolower (ch) == 's')
						break;
					/* Fall through . . . */
				case 5:		CHK (':');	break;
				case 6:		CHK ('/');	break;
				case 7:
					if (ch == '/')
						state = ST_START_FOUND;
					else
						state = ST_INITIAL;
					break;
				case 100:	CCHK ('a');	break;
# define	HCHK(ccc)	do { if ((ccc) == tolower (ch)) ++state; else if ('>' == ch) state = ST_INITIAL; else state = 101; } while (0)
				case 101:	HCHK ('h');	break;
				case 102:	HCHK ('r');	break;
				case 103:	HCHK ('e');	break;
				case 104:	HCHK ('f');	break;
# undef		HCHK						
				case 105:	CHK ('=');	break;
				case 106:
					++state;
					if (ch == '"')
						break;
					/* Fall through . . */
				case 107:
					if (tolower (ch) == 'h') {
						state = 1;
						start = n;
					} else
						state = ST_INITIAL;
					break;
				case ST_START_FOUND:
					if (isspace ((int) ((unsigned char) ch)) ||
					    (ch == '"') ||
					    (ch == '>')) {
						end = n;
						state = ST_END_FOUND;
					}
					break;
				}
# undef		CHK
# undef		CCHK					
			} else {
				if (state == ST_START_FOUND) {
					end = n;
					state = ST_END_FOUND;
				} else
					state = ST_INITIAL;
			}
			n += clen;
		} else {
			if (state == ST_START_FOUND) {
				end = n;
				state = ST_END_FOUND;
			}
			++n;
		}
		if (state == ST_END_FOUND) {
			int	ulen, m, o;

			ulen = end - start;
			for (m = 0; m < blockmail -> url_count; ++m)
				if ((blockmail -> url[m] -> usage & mask) &&
				    (blockmail -> url[m] -> dlen == ulen) &&
				    (! xmlStrncmp (blockmail -> url[m] -> dptr, cont + start, ulen)))
					break;
			if (lstore < start) {
				xmlBufferAdd (block -> out, cont + lstore, start - lstore);
				lstore = start;
			}
			if (m < blockmail -> url_count) {
				for (o = 0; o < rec -> url_count; ++o)
					if (rec -> url[o] -> uid == blockmail -> url[m] -> uid)
						break;
				if (o < rec -> url_count)
					xmlBufferAdd (block -> out, rec -> url[o] -> dptr, rec -> url[o] -> dlen);
				lstore = end;
				changed = true;
			}
			state = ST_INITIAL;
		}
	}
	if (changed) {
		if (lstore < len)
			xmlBufferAdd (block -> out, cont + lstore, len - lstore);
		SWAP (block);
	}
	return true;
}/*}}}*/
static bool_t
collect_links (blockmail_t *blockmail, block_t *block, links_t *links) /*{{{*/
{
	bool_t		rc;
	int		n, clen;
	int		len;
	const xmlChar	*cont;
	int		start;
	
	rc = true;
	len = xmlBufferLength (block -> in);
	cont = xmlBufferContent (block -> in);
	for (n = 0; rc && (n < len); ) {
		clen = xmlCharLength (cont[n]);
		if ((clen == 1) && (cont[n] == 'h')) {
			start = n;
			++n;
			if ((n + 3 < len) && (cont[n] == 't') && (cont[n + 1] == 't') && (cont[n + 2] == 'p')) {
				n += 3;
				if ((n + 1 < len) && (cont[n] == 's'))
					++n;
				if ((n + 3 < len) && (cont[n] == ':') && (cont[n + 1] == '/') && (cont[n + 2] == '/')) {
					n += 3;
					while ((n < len) && (xmlCharLength (cont[n]) == 1) &&
					       (cont[n] != '"') && (cont[n] != '<') && (cont[n] != '>') &&
					       (! isspace (cont[n])))
						++n;
					rc = links_nadd (links, (const char *) (cont + start), n - start);
				}
			}
		} else
			n += clen;
	}
	return rc;
}/*}}}*/
static int
find_top (const xmlChar *cont, int len) /*{{{*/
{
	int		pos;
	int		state;
	int		n;
	int		clen;
	unsigned char	ch;

	for (pos = -1, state = 0, n = 0; (n < len) && (pos == -1); ) {
		clen = xmlCharLength (cont[n]);
		if (clen > 1)
			state = 0;
		else {
			ch = cont[n];

			switch (state) {
			case 0:
				if (ch == '<')
					state = 1;
				break;
			case 1:
				if ((ch == 'b') || (ch == 'B'))
					state = 2;
				else if (ch == '>')
					state = 0;
				else if (! isspace (ch))
					state = 100;
				break;
			case 2:
				if ((ch == 'o') || (ch == 'O'))
					state = 3;
				else if (ch == '>')
					state = 0;
				else
					state = 100;
				break;
			case 3:
				if ((ch == 'd') || (ch == 'D'))
					state = 4;
				else if (ch == '>')
					state = 0;
				else
					state = 100;
				break;
			case 4:
				if ((ch == 'y') || (ch == 'Y'))
					state = 5;
				else if (ch == '>')
					state = 0;
				else
					state = 100;
				break;
			case 5:
				if (ch == '>') {
					pos = n + clen;
					state = 0;
				} else if (isspace (ch))
					state = 6;
				else
					state = 100;
				break;
			case 6:
				if (ch == '>') {
					pos = n + clen;
					state = 0;
				}
# ifdef		STRICT				
				else if (ch == '"')
					state = 7;
				break;
			case 7:
				if (ch == '"')
					state = 6;
# endif		/* STRICT */
				break;
			case 100:
				if (ch == '>')
					state = 0;
				break;
			}
		}
		n += clen;
	}
	return pos;
}/*}}}*/
static int
find_bottom (const xmlChar *cont, int len) /*{{{*/
{
	int	pos;
	int	last;
	int	m;
	int	bclen;
	
	for (pos = -1, last = len, m = len - 1; (m >= 0) && (pos == -1); ) {
		bclen = xmlCharLength (cont[m]);
		if ((bclen == 1) && (cont[m] == '<')) {
			int		n;
			int		state;
			int		clen;
			unsigned char	ch;

			for (n = m + bclen, state = 1; (n < last) && (state > 0) && (state != 99); ) {
				clen = xmlCharLength (cont[n]);
				if (clen != 1)
					state = 0;
				else {
					ch = cont[n];
					switch (state) {
					case 1:
						if (ch == '/')
							state = 2;
						else if (! isspace (ch))
							state = 0;
						break;
					case 2:
						if ((ch == 'b') || (ch == 'B'))
							state = 3;
						else if (! isspace (ch))
							state = 0;
						break;
					case 3:
						if ((ch == 'o') || (ch == 'O'))
							state = 4;
						else
							state = 0;
						break;
					case 4:
						if ((ch == 'd') || (ch == 'D'))
							state = 5;
						else
							state = 0;
						break;
					case 5:
						if ((ch == 'y') || (ch == 'Y'))
							state = 6;
						else
							state = 0;
						break;
					case 6:
						if ((ch == '>') || isspace (ch))
							state = 99;
						else
							state = 0;
						break;
					}
				}
				n += clen;
			}
			if (state == 99)
				pos = m;
		} else if ((bclen == 1) && (cont[m] == '>'))
			last = m + bclen;
		m -= bclen;
	}
	return pos;
}/*}}}*/
static bool_t
add_onepixellog_image (blockmail_t *blockmail, receiver_t *rec, block_t *block, opl_t opl) /*{{{*/
{
	bool_t		rc;
	const xmlChar	tname[] = "[agnONEPIXEL]";
	int		tlen = sizeof (tname) - 1;
	tag_t		*opltag;
	
	rc = true;
	for (opltag = rec -> tag; opltag; opltag = opltag -> next)
		if (tag_match (opltag, tname, tlen))
			break;
	if (opltag && opltag -> value) {
		int		pos;
		int		len;
		const xmlChar	*cont;
		
		pos = -1;
		len = xmlBufferLength (block -> in);
		cont = xmlBufferContent (block -> in);
		switch (opl) {
		case OPL_None:
			break;
		case OPL_Top:
			pos = find_top (cont, len);
			if (pos == -1)
				pos = 0;
			break;
		case OPL_Bottom:
			pos = find_bottom (cont, len);
			if (pos == -1)
				pos = len;
			break;
		}
		if (pos != -1) {
			const xmlChar	lprefix[] = "<img src=\"";
			const xmlChar	lpostfix[] = "\" alt=\"\" border=\"0\" height=\"1\" width=\"1\">";
			
			xmlBufferEmpty (block -> out);
			if (pos > 0)
				xmlBufferAdd (block -> out, cont, pos);
			xmlBufferAdd (block -> out, lprefix, sizeof (lprefix) - 1);
			xmlBufferAdd (block -> out, xmlBufferContent (opltag -> value), xmlBufferLength (opltag -> value));
			xmlBufferAdd (block -> out, lpostfix, sizeof (lpostfix) - 1);
			if (pos < len)
				xmlBufferAdd (block -> out, cont + pos, len - pos);
			SWAP (block);
		}
	}
	return rc;
}/*}}}*/
static
# ifdef		__OPTIMIZE__
inline
# endif		/* __OPTIMIZE__ */
bool_t
islink (const xmlChar *str, int len) /*{{{*/
{
	int	n, state, clen;
	
	for (n = 0, state = 1; (n < len) && state; ) {
		clen = xmlCharLength (str[n]);
		if (clen != 1)
			return false;
		switch (state) {
		default: /* should NEVER happen */
			return false;
		case 1:	/* check for http:// https:// and mailto: */
			if ((str[n] == 'h') || (str[n] == 'H'))
				++state;
			else if ((str[n] == 'm') || (str[n] == 'M'))
				state = 100;
			else
				return false;
			break;
		case 2:
			if ((str[n] == 't') || (str[n] == 'T'))
				++state;
			else
				return false;
			break;
		case 3:
			if ((str[n] == 't') || (str[n] == 'T'))
				++state;
			else
				return false;
			break;
		case 4:
			if ((str[n] == 'p') || (str[n] == 'P'))
				++state;
			else
				return false;
			break;
		case 5:
			if ((str[n] == 's') || (str[n] == 'S'))
				++state;
			else if (str[n] == ':')
				state += 2;
			else
				return false;
			break;
		case 6:
			if (str[n] == ':')
				++state;
			else
				return false;
			break;
		case 7:
			if (str[n] == '/')
				++state;
			else
				return false;
			break;
		case 8:
			if (str[n] == '/')
				state = 0;
			else
				return false;
			break;
		case 100:
			if ((str[n] == 'a') || (str[n] == 'A'))
				++state;
			else
				return false;
			break;
		case 101:
			if ((str[n] == 'i') || (str[n] == 'I'))
				++state;
			else
				return false;
			break;
		case 102:
			if ((str[n] == 'l') || (str[n] == 'L'))
				++state;
			else
				return false;
			break;
		case 103:
			if ((str[n] == 't') || (str[n] == 'T'))
				++state;
			else
				return false;
			break;
		case 104:
			if ((str[n] == 'o') || (str[n] == 'O'))
				++state;
			else
				return false;
			break;
		case 105:
			if (str[n] == ':')
				state = 0;
			else
				return false;
			break;
		}
		n += clen;
	}
	return (! state) ? true : false;
}/*}}}*/
static bool_t
modify_linelength (blockmail_t *blockmail, block_t *block, blockspec_t *bspec) /*{{{*/
{
# define	DOIT_NONE	(0)
# define	DOIT_RESET	(1 << 0)
# define	DOIT_NEWLINE	(1 << 1)
# define	DOIT_IGNORE	(1 << 2)
# define	DOIT_SKIP	(1 << 3)	
	int		n;
	int		len;
	const xmlChar	*cont;
	int		spos, slen;
	int		space, dash;
	int		spchr, dachr;
	int		inspace, spacecount;
	int		llen, wordstart;
	int		doit;
	int		skipcount;
	bool_t		changed;

	xmlBufferEmpty (block -> out);
	len = xmlBufferLength (block -> in);
	cont = xmlBufferContent (block -> in);
	spos = 0;
	space = -1;
	dash = -1;
	spchr = -1;
	dachr = -1;
	inspace = 0;
	spacecount = 0;
	llen = 0;
	wordstart = 0;
	doit = DOIT_NONE;
	skipcount = 0;
	changed = false;
	for (n = 0; n < len; ) {
		if ((cont[n] == '\r') || (cont[n] == '\n')) {
			doit = DOIT_RESET;
			if (inspace && (spchr + inspace == llen)) {
				n = space;
				doit |= DOIT_NEWLINE | DOIT_IGNORE | DOIT_SKIP;
				skipcount = inspace + 1;
			}
		} else {
			if ((cont[n] == ' ') || (cont[n] == '\t')) {
				if (! inspace++) {
					space = n;
					spchr = llen;
				}
				spacecount = inspace;
			} else {
				if (inspace) {
					inspace = 0;
					wordstart = n;
				}
				if ((cont[n] == '-') && (llen > 2) &&
				    ((spchr == -1) || (llen - spchr > 2)) &&
				    (! islink (cont + wordstart, n - wordstart))) {
					dash = n;
					dachr = llen;
				}
			}
			if (++llen >= bspec -> linelength)
				if ((space != -1) || (dash != -1)) {
					if (space > dash) {
						if (! inspace) {
							n = space;
							doit = DOIT_RESET | DOIT_NEWLINE | DOIT_IGNORE | DOIT_SKIP;
							skipcount = spacecount;
						}
					} else {
						n = dash;
						doit = DOIT_RESET | DOIT_NEWLINE;
					}
				}
		}
		if (! (doit & DOIT_IGNORE))
			n += xmlCharLength (cont[n]);
		if (doit) {
			slen = n - spos;
			if (slen > 0)
				xmlBufferAdd (block -> out, cont + spos, slen);
			if (doit & DOIT_NEWLINE) {
				xmlBufferAdd (block -> out, bspec -> linesep, bspec -> seplength);
				changed = true;
			}
			if (doit & DOIT_SKIP) {
				while ((skipcount-- > 0) && (n < len))
					n += xmlCharLength (cont[n]);
				changed = true;
			}
			spos = n;
			space = -1;
			dash = -1;
			spchr = -1;
			dachr = -1;
			inspace = 0;
			spacecount = 0;
 			llen = 0;
			wordstart = n;
			doit = DOIT_NONE;
		}
	}
	if (changed) {
		if (spos < len)
			xmlBufferAdd (block -> out, cont + spos, len - spos);
		SWAP (block);
	}
	return true;
# undef		DOIT_NONE
# undef		DOIT_RESET
# undef		DOIT_NEWLINE
# undef		DOIT_IGNORE
# undef		DOIT_SKIP
}/*}}}*/
bool_t
modify_output (blockmail_t *blockmail, receiver_t *rec, block_t *block, blockspec_t *bspec, links_t *links) /*{{{*/
{
	bool_t	rc;
	
	rc = true;
	if (rc &&
	    (((block -> tid == TID_EMail_Text) && (blockmail -> url_usage & 1)) ||
	     ((block -> tid == TID_EMail_HTML) && (blockmail -> url_usage & 2))))
		rc = modify_urls (blockmail, rec, block);
	if (rc &&
	    (block -> tid == TID_EMail_HTML) &&
	    links)
		rc = collect_links (blockmail, block, links);
	if (rc && (block -> tid == TID_EMail_HTML) && (bspec -> opl != OPL_None))
		rc = add_onepixellog_image (blockmail, rec, block, bspec -> opl);
	if (rc && (bspec -> linelength > 0) && bspec -> linesep)
		rc = modify_linelength (blockmail, block, bspec);
	return rc;
}/*}}}*/
