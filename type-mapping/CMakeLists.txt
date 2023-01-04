project(type-mapping)

cmake_minimum_required(VERSION 3.24)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY /home/kryo/Desktop/meshchat/client/src/main/resources)

add_library(
  typemapping SHARED
  int.h
  int.c
  arr.h
  arr.c
  struct.c
  struct.h
  union.h
  union.c
  tcp-glob.c
  tcp.h
  debug.h
  color.h
  client.h
  client.c
)

add_executable(main main.c)

target_link_libraries(main typemapping)
