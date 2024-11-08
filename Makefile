# Variables
CXX = g++
CXXFLAGS = -Wall -std=c++17
TARGET = read_logs
SRC = read_logs.cpp
LOG_DIR = logs

# Default target to build the executable
all: build

# Build rule
build: $(SRC)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(SRC)

# Run rule (assumes "logs" directory exists)
run: build
	./$(TARGET)

# Clean rule to remove the executable
clean:
	rm -f $(TARGET)

# Phony targets to avoid file conflicts
.PHONY: all build run clean

