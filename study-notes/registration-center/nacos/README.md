# Nacos
> https://nacos.io/zh-cn/docs/what-is-nacos.html
> version: 1.1.4

## 介绍
Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

Nacos 支持几乎所有主流类型的“服务”的发现、配置和管理。

## 源码导读
### 入口
`Nacos`的打包项目由`distribution`模块根据`pom.xml`进行组装打包。

这里以`release`版本进行打包说明：

从`pom.xml`的`profile`中找到`release-nacos`，主要根据`release-nacos.xml`进行组装。

在`release-nacos.xml`中，会将资源打包成`nacos-server.jar`，打包资源包括：

```
nacos-distribution/plugins/**
nacos-distribution/conf/**
nacos-distribution/bin/*
```
和`nacos-console`模块， 然后放置到`distribution/target/`下。

### 模块说明
`console`：易用控制台，做服务管理、配置管理等操
