@echo off
rem	-*- no -*-
set chome=C:\Programme\Microsoft Visual Studio 8\VC
cl "/I%chome%\include" lemon.c /link "/LIBPATH:%chome%\lib"
lemon -c -s parse.y
cl /DWIN32 /Dinline= /I.. /I..\..\lib /I..\..\lib\win "/I%chome%\include" /c transform.c parse.c
