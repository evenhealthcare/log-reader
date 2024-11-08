package main

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

func countLines(filePath string) int {
	file, err := os.Open(filePath)
	if err != nil {
		return 0
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	lines := 0
	for scanner.Scan() {
		lines++
	}
	return lines
}

func parseLine(filePath string, lineNumber int, dailyCounts map[string]map[string]int) {
	file, err := os.Open(filePath)
	if err != nil {
		return
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	currentLine := 0

	for scanner.Scan() {
		if currentLine == lineNumber {
			line := scanner.Text()
			if len(line) < 23 {
				return
			}

			date := line[1:11]
			level := line[22 : strings.Index(line[22:], ":")+22]

			if _, exists := dailyCounts[date]; !exists {
				dailyCounts[date] = make(map[string]int)
			}
			dailyCounts[date][level]++
			return
		}
		currentLine++
	}
}

func processFile(filePath string) {
	numLines := countLines(filePath)
	dailyCounts := make(map[string]map[string]int)

	fmt.Printf("Summary for file: %s\n", filePath)
	for i := 0; i < numLines; i++ {
		parseLine(filePath, i, dailyCounts)
	}

	for date, counts := range dailyCounts {
		fmt.Printf("Date: %s\n", date)
		for level, count := range counts {
			fmt.Printf("  %s: %d\n", level, count)
		}
	}
	fmt.Println()
}

func main() {
	files := make(map[string]string)

	entries, err := ioutil.ReadDir("logs")
	if err != nil {
		return
	}

	for _, entry := range entries {
		if !entry.IsDir() && filepath.Ext(entry.Name()) == ".txt" {
			files[entry.Name()] = filepath.Join("logs", entry.Name())
		}
	}

	for _, filePath := range files {
		processFile(filePath)
	}
}
