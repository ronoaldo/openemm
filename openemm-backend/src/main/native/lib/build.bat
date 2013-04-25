@echo off
rem	-*- no -*-
set chome=C:\Programme\Microsoft Visual Studio 8\VC
cl /DWIN32 /I. /Iwin "/I%chome%\include" /c atob.c buffer.c hash.c lock.c log.c node.c map.c set.c skip.c str.c tzdiff.c var.c xml.c compat.c
	  	
