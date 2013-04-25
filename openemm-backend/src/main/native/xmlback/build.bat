@echo off
rem	-*- no -*-
set chome=C:\Programme\Microsoft Visual Studio 8\VC
cl /DWIN32 /Dinline= /I. /I..\lib /I..\lib\win "/I%chome%\include" /c append.c block.c blockmail.c blockspec.c codec.c convert.c count.c counter.c create.c data.c dcache.c dyn.c eval.c field.c fix.c generate.c links.c mailtype.c media.c misc.c modify.c none.c parse.c postfix.c receiver.c replace.c sqllike.c tag.c tagpos.c url.c xmlback.c preview.c rblock.c
rem cl /DWIN32 /Dinline= /I. /I..\lib /I..\lib\win "/IC:\Programme\Microsoft Visual Studio 8\VC\include" /c tag.c
cl *.obj grammar\transform.obj grammar\parse.obj ..\lib\*.obj /link /OUT:xmlback.exe /LIBPATH:..\lib\win /DEFAULTLIB:libxml2.lib /DEFAULTLIB:zlib.lib /DEFAULTLIB:iconv.lib /DEFAULTLIB:libslang.lib "/LIBPATH:%chome%\lib"
