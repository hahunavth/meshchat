# MeshChat Client
MeshChat Client is a messaging application that allows users to communicate with each other in a secure network. This repository contains the code for the client-side implementation of the application, written in Java.

# Dependencies
To build and run MeshChat Client, you will need:
- Java Development Kit (JDK) 17 or later
- JavaFX SDK 17 or later
- Gradle build system
The native library libcli_service.so is also included in the repository, which provides the TCP socket API for the client.
- How to build: [here](../client-lib/README.md)

### Map type
```java
/**
 * Map type parameters: C -> Java <br>
 * <br>
 * I/O: <br>
 * const -> @In <br>
 * _ prefix -> @Out <br>
 * <br>
 * IN: <br>
 * const char * -> @In CharSequence <br>
 * uint32_t -> @In @u_int32_t long <br>
 * uint16_t -> @In @u_int32_t int <br>
 * const struct * -> @In ... <br>
 * <br>
 * OUT:  <br>
 * char * -> @Out CharSequence <br>
 * uint32_t * -> @Out @u_int32_t NativeLongByReference <br>
 * uint16_t * -> @Out @u_int16_t NativeIntByReference <br>
 * uint32_t[] -> @Out @u_int32_t long[] <br>
 * struct * -> @Out ... <br>
 * 
 * @see CAPIServiceLib
 */
```

## Folder structure
```yaml
.
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties   
├── gradlew
├── gradlew.bat
├── README.md
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   ├── com
    │   │   │   └── meshchat
    │   │   │       └── client
    │   │   │           ├── viewModels          # screen controller
    │   │   │           ├── exceptions           #
    │   │   │           ├── experiments          #
    │   │   │           ├── Launcher.java        #
    │   │   │           ├── launchers            # preview launcher
    │   │   │           ├── model                # define data schema
    │   │   │           ├── ModelSingleton.java  #
    │   │   │           ├── net                  # network
    │   │   │           ├── pipe                 #
    │   │   │           ├── utils                #
    │   │   │           └── views                # screen handler
    │   │   └── module-info.java
    │   └── resources                   # resources
    │       ├── com
    │       │   └── meshchat
    │       │       └── client          # file .fxml
    │       └── libtypemapping.so             # libc
    └── test
        ├── java
        │   └── com
        │       └── meshchat
        │           └── client          # test file
        └── resources
            ├── com.meshchat.client           # test resource
```
