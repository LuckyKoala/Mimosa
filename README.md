> 当前进度： 生成中间代码

[LET](./notes/LET.md)
[Lambda与词法闭包](./notes/Lambda.md)

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
