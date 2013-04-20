__aps__ = {
	'api':		'1.0.0',
	'version':	'1.0',
	'uri':		None
}
#
def handleOutgoingMail (ctx, mail):
	uri = __aps__['uri']
	if uri:
		found = None
		for line in mail.head:
			if line.lower ().startswith ('list-unsubscribe:'):
				found = line
				break
		if found is None:
			try:
				from urllib2 import quote
			except ImportError:
				from urllib import quote
			data = {
				'sender': mail.sender,
				'urlsender': quote (mail.sender),
				'recv': mail.receiver,
				'urlrecv': quote (mail.receiver)
			}
			mail.head.append ('List-Unsubscribe: %s' % (uri % data, ))

if __name__ == '__main__':
	def _main ():
		class struct:
			pass
		mail = struct ()
		mail.head = []
		mail.sender = 'news@letter.com'
		mail.receiver = 'someone@somewhere.com'
		__aps__['uri'] = 'http://localhost/unsubscribe?%(urlrecv)s'
		handleOutgoingMail (mail)
		print mail.head[0]
	
	_main ()
	
