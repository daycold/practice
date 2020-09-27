package com.practice.practice

import com.sun.jndi.dns.DnsContextFactory
import java.util.*
import javax.naming.NamingException
import javax.naming.directory.Attributes
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext

/**
 * @author Stefan Liu
 */
fun main(args: Array<String>) {
//    Kafka(args[0]).doMain()
//    val ip = InetAddress.getLocalHost()
//    val network = NetworkInterface.getByInetAddress(ip)
//    val mac = network.hardwareAddress
//    println(String(mac))
//    JkTool().doMain()
//    val get = HttpGet("http://localhost:9097/mgmt/product/activity/products/template")
//    get.addHeader(
//        HttpHeaders.AUTHORIZATION,
//        "bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRpdGlvbmFsSW5mb3JtYXRpb24iOnsiYWNjb3VudElkIjoiREFBRDgxMDgtMzM1OS00QTQzLThGNzAtMDI4NTQyNDAzQjY0Iiwibmlja25hbWUiOiIxODcqKioqNTYyMyIsImF2YXRhciI6Imh0dHBzOi8vaW1nLnRpYW5kaXRhby5jb20vbWFsbC92bWFsbC9pbmRleC8yMDIwMDYvZjA5N2ZjZTc4ZGE4NDBjZDk5Yjk4NWJjZWM5MmMzNWEucG5nIiwidXNlcm5hbWUiOiJEQUFEODEwOC0zMzU5LTRBNDMtOEY3MC0wMjg1NDI0MDNCNjQiLCJvcmdhbml6YXRpb25Db2RlIjoiY3JtLmppYW5rZSIsImxvZ2luTmFtZSI6IkJDMjkyRTYxOTE2NERGRkFBQjZFNzk1QzI4OTU0MzJDIiwiYXV0aG9yaXRpZXMiOltdfSwiZXhwIjoxNjA5NDk3MDM2LCJ1c2VyX25hbWUiOiJEQUFEODEwOC0zMzU5LTRBNDMtOEY3MC0wMjg1NDI0MDNCNjQiLCJqdGkiOiJmZDNkOTgzZi05MWVlLTRhYmItOGExMy04ODAxYTBhNGNmZmQiLCJjbGllbnRfaWQiOiJtYWxsX2FwcCIsInNjb3BlIjpbIm9wZW5pZCJdfQ.R16slmtqNGDFOQ7yhLdg18q1w9H3ru1r2ppRjofx3RsZ6scPtbumRG55ZZZjRELbT_hhiYX-RXNBXMi39qlHfJaTUtiAkV-0VXAv8cdRhacFHsAoKiahVN3zRMbbcGg_nLFn9mB1Lrbl7J8Mcf6Z5LmIfwIrBk7qYVvCJq6h2m4"
//    )
//    HttpClients.createDefault().execute(get).use { response ->
//        response.entity.content.use { inputStream ->
//            val file = File("temp.xlsx")
//            file.createNewFile()
//            FileOutputStream(file).use { out ->
//                inputStream.copyTo(out)
//                out.flush()
//            }
//        }
//    }
}

interface Mainable {
    fun doMain()
}

