# JMonkey

## 项目来源

- [Writing An Interpreter In Go](https://interpreterbook.com/)
- [Writing A Compiler In Go](https://compilerbook.com/)
- [《用Go语言自制解释器》](https://m.ituring.com.cn/book/2883)
- [《用Go语言自制编译器》](https://book.douban.com/subject/35909089/)

## 简述

Java版Monkey解释器，实现了书中前4章的内容，第5章宏的部分由于改写Go源码时结点定义全部使用的record，导致无法替换AST中的结点，故放弃实现。

写这个项目基本属于搬砖，核心代码最多几百行，其他的全在堆砌`token -> lexer -> paser -> eval`的逻辑，毫无难度可言。

全书包括宏系统实现的Go源码在Releases中，可以直接点击[获取](https://github.com/Ai-Kotoba/JMonkey/releases/download/0.9/book-code-Go.zip)
。

***如果有什么问题请提issue，我会尽可能回复。***

```
                 __,__
        .--.  .-"     "-.  .--.
       / .. \/  .-. .-.  \/ .. \
      | |  '|  /   Y   \  |'  | |
      | \   \  \ 0 | 0 /  /   / |
       \ '- ,\.-"`` ``"-./, -' /
        `'-' /_   ^ ^   _\ '-'`
        .--'|  \._ _ _./  |'--. 
      /`    \   \.-.  /   /    `\
     /       '._/  |-' _.'       \
    /          ;  /--~'   |       \
   /        .'\|.-\--.     \       \
  /   .'-. /.-.;\  |\|'~'-.|\       \
  \       `-./`|_\_/ `     `\'.      \
   '.      ;     ___)        '.`;    /
     '-.,_ ;     ___)          \/   /
      \   ``'------'\       \   `  /
       '.    \       '.      |   ;/_
jgs  ___>     '.       \_ _ _/   ,  '--.
   .'   '.   .-~~~~~-. /     |--'`~~-.  \
  // / .---'/  .-~~-._/ / / /---..__.'  /				搬砖好累=-=
 ((_(_/    /  /      (_(_(_(---.__    .'
           | |     _              `~~`
           | |     \'.
            \ '....' |
             '.,___.'
```

## 开发环境

Windows10、IntelliJ IDEA、Git

## 依赖

OpenJDK-17 Preview、JUnit5.9.0、Maven5.8.1

## 运行方法

### CLI

```shell
$~ git clone https://github.com/Ai-Kotoba/JMonkey-Interpreter.git
$~ cd JMonkey-Interpreter
$~ mvn compile assembly:single 
$~ java -jar --enable-preview target/JMonkey-Interpreter-jar-with-dependencies.jar
```

### IntelliJ IDEA

克隆项目后，用IDEA直接打开项目即可运行。

## 问题总结

1. Maven用的POM.xml编写实在太麻烦了，而且是初次使用Maven，命令和XML的使用都很不熟练，也很难定位依赖错误原因，造成了很大困扰。
2. 然后是项目使用了JDK17 Preview
   Feature中的switch模式匹配语法糖，这是Go很早就支持的特性，其实用反射加上普通的switch就可以实现，只是过于丑陋，所以通过`--enable-preview`
   启用了该特性，就是这一点在Maven中造成了大量的依赖问题，最后还是查阅资料勉强将项目打包成了可执行的JAR文件。
3. 使用record也许不适合实现宏，但是如果不实现宏，record在这个项目就是最合适的，也非常优雅，想实现宏需要将ast包的record改为普通class，意义不大。

## 更新预告

- Java21LTS发布时会将JMonkey的JDK版本升级到JDK21，用来移除项目中的Java17 Preview Feature相关的XML配置和命令。

- 会尽快用Java实现Monkey编译器，然后发布到Github。
