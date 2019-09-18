package de.lizsoft.heart.interfaces.common.model

import java.io.Serializable
import java.text.NumberFormat
import java.util.*

data class Money(
    val amount: Double,
    val amountCents: Int,
    val currency: String,
    val currencySymbol: String
) : Serializable {

    operator fun plus(newMoney: Money): Money {
        return this.copy(
              amount = amount + newMoney.amount,
              amountCents = amountCents + newMoney.amountCents,
              currency = newMoney.currency,
              currencySymbol = newMoney.currencySymbol
        )
    }

    operator fun plusAssign(newMoney: Money) {
        plus(newMoney)
    }

    fun formatted(): String {
        if (amount <= 0) {
            return "$currencySymbol —"
        }

        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(currency)
        return formatter.format(amount)
    }

    companion object {
        val EMPTY: Money = Money(
              amount = 0.0,
              amountCents = 0,
              currency = "EUR",
              currencySymbol = "€" //TODO Use the currency symbol from customer
        )
    }
}