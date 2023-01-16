project(client-lib-test)

add_executable(auth_test auth_test.c)
target_link_libraries(auth_test PUBLIC cli_service)
add_test(
	NAME auth_test
	COMMAND $<TARGET_FILE:auth_test>
)

add_executable(user_test user_test.c)
target_link_libraries(user_test PUBLIC cli_service)
add_test(
	NAME user_test
	COMMAND $<TARGET_FILE:user_test>
)

add_executable(conv_test conv_test.c)
target_link_libraries(conv_test PUBLIC cli_service)
add_test(
	NAME conv_test
	COMMAND $<TARGET_FILE:conv_test>
)

add_executable(chat_test chat_test.c)
target_link_libraries(chat_test PUBLIC cli_service)
add_test(
	NAME chat_test
	COMMAND $<TARGET_FILE:chat_test>
)
