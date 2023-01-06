add_executable(sqlite3_test sqlite3_test.c)
target_link_libraries(sqlite3_test PRIVATE sqlite3)
add_test(
	NAME sqlite3_test
	COMMAND $<TARGET_FILE:sqlite3_test>
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

add_executable(sql_test sql_test.c)
target_link_libraries(sql_test
	PRIVATE sqlite3
	PRIVATE sql
	PRIVATE util_random
)
add_test(
	NAME sql_test
	COMMAND $<TARGET_FILE:sql_test>
)

add_executable(request_test request_test.c)
target_link_libraries(request_test
	PRIVATE request
)
add_test(
	NAME request_test
	COMMAND $<TARGET_FILE:request_test>
)

add_executable(response_test response_test.c)
target_link_libraries(response_test
	PRIVATE response
)
add_test(
	NAME response_test
	COMMAND $<TARGET_FILE:response_test>
)