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
/** @file tzdiff.c
 * Timezone difference calculator.
 */
# include	<time.h>
# include	"agn.h"

/** Timezone diff to gm time.
 * Calculates the difference between localtime and gmrime in seconds
 * @param tim current time
 * @return the difference in seconds
 */
int
tzdiff (time_t tim) /*{{{*/
{
	int		diff;
	time_t		gm, loc;
	struct tm	*tp;
	struct tm	tt;

	diff = 0;
	if (tp = gmtime (& tim)) {
		tt = *tp;
		tt.tm_isdst = 0;
		if ((gm = mktime (& tt)) != (time_t) -1) {
			if (tp = localtime (& tim)) {
				tt = *tp;
				tt.tm_isdst = 0;
				if ((loc = mktime (& tt)) != (time_t) -1)
					diff = loc - gm;
			}
		}
	}
	return diff;
}/*}}}*/
		
