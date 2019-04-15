# PathMatchingResourcePatternResolver

[SpringDoc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/io/support/PathMatchingResourcePatternResolver.html)

实现接口：
  ResourceLoader, ResourcePatternResolver

已知实现类：
  ServletContextResourcePatternResolver

 解析资源文件的实现类

  用法：
  ```java
  //获取文件系统文件
  getResource("file:pom.xml");
  //从类路径下获取指定的文件
  getResource("pom.xml");
  //类路径下目录文件
  getResource("META-INF/spring.factor");
  getResource("classpath:META-INF/spring.factor");
  //获取所有类路径下的指定文件
  getResource("classpath*:/META-INF/spring.factor");
  //classpath与classpath*的区别在于，classpath只能获取当前类路径下的资源文件，而classpath*可以获取所膦在路径下的资源文件，包括jar中的
  //通配符
  getResource("classpath*:spring-*.xml");
  getResource("classapth*:com/**/spring-*.xml");
  ```


- \#getResources(String locationPattern)
    ```java
    ->是否以`classpath*:`开头
    --> 是
    ----> AntPathMatcher#isPattern 判断lpcationPattern后是否存在*或者?
    ----> 是 #findPathMatchingResources
    ----> 否 #findAllClassPathResources
    --> 否
    ---> AntPathMatcher#isPattern ?
    ----> 是 findPathMatchingResources
    ----> 否 无通配符匹配，使用DefaultResourceLoader加载
    ```

- \#findPathMatchingResources
  ```java
  protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
      //获取地址的根目录，比如：/WEB-INF/*.xml = /WEB-INF/
      String rootDirPath = determineRootDir(locationPattern);
      //除根目录外的地址
      String subPattern = locationPattern.substring(rootDirPath.length());
      //递归获取资源
      Resource[] rootDirResources = getResources(rootDirPath);
      Set<Resource> result = new LinkedHashSet<>(16);
      for (Resource rootDirResource : rootDirResources) {
          rootDirResource = resolveRootDirResource(rootDirResource);
          URL rootDirUrl = rootDirResource.getURL();
          //解析地址前缀
          if (equinoxResolveMethod != null && rootDirUrl.getProtocol().startsWith("bundle")) {
              URL resolvedUrl = (URL) ReflectionUtils.invokeMethod(equinoxResolveMethod, null, rootDirUrl);
              if (resolvedUrl != null) {
                  rootDirUrl = resolvedUrl;
              }
              rootDirResource = new UrlResource(rootDirUrl);
          }
          //vfs
          if (rootDirUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
              result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirUrl, subPattern, getPathMatcher()));
          }
          //jar
          else if (ResourceUtils.isJarURL(rootDirUrl) || isJarResource(rootDirResource)) {
              //获取Jar下资源
              result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
          }
          else {
              //获取地址下文件资源
              result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
          }
      }
      if (logger.isDebugEnabled()) {
          logger.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
      }
      return result.toArray(new Resource[0]);
  }
  ```
