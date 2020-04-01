package com.practice.practice.request

import com.practice.practice.Mainable
import org.apache.catalina.connector.Connector
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import javax.servlet.Servlet
import javax.servlet.ServletConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * @author Stefan Liu
 */
class Tomcat : Mainable {
    override fun doMain() {
        val tomcat = Tomcat()
        tomcat.setBaseDir("tomcat.8080")
        tomcat.host.autoDeploy = false
        val connector = Connector("HTTP/1.1").apply {
            port = 8080
        }
        tomcat.connector = connector
        val context =
            StandardContext().apply {
                name = "defaultContext"
                docBase = "default"
                addLifecycleListener(Tomcat.FixContextListener())
                path = ""
            }
        tomcat.host.addChild(context)
        tomcat.addServlet("defaultContext", "defaultServlet", TomcatServlet())
        context.addServletMappingDecoded("/test", "defaultServlet")
        tomcat.engine
        tomcat.start()
    }

    private class TomcatServlet : Servlet {
        private var servletConfig: ServletConfig? = null

        override fun getServletConfig(): ServletConfig? {
            return servletConfig
        }

        override fun destroy() {
        }

        override fun init(config: ServletConfig?) {
            this.servletConfig = config
        }

        override fun getServletInfo(): String {
            return "default servlet"
        }

        override fun service(req: ServletRequest, res: ServletResponse) {
            req.inputStream.use { stream ->
                res.outputStream.use { out ->
                    stream.transferTo(out)
                }
            }
        }
    }
}
