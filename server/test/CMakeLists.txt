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

add_executable(sll_test sll_test.c)
target_link_libraries(sll_test PRIVATE util_sll)
add_test(
	NAME sll_test
	COMMAND $<TARGET_FILE:sll_test>
)

add_executable(string_test string_test.c)
target_link_libraries(string_test PRIVATE util_string)
add_test(
	NAME string_test
	COMMAND $<TARGET_FILE:string_test>
)