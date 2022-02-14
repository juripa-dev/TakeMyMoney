package com.juripa.takemymoney

import com.juripa.takemymoney.gas.WeiConverter
import java.math.BigInteger

object TokenRule {
    const val ENABLE_LOCATION_CHANGE_TOKEN_NUMBER = 10
    val LOCK_ETH = WeiConverter.toWeiBInt(BigInteger.valueOf(100), WeiConverter.Unit.FINNEY)
}