# vi与vim如何修改tab为4个空格方法示例

    - 配置文件位置：/etc/virc 和 /etc/vimrc
    - 配置文件中如果要添加注释，不能用#，要使用”
    ```
    " add by school1024.com
    set ts=4
    set softtabstop=4
    set shiftwidth=4
    set expandtab
    set autoindent
    ```
    ts 是tabstop的缩写，设TAB宽度为4个空格。
    softtabstop 表示在编辑模式的时候按退格键的时候退回缩进的长度，当使用 expandtab 时特别有用。
    shiftwidth 表示每一级缩进的长度，一般设置成跟 softtabstop 一样。
    expandtab 表示缩进用空格来表示，noexpandtab 则是用制表符表示一个缩进。
    autoindent 自动缩进


    - 对以前的文件可以用下面的命令进行空格和TAB互换

        - TAB替换为空格
        ```
        :set ts=4
        :set expandtab
        :%retab!
        ```
        - 空格替换为TAB
        ```
        :set ts=4
        :set noexpandtab
        :%retab!
        ```
        加!是用于处理非空白字符之后的TAB，即所有的TAB，若不加!，则只处理行首的TAB。
