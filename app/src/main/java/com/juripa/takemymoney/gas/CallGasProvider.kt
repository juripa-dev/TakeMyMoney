package com.juripa.takemymoney.gas

import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

object CallGasProvider : ContractGasProvider {

    private val PRICE = BigInteger.valueOf(4_000_000_008L)
    private val LIMIT = BigInteger.valueOf(100_000L)

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