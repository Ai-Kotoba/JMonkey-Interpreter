# JMonkey-Interpreter
## 简述

《用Go语言自制解释器》的Java版Monkey解释器，实现了书中前4章的内容，第5章宏的部分大多都是前4章的重复内容，没有必要实现，故没有实现。

写这个项目基本属于搬砖，核心代码最多几百行，其他的全在堆砌`token -> lexer -> paser -> eval`的逻辑，毫无难度可言。

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

## 项目结构

```
Folder PATH listing for volume Center
Volume serial number is 9E4B-9BD2
C:.
├─.idea
├─src
│  ├─main
│  │  ├─java
│  │  │  ├─ast
│  │  │  ├─evaluator
│  │  │  ├─lexer
│  │  │  ├─object
│  │  │  ├─parser
│  │  │  ├─repl // main class
│  │  │  └─token
│  │  └─resources //含有类图和项目目录结构
│  └─test
│      └─java //测试源代码
└─target // jar包生成处

```

## 开发环境

Windows10、IntelliJ IDEA

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

### IDEA

克隆项目后，用IDEA直接打开项目即可运行。

## 问题总结

1. Maven用的POM.xml编写实在太麻烦了，而且是初次使用Maven，命令和XML的使用都很不熟练，也很难定位依赖错误原因，造成了很大困扰。
2. 然后是项目使用了JDK17 Preview Feature中的switch模式匹配语法糖，这是Go很早就支持的特性，其实用反射加上普通的switch就可以实现，只是过于丑陋，所以通过`--enable-preview`启用了该特性，就是这一点在Maven中造成了大量的依赖问题，最后还是查阅资料勉强将项目打包成了可执行的JAR文件。
