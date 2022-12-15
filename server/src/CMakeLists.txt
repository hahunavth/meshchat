add_subdirectory(socklib)
add_subdirectory(utils)

add_executable(server main.c)
target_link_libraries(server
	PUBLIC socklib
)

IF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)
	set_target_properties(server PROPERTIES
		RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}"
	)
ENDIF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)