# 项目介绍
## 用途：
    为生产环境的配置影子库，方便在生产环境使用影子账号进行业务测试
## 使用：
    1. 加载本项目作为依赖，本项目会作为一个 starter 跟随项目启动而生效，自动装配；
    2. 为要使用影子库的服务建立相同表结构的schema
    3. 在配置文件加上配置 eg：
        shadow:
            enable: true
            datasource:
                driver-class-name: com.mysql.cj.jdbc.Driver
                url: jdbc:mysql://localhost:3306/eke_know_shadow?serverTimezone=GMT%2B8&characterEncoding=utf8&connectTimeout=2000&socketTimeout=5000&autoReconnect=true&tinyInt1isBit=false
                username: root
                password: MayEke2//
            # 如下的配置是同时生效的，每一个配置对应一个ShadowChecker，只有所有的 ShadowChecker 都检查可以路由到影子库才会判定路由到影子库
            config:
                excludeTables: "dic_info"  # 不进行影子库读写的表，例如字典表，配置表等
                shadowUserPrefix: "sd_shadow_"  # 根据当前系统的登陆名进行影子库读写判定
                specialShadowUsers: "苏亦客，骆驼"  # 特别指定影子库读写的影子账号
## 扩展:
    项目只包含了笔者预定义的几个 ShadowChecker，用户可以自定义影子库判定检查器，只需要实现 ShadowChecker 接口 并且注册成为 Spring 的 Bean 即可。
## 项目产生缘由：
        当前公司有预发布环境和生产环境，但是预发布环境的数据库和生产是同一个，因此每次我们上预发布之后只能点点查询，而不方便进行写操作，预发布环境的验收也就变得异常困难，大多数时候都是上完预发布没干啥就上生产了，个别服务数据隔离性比较好的可以偷偷建立测试账号进行测试，但是也会影响到报表的数据准确性，因此此方案也是较少采用。
    笔者前段时间在看 sharding 的文档的时候发现 sharding 提供了一个影子库的功能，突发奇想这或许可以解决我们的问题，于是试用了一下。发现sharding 提供的影子库功能有如下缺点：
        1. 配置太麻烦，一个表的增删改查都得独立配置
        2. 对 shadow 操作的校验 只支持 列 匹配，且只支持 String 类型 匹配；
        3. 我们的业务里面一些表有主表和子表，他们应该是绑定关系，sharding 不支持此操作
       虽然 sharding 的影子库方案经过研究被pass掉了，但是它的思路是可以借鉴的，于是就研究了一下多数据源管理，恰巧公司试用的是 mybatis-plus，可以做到优雅的管理多数据源。且提供了拦截器扩展，方便进行拦截加盐操作，于是开始了本项目的开发。

