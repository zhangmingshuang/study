# jemalloc

`cd /data`

`mkdir ./jemalloc`

`tar -vxf ./env/jemalloc-5.2.1.tar.bz2 -C ./jemalloc --strip-components 1`

`cd jemalloc`

`./autogen.sh`

`make`

`make install`

`echo '/usr/local/lib' > /etc/ld.so.conf.d/local.conf`

`ldconfig`