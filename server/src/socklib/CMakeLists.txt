add_library(socklib STATIC common.c tcp.c udp.c)
target_link_libraries(socklib
	PUBLIC pthread
)