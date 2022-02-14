package com.juripa.takemymoney.gas

import java.math.BigDecimal
import java.math.BigInteger

object WeiConverter {
    fun fromWei(number: String, unit: Unit): BigDecimal {
        return fromWei(BigDecimal(number), unit)
    }

    fun fromWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.divide(unit.weiFactorD)
    }

    fun toWei(number: String, unit: Unit): BigDecimal {
        return toWei(BigDecimal(number), unit)
    }

    fun toWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.multiply(unit.weiFactorD)
    }

    fun fromWeiBInt(number: String, unit: Unit): BigInteger {
        return fromWeiBInt(BigInteger(number), unit)
    }

    fun fromWeiBInt(number: BigInteger, unit: Unit): BigInteger {
        return number.divide(unit.weiFactorB)
    }

    fun toWeiBInt(number: String, unit: Unit): BigInteger {
        return toWeiBInt(BigInteger(number), unit)
    }

    fun toWeiBInt(number: BigInteger, unit: Unit): BigInteger {
        return number.multiply(unit.weiFactorB)
    }

    enum class Unit(name: String, factor: Int) {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        GWEI("gwei", 9),
        SZABO("szabo", 12),
        FINNEY("finney", 15), // 1000 = 1 ETH, 100 = 0.1 ETH
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether", 24),
        GETHER("gether", 27);

        val weiFactorD: BigDecimal = BigDecimal.TEN.pow(factor)
        val weiFactorB: BigInteger = BigInteger.TEN.pow(factor)

        override fun toString(): String {
            return name
        }

        companion object {
            fun fromString(name: String): Unit {
                    for (unit in values()) {
                        if (name.equals(unit.name, ignoreCase = true)) {
                            return unit
                        }
                    }

                return valueOf(name)
            }
        }

    }
}