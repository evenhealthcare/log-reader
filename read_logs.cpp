#include <filesystem>
#include <fstream>
#include <iostream>
#include <map>
#include <string>

namespace fs = std::filesystem;

int count_lines(const std::string &file_path) {
  std::ifstream file(file_path);
  if (!file.is_open()) {
    return 0;
  }
  int lines = 0;
  std::string line;
  while (std::getline(file, line)) {
    lines++;
  }
  return lines;
}

void parse_line(
    const std::string &file_path, int line_number,
    std::map<std::string, std::map<std::string, int>> &daily_counts) {
  std::ifstream file(file_path);
  if (!file.is_open()) {
    return;
  }

  std::string line;
  int current_line = 0;

  while (std::getline(file, line)) {
    if (current_line == line_number) {
      std::string date = line.substr(1, 10);
      std::string level = line.substr(22, line.find(':', 22) - 22);

      if (daily_counts[date].find(level) == daily_counts[date].end()) {
        daily_counts[date][level] = 0;
      }
      daily_counts[date][level] += 1;
      return;
    }
    current_line++;
  }
}

void process_file(const std::string &file_path) {
  int num_lines = count_lines(file_path);
  std::map<std::string, std::map<std::string, int>> daily_counts;

  std::cout << "Summary for file: " << file_path << "\n";
  for (int i = 0; i < num_lines; ++i) {
    parse_line(file_path, i, daily_counts);
  }

  for (const auto &[date, counts] : daily_counts) {
    std::cout << "Date: " << date << "\n";
    for (const auto &[level, count] : counts) {
      std::cout << "  " << level << ": " << count << "\n";
    }
  }
  std::cout << "\n";
}

int main() {
  std::map<std::string, std::string> files;

  for (const auto &entry : fs::directory_iterator("logs")) {
    if (entry.is_regular_file() && entry.path().extension() == ".txt") {
      files[entry.path().filename().string()] = entry.path().string();
    }
  }
  for (const auto &[filename, file_path] : files) {
    process_file(file_path);
  }
  return 0;
}
