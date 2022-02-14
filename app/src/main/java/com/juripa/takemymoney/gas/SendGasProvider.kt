package com.juripa.takemymoney.gas

import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

object SendGasProvider : ContractGasProvider {

    private val PRICE = WeiConverter.toWeiBInt(BigInteger.valueOf(5), WeiConverter.Unit.GWEI)
    private val LIMIT = BigInteger.valueOf(150_000L)

    override fun getGasPrice(contractFunc: String?): BigInteger {
        return PRICE
    }

    override fun getGasPrice(): BigInteger {
        return PRICE
    }

    override fun getGasLimit(contractFunc: String?): BigInteger {
        return LIMIT
    }

    override fun getGasLimit(): BigInteger {
        return LIMIT
    }
}