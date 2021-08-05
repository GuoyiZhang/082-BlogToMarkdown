package pro.guoyi.blog.bean;

import java.util.List;

/**
 * @author guoyi
 * @version Id: ArticleInfo.java, v 0.1 2021/8/5 下午9:21 guoyi Exp $$
 */
public class ArticleInfo {

    /**
     * code : 200
     * msg : success
     * data : {"article_id":"119325976","title":"什么是分布式锁？实现分布式锁的三种方式","description":"在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。那具体什么是分布式锁，分布式锁应用在哪些业务场景、如何来实现分布式锁呢？\n\n一 为什么要使用分布式锁\n\n我们在开发应用的时候，如果需要对某一个共享变量进行多线程同步访问的时候，可以使用我们学到的锁进行处理，并且可以完美的运行，毫无Bug！\n注意这是单机应用，后来业务发展，需要做集群，一个应用需要部署到几台机器上然后做负载均衡，大致如下图：\n\n\n\n上图可以看到，变量A存在三个服务器内存中（这个变量A主要体现是在一","content":"<p>在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。那具体什么是分布式锁，分布式锁应用在哪些业务场景、如何来实现分布式锁呢？<\/p>\n\n<h2>一 为什么要使用分布式锁<\/h2>\n\n<p>我们在开发应用的时候，如果需要对某一个共享变量进行多线程同步访问的时候，可以使用我们学到的锁进行处理，并且可以完美的运行，毫无Bug！<br /><br />\n注意这是单机应用，后来业务发展，需要做集群，一个应用需要部署到几台机器上然后做负载均衡，大致如下图：<\/p>\n\n<p><a href=\"https://img2018.cnblogs.com/blog/1350514/201906/1350514-20190625021257274-823428432.png\"><img alt=\"\" src=\"https://img-blog.csdnimg.cn/img_convert/1b3bf7ac42d6ffcbd82c939a4757566d.png\" /><\/a><\/p>\n\n<p>上图可以看到，变量A存在三个服务器内存中（这个变量A主要体现是在一个类中的一个成员变量，是一个有状态的对象），如果不加任何控制的话，变量A同时都会在分配一块内存，三个请求发过来同时对这个变量操作，显然结果是不对的！即使不是同时发过来，三个请求分别操作三个不同内存区域的数据，变量A之间不存在共享，也不具有可见性，处理的结果也是不对的！<br /><br />\n如果我们业务中确实存在这个场景的话，我们就需要一种方法解决这个问题！<br /><br />\n为了保证一个方法或属性在高并发情况下的同一时间只能被同一个线程执行，在传统单体应用单机部署的情况下，可以使用并发处理相关的功能进行互斥控制。但是，随着业务发展的需要，原单体单机部署的系统被演化成分布式集群系统后，由于分布式系统多线程、多进程并且分布在不同机器上，这将使原单机部署情况下的并发控制锁策略失效，单纯的应用并不能提供分布式锁的能力。为了解决这个问题就需要一种跨机器的互斥机制来控制共享资源的访问，这就是分布式锁要解决的问题！<\/p>\n\n<h2>二、分布式锁应该具备哪些条件<\/h2>\n\n<p>在分析分布式锁的三种实现方式之前，先了解一下分布式锁应该具备哪些条件：<br /><br />\n1、在分布式系统环境下，一个方法在同一时间只能被一个机器的一个线程执行；<br />\n2、高可用的获取锁与释放锁；<br />\n3、高性能的获取锁与释放锁；<br />\n4、具备可重入特性；<br />\n5、具备锁失效机制，防止死锁；<br />\n6、具备非阻塞锁特性，即没有获取到锁将直接返回获取锁失败。<\/p>\n\n<h2>三、分布式锁的三种实现方式<\/h2>\n\n<p>目前几乎很多大型网站及应用都是分布式部署的，分布式场景中的数据一致性问题一直是一个比较重要的话题。分布式的CAP理论告诉我们\u201c任何一个分布式系统都无法同时满足一致性（Consistency）、可用性（Availability）和分区容错性（Partition tolerance），最多只能同时满足两项。\u201d所以，很多系统在设计之初就要对这三者做出取舍。在互联网领域的绝大多数的场景中，都需要牺牲强一致性来换取系统的高可用性，系统往往只需要保证\u201c最终一致性\u201d，只要这个最终时间是在用户可以接受的范围内即可。<br /><br />\n在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。有的时候，我们需要保证一个方法在同一时间内只能被同一个线程执行。<\/p>\n\n<pre>\n基于数据库实现分布式锁；\n基于缓存（Redis等）实现分布式锁；\n基于Zookeeper实现分布式锁；<\/pre>\n\n<h2>四、基于数据库的实现方式<\/h2>\n\n<p>基于数据库的实现方式的核心思想是：在数据库中创建一个表，表中包含<strong>方法名<\/strong>等字段，并在<strong>方法名字段上创建唯一索引<\/strong>，想要执行某个方法，就使用这个方法名向表中插入数据，成功插入则获取锁，执行完成后删除对应的行数据释放锁。<\/p>\n\n<p>（1）创建一个表：<\/p>\n\n<pre>\n<code class=\"language-sql\">DROP TABLE IF EXISTS `method_lock`;\nCREATE TABLE `method_lock` (\n  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n  `method_name` varchar(64) NOT NULL COMMENT '锁定的方法名',\n  `desc` varchar(255) NOT NULL COMMENT '备注信息',\n  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n  PRIMARY KEY (`id`),\n  UNIQUE KEY `uidx_method_name` (`method_name`) USING BTREE\n) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='锁定中的方法';<\/code><\/pre>\n\n<p><a href=\"https://img2018.cnblogs.com/blog/1350514/201906/1350514-20190625021849873-1425693083.png\"><img alt=\"\" src=\"https://img-blog.csdnimg.cn/img_convert/d95f25d871b94728e5ed1d203c1a8ea8.png\" /><\/a><\/p>\n\n<p>（2）想要执行某个方法，就使用这个方法名向表中插入数据：<\/p>\n\n<pre>\nINSERT INTO method_lock (method_name, desc) VALUES ('methodName', '测试的methodName');<\/pre>\n\n<p>因为我们对<code>method_name<\/code>做了<strong>唯一性约束<\/strong>，这里如果有多个请求同时提交到数据库的话，数据库会保证只有一个操作可以成功，那么我们就可以认为操作成功的那个线程获得了该方法的锁，可以执行方法体内容。<\/p>\n\n<p>（3）成功插入则获取锁，执行完成后删除对应的行数据释放锁：<\/p>\n\n<pre>\ndelete from method_lock where method_name ='methodName';<\/pre>\n\n<p>注意：这只是使用基于数据库的一种方法，使用数据库实现分布式锁还有很多其他的玩法！<br /><br />\n使用基于数据库的这种实现方式很简单，但是对于分布式锁应该具备的条件来说，它有一些问题需要解决及优化：<br /><br />\n1、因为是基于数据库实现的，数据库的可用性和性能将直接影响分布式锁的可用性及性能，所以，数据库需要双机部署、数据同步、主备切换；<br /><br />\n2、不具备可重入的特性，因为同一个线程在释放锁之前，行数据一直存在，无法再次成功插入数据，所以，需要在表中新增一列，用于记录当前获取到锁的机器和线程信息，在再次获取锁的时候，先查询表中机器和线程信息是否和当前机器和线程相同，若相同则直接获取锁；<br /><br />\n3、没有锁失效机制，因为有可能出现成功插入数据后，服务器宕机了，对应的数据没有被删除，当服务恢复后一直获取不到锁，所以，需要在表中新增一列，用于记录失效时间，并且需要有定时任务清除这些失效的数据；<br /><br />\n4、不具备阻塞锁特性，获取不到锁直接返回失败，所以需要优化获取逻辑，循环多次去获取。<br /><br />\n5、在实施的过程中会遇到各种不同的问题，为了解决这些问题，实现方式将会越来越复杂；依赖数据库需要一定的资源开销，性能问题需要考虑。<\/p>\n\n<h2>五、基于Redis的实现方式<\/h2>\n\n<p>1、选用Redis实现分布式锁原因：<br /><br />\n（1）Redis有很高的性能；<br />\n（2）Redis命令对此支持较好，实现起来比较方便<br /><br />\n2、使用命令介绍：<br /><br />\n（1）SETNX<br /><br />\nSETNX key val：当且仅当key不存在时，set一个key为val的字符串，返回1；若key存在，则什么都不做，返回0。<br /><br />\n（2）expire<br /><br />\nexpire key timeout：为key设置一个超时时间，单位为second，超过这个时间锁会自动释放，避免死锁。<br /><br />\n（3）delete<br /><br />\ndelete key：删除key<br /><br />\n在使用Redis实现分布式锁的时候，主要就会使用到这三个命令。<br /><br />\n3、实现思想：<br /><br />\n（1）获取锁的时候，使用setnx加锁，并使用expire命令为锁添加一个超时时间，超过该时间则自动释放锁，锁的value值为一个随机生成的UUID，通过此在释放锁的时候进行判断。<br /><br />\n（2）获取锁的时候还设置一个获取的超时时间，若超过这个时间则放弃获取锁。<br /><br />\n（3）释放锁的时候，通过UUID判断是不是该锁，若是该锁，则执行delete进行锁释放。<br /><br />\n4、 分布式锁的简单实现代码：<\/p>\n\n<p><a><img alt=\"复制代码\" src=\"https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png\" /><\/a><\/p>\n\n<pre>\n#连接redis\nredis_client = redis.Redis(host=\"localhost\",\n                           port=6379,\n                           password=password,\n                           db=10)\n\n#获取一个锁\nlock_name：锁定名称\nacquire_time: 客户端等待获取锁的时间\ntime_out: 锁的超时时间\ndef acquire_lock(lock_name, acquire_time=10, time_out=10):\n    \"\"\"获取一个分布式锁\"\"\"\n    identifier = str(uuid.uuid4())\n    end = time.time() + acquire_time\n    lock = \"string:lock:\" + lock_name\n    while time.time() &lt; end:\n        if redis_client.setnx(lock, identifier):\n            # 给锁设置超时时间, 防止进程崩溃导致其他进程无法获取锁\n            redis_client.expire(lock, time_out)\n            return identifier\n        elif not redis_client.ttl(lock):\n            redis_client.expire(lock, time_out)\n        time.sleep(0.001)\n    return False\n\n#释放一个锁\ndef release_lock(lock_name, identifier):\n    \"\"\"通用的锁释放函数\"\"\"\n    lock = \"string:lock:\" + lock_name\n    pip = redis_client.pipeline(True)\n    while True:\n        try:\n            pip.watch(lock)\n            lock_value = redis_client.get(lock)\n            if not lock_value:\n                return True\n\n            if lock_value.decode() == identifier:\n                pip.multi()\n                pip.delete(lock)\n                pip.execute()\n                return True\n            pip.unwatch()\n            break\n        except redis.excetions.WacthcError:\n            pass\n    return False<\/pre>\n\n<p><a><img alt=\"复制代码\" src=\"https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png\" /><\/a><\/p>\n\n<p><strong>5、测试刚才实现的分布式锁<\/strong><\/p>\n\n<p>例子中使用50个线程模拟秒杀一个商品，使用\u2013运算符来实现商品减少，从结果有序性就可以看出是否为加锁状态。<\/p>\n\n<p><a><img alt=\"复制代码\" src=\"https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png\" /><\/a><\/p>\n\n<pre>\ndef seckill():\n    identifier=acquire_lock('resource')\n    print(Thread.getName(),\"获得了锁\")\n    release_lock('resource',identifier)\n\n\nfor i in range(50):\n    t = Thread(target=seckill)\n    t.start()<\/pre>\n\n<p><a><img alt=\"复制代码\" src=\"https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png\" /><\/a><\/p>\n\n<h2>六、基于ZooKeeper的实现方式<\/h2>\n\n<p>ZooKeeper是一个为分布式应用提供一致性服务的开源组件，它内部是一个分层的文件系统目录树结构，规定同一个目录下只能有一个唯一文件名。基于ZooKeeper实现分布式锁的步骤如下：<br /><br />\n（1）创建一个目录mylock；<br />\n（2）线程A想获取锁就在mylock目录下创建临时顺序节点；<br />\n（3）获取mylock目录下所有的子节点，然后获取比自己小的兄弟节点，如果不存在，则说明当前线程顺序号最小，获得锁；<br />\n（4）线程B获取所有节点，判断自己不是最小节点，设置监听比自己次小的节点；<br />\n（5）线程A处理完，删除自己的节点，线程B监听到变更事件，判断自己是不是最小的节点，如果是则获得锁。<br /><br />\n这里推荐一个Apache的开源库Curator，它是一个ZooKeeper客户端，Curator提供的InterProcessMutex是分布式锁的实现，acquire方法用于获取锁，release方法用于释放锁。<br /><br />\n优点：具备高可用、可重入、阻塞锁特性，可解决失效死锁问题。<br /><br />\n缺点：因为需要频繁的创建和删除节点，性能上不如Redis方式。<\/p>\n\n<h2>七、总结<\/h2>\n\n<p><br />\n上面的三种实现方式，没有在所有场合都是完美的，所以，应根据不同的应用场景选择最适合的实现方式。<br /><br />\n在分布式环境中，对资源进行上锁有时候是很重要的，比如抢购某一资源，这时候使用分布式锁就可以很好地控制资源。<br />\n当然，在具体使用中，还需要考虑很多因素，比如超时时间的选取，获取锁时间的选取对并发量都有很大的影响，上述实现的分布式锁也只是一种简单的实现，主要是一种思想<\/p>\n\n<pre>\n\n<\/pre>\n","markdowncontent":"","tags":"分布式锁,分布式","categories":"Java,后端,高并发系统设计","type":"original","status":1,"read_type":"public","reason":"","resource_url":"","original_link":"","authorized_status":false,"check_original":false,"editor_type":0,"plan":[],"cover_type":"0","cover_images":[]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * article_id : 119325976
         * title : 什么是分布式锁？实现分布式锁的三种方式
         * description : 在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。那具体什么是分布式锁，分布式锁应用在哪些业务场景、如何来实现分布式锁呢？

         一 为什么要使用分布式锁

         我们在开发应用的时候，如果需要对某一个共享变量进行多线程同步访问的时候，可以使用我们学到的锁进行处理，并且可以完美的运行，毫无Bug！
         注意这是单机应用，后来业务发展，需要做集群，一个应用需要部署到几台机器上然后做负载均衡，大致如下图：



         上图可以看到，变量A存在三个服务器内存中（这个变量A主要体现是在一
         * content : <p>在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。那具体什么是分布式锁，分布式锁应用在哪些业务场景、如何来实现分布式锁呢？</p>

         <h2>一 为什么要使用分布式锁</h2>

         <p>我们在开发应用的时候，如果需要对某一个共享变量进行多线程同步访问的时候，可以使用我们学到的锁进行处理，并且可以完美的运行，毫无Bug！<br /><br />
         注意这是单机应用，后来业务发展，需要做集群，一个应用需要部署到几台机器上然后做负载均衡，大致如下图：</p>

         <p><a href="https://img2018.cnblogs.com/blog/1350514/201906/1350514-20190625021257274-823428432.png"><img alt="" src="https://img-blog.csdnimg.cn/img_convert/1b3bf7ac42d6ffcbd82c939a4757566d.png" /></a></p>

         <p>上图可以看到，变量A存在三个服务器内存中（这个变量A主要体现是在一个类中的一个成员变量，是一个有状态的对象），如果不加任何控制的话，变量A同时都会在分配一块内存，三个请求发过来同时对这个变量操作，显然结果是不对的！即使不是同时发过来，三个请求分别操作三个不同内存区域的数据，变量A之间不存在共享，也不具有可见性，处理的结果也是不对的！<br /><br />
         如果我们业务中确实存在这个场景的话，我们就需要一种方法解决这个问题！<br /><br />
         为了保证一个方法或属性在高并发情况下的同一时间只能被同一个线程执行，在传统单体应用单机部署的情况下，可以使用并发处理相关的功能进行互斥控制。但是，随着业务发展的需要，原单体单机部署的系统被演化成分布式集群系统后，由于分布式系统多线程、多进程并且分布在不同机器上，这将使原单机部署情况下的并发控制锁策略失效，单纯的应用并不能提供分布式锁的能力。为了解决这个问题就需要一种跨机器的互斥机制来控制共享资源的访问，这就是分布式锁要解决的问题！</p>

         <h2>二、分布式锁应该具备哪些条件</h2>

         <p>在分析分布式锁的三种实现方式之前，先了解一下分布式锁应该具备哪些条件：<br /><br />
         1、在分布式系统环境下，一个方法在同一时间只能被一个机器的一个线程执行；<br />
         2、高可用的获取锁与释放锁；<br />
         3、高性能的获取锁与释放锁；<br />
         4、具备可重入特性；<br />
         5、具备锁失效机制，防止死锁；<br />
         6、具备非阻塞锁特性，即没有获取到锁将直接返回获取锁失败。</p>

         <h2>三、分布式锁的三种实现方式</h2>

         <p>目前几乎很多大型网站及应用都是分布式部署的，分布式场景中的数据一致性问题一直是一个比较重要的话题。分布式的CAP理论告诉我们“任何一个分布式系统都无法同时满足一致性（Consistency）、可用性（Availability）和分区容错性（Partition tolerance），最多只能同时满足两项。”所以，很多系统在设计之初就要对这三者做出取舍。在互联网领域的绝大多数的场景中，都需要牺牲强一致性来换取系统的高可用性，系统往往只需要保证“最终一致性”，只要这个最终时间是在用户可以接受的范围内即可。<br /><br />
         在很多场景中，我们为了保证数据的最终一致性，需要很多的技术方案来支持，比如分布式事务、分布式锁等。有的时候，我们需要保证一个方法在同一时间内只能被同一个线程执行。</p>

         <pre>
         基于数据库实现分布式锁；
         基于缓存（Redis等）实现分布式锁；
         基于Zookeeper实现分布式锁；</pre>

         <h2>四、基于数据库的实现方式</h2>

         <p>基于数据库的实现方式的核心思想是：在数据库中创建一个表，表中包含<strong>方法名</strong>等字段，并在<strong>方法名字段上创建唯一索引</strong>，想要执行某个方法，就使用这个方法名向表中插入数据，成功插入则获取锁，执行完成后删除对应的行数据释放锁。</p>

         <p>（1）创建一个表：</p>

         <pre>
         <code class="language-sql">DROP TABLE IF EXISTS `method_lock`;
         CREATE TABLE `method_lock` (
         `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
         `method_name` varchar(64) NOT NULL COMMENT '锁定的方法名',
         `desc` varchar(255) NOT NULL COMMENT '备注信息',
         `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         PRIMARY KEY (`id`),
         UNIQUE KEY `uidx_method_name` (`method_name`) USING BTREE
         ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='锁定中的方法';</code></pre>

         <p><a href="https://img2018.cnblogs.com/blog/1350514/201906/1350514-20190625021849873-1425693083.png"><img alt="" src="https://img-blog.csdnimg.cn/img_convert/d95f25d871b94728e5ed1d203c1a8ea8.png" /></a></p>

         <p>（2）想要执行某个方法，就使用这个方法名向表中插入数据：</p>

         <pre>
         INSERT INTO method_lock (method_name, desc) VALUES ('methodName', '测试的methodName');</pre>

         <p>因为我们对<code>method_name</code>做了<strong>唯一性约束</strong>，这里如果有多个请求同时提交到数据库的话，数据库会保证只有一个操作可以成功，那么我们就可以认为操作成功的那个线程获得了该方法的锁，可以执行方法体内容。</p>

         <p>（3）成功插入则获取锁，执行完成后删除对应的行数据释放锁：</p>

         <pre>
         delete from method_lock where method_name ='methodName';</pre>

         <p>注意：这只是使用基于数据库的一种方法，使用数据库实现分布式锁还有很多其他的玩法！<br /><br />
         使用基于数据库的这种实现方式很简单，但是对于分布式锁应该具备的条件来说，它有一些问题需要解决及优化：<br /><br />
         1、因为是基于数据库实现的，数据库的可用性和性能将直接影响分布式锁的可用性及性能，所以，数据库需要双机部署、数据同步、主备切换；<br /><br />
         2、不具备可重入的特性，因为同一个线程在释放锁之前，行数据一直存在，无法再次成功插入数据，所以，需要在表中新增一列，用于记录当前获取到锁的机器和线程信息，在再次获取锁的时候，先查询表中机器和线程信息是否和当前机器和线程相同，若相同则直接获取锁；<br /><br />
         3、没有锁失效机制，因为有可能出现成功插入数据后，服务器宕机了，对应的数据没有被删除，当服务恢复后一直获取不到锁，所以，需要在表中新增一列，用于记录失效时间，并且需要有定时任务清除这些失效的数据；<br /><br />
         4、不具备阻塞锁特性，获取不到锁直接返回失败，所以需要优化获取逻辑，循环多次去获取。<br /><br />
         5、在实施的过程中会遇到各种不同的问题，为了解决这些问题，实现方式将会越来越复杂；依赖数据库需要一定的资源开销，性能问题需要考虑。</p>

         <h2>五、基于Redis的实现方式</h2>

         <p>1、选用Redis实现分布式锁原因：<br /><br />
         （1）Redis有很高的性能；<br />
         （2）Redis命令对此支持较好，实现起来比较方便<br /><br />
         2、使用命令介绍：<br /><br />
         （1）SETNX<br /><br />
         SETNX key val：当且仅当key不存在时，set一个key为val的字符串，返回1；若key存在，则什么都不做，返回0。<br /><br />
         （2）expire<br /><br />
         expire key timeout：为key设置一个超时时间，单位为second，超过这个时间锁会自动释放，避免死锁。<br /><br />
         （3）delete<br /><br />
         delete key：删除key<br /><br />
         在使用Redis实现分布式锁的时候，主要就会使用到这三个命令。<br /><br />
         3、实现思想：<br /><br />
         （1）获取锁的时候，使用setnx加锁，并使用expire命令为锁添加一个超时时间，超过该时间则自动释放锁，锁的value值为一个随机生成的UUID，通过此在释放锁的时候进行判断。<br /><br />
         （2）获取锁的时候还设置一个获取的超时时间，若超过这个时间则放弃获取锁。<br /><br />
         （3）释放锁的时候，通过UUID判断是不是该锁，若是该锁，则执行delete进行锁释放。<br /><br />
         4、 分布式锁的简单实现代码：</p>

         <p><a><img alt="复制代码" src="https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png" /></a></p>

         <pre>
         #连接redis
         redis_client = redis.Redis(host="localhost",
         port=6379,
         password=password,
         db=10)

         #获取一个锁
         lock_name：锁定名称
         acquire_time: 客户端等待获取锁的时间
         time_out: 锁的超时时间
         def acquire_lock(lock_name, acquire_time=10, time_out=10):
         """获取一个分布式锁"""
         identifier = str(uuid.uuid4())
         end = time.time() + acquire_time
         lock = "string:lock:" + lock_name
         while time.time() &lt; end:
         if redis_client.setnx(lock, identifier):
         # 给锁设置超时时间, 防止进程崩溃导致其他进程无法获取锁
         redis_client.expire(lock, time_out)
         return identifier
         elif not redis_client.ttl(lock):
         redis_client.expire(lock, time_out)
         time.sleep(0.001)
         return False

         #释放一个锁
         def release_lock(lock_name, identifier):
         """通用的锁释放函数"""
         lock = "string:lock:" + lock_name
         pip = redis_client.pipeline(True)
         while True:
         try:
         pip.watch(lock)
         lock_value = redis_client.get(lock)
         if not lock_value:
         return True

         if lock_value.decode() == identifier:
         pip.multi()
         pip.delete(lock)
         pip.execute()
         return True
         pip.unwatch()
         break
         except redis.excetions.WacthcError:
         pass
         return False</pre>

         <p><a><img alt="复制代码" src="https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png" /></a></p>

         <p><strong>5、测试刚才实现的分布式锁</strong></p>

         <p>例子中使用50个线程模拟秒杀一个商品，使用–运算符来实现商品减少，从结果有序性就可以看出是否为加锁状态。</p>

         <p><a><img alt="复制代码" src="https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png" /></a></p>

         <pre>
         def seckill():
         identifier=acquire_lock('resource')
         print(Thread.getName(),"获得了锁")
         release_lock('resource',identifier)


         for i in range(50):
         t = Thread(target=seckill)
         t.start()</pre>

         <p><a><img alt="复制代码" src="https://img-blog.csdnimg.cn/img_convert/48304ba5e6f9fe08f3fa1abda7d326ab.png" /></a></p>

         <h2>六、基于ZooKeeper的实现方式</h2>

         <p>ZooKeeper是一个为分布式应用提供一致性服务的开源组件，它内部是一个分层的文件系统目录树结构，规定同一个目录下只能有一个唯一文件名。基于ZooKeeper实现分布式锁的步骤如下：<br /><br />
         （1）创建一个目录mylock；<br />
         （2）线程A想获取锁就在mylock目录下创建临时顺序节点；<br />
         （3）获取mylock目录下所有的子节点，然后获取比自己小的兄弟节点，如果不存在，则说明当前线程顺序号最小，获得锁；<br />
         （4）线程B获取所有节点，判断自己不是最小节点，设置监听比自己次小的节点；<br />
         （5）线程A处理完，删除自己的节点，线程B监听到变更事件，判断自己是不是最小的节点，如果是则获得锁。<br /><br />
         这里推荐一个Apache的开源库Curator，它是一个ZooKeeper客户端，Curator提供的InterProcessMutex是分布式锁的实现，acquire方法用于获取锁，release方法用于释放锁。<br /><br />
         优点：具备高可用、可重入、阻塞锁特性，可解决失效死锁问题。<br /><br />
         缺点：因为需要频繁的创建和删除节点，性能上不如Redis方式。</p>

         <h2>七、总结</h2>

         <p><br />
         上面的三种实现方式，没有在所有场合都是完美的，所以，应根据不同的应用场景选择最适合的实现方式。<br /><br />
         在分布式环境中，对资源进行上锁有时候是很重要的，比如抢购某一资源，这时候使用分布式锁就可以很好地控制资源。<br />
         当然，在具体使用中，还需要考虑很多因素，比如超时时间的选取，获取锁时间的选取对并发量都有很大的影响，上述实现的分布式锁也只是一种简单的实现，主要是一种思想</p>

         <pre>

         </pre>
         * markdowncontent :
         * tags : 分布式锁,分布式
         * categories : Java,后端,高并发系统设计
         * type : original
         * status : 1
         * read_type : public
         * reason :
         * resource_url :
         * original_link :
         * authorized_status : false
         * check_original : false
         * editor_type : 0
         * plan : []
         * cover_type : 0
         * cover_images : []
         */

        private String article_id;
        private String title;
        private String description;
        private String content;
        private String markdowncontent;
        private String tags;
        private String categories;
        private String type;
        private int status;
        private String read_type;
        private String reason;
        private String resource_url;
        private String original_link;
        private boolean authorized_status;
        private boolean check_original;
        private int editor_type;
        private String cover_type;
        private List<?> plan;
        private List<?> cover_images;

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMarkdowncontent() {
            return markdowncontent;
        }

        public void setMarkdowncontent(String markdowncontent) {
            this.markdowncontent = markdowncontent;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getRead_type() {
            return read_type;
        }

        public void setRead_type(String read_type) {
            this.read_type = read_type;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getResource_url() {
            return resource_url;
        }

        public void setResource_url(String resource_url) {
            this.resource_url = resource_url;
        }

        public String getOriginal_link() {
            return original_link;
        }

        public void setOriginal_link(String original_link) {
            this.original_link = original_link;
        }

        public boolean isAuthorized_status() {
            return authorized_status;
        }

        public void setAuthorized_status(boolean authorized_status) {
            this.authorized_status = authorized_status;
        }

        public boolean isCheck_original() {
            return check_original;
        }

        public void setCheck_original(boolean check_original) {
            this.check_original = check_original;
        }

        public int getEditor_type() {
            return editor_type;
        }

        public void setEditor_type(int editor_type) {
            this.editor_type = editor_type;
        }

        public String getCover_type() {
            return cover_type;
        }

        public void setCover_type(String cover_type) {
            this.cover_type = cover_type;
        }

        public List<?> getPlan() {
            return plan;
        }

        public void setPlan(List<?> plan) {
            this.plan = plan;
        }

        public List<?> getCover_images() {
            return cover_images;
        }

        public void setCover_images(List<?> cover_images) {
            this.cover_images = cover_images;
        }
    }
}