@echo off
chcp 65001 > nul
javac -encoding UTF-8 -d bin src/token/*.java src/lexer/*.java src/parser/*.java src/ast/*.java src/semantic/*.java src/main/*.java
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp bin main.Main
