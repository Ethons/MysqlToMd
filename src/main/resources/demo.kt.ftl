# ${name}
## 数据表
```javascript
连接地址: ${dataUrl}
用户名: ${dataUserName}
密码: ${dataPassWord}
库名: ${dataTableName}
```
### 表结构
<#list tableList as table >
#### ${table.name}

|     栏      |   类型   |                释义                 |
| :---------: | :------: | :-----------------------: |
<#list table.columnList as column>
    |     ${column.columnName}      |   ${column.columnType}(${column.columnSize})   |               <#if column.columnName = 'id'>自增ID<#else>${column.columnMean}</#if>              |
</#list>
</#list>

## 接口文档