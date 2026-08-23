# BornoCompiler 🇧🇩

### A Bangla Programming Language Compiler

**BornoCompiler** is a compiler design project that implements the fundamental phases of a compiler for a simple **Bangla programming language**.

The project is designed to demonstrate how a source program written using Bangla keywords can be processed through **Lexical Analysis, Syntax Analysis, Abstract Syntax Tree (AST) construction, and Semantic Analysis**.

---

## 📌 Project Overview

BornoCompiler is a custom compiler for a simple Bangla-based programming language.

Instead of writing programs using traditional English keywords such as `int`, `if`, and `else`, the language uses Bangla programming constructs such as:

```text
ধরি সংখ্যা বয়স = 20;
```

The compiler processes the source code step by step:

```text
Bangla Source Code
        ↓
   Lexical Analysis
        ↓
      Tokens
        ↓
   Syntax Analysis
        ↓
       AST
        ↓
 Semantic Analysis
        ↓
 Validated Program
```

The primary goal of this project is to understand and demonstrate the core concepts of **Compiler Design and Construction**.

---

# 🎯 Objectives

The main objectives of BornoCompiler are:

* Build a basic compiler from scratch.
* Support programming constructs using Bangla keywords.
* Implement lexical analysis.
* Implement syntax analysis using a parser.
* Construct an Abstract Syntax Tree (AST).
* Perform semantic analysis.
* Implement type checking.
* Validate variable declarations and assignments.
* Support conditional statements using `if-else`.
* Detect semantic errors.
* Detect division-by-zero errors.
* Handle syntax errors and recover from certain syntax mistakes.
* Demonstrate operator precedence.
* Provide meaningful compiler-style error messages.

---

# 🛠️ Technologies Used

* **Java**
* Object-Oriented Programming
* Compiler Design concepts
* Lexical Analysis
* Recursive-descent style parsing
* Abstract Syntax Tree
* Semantic Analysis
* Symbol Table
* Git & GitHub

---

# 📂 Project Structure

```text
BornoCompiler/
│
├── src/
│   │
│   ├── token/
│   │   ├── Lexer.java
│   │   ├── Token.java
│   │   └── TokenType.java
│   │
│   ├── parser/
│   │   └── ...
│   │
│   ├── ast/
│   │   └── ...
│   │
│   ├── semantic/
│   │   ├── SemanticAnalyzer.java
│   │   └── SymbolTable.java
│   │
│   └── Main.java
│
├── examples/
│   ├── test1.brn
│   ├── test2.brn
│   └── test3.brn
│
├── .gitignore
└── README.md
```

> The exact internal structure may evolve as additional compiler phases are implemented.

---

# 🔤 1. Lexical Analysis

The first phase of BornoCompiler is the **Lexer**.

The lexer reads the source program character by character and converts it into a sequence of tokens.

For example:

```text
ধরি সংখ্যা বয়স = 20;
```

may produce tokens similar to:

```text
DHORI       : 'ধরি'
SONGKHA     : 'সংখ্যা'
IDENTIFIER  : 'বয়স'
ASSIGN      : '='
NUMBER      : '20'
SEMICOLON   : ';'
```

## Lexer Responsibilities

The lexer recognizes:

* Bangla keywords
* Identifiers
* Numbers
* Strings
* Assignment operators
* Arithmetic operators
* Comparison operators
* Parentheses
* Braces
* Semicolons
* Other supported symbols

It also keeps track of source positions such as:

```text
line
column
```

This allows the compiler to report errors at the appropriate location.

---

# 🌳 2. Syntax Analysis

After lexical analysis, the generated tokens are passed to the **Parser**.

The parser checks whether the sequence of tokens follows the grammar rules of the language.

For example:

```text
ধরি সংখ্যা বয়স = 20;
```

is parsed as a valid assignment/declaration statement.

The parser is responsible for recognizing constructs such as:

* Variable declarations
* Assignment statements
* Expressions
* Arithmetic operations
* Conditional statements
* `if-else` blocks

---

# 🌲 3. Abstract Syntax Tree (AST)

BornoCompiler constructs an **Abstract Syntax Tree (AST)** to represent the logical structure of the program.

For example:

```text
ধরি সংখ্যা ক = 10 + 5;
```

can conceptually be represented as:

```text
Assignment
├── Variable: ক
└── Binary Expression: +
    ├── 10
    └── 5
```

The AST removes unnecessary grammar details and keeps the important structure of the program.

This makes it easier for later compiler phases, especially semantic analysis.

---

# 🧠 4. Semantic Analysis

After parsing, the compiler performs **Semantic Analysis**.

The semantic analyzer checks whether the program is logically meaningful according to the language rules.

BornoCompiler includes a **Symbol Table** to keep track of declared identifiers and their information.

Example:

```text
Name       Type       Initialized
---------------------------------
বয়স        সংখ্যা      Yes
নাম        লেখা        Yes
```

## Semantic Checks

The compiler performs checks such as:

### Variable Declaration

The compiler verifies whether variables are declared correctly.

### Variable Usage

The compiler checks whether an identifier is known before it is used.

### Type Checking

The compiler validates whether expressions and assignments use compatible types.

For example, an invalid assignment between incompatible types can produce a semantic error.

### Duplicate Declaration

The symbol table can detect attempts to declare the same variable incorrectly within the relevant scope.

### Division by Zero

The compiler detects invalid expressions such as:

```text
ধরি সংখ্যা ক = 10;
ধরি সংখ্যা খ = 0;
ধরি সংখ্যা গ = ক / খ;
```

and reports a semantic error instead of allowing an invalid division.

---

# 🔀 5. IF-ELSE Support

BornoCompiler supports conditional logic using `if-else`.

Conceptually:

```text
যদি (condition) {
    ...
}
নাহলে {
    ...
}
```

The compiler validates:

* Condition syntax
* Condition type
* Statements inside the `if` block
* Statements inside the `else` block
* Scope-related semantic information

The semantic output can also identify the relevant scope, making compiler diagnostics easier to understand.

---

# ⚠️ 6. Error Handling

A compiler must not only accept valid programs; it should also identify invalid programs.

BornoCompiler includes error handling for different categories of problems.

## Syntax Errors

For example, a missing semicolon:

```text
ধরি সংখ্যা বয়স = 20
ধরি সংখ্যা উচ্চতা = 170;
```

The parser can report the syntax problem and attempt to continue parsing subsequent statements.

## Semantic Errors

Examples include:

* Invalid variable usage
* Type mismatch
* Invalid conditional expression
* Division by zero
* Other symbol-table-related errors

---

# ➗ 7. Operator Precedence

BornoCompiler supports expression parsing with operator precedence.

For example:

```text
10 + 5 * 2
```

should be interpreted as:

```text
10 + (5 * 2)
```

rather than:

```text
(10 + 5) * 2
```

This behavior is represented correctly in the AST.

Conceptually:

```text
       +
      / \
    10   *
        / \
       5   2
```

This demonstrates that multiplication has higher precedence than addition.

---

# 🧪 Testing

The project contains several test scenarios for validating different compiler phases.

Testing includes:

### Lexer Tests

* Keyword recognition
* Identifier recognition
* Number recognition
* String recognition
* Operator recognition
* Character-based scanning

### Parser Tests

* Assignment statements
* Expressions
* Operator precedence
* Syntax errors
* Missing semicolon recovery
* Conditional statements

### Semantic Tests

* Type checking
* Variable validation
* IF condition validation
* IF-ELSE semantic validation
* Division by zero
* Symbol table validation
* Semantic error handling

---

# ▶️ How to Run

## 1. Clone the Repository

```bash
git clone https://github.com/mahdi867858/BornoCompiler.git
```

## 2. Enter the Project

```bash
cd BornoCompiler
```

## 3. Compile the Java Source

Depending on the current project structure, compile the source files using:

```bash
javac -d out src/**/*.java
```

On Windows, if the wildcard pattern is not supported by the shell, compile using your IDE or an appropriate Java compilation command.

## 4. Run the Compiler

For example:

```bash
java -cp out Main
```

The exact run command may change as the project structure evolves.

---

# 📝 Example Program

A simple Borno program can look like:

```text
ধরি সংখ্যা বয়স = 20;
ধরি সংখ্যা বছর = 5;
ধরি সংখ্যা ভবিষ্যৎ = বয়স + বছর;
```

The compiler processes the program through:

```text
Source Code
     ↓
Lexer
     ↓
Tokens
     ↓
Parser
     ↓
AST
     ↓
Semantic Analyzer
     ↓
Validation Result
```

---

# 📊 Compiler Architecture

```text
                  ┌──────────────────┐
                  │   Source Code    │
                  │   (.brn file)    │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │      Lexer       │
                  │ Lexical Analysis │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │     Tokens       │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │      Parser      │
                  │ Syntax Analysis  │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │       AST        │
                  │ Abstract Syntax  │
                  │       Tree       │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │Semantic Analyzer │
                  │  Type Checking   │
                  │ Symbol Validation│
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ Validated Program│
                  └──────────────────┘
```

---

# 📚 Compiler Phases Implemented

| Compiler Phase             | Status         |
| -------------------------- | -------------- |
| Lexical Analysis           | ✅ Implemented  |
| Token Generation           | ✅ Implemented  |
| Syntax Analysis            | ✅ Implemented  |
| AST Construction           | ✅ Implemented  |
| Symbol Table               | ✅ Implemented  |
| Semantic Analysis          | ✅ Implemented  |
| Type Checking              | ✅ Implemented  |
| IF-ELSE Validation         | ✅ Implemented  |
| Syntax Error Recovery      | ✅ Implemented  |
| Division-by-Zero Detection | ✅ Implemented  |
| Operator Precedence        | ✅ Implemented  |
| Code Generation            | 🔄 Future Work |
| Machine Code Generation    | 🔄 Future Work |

---

# 🚀 Future Improvements

Possible future extensions include:

* More data types
* Loops such as `while` and `for`
* Functions and procedures
* Arrays
* More advanced expressions
* Better error recovery
* Intermediate Representation (IR)
* Three-address code
* Optimization
* Code generation
* Bytecode generation
* Native machine-code generation
* A command-line compiler interface
* An interactive IDE/editor for the Bangla language

---

# 👥 Contributors

This project was developed collaboratively as part of a **Compiler Design and Construction** project.

### Mumshad Chowdhury Mahdi

* Project structure
* Parser development
* Semantic analysis components
* AST-related work
* Error handling
* Testing
* Compiler integration
* Project maintenance

### Sharon Shahrin Mim

* Parser development and fixes
* Semantic Analysis
* Semantic validation
* IF-ELSE semantic handling
* Semantic error handling
* Testing and debugging

### Tasmina

* Lexer development
* Character-based lexical scanning
* Keyword recognition
* Number and string tokenization
* Operator recognition
* Source-file support

---

# 🎓 Academic Context

**Project:** BornoCompiler — Bangla Programming Language Compiler

**Course:** Compiler Design and Construction

The project demonstrates practical implementation of concepts including:

* Lexical Analysis
* Syntax Analysis
* Parsing
* Abstract Syntax Trees
* Symbol Tables
* Semantic Analysis
* Type Checking
* Error Detection and Recovery

---

# 📌 Current Project Scope

The current version focuses primarily on:

```text
Lexer
   ↓
Parser
   ↓
AST
   ↓
Semantic Analysis
   ↓
IF-ELSE Validation
```

The project is intentionally being developed incrementally, with each compiler phase tested and validated before moving toward more advanced compiler functionality.

---

# 📄 License

This project is primarily an academic project developed for educational and learning purposes.

---

# ⭐ Acknowledgement

BornoCompiler was developed to gain practical experience with compiler construction and to explore how a programming language can be designed using Bangla syntax.

The project aims to demonstrate that programming language concepts such as **lexing, parsing, AST construction, semantic analysis, type checking, and error handling** can be implemented for a Bangla-based programming language.

---

## 💻 BornoCompiler

**Building a programming language in Bangla — one compiler phase at a time. 🇧🇩**
