# include	"agn.h"

char *
get_local_fqdn (void) {
	return NULL;
}
char *
gethostname (char *buf, int blen) {
	strncpy (buf, "localhost", blen);
	return buf;
}
