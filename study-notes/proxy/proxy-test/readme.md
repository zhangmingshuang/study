# 单例情况测试 
[ProxyMain.java](./src/main/java/com/test/proxy/ProxyMain.java)

|代理|1K|1W|10W|100W|1000W|1B|5B|
|--|--|--|--|--|--|--|--|
|原生|0|1|0|3|32|331|1320|
|JDK|1|4|2|9|84|772|3373|
|CGLib|0|4|8|11|99|842|3944|


