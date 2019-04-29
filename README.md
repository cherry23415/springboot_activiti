# springboot_activiti 
### springboot_activiti项目，springboot+mybatis+activiti搭建工作流，activiti modeler自定义流程及发布，activiti diagram rest跟踪流程
### 使用gradle管理jar包

1. 数据库部分：

    1) ACT_开头的为activiti相关表,第一次启动项目自动创建；
    
2. Linux系统支持流程图字体宋体配置：

    1) 切换到%JAVA_HOME%/jre/lib/fonts目录。
    2) 创建fallback目录，mkdir fallback。
    3) 网上下载一个支持Linux宋体的simsun.ttf文件拷贝到fallback目录。
    4) 设置读权限，chmod 644 simsun.ttf（644不行就777）
    5) 重启tomcat，重新操作查看宋体中文已显示。
    
3. 可执行jar包（springboot）：

```
 打包：gradle clean bootJar

 执行：java -jar XXX.jar
 或执行：java -jar XXX.jar --spring.profiles.active=dev

```

### 部分目录说明：
  static/diagram-viewer为流程图跟踪的文件
  static/editor-app为自定义流程图的相关文件
  
    static/editor-app/app-cfg.js文件中的contextRoot表示activiti modeler相关接口都需要增加这个路径
    可见com.ying.controller.modeler包中的三个类，都在类上增加该路径(这三个类从activiti5.22的modeler源码中摘出)
    editor-app可直接从activiti 5.22的explorer中复制
    
http://localhost:8090/druid/index.html可访问druid监控