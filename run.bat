@echo off
chcp 65001 > nul
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src/token/*.java src/lexer/*.java src/ast/*.java src/parser/*.java src/semantic/*.java src/main/*.java
if %errorlevel% equ 0 (
    java -Dfile.encoding=UTF-8 -cp bin main.Main
) else (
    echo Compilation failed.
)
