## LET 语言

基本数据类型： Bool, Number(目前仅支持整数), Symbol

组合数据类型： Pair, List

表达式类型：

* 1 -> 1
* x -> env[x]
* (- x 1) -> env[x]-1
* (zero? 1) -> #t
* (zero? 0) -> #f
* (if (zero? x) 1 0) -> env[x]==0 ? 1 : 0
* (let (x 1) x) -> extendedEnv[x]=1, x=1

LET语言是一个简单的语言，在实现这个语言的过程中，搭建并测试了解释器的整体框架。

实现了基本的REPL环境。

解释器使用Java编写，项目源码使用Git管理，测试框架使用JUnit。
