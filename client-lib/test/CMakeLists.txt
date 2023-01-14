project(client-lib-test)

add_executable(auth_test auth_test.c)
target_link_libraries(auth_test PUBLIC cli_service)
add_test(
	NAME auth_test
	COMMAND $<TARGET_FILE:auth_test>
)
