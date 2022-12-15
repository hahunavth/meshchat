add_library(libfdr_jval STATIC jval.c)

add_library(libfdr_dllist STATIC dllist.c jval.c)

add_library(libfdr_jrb STATIC jrb.c jval.c)
