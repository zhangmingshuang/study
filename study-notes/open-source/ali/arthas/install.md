
from : [alibaba.github.io](https://alibaba.github.io/arthas/install-detail.html)
- 快速安装

 使用`arthas-boot`（推荐）

 下载`arthas-boot.jar`，然后用`java -jar`的方式启动：

 ```sh
 wget https://alibaba.github.io/arthas/arthas-boot.jar
 java -jar arthas-boot.jar
 ```
 打印帮助信息：`java -jar arthas-boot.jar -h`

  - 如果下载速度比较慢，可以使用aliyun的镜像：

   `java -jar arthas-boot.jar --repo-mirror aliyun --use-http`

  - 如果从github下载有问题，可以使用gitee镜像

   `wget https://arthas.gitee.io/arthas-boot.jar`

---

- 使用 `as.sh`

 Arthas 支持在 Linux/Unix/Mac 等平台上一键安装，请复制以下内容，并粘贴到命令行中，敲 回车 执行即可：

 `curl -L https://alibaba.github.io/arthas/install.sh | sh`

 上述命令会下载启动脚本文件 as.sh 到当前目录，你可以放在任何地方或将其加入到 $PATH 中。

 直接在shell下面执行./as.sh，就会进入交互界面。

 也可以执行./as.sh -h来获取更多参数信息。

 - 如果从github下载有问题，可以使用gitee镜像

 `curl -L https://arthas.gitee.io/install.sh | sh`

---

- 全量安装
  [maven down\[v3.0.5\]](https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/taobao/arthas/arthas-packaging/3.0.5/arthas-packaging-3.0.5-bin.zip)

 解压后，在文件夹里有arthas-boot.jar，直接用java -jar的方式启动：

 `java -jar arthas-boot.jar`

 打印帮助信息：

 `java -jar arthas-boot.jar -h`

---

- 卸载
 - 在 Linux/Unix/Mac 平台

   删除下面文件：
    ```sh
    rm -rf ~/.arthas/
    rm -rf ~/logs/arthas
    ```

 - Windows平台直接删除user home下面的.arthas和logs/arthas目录
