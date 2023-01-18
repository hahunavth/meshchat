add_library(sqlite3 SHARED sqlite3.c)
target_link_libraries(sqlite3
	PUBLIC pthread
	PUBLIC dl
	PUBLIC m
)
target_compile_definitions(sqlite3 PRIVATE SQLITE_THREADSAFE=1)

add_executable(sqlite shell.c sqlite3.c)
target_link_libraries(sqlite
	PUBLIC pthread
	PUBLIC dl
	PUBLIC m
)

IF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)
	set_target_properties(sqlite PROPERTIES
		RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}"
	)
ENDIF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)