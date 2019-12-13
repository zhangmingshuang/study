# 安装

## 卸载旧版本
旧版本的`Docker`名称叫做`docer`或者`docker-engine`，如果有安装，需要进行卸载。
```sh
$ sudo yum remove docker \
          docker-client \
          docker-client-latest \
          docker-common \
          docker-latest \
          docker-latest-logrotate \
          docker-logrotate \
          docker-engine
```
卸载完成，原有的内容（`/var/lib/docker`）内的镜像(`images`)，容器(`containers`)，挂载(`volumes`)和网络相关(`networks`)都会保留。

最新的`Docker`名为叫做：`docker-ce`（社区版）

## 安装社区版Docker
### 使用仓库安装
#### 设置仓库
- `yum-utils`提供了`yum-config-manager`
- `device-mapper-persistent-data`和`lvm2`提供了存储映射
    ```sh
    $ sudo yum install -y yum-utils \
      device-mapper-persistent-data \
      lvm2
    ```
- 使用`yum-config-manager`设置仓库
    ```sh
    $ sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
    ```

#### 安装Docker
##### 安装最新版
`$ sudo yum install docker-ce docker-ce-cli containerd.io`
##### 安装指定版本
```sh
$ yum list docker-ce --showduplicates | sort -r

docker-ce.x86_64  3:18.09.1-3.el7                     docker-ce-stable
docker-ce.x86_64  3:18.09.0-3.el7                     docker-ce-stable
docker-ce.x86_64  18.06.1.ce-3.el7                    docker-ce-stable
docker-ce.x86_64  18.06.0.ce-3.el7                    docker-ce-stable
```
安装指定版本，根据列表中的名称为`docker-ce`的包名，如果要安装`docker-ce.x86_64  3:18.09.1-3.el7`版本，指定包名为`18.09.1`，也就是说，版本信息为`:`之后`-`之前。

如：

`sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io`

指定版本`18.09.1`

`sudo yum install docker-ce-18.09.1 docker-ce-cli-18.09.1 containerd.io`

##### 启动Docker
`sudo systemctl start docker`
>
> [root@localhost ~]# `ps -ef | grep docker`
>
> root     16935     1  3 09:34 ?        00:00:00 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
root     17090 16258  0 09:35 pts/1    00:00:00 grep --color=auto docker

##### 测试HelloWorld
`sudo docker run hello-world`

> Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
1b930d010525: Pull complete
Digest: sha256:4fe721ccc2e8dc7362278a29dc660d833570ec2682f4e4194f4ee23e415e1064
Status: Downloaded newer image for hello-world:latest
>
> Hello from Docker!
This message shows that your installation appears to be working correctly.
>
> To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.
>
> To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash
>
> Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/
>
> For more examples and ideas, visit:
 https://docs.docker.com/get-started/

##### 开机启动
`sudo systemctl enable docker`
