cmake_minimum_required(VERSION 3.18.1)
project("lottttto-miner")

# Путь к библиотекам
set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs)

# Добавляем флаги в зависимости от архитектуры
if(CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv8-a+fp+simd")
endif()

# Базовая оптимизация для всех архитектур
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -fomit-frame-pointer -funroll-loops")

# Импортируем библиотеку XMRig
add_library(xmrig SHARED IMPORTED)
set_target_properties(xmrig
    PROPERTIES
    IMPORTED_LOCATION ${LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libxmrig.so
)

# Создаем нашу библиотеку-обертку
add_library(miner_jni SHARED native-lib.cpp)

# Находим системную библиотеку для логирования
find_library(log-lib log)

# Линкуем все вместе
target_link_libraries(miner_jni
    xmrig
    ${log-lib}
)
