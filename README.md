在构建J2EE的RestFul接口的时候，Servlet不再是继承HttpServlet，而是调用别的Rest框架jersey

Eclipse
    Dynamic Web project
    lib中加入jaxrs-ri-2.25.1.zip的lib，也可以用maven构造
    
Idea
    加入Web framework
    src
        webapp
            WEB-INF
                web.xml
            index.jsp
            
不管是Eclipse还是Idea，其web.xml的核心都是一样的

    <servlet>
            <servlet-name>Jersey Web Application</servlet-name>
            <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
            <init-param>
                <param-name>jersey.config.server.provider.packages</param-name>
                <param-value>com.example</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>
        <servlet-mapping>
            <servlet-name>Jersey Web Application</servlet-name>
            <url-pattern>/webapi/*</url-pattern>
        </servlet-mapping>
        
    以后可以调用这个构造模版
 
### 2.2.2 Jarry插件与Rest服务
将simple-service-webapp运行在三种不同的容器环境中，为不同需求的读者提供参考，分别是Maven插件Jetty，Servlet容器Tomcat，
和Java EE容器Glassfish

complie--> test-complie--> test--> package--> install --> deploy
Jetty的位置: pre-integration-test integration-test  post-integration-test三个阶段
中的pre和post启动和停止内置Jetty服务器的。

配置Jetty服务器在pom文件中

mvn jetty:run  启动

tomcat : mvn clean package -DskipTests=true  

### 2.2.4 运行在Java EE容器



## 2.3 REST服务类型
在Rest服务中，资源类是接收REST请求并完成响应的核心类，而资源类是由REST服务的“提供者”来调度的。
这一概念类似其他框架中自定义的Servlet类，该类会将请求分派给指定的Controller/Action类来处理，JAX-RS
2.0 中定义的Application以及Servlet

Application类在JAX-RS 2.0标准中定义为javax.ws.rs.core.Application,相当于JAX-RS 2.0服务的入口。作为应用的入口，
Application需要知道具体的资源类文件，可以通过包扫描或者直接指定类文件的方式获得。如果REST服务没有自定义Application
的子类，容器将默认生成一个javax.ws.rs.core.Application 类

将REST服务分成4类

    类型1:当服务中没有Application子类时，容器查找Servlet的子类来做入口，如果Servlet的子类也不存在，则Rest服务类型为类型一
    类型2:Application(no) --> Servlet(yes)
    类型3:Application(yes)--> @ApplicationPath(yes)
    类型4:Application(yes)--> @ApplicationPath(no) 
    
REST服务类型一

    Application(no) Servlet(no)
    为REST服务生成一个名为javax.ws.rs.core.Application的Servlet实例，并自动探测匹配资源；根据Servlet的不同版本，在
    web.xml中定义REST请求处理的Servlet为这个动态生成的Servlet，并定义该Servlet为资源路径的匹配
    
    Servlet3 配置，极简单
    <servlet>
    		<servlet-name>javax.ws.rs.core.Application</servlet-name>
    	</servlet>
    	<servlet-mapping>
    		<servlet-name>javax.ws.rs.core.Application</servlet-name>
    		<url-pattern>/rest/*</url-pattern>
    </servlet-mapping>
    
    servlet2配置
    <servlet>
            <servlet-name>Jersey Web Application</servlet-name>
            <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
            <init-param>
                <param-name>jersey.config.server.provider.packages</param-name>
                <param-value>com.example</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>
        <servlet-mapping>
            <servlet-name>Jersey Web Application</servlet-name>
            <url-pattern>/webapi/*</url-pattern>
        </servlet-mapping>
        初始化参数需要显式给出要加载的资源类所在的包名，servlet2的支持包jersey-container-servlet-core不具备自动扫描资源类的功能
        
2REST服务类型2

    Application(no),Servlet(yes)
    定义Servlet的子类: extends ServletContainer
    
    import javax.servlet.annotation.WebInitParam;
    import javax.servlet.annotation.WebServlet;
    
    /**
     * Created by JackNiu on 2017/7/20.
     */
    @WebServlet(
            initParams =@WebInitParam(name="jersey.config.server.provider.packages",value ="com.rest2"),
            urlPatterns = "/webapi/*",
            loadOnStartup = 1
    )
    public class AirServlet  extends ServletContainer{
    }
    没有webx.ml等文件，是Servlet3 有而Servlet没有的功能
    修改pom.xml   failOnMissingWebXml
    <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-war-plugin</artifactId>
                    <version>2.3</version>
                    <configuration>
                        <failOnMissingWebXml>false</failOnMissingWebXml>
                    </configuration>
    </plugin>
    
3 REST服务类型3

    Application(存在), @ApplicationPath(存在)
    Servlet 2 中Application存在的是继承 ResourceConfig
    
    @ApplicationPath("/webapi/*")
    public class AirResourceConfig extends ResourceConfig {
        public AirResourceConfig(){
            packages("com.rest3");
        }
    }
    需要定义web.xml，但它是一个空文件
    
    Servket3 定义，继承Application，实现资源类的配置，Servlet3 统一不需要web.xml
    
    @ApplicationPath("/webapi/*")
    public class AirApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            final Set<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(MyResource.class);
            return classes;
        }
    }
    
类型4:

    Application(有)，servlet(没有)或没有@ApplicationPath
    
    servlet2 存在Application，没有path注解，也就意味着没有servlet，配置servlet
    public class AirApplication extends Application {
    	@Override
    	public Set<Class<?>> getClasses() {
    		final Set<Class<?>> classes = new HashSet<Class<?>>();
    		classes.add(MyResource.class);
    		return classes;
    	}
    }
    
    在web.xml中配置
    <servlet>
    		<servlet-name>Jersey Web Application</servlet-name>
    		<servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
    		<init-param>
    			<param-name>javax.ws.rs.Application</param-name>
    			<param-value>com.example.AirApplication</param-value>
    		</init-param>
    		<load-on-startup>1</load-on-startup>
    	</servlet>
    	<servlet-mapping>
    		<servlet-name>Jersey Web Application</servlet-name>
    		<url-pattern>/webapi/*</url-pattern>
    </servlet-mapping>
    
    servlet3 也是没有Servlet的配置
    
    <servlet>
    		<servlet-name>com.example.AirApplication</servlet-name>
    	</servlet>
    	<servlet-mapping>
    		<servlet-name>com.example.AirApplication</servlet-name>
    		<url-pattern>/webapi/*</url-pattern>
    </servlet-mapping>
    
    综上所属，还是类型1好
    
## 2.4 REST应用描述
以XML格式展示当前REST环境中所提供的REST的服务接口，这种XML格式的描述就是WADL
WADL(Web Application Description Language,Web应用描述性语言)是用来描述基于HTTP的REST式Web服务部署情况的。
采用XML格式，支持多种数据类型的描述。

通过浏览器访问"服务器路径/application.wadl"即可打开该服务的WADL内容。相对于WADL，WSDL更为人们所熟知。

