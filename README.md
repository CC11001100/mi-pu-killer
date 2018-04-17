## Mi-Pu Killer

可以免费使用米扑代理


## 添加依赖
在POM.xml添加仓库：
```
<repositories>
    <repository>
        <id>maven-repo-cc11001100</id>
        <url>https://raw.github.com/cc11001100/maven-repo/mi-pu-killer/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```
添加对应依赖：
```
<dependency>
    <groupId>cc11001100</groupId>
    <artifactId>mi-pu-killer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 例子
```
MiPuKiller miPuKiller = new MiPuKiller();
for (int i = 0; i < 10; i++) {
    System.out.println(System.currentTimeMillis());
    miPuKiller.get().forEach(x -> {
        System.out.println(x.getIp() + ":" + x.getPort());
    });
}
```



