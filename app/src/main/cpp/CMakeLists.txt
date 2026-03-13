cmake_minimum_required(VERSION 3.18.1)
project("lottttto-miner")

# Путь к библиотекам
set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs)

message(STATUS "CMAKE_ANDROID_ARCH_ABI: ${CMAKE_ANDROID_ARCH_ABI}")
message(STATUS "Looking for libxmrig.so in: ${LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libxmrig.so")

# Добавляем флаги в зависимости от архитектуры
if(CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv8-a+fp+simd")
endif()

# Базовая оптимизация для всех архитектур
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -fomit-frame-pointer -funroll-loops")

# Создаем нашу библиотеку-обертку
add_library(miner_jni SHARED native-lib.cpp)

# Находим системную библиотеку для логирования
find_library(log-lib log)

# Проверяем существование файла библиотеки
if(EXISTS ${LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libxmrig.so)
    message(STATUS "Found libxmrig.so at: ${LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libxmrig.so")
    
    # Импортируем библиотеку XMRig
    add_library(xmrig SHARED IMPORTED)
    set_target_properties(xmrig
        PROPERTIES
        IMPORTED_LOCATION ${LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libxmrig.so
    )
    
    # Линкуем с реальной библиотекой
    target_link_libraries(miner_jni xmrig ${log-lib})
    target_compile_definitions(miner_jni PRIVATE HAVE_XMRIG=1)
    message(STATUS "Using REAL XMRig library")
else()
    message(WARNING "libxmrig.so not found! Using mock implementation.")
    # Линкуем только с системными библиотеками
    target_link_libraries(miner_jni ${log-lib})
    target_compile_definitions(miner_jni PRIVATE HAVE_XMRIG=0)
endif()
