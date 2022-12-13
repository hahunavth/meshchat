add_executable(sqlite3_test sqlite3_test.c)
target_link_libraries(sqlite3_test PRIVATE sqlite3)
add_test(
	NAME sqlite3_test
	COMMAND $<TARGET_FILE:sqlite3_test>
)

add_executable(socklib_test socklib_test.c)
target_link_libraries(socklib_test PRIVATE socklib)
add_test(
	NAME socklib_test
	COMMAND $<TARGET_FILE:socklib_test>
)