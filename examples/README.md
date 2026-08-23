# বর্ণ কম্পাইলার — টেস্ট কেস সংগ্রহ (Borno Compiler Examples)

এই ফোল্ডারে বর্ণ প্রোগ্রামিং ভাষার কম্পাইলার টেস্ট করার জন্য ক্যাটাগরিভিত্তিক স্যাম্পল সোর্স কোড (.brn) সাজানো হয়েছে।

---

## ১. বৈধ প্রোগ্রাম ও অপারেটর প্রিসিডেন্স টেস্ট (Valid & Operator Precedence — Expected: SUCCESS)

| ফাইল নাম | বিবরণ |
| :--- | :--- |
| `valid_basic_declaration.brn` | সংখ্যা ও বাক্য ভ্যারিয়েবল ডিক্লেয়ারেশন ও অ্যাসাইনমেন্ট। |
| `valid_precedence_basic.brn` | যোগ/বিয়োগের তুলনায় গুণ/ভাগের অগ্রাধিকার (`২ + ৩ * ৪ = ১৪`)। |
| `valid_precedence_parentheses.brn` | বন্ধনী `( )` ব্যবহার করে ডিফল্ট প্রিসিডেন্স পরিবর্তন (`(২ + ৩) * ৪ = ২০`)। |
| `valid_precedence_mixed_operators.brn` | ভাগশেষ (`%`), গুণ (`*`), যোগ ও বিয়োগের মিশ্র প্রিসিডেন্স টেস্ট। |
| `valid_precedence_relational.brn` | রিলেশনাল বা শর্তমূলক তুলনায় গাণিতিক অগ্রাধিকার (`ক * ২ + ৫ >= খ + ৩`)। |
| `valid_arithmetic_precedence.brn` | অপারেটর প্রিসিডেন্সসহ জটিল গাণিতিক এক্সপ্রেশন ও প্রিন্ট। |
| `valid_nested_if_else.brn` | নেস্টেড `যদি-নাহলে` এবং ব্লক স্কোপিং টেস্ট। |
| `valid_string_concat.brn` | একাধিক স্ট্রিং/বাক্য ভ্যারিয়েবল যোগ (Concatenation) করা। |
| `valid_full_demo.brn` | ভ্যারিয়েবল, কন্ডিশনাল ব্রাঞ্চিং ও প্রিন্টসহ সম্পূর্ণ ডেমো প্রোগ্রাম। |

---

## ২. লেক্সিক্যাল এরর টেস্ট (Lexical Errors — Expected: LEXICAL ERROR)

| ফাইল নাম | বিবরণ |
| :--- | :--- |
| `error_lex_english_digits.brn` | ইংরেজি ডিজিট (0-9) ব্যবহার করলে এরর ডিটেকশন। |
| `error_lex_invalid_char.brn` | অবৈধ ক্যারেক্টার (যেমন `$`) ব্যবহার প্রতিরোধ। |
| `error_lex_unclosed_string.brn` | শেষ কোটেশন ছাড়া অসমাপ্ত স্ট্রিং লিটারাল ডিটেকশন। |
| `error_lex_english_keyword.brn` | ইংরেজি কিওয়ার্ড (যেমন `if`) ব্যবহারের বিরুদ্ধে এরর। |

---

## ৩. সিনট্যাক্স এরর টেস্ট (Syntax Errors — Expected: SYNTAX ERROR)

| ফাইল নাম | বিবরণ |
| :--- | :--- |
| `error_syntax_missing_semicolon.brn` | স্টেটমেন্টের শেষে সেমিকোলন (`;`) মিসিং থাকলে এরর। |
| `error_syntax_unbalanced_paren.brn` | শর্তে ক্লোজিং বন্ধনী (`)`) মিসিং থাকলে এরর। |

---

## ৪. সেমান্টিক এরর টেস্ট (Semantic Errors — Expected: SEMANTIC ERROR)

| ফাইল নাম | বিবরণ |
| :--- | :--- |
| `error_sem_type_mismatch.brn` | সংখ্যা টাইপের ভ্যারিয়েবলে স্ট্রিং মান দেওয়া (Type Mismatch)। |
| `error_sem_division_by_zero.brn` | শূন্য দিয়ে ভাগ বা ভ্যারিয়েবলের মান ০ দিয়ে ভাগ ডিটেকশন। |
| `error_sem_undeclared_var.brn` | পূর্বে ঘোষণা না করা ভ্যারিয়েবল ব্যবহারে এরর। |
| `error_sem_duplicate_declaration.brn` | একই স্কোপে একই নামের ভ্যারিয়েবল একাধিকবার ঘোষণা। |
| `error_sem_invalid_if_condition.brn` | `যদি` শর্তে বুলিয়ান ছাড়া সংখ্যা ব্যবহার প্রতিরোধ। |
