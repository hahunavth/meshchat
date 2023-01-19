project(client-lib)
cmake_minimum_required(VERSION 3.2)
add_definitions(-Wall -Wextra -std=c99)

include_directories(
  ${CMAKE_CURRENT_SOURCE_DIR}/../server/inc
  ${CMAKE_CURRENT_SOURCE_DIR}/../server/lib
  )
include_directories(./inc)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../server/lib/libfdr ${CMAKE_CURRENT_BINARY_DIR}/libfdr)
file(GLOB LIBFDR CONFIGURE_DEPENDS
  ${CMAKE_CURRENT_SOURCE_DIR}/../server/lib/libfdr/*.c
  ${CMAKE_CURRENT_SOURCE_DIR}/../server/lib/libfdr/*.h
)

add_library(libfdr STATIC ${LIBFDR})

add_library(util_sll STATIC ../server/src/utils/sll.c)
add_library(util_string STATIC ../server/src/utils/string.c)

add_library(request STATIC ../server/src/request.c)
add_library(response STATIC ../server/src/response.c)

add_library(cli_service SHARED ./src/service.c ./src/map_types.c ./src/connection.c )
set_target_properties(cli_service PROPERTIES
		LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/../client/src/main/resources/"
	)
add_executable(main main.c)

enable_testing()
add_subdirectory(./test)

target_link_libraries(util_sll PUBLIC libfdr_jval)
target_link_libraries(util_string PUBLIC util_sll)

target_link_libraries(request PUBLIC util_string)
target_link_libraries(response PUBLIC util_string)

target_link_libraries(cli_service PUBLIC libfdr)
target_link_libraries(cli_service PUBLIC util_sll)
target_link_libraries(cli_service PUBLIC util_string)
target_link_libraries(cli_service PUBLIC request)
target_link_libraries(cli_service PUBLIC response)

target_link_libraries(main PUBLIC cli_service)
