package com.example.maia.util

import com.example.maia.model.cart.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailService {

    // Ndrysho keto me email-in dhe App Password te Gmail-it tend
    private const val FROM_EMAIL = "maia.shopservice@gmail.com"
    private const val FROM_PASSWORD = "xxxx xxxx xxxx xxxx"

    suspend fun sendInvoice(
        toEmail: String,
        userName: String,
        orderRef: String,
        total: Double,
        items: List<CartItem>
    ) = withContext(Dispatchers.IO) {
        try {
            val props = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication() =
                    PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD)
            })

            val itemLines = items.joinToString("\n") { item ->
                val name = item.productName.ifEmpty { "Product #${item.productId}" }
                "  $name  x${item.quantity}  —  ${String.format("%.0f", item.price * item.quantity)} EUR"
            }

            val body = """
MAIA — ORDER INVOICE
====================
Order Reference : $orderRef
Customer        : $userName

ITEMS
-----
$itemLines

TOTAL : ${String.format("%.0f", total)} EUR
====================

Thank you for shopping at MAIA.
We will notify you when your order is on its way.

— The MAIA Team
            """.trimIndent()

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(FROM_EMAIL, "MAIA"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Your MAIA Order — $orderRef"
                setText(body)
            }

            Transport.send(message)
        } catch (_: Exception) {
            // Email failure is non-critical
        }
    }
}
