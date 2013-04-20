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
# ifndef	__XMLBACK_H
# define	__XMLBACK_H		1
# include	<stdio.h>
# include	<libxml/parser.h>
# include	<libxml/parserInternals.h>
# include	<libxml/xmlmemory.h>
# include	<libxml/xmlerror.h>
# include	"agn.h"
# include	"xml.h"


/*
 * These macros are used for variables with these properties
 * (assuming type = XXX):
 * 1.) The type fo the variable is XXX_t
 * 2.) It is part of the structure `what'
 * 3.) The structure has a member XXX declared as `XXX_t **XXX;'
 * 4.) The structure has a memeber XXX_count of type int
 *     (currently used elements in XXX)
 * 5.) The structure has a memeber XXX_size of type int
 *     (currently allocated space in XXX)
 * 
 * Special notice per macro:
 * - DO_EXPAND
 *   * grab is of type `XXX_t *' and is the next free slot (on success)
 *     or NULL on failure
 * - DO_SHRINK
 *   * removes and frees the last element in the array. A function with
 *     the prototype `XXX_t * XXX_free (XXX_t *);' is required to free
 *     that element
 *//*{{{*/
# define	DO_DECL(type)											\
		int		type ## _count;									\
		int		type ## _size;									\
		type ## _t	**type
# define	DO_ZERO(what, type)										\
		do {												\
			(what) -> type ## _count = 0;								\
			(what) -> type ## _size = 0;								\
			(what) -> type = NULL;									\
		}	while (0);
# define	DO_EXPAND(grab, what, type) 									\
		do {												\
			if ((what) -> type ## _count >= (what) -> type ## _size) {				\
				int		__tmpsize;							\
				type ## _t	**__tmp;							\
														\
				__tmpsize = ((what) -> type ## _size ? ((what) -> type ## _size << 1) : 8);	\
				if (__tmp = (type ## _t **)							\
				    realloc ((what) -> type, __tmpsize * sizeof (type ## _t *))) {		\
					(what) -> type = __tmp;							\
					(what) -> type ## _size = __tmpsize;					\
				}										\
			}											\
			(grab) = NULL;										\
			if (((what) -> type ## _count < (what) -> type ## _size) &&				\
			    ((what) -> type[(what) -> type ## _count] = type ## _alloc ()))			\
				(grab) = (what) -> type[(what) -> type ## _count++];				\
		}	while (0)
# define	DO_SHRINK(what, type)										\
		do {												\
			if ((what) -> type ## _count > 0) {							\
				(what) -> type ## _count--;							\
				if ((what) -> type[(what) -> type ## _count])					\
					(what) -> type[(what) -> type ## _count] =				\
						type ## _free ((what) -> type[(what) -> type ## _count]);	\
			}											\
		}	while (0);
# define	DO_CLEAR(what, type)										\
		do {												\
			if ((what) -> type) {									\
				int	__tmp;									\
														\
				for (__tmp = 0; __tmp < (what) -> type ## _count; ++__tmp)			\
					if ((what) -> type[__tmp])						\
						what -> type[__tmp] = type ## _free ((what) -> type[__tmp]);	\
			}											\
			(what) -> type ## _count = 0;								\
		}	while (0)
# define	DO_FREE(what, type)										\
		do {												\
			DO_CLEAR ((what), type);								\
			if ((what) -> type)									\
				free ((what) -> type);								\
			DO_ZERO ((what), type);									\
		}	while (0)
/*}}}*/

/* Tagpositions types */
# define	TP_NONE			0
# define	TP_DYNAMIC		(1 << 0)
# define	TP_DYNAMICVALUE		(1 << 1)
# define	IS_DYNAMIC(xxx)		((xxx) & (TP_DYNAMIC | TP_DYNAMICVALUE))

/* evaluation spheres */
# define	SP_DYNAMIC		0
# define	SP_BLOCK		1

/* Mailtype bits */
# define	MAILTYPE_HTML		(1 << 0)
# define	MAILTYPE_HTML_OFFLINE	(1 << 1)

typedef enum { /*{{{*/
	EncNone,
	EncHeader,
	Enc8bit,
	EncQuotedPrintable,
	EncBase64
	/*}}}*/
}	encoding_t;

typedef enum { /*{{{*/
	MT_Unspec = -1,
	MT_EMail = 0
	/*}}}*/
}	mtype_t;
typedef enum { /*{{{*/
	MS_Unused = 0,
	MS_Inactive = 1,
	MS_Active = 2
	/*}}}*/
}	mstat_t;
typedef struct pval { /*{{{*/
	xmlBufferPtr	v;	/* the value itself			*/
	struct pval	*next;
	/*}}}*/
}	pval_t;
typedef struct parm { /*{{{*/
	char		*name;	/* variable name			*/
	pval_t		*value;	/* list of variable values		*/
	struct parm	*next;
	/*}}}*/
}	parm_t;
typedef struct { /*{{{*/
	mtype_t		type;	/* output type				*/
	long		prio;	/* priority				*/
	mstat_t		stat;	/* status				*/
	parm_t		*parm;	/* all parameter			*/
	/*}}}*/
}	media_t;
typedef struct output		output_t;
typedef struct block		block_t;
typedef struct blockmail	blockmail_t;
typedef struct receiver		receiver_t;
typedef struct { /*{{{*/
	xmlBufferPtr	name;		/* the name of this position	*/
	long		hash;		/* the hashvalue		*/
	long		start, end;	/* start/end of the occurance	*/
	unsigned long	type;		/* type of tag position		*/
	char		*tname;		/* optional the name part	*/
	block_t		*content;	/* optional content for blocks	*/
	/*}}}*/
}	tagpos_t;
typedef enum { /*{{{*/
	TID_Unspec = 0,
	TID_EMail_Head = 1,
	TID_EMail_Text = 2,
	TID_EMail_HTML = 3
	/*}}}*/
}	tid_t;

struct block { /*{{{*/
	int		bid;		/* the unique blockID		*/
	int		nr;		/* the passed blocknumber	*/
	char		*mime;		/* mime type			*/
	char		*charset;	/* the used character set	*/
	char		*encode;	/* output encoding		*/
	encoding_t	method;		/* the real encoding method	*/
	char		*cid;		/* content ID			*/
	tid_t		tid;		/* type ID, get from cid	*/
	bool_t		binary;		/* if this is a binary		*/
	bool_t		attachment;	/* if this is an attachment	*/
	char		*media;		/* related to which media?	*/
	mtype_t		mediatype;	/* the mediatype		*/
	xmlBufferPtr	condition;	/* the condition for this block	*/
	xmlBufferPtr	content;	/* content in UTF-8 as parsed	*/
	xmlCharEncodingHandlerPtr
			translate;	/* translate UTF-8 to the ..	*/
					/* .. charset of this block	*/
	xmlBufferPtr	in, out;	/* temp. buffers for converting	*/
	buffer_t	*bcontent;	/* the converted binary content	*/
	buffer_t	*bout;		/* encoded binary content	*/
	DO_DECL (tagpos);		/* all tags with position in ..	*/
					/* .. content			*/
	bool_t		inuse;		/* required during generation	*/
	/*}}}*/
};

typedef struct blockspec	blockspec_t;

typedef struct { /*{{{*/
	xmlBufferPtr	cont;		/* content w/o attachments	*/
	xmlBufferPtr	acont;		/* content w/ attachments	*/
	/*}}}*/
}	fix_t;
typedef struct postfix { /*{{{*/
	fix_t		*c;		/* content for the postfix	*/
	char		*pid;		/* postfix ID			*/
	int		after;		/* output after which block	*/
	struct blockspec
			*ref;		/* backreference to blockspec	*/
	struct postfix	*stack;		/* for stack output		*/
	/*}}}*/
}	postfix_t;
typedef enum {
	OPL_None = 0,
	OPL_Top = 1,
	OPL_Bottom = 2
}	opl_t;
struct blockspec { /*{{{*/
	int		nr;		/* the block to use		*/
	block_t		*block;		/* reference to block		*/
	fix_t		*prefix;	/* optional prefix and ..	*/
	DO_DECL (postfix);		/* .. postfix buffer		*/
	/* optional modifiers						*/
	int		linelength;	/* if >0, enforce linebreaks	*/
	xmlChar		*linesep;	/* the line separator		*/
	int		seplength;	/* the length of the line sep.	*/
	opl_t		opl;		/* insert onepixel URLs		*/
	/*}}}*/
};
typedef struct { /*{{{*/
	char		*ident;		/* the mailtype identifier	*/
	int		idnr;		/* numeric value of ident	*/
	bool_t		offline;	/* is this a offline html?	*/
	DO_DECL (blockspec);
	/*}}}*/
}	mailtype_t;
typedef struct tag { /*{{{*/
	xmlBufferPtr	name;		/* the name of the tag		*/
	char		*cname;		/* normalized name		*/
	long		hash;		/* the hashvalue		*/
	char		*type;		/* internal tag type		*/
	char		*topt;		/* itnernal tag options		*/
	xmlBufferPtr	value;		/* the value for this user	*/
	var_t		*parm;		/* parsed parameter		*/
	bool_t		used;		/* marker to avoid loops	*/
	struct tag	*next;
	/*}}}*/
}	tag_t;
typedef struct dyn { /*{{{*/
	int		did;		/* unique ID			*/
	char		*name;		/* name of the dynamic part	*/
	int		order;		/* the order in the chain	*/
	xmlBufferPtr	condition;	/* the condition for this part	*/
	DO_DECL (block);		/* the content of the dyn part	*/
	struct dyn	*sibling;	/* all parts with the same name	*/
	struct dyn	*next;		/* next part chain		*/
	/*}}}*/
}	dyn_t;

typedef struct { /*{{{*/
	long		uid;		/* unique URL ID		*/
	xmlBufferPtr	dest;		/* destination (the url itself)	*/
	const xmlChar	*dptr;		/* pointer into destination	*/
	int		dlen;		/* length of destination	*/
	int		usage;		/* to use in which part?	*/
	/*}}}*/
}	url_t;

typedef struct { /*{{{*/
	char		*name;		/* name of the field		*/
	char		*lname;		/* lower case variant of name	*/
	char		type;		/* type of the field		*/
	/*}}}*/
}	field_t;

typedef struct { /*{{{*/
	void		*e;		/* evaluator specific data	*/
	blockmail_t	*blockmail;	/* reference to blockmail	*/
	bool_t		in_condition;	/* we have got conditions	*/
	bool_t		in_variables;	/* we have got variables	*/
	bool_t		in_data;	/* we have got data		*/
	bool_t		in_match;	/* we are doing matches		*/
	/*}}}*/
}	eval_t;

typedef struct counter { /*{{{*/
	char		*mediatype;	/* the mediatype		*/
	int		subtype;	/* subtype, e.g. the mailtype	*/
	long		unitcount;	/* # of units deliviered	*/
	long long	bytecount;	/* # of bytes delivered		*/
	struct counter	*next;
	/*}}}*/
}	counter_t;
typedef struct rblock { /*{{{*/
	tid_t		tid;		/* type ID			*/
	char		*bname;		/* name of this block		*/
	xmlBufferPtr	content;	/* content of the block		*/
	struct rblock	*next;
	/*}}}*/
}	rblock_t;
struct blockmail { /*{{{*/
	/* internal used only data */
	const char	*fname;		/* current filename		*/
	char		syfname[PATH_MAX + 1];	/* sync filename	*/
	bool_t		syeof;		/* if we hit EOF		*/
	FILE		*syfp;		/* filepointer to sync file	*/
	xmlBufferPtr	in, out;	/* temp. in/out buffer		*/
	xmlCharEncodingHandlerPtr
			translate;	/* required in parsing		*/
	log_t		*lg;		/* logging interface		*/
	eval_t		*eval;		/* to interpret dynamic content	*/
	/* output related data */
	bool_t		usecrlf;	/* use CRLF or LF on output	*/
	bool_t		raw;		/* just generate raw output	*/
	output_t	*output;	/* output information		*/
	void		*outputdata;	/* output related private data	*/
	counter_t	*counter;	/* counter for created mails	*/
	bool_t		active;		/* if user is active		*/
	buffer_t	*head;		/* the created head ..		*/
	buffer_t	*body;		/* .. and body			*/
	rblock_t	*rblocks;	/* the raw blocks		*/

	/*
	 * from here, the data is from the input file or from dynamic enviroment
	 */
	/* description part */
	int		company_id;
	int		mailinglist_id;
	int		mailing_id;
	xmlBufferPtr	mailing_name;
	int		maildrop_status_id;
	char		status_field;
	/* general part */
	xmlBufferPtr	profile_url;
	xmlBufferPtr	unsubscribe_url;
	xmlBufferPtr	auto_url;
	xmlBufferPtr	onepixel_url;
	xmlBufferPtr	password;
	long		total_subscribers;
	struct {
		xmlBufferPtr	subject;
		xmlBufferPtr	from;
	}	email;
	/* mailcreation part */
	int		blocknr;
	char		*innerboundary;
	char		*outerboundary;
	char		*attachboundary;
	
	/* the more complex parts */
	
	/* blocks */
	DO_DECL (block);
	/* media */
	DO_DECL (media);
	/* mail types */
	DO_DECL (mailtype);
	/* all known tags */
	tag_t		*ltag;
	int		taglist_count;
	/* global tags */
	tag_t		*gtag;
	int		globaltag_count;
	/* dynamic definitions */
	dyn_t		*dyn;
	int		dynamic_count;
	xmlBufferPtr	mtbuf[2];
	
	/* URLs in the mailing */
	DO_DECL (url);
	int		url_usage;

	/* layout of the customer table */
	DO_DECL (field);
	int		mailtype_index;
	
	/* counter for receivers */
	int		receiver_count;
	/* our full qualified domain name */
	char		*fqdn;
	/* cache for conversion functions */
	xconv_t		*xconv;
	/*}}}*/
};

typedef struct dcache { /*{{{*/
	const char	*name;		/* for which dynamic part	*/
	const dyn_t	*dyn;		/* the resolved part itself	*/
	struct dcache	*next;
	/*}}}*/
}	dcache_t;
struct receiver { /*{{{*/
	int		customer_id;	/* customer id, as in the dbase	*/
	char		user_type;	/* user type for mailing	*/
	xmlBufferPtr	to_email;	/* receiver e-mail address	*/
	xmlBufferPtr	message_id;	/* the message id to use	*/
	int		mailtype;	/* which mailtype to use	*/
	char		*mediatypes;	/* permission for which medias?	*/
	media_t		*media;		/* pointer to found media	*/
	char		mid[32];	/* media ID			*/
	tag_t		*tag;		/* all tag informations		*/
	DO_DECL (url);			/* the coded URLs		*/
	xmlBufferPtr	*data;		/* receivers data content	*/
	bool_t		*dnull;		/* which data is a SQL NULL	*/
	int		dsize;		/* room in data			*/
	int		dpos;		/* current position		*/
	dcache_t	*cache;		/* dynamic cache		*/
	/*}}}*/
};

typedef struct { /*{{{*/
	char	**l;
	bool_t	*seen;
	int	lcnt, lsiz;
	/*}}}*/
}	links_t;

struct output { /*{{{*/
	const char	*name;
	const char	*desc;
	bool_t		syncfile;
	void		*(*oinit) (blockmail_t *, var_t *);
	bool_t		(*odeinit) (void *, blockmail_t *, bool_t);
	bool_t		(*owrite) (void *, blockmail_t *, receiver_t *);
	/*}}}*/
};

extern bool_t		parse_file (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base);
extern bool_t		create_output (blockmail_t *blockmail, receiver_t *rec);
extern bool_t		replace_tags (blockmail_t *blockmail, receiver_t *rec, block_t *block,
				      const xmlChar *(*replace) (const xmlChar *, int, int *),
				      bool_t ishtml);
extern bool_t		modify_output (blockmail_t *blockmail, receiver_t *rec, block_t *block, blockspec_t *bspec, links_t *links);
extern int		convert_block (xmlCharEncodingHandlerPtr translate, xmlBufferPtr in, xmlBufferPtr out, bool_t isoutput);
extern bool_t		convert_charset (blockmail_t *blockmail, block_t *block);
extern bool_t		append_mixed (buffer_t *dest, const char *desc, ...);
extern bool_t		append_pure (buffer_t *dest, const xmlBufferPtr src);
extern bool_t		append_raw (buffer_t *dest, const buffer_t *src);
extern bool_t		append_cooked (buffer_t *dest, const xmlBufferPtr src,
				       const char *charset, encoding_t method);

extern tagpos_t		*tagpos_alloc (void);
extern tagpos_t		*tagpos_free (tagpos_t *t);
extern void		tagpos_find_name (tagpos_t *t);
extern block_t		*block_alloc (void);
extern block_t		*block_free (block_t *b);
extern bool_t		block_setup_charset (block_t *b);
extern void		block_find_method (block_t *b);
extern bool_t		block_code_binary_out (block_t *b);
extern bool_t		block_code_binary (block_t *b);
extern bool_t		block_match (block_t *b, eval_t *eval);
extern pval_t		*pval_alloc (void);
extern pval_t		*pval_free (pval_t *p);
extern pval_t		*pval_free_all (pval_t *p);
extern parm_t		*parm_alloc (void);
extern parm_t		*parm_free (parm_t *p);
extern parm_t		*parm_free_all (parm_t *p);
extern xmlBufferPtr	parm_valuecat (parm_t *p, const char *sep);
extern media_t		*media_alloc (void);
extern media_t		*media_free (media_t *m);
extern bool_t		media_set_type (media_t *m, const char *type);
extern bool_t		media_set_priority (media_t *m, long prio);
extern bool_t		media_set_status (media_t *m, const char *status);
extern parm_t		*media_find_parameter (media_t *m, const char *name);
extern void		media_postparse (media_t *m, blockmail_t *blockmail);
extern bool_t		media_parse_type (const char *str, mtype_t *type);
extern const char	*media_typeid (mtype_t type);
extern fix_t		*fix_alloc (void);
extern fix_t		*fix_free (fix_t *f);
extern postfix_t	*postfix_alloc (void);
extern postfix_t	*postfix_free (postfix_t *p);
extern blockspec_t	*blockspec_alloc (void);
extern blockspec_t	*blockspec_free (blockspec_t *b);
extern bool_t		blockspec_set_lineseparator (blockspec_t *b, const xmlChar *sep, int slen);
extern bool_t		blockspec_find_lineseparator (blockspec_t *b);
extern mailtype_t	*mailtype_alloc (void);
extern mailtype_t	*mailtype_free (mailtype_t *m);
extern counter_t	*counter_alloc (const char *mediatype, int subtype);
extern counter_t	*counter_free (counter_t *c);
extern counter_t	*counter_free_all (counter_t *c);
extern rblock_t		*rblock_alloc (tid_t tid, const char *bname, xmlBufferPtr content);
extern rblock_t		*rblock_free (rblock_t *r);
extern rblock_t		*rblock_free_all (rblock_t *r);
extern bool_t		rblock_set_name (rblock_t *r, const char *bname);
extern bool_t		rblock_set_content (rblock_t *r, xmlBufferPtr content);
extern bool_t		rblock_retreive_content (rblock_t *r, buffer_t *content);
extern blockmail_t	*blockmail_alloc (const char *fname, bool_t syncfile, log_t *lg);
extern blockmail_t	*blockmail_free (blockmail_t *b);
extern bool_t		blockmail_count (blockmail_t *b, const char *mediatype, int subtype, long bytes);
extern void		blockmail_count_sort (blockmail_t *b);
extern void		blockmail_unsync (blockmail_t *b);
extern bool_t		blockmail_insync (blockmail_t *b, int cid, const char *mediatype, int subtype);
extern bool_t		blockmail_tosync (blockmail_t *b, int cid, const char *mediatype, int subtype);
extern bool_t		blockmail_extract_mediatypes (blockmail_t *b);
extern tag_t		*tag_alloc (void);
extern tag_t		*tag_free (tag_t *t);
extern tag_t		*tag_free_all (tag_t *t);
extern void		tag_parse (tag_t *t, blockmail_t *blockmail);
extern bool_t		tag_match (tag_t *t, const xmlChar *name, int nlen);
extern const xmlChar	*tag_content (tag_t *t, blockmail_t *blockmail, receiver_t *rec, int *length);
extern dyn_t		*dyn_alloc (int did, int order);
extern dyn_t		*dyn_free (dyn_t *d);
extern dyn_t		*dyn_free_all (dyn_t *d);
extern bool_t		dyn_match (const dyn_t *d, eval_t *eval);
extern url_t		*url_alloc (void);
extern url_t		*url_free (url_t *u);
extern void		url_set_destination (url_t *u, xmlBufferPtr dest);
extern bool_t		url_copy_destination (url_t *u, xmlBufferPtr dest);
extern field_t		*field_alloc (void);
extern field_t		*field_free (field_t *f);
extern bool_t		field_normalize_name (field_t *f);
extern dcache_t		*dcache_alloc (const char *name, const dyn_t *dyn);
extern dcache_t		*dcache_free (dcache_t *d);
extern dcache_t		*dcache_free_all (dcache_t *d);
extern receiver_t	*receiver_alloc (int data_blocks);
extern receiver_t	*receiver_free (receiver_t *r);
extern void		receiver_clear (receiver_t *r);
extern links_t		*links_alloc (void);
extern links_t		*links_free (links_t *l);
extern bool_t		links_expand (links_t *l);
extern bool_t		links_nadd (links_t *l, const char *lnk, int llen);


/*
 * some support routines
 */
extern bool_t		decode_base64 (const xmlBufferPtr src, buffer_t *dest);
extern bool_t		encode_none (const xmlBufferPtr src, buffer_t *dest);
extern bool_t		encode_header (const xmlBufferPtr src, buffer_t *dest, const char *charset);
extern bool_t		encode_8bit (const xmlBufferPtr src, buffer_t *dest);
extern bool_t		encode_quoted_printable (const xmlBufferPtr src, buffer_t *dest);
extern bool_t		encode_base64 (const xmlBufferPtr src, buffer_t *dest);

# ifndef	__OPTIMIZE__
extern bool_t		xmlEqual (xmlBufferPtr p1, xmlBufferPtr p2);
extern int		xmlCharLength (xmlChar ch);
extern int		xmlStrictCharLength (xmlChar ch);
extern int		xmlValidPosition (const xmlChar *str, int length);
extern bool_t		xmlValid (const xmlChar *str, int length);
extern char		*xml2string (xmlBufferPtr p);
extern const char	*xml2char (const xmlChar *s);
extern const xmlChar	*char2xml (const char *s);
extern const char	*byte2char (const byte_t *b);
extern int		xmlstrcmp (const xmlChar *s1, const char *s2);
extern int		xmlstrncmp (const xmlChar *s1, const char *s2, size_t n);
extern long		xml2long (xmlBufferPtr p);
# else		/* __OPTIMIZE__ */
# define	I	static inline
# include	"misc.c"
# undef		I
# endif		/* __OPTIMIZE__ */
extern bool_t		xmlSQLlike (const xmlChar *pattern, int plen,
				    const xmlChar *string, int slen);


extern eval_t		*eval_alloc (blockmail_t *blockmail);
extern eval_t		*eval_free (eval_t *e);
extern bool_t		eval_set_condition (eval_t *e, int sphere, int eid, xmlBufferPtr condition);
extern bool_t		eval_done_condition (eval_t *e);
extern bool_t		eval_set_variables (eval_t *e, field_t **fld, int fld_cnt, int *failpos);
extern bool_t		eval_done_variables (eval_t *e);
extern bool_t		eval_setup (eval_t *e);
extern bool_t		eval_set_data (eval_t *e, xmlBufferPtr *data, bool_t *dnull, int data_cnt);
extern bool_t		eval_done_data (eval_t *e);
extern bool_t		eval_change_data (eval_t *e, xmlBufferPtr data, bool_t dnull, int pos);
extern bool_t		eval_match (eval_t *e, int sphere, int eid);
extern bool_t		eval_done_match (eval_t *e);
extern void		eval_dump (eval_t *e, FILE *fp);

/*
 * outputmodules
 */
extern void		*none_oinit (blockmail_t *blockmail, var_t *opts);
extern bool_t		none_odeinit (void *data, blockmail_t *blockmail, bool_t success);
extern bool_t		none_owrite (void *data, blockmail_t *blockmail, receiver_t *rec);
extern void		*generate_oinit (blockmail_t *blockmail, var_t *opts);
extern bool_t		generate_odeinit (void *data, blockmail_t *blockmail, bool_t success);
extern bool_t		generate_owrite (void *data, blockmail_t *blockmail, receiver_t *rec);
extern void		*count_oinit (blockmail_t *blockmail, var_t *opts);
extern bool_t		count_odeinit (void *data, blockmail_t *blockmail, bool_t success);
extern bool_t		count_owrite (void *data, blockmail_t *blockmail, receiver_t *rec);
extern void		*preview_oinit (blockmail_t *blockmail, var_t *opts);
extern bool_t		preview_odeinit (void *data, blockmail_t *blockmail, bool_t success);
extern bool_t		preview_owrite (void *data, blockmail_t *blockmail, receiver_t *rec);
#endif		/* __XMLBACK_H */
