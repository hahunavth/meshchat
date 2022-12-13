add_subdirectory(socklib)

add_executable(server main.c)
target_link_libraries(server
	PUBLIC socklib
)

set_target_properties(server PROPERTIES
	RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}"
)