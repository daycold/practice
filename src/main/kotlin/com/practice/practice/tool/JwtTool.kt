package com.practice.practice.tool

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.practice.practice.Mainable

/**
Copyright 2021 ChinaMobile Info. Tech Ltd. All rights reserved.

@author liuwang
@date 2021年08月19日
 */
class JwtTool : Mainable{
    override fun doMain() {
        val token = "Qe6NFQf9UWEa029LmPMpscI1Czb/1e6xuCjRupqga6t0A5/pT2Dm6KqWG9M5cd+xOaHC1Qad+nDLj2Dee3z6X4XF4BUmqYOBPOWoFWyj6AlNqsoVUYnF/dLDJicQCbAcPGXcYeiyR/n03GZAG0hEal+TZ8D8zjVnLT6NFXiyqHyjzAQeJahi56YW/KjADZxudXwJMP4iJGNY+S9saqKf9oCxe0T6nBwmQuClvZg23ojTfmBlQCGaLiiE8EJSfxNGR6cnBjvpDkl/+B450aY3jA==";
        val jwt =JWT.decode(token)
        println(jwt.payload)
        println(jwt.header)
    }
}