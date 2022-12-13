add_library(thpool STATIC thpool.c)
target_link_libraries(thpool PUBLIC pthread)