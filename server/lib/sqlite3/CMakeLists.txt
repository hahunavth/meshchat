add_library(sqlite3 SHARED sqlite3.c)
target_link_libraries(sqlite3
	PUBLIC pthread
	PUBLIC dl
	PUBLIC m
)

add_executable(sqlite shell.c sqlite3.c)
target_link_libraries(sqlite
	PUBLIC pthread
	PUBLIC dl
	PUBLIC m
)

set_target_properties(sqlite PROPERTIES
	RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}"
)