cmake_minimum_required(VERSION 3.2)

project(meshserver LANGUAGES C)

add_definitions(-Wall -Wextra -std=c99)

include_directories(
	${PROJECT_SOURCE_DIR}/inc
	${PROJECT_SOURCE_DIR}/lib
)

add_subdirectory(lib)
add_subdirectory(src)

enable_testing()
add_subdirectory(test)
