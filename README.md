> 当前进度： 优化中间代码

[LETg](./notes/LET.md)
[Lambda与词法闭包](./notes/Lambda.md)
[中间代码生成与执行](./notes/IR.md)

> 数据类型

* boolean number symbol function 基本数据类型
* pair list 基本组合数据类型

> 基本表达式类型(表达式的类型是pair)

* `(lambda (var ...) exp1 ...)` 匿名函数表达式
* `(function params)` 调用函数
* `(define var val)` 定义全局变量，拓展环境
* `(set! var val)` 改变变量的值，修改环境
* `(if predicate trueExpr falseExpr)` 条件表达式
* `(quote exp)` 引用表达式，意思是只对参数进行parse而不求值 `'exp`将会被解释为`(quote exp)`

> 拓展表达式类型(通过提取语法元素重新组合成基本表达式实现)

* `(let ((var val) ...) exp1 ...)` 绑定局部变量 => lambda表达式
* `(begin exp1 ...)` 顺序执行多个表达式 => lambda表达式
* `(do ([id init-expr step-expr] ...) (stop?-expr finish-expr ...) commands ...)` 循环直到终止断言为真 => lambda表达式

> 中间代码： 基于栈的虚拟机指令

 * (label name) 代码块标签，用于跳转
 * (function name) 函数标签
 *
 * (leave) 离开函数，返回上一层
 *
 * (jne name) !=
 * (je name) ==
 * (jge name) >=
 * (jle name) <=
 * (jmp name) 无条件跳转
 * (push symbol/immediate) 将对应符号的值/立即数入栈
 * (pop symbol) 将出栈的值与符号关联
 * (call func) 调用func (参数提前入栈，结果也在栈上)
 *
 * (global symbol/immediate target) (全局)将对应符号的值/立即数与target符号关联
 * (mov symbol/immediate target) 将对应符号的值/立即数与target符号关联
 * (compare symbol/immediate symbol/immediate) 值比较，置flag
 *
 * (#primitive function# symbol/immediate target)
