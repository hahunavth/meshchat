# Client

## Native library

- libcli_service: TCP socket API library for client in `client/src/main/resources/libcli_service.so`
  - How to build: [here](../client-lib/README.md)

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