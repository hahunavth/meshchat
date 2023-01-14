add_library(util_sll STATIC sll.c)
target_link_libraries(util_sll PUBLIC libfdr_jval)

add_library(util_string STATIC string.c)
target_link_libraries(util_string
	PUBLIC pthread
	PUBLIC util_sll
)

add_library(util_random STATIC random.c)