package com.practice.practice.request

import com.practice.practice.Mainable
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Stefan Liu
 */
class Jetty : Mainable {
    override fun doMain() {
        val server = Server(8080)
        server.handler = JettyHandler(server)
        server.start()
    }

    private class JettyHandler(server: Server) : Handler by server {
        override fun handle(
            target: String?,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse
        ) {
            request.inputStream.use { stream ->
                response.outputStream.use { out ->
//                    stream.transferTo(out)
                }
            }
        }
    }
}
