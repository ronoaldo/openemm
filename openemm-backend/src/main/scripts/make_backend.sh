#!/bin/bash

export PATH=$PATH:$OE_HOME/bin

tar -xvzf sa-1.2.6.tar.gz
cd sa-1.2.6
./configure --prefix=$OE_HOME --disable-shared
make
make install
cd ..

tar -xvzf slang-1.4.9.tar.gz
cd slang-1.4.9
./configure --prefix=$OE_HOME
make
make install
cd ..

cd $SRC/src/c/lib
make
cd $SRC/src/c/tools
make
cd $SRC/src/c/xmlback
make
cd $SRC/src/c/bav
make