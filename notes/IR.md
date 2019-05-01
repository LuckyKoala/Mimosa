## 中间代码生成与执行

中间代码可运行在基于栈的虚拟机上，这个虚拟机的实现类为IRVM。

下面是一段求阶乘的程序，先定义，然后求5的阶乘。

以下是一个完整示例，运行REPL环境，定义阶乘函数，调用阶乘函数，生成中间代码，然后在虚拟机上执行，输出结果。

```
(IR) REPL>
//输入源码
(begin
  (define (iter x i sum)
    (if (> i x)
        sum
        (iter x (+ i 1) (* sum i))))
  (define (factorial arg)
    (iter arg 1 1))
  (factorial 5))

//输出中间代码
(function lambda$0)
	(pop __ret_address)
	(pop sum)
	(pop i)
	(pop x)
	(push __ret_address)
	(push i)
	(pop __intermedia)
	(push x)
	(pop __temp)
	(compare __intermedia __temp)
	(jle false$0)
(label true$0)
	(push sum)
	(jmp final$0)
(label false$0)
	(push x)
	(push i)
	(pop __temp)
	(push 1)
	(pop __intermedia)
	(+ __intermedia __temp)
	(push __temp)
	(push sum)
	(pop __temp)
	(push i)
	(pop __intermedia)
	(* __intermedia __temp)
	(push __temp)
	(push iter)
	(pop __func)
	(call __func)
(label final$0)
	(pop ret)
	(pop __ret_address)
	(push ret)
	(leave)
	(push lambda$0)
	(global lambda$0 iter)
(function lambda$1)
	(pop __ret_address)
	(pop arg)
	(push __ret_address)
	(push arg)
	(push 1)
	(push 1)
	(push iter)
	(pop __func)
	(call __func)
	(pop ret)
	(pop __ret_address)
	(push ret)
	(leave)
	(push lambda$1)
	(global lambda$1 factorial)
	(push 5)
	(push factorial)
	(pop __func)
	(call __func)

//输出结果
120
```



