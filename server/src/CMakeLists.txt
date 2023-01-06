add_subdirectory(utils)

add_library(sql STATIC sql.c)
target_link_libraries(sql
	PUBLIC pthread
	PUBLIC sqlite3
	PUBLIC util_string
)

add_library(request STATIC request.c)
target_link_libraries(request
	PUBLIC util_string
)

add_library(response STATIC response.c)
target_link_libraries(response
	PUBLIC util_string
)

add_executable(server main.c)
target_link_libraries(server
	PUBLIC pthread
)

IF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)
	set_target_properties(server PROPERTIES
		RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}"
	)
ENDIF(NOT CMAKE_BUILD_TYPE MATCHES DEBUG)