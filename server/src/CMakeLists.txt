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

find_package(OpenSSL REQUIRED)

add_executable(server main.c handler.c)
target_link_libraries(server
	PUBLIC pthread
	PUBLIC OpenSSL::SSL
	PUBLIC sqlite3
	PUBLIC sql
	PUBLIC request
	PUBLIC response
)

set_target_properties(server PROPERTIES RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}")

add_executable(seed seed.c)
target_link_libraries(seed
	PUBLIC OpenSSL::SSL
	PUBLIC sql
	PUBLIC util_random
)
set_target_properties(seed PROPERTIES RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}")