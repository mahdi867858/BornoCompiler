@echo off
chcp 65001 > nul
javac -encoding UTF-8 -d bin src/token/TokenType.java src/token/Token.java src/lexer/Lexer.java src/main/Main.java
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp bin main.Main
