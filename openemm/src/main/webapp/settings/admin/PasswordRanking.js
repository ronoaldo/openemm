function	PasswordRanking()	{
}

PasswordRanking.prototype.regexpRank=function (pass, pat)	{
	if(pass.match(pat)) {
		if(pass.match(pat+".*"+pat)) {
			return 15;
		}
		return 5;
	}
	return 0;
}

PasswordRanking.prototype.securityCheck=function (bar, id)	{
	var	pass=document.getElementById(id).value;
	var	level=0;

	level+=this.regexpRank(pass, "[a-z]");
	level+=this.regexpRank(pass, "[A-Z]");
	level+=this.regexpRank(pass, "[0-9]");
	level+=this.regexpRank(pass, "[^a-zA-Z0-9]");
	if(pass.length >= 6) {
		if(pass.length >= 8) {
			level+=15;
		}
		level+=25;
	}
	document.getElementById(bar).style.width=(level*1.50)+"px";
	if(level < 50) {
		document.getElementById(bar).style.background="#f00";
	} else {
		document.getElementById(bar).style.background="#66f";
	}
}
	
PasswordRanking.prototype.showBar=function (bar, secure, insecure)	{
	document.write('<table>\n');
	document.write('<tr>\n');
	document.write('\t<td>' + insecure + '</td>');
	document.write('\t<td><div id="box" style="border: 1px solid black;width: 150px;"><div id="'+bar+'" style="width: 0px; height: 16px; background-color: lightblue;"></div></div></td>\n');
	document.write('\t<td>' + secure + '</td>\n');
	document.write('</tr>\n');
	document.write('</table>');
}

PasswordRanking.prototype.checkMatch=function (passId, repeatId)  {
	var	pass=document.getElementById(passId).value;
	var	repeat=document.getElementById(repeatId).value;

	return (pass != repeat);
}

PasswordRanking.prototype.enableButton=function (name, msg, how)    {
	if(how) {
		if(document.getElementById(name).style.visibility != "hidden") {
			document.getElementById(name).style.visibility ="hidden";
		}
	} else {
		if(document.getElementById(name).style.visibility != "visible") {
			document.getElementById(name).style.visibility="visible";
		}
	}
}

