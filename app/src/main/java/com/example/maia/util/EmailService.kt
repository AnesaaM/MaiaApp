package com.example.maia.util

import com.example.maia.model.cart.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

object EmailService {

    private const val FROM_EMAIL = "anesamecinaj28@gmail.com"
    private const val FROM_PASSWORD = "hsno zpmn qtva tszp"

    suspend fun sendInvoice(
        toEmail: String,
        userName: String,
        orderRef: String,
        total: Double,
        items: List<CartItem>,
        address: String = "",
        city: String = "",
        postalCode: String = "",
        phone: String = "",
        paymentMethod: String = "cash"
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

            val dateStr = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH).format(Date())
            val orderId = orderRef.removePrefix("MAIA-").trimStart('0').ifEmpty { "1" }
            val paymentLabel = if (paymentMethod == "card") "Credit Card" else "Cash on Delivery"

            val itemRows = items.joinToString("") { item ->
                val name = item.productName.ifEmpty { "Product #${item.productId}" }.uppercase()
                val subtotal = item.price * item.quantity
                """
                <tr>
                  <td style="padding:14px 12px;border-bottom:1px solid #EDE8E3;font-size:13px;color:#1C0A06;">$name</td>
                  <td style="padding:14px 12px;border-bottom:1px solid #EDE8E3;font-size:13px;color:#1C0A06;text-align:center;">${item.quantity}</td>
                  <td style="padding:14px 12px;border-bottom:1px solid #EDE8E3;font-size:13px;color:#1C0A06;text-align:right;">${String.format("%.2f", item.price)} EUR</td>
                  <td style="padding:14px 12px;border-bottom:1px solid #EDE8E3;font-size:13px;color:#1C0A06;text-align:right;font-weight:600;">${String.format("%.2f", subtotal)} EUR</td>
                </tr>
                """.trimIndent()
            }

            val html = """
<!DOCTYPE html>
<html>
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
<body style="margin:0;padding:0;background:#F5EFE8;font-family:Georgia,serif;">
<table width="100%" cellpadding="0" cellspacing="0" style="background:#F5EFE8;padding:32px 0;">
  <tr><td align="center">
    <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%;">

      <!-- HEADER -->
      <tr><td style="background:#1C0A06;padding:36px 40px;border-radius:4px 4px 0 0;">
        <div style="font-size:32px;font-style:italic;font-weight:400;letter-spacing:4px;color:#F5EFE8;">MAIA</div>
        <div style="font-size:10px;letter-spacing:4px;color:#9E8878;margin-top:6px;">ORDER CONFIRMATION</div>
      </td></tr>

      <!-- BODY -->
      <tr><td style="background:#ffffff;padding:40px 40px 32px;">

        <p style="font-size:15px;color:#1C0A06;margin:0 0 6px;">Dear <strong>${userName}</strong>,</p>
        <p style="font-size:13px;color:#7A6A64;margin:0 0 28px;line-height:1.6;">Thank you for your order. Here is your invoice summary.</p>

        <!-- ORDER META -->
        <div style="background:#F9F5F1;border-left:3px solid #C4A98A;padding:16px 20px;margin-bottom:28px;border-radius:2px;">
          <span style="font-size:11px;color:#9E8878;letter-spacing:1px;">ORDER <strong style="color:#1C0A06;">#${orderId}</strong></span>
          <span style="color:#CCC0BB;margin:0 10px;">·</span>
          <span style="font-size:11px;color:#9E8878;">$dateStr</span>
          <span style="color:#CCC0BB;margin:0 10px;">·</span>
          <span style="font-size:11px;color:#9E8878;">Status: <strong style="color:#1C0A06;">Pending</strong></span>
        </div>

        <!-- ITEMS TABLE -->
        <div style="font-size:9px;letter-spacing:3px;color:#9E8878;margin-bottom:12px;">ORDER ITEMS</div>
        <table width="100%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
          <thead>
            <tr style="background:#F9F5F1;">
              <th style="padding:10px 12px;font-size:9px;letter-spacing:2px;color:#9E8878;text-align:left;font-weight:400;">PRODUCT</th>
              <th style="padding:10px 12px;font-size:9px;letter-spacing:2px;color:#9E8878;text-align:center;font-weight:400;">QTY</th>
              <th style="padding:10px 12px;font-size:9px;letter-spacing:2px;color:#9E8878;text-align:right;font-weight:400;">PRICE</th>
              <th style="padding:10px 12px;font-size:9px;letter-spacing:2px;color:#9E8878;text-align:right;font-weight:400;">SUBTOTAL</th>
            </tr>
          </thead>
          <tbody>$itemRows</tbody>
          <tfoot>
            <tr>
              <td colspan="3" style="padding:16px 12px;text-align:right;font-size:11px;letter-spacing:2px;color:#1C0A06;font-weight:600;">TOTAL</td>
              <td style="padding:16px 12px;text-align:right;font-size:16px;color:#1C0A06;font-weight:700;">${String.format("%.2f", total)} EUR</td>
            </tr>
          </tfoot>
        </table>

        <!-- DELIVERY + PAYMENT -->
        <table width="100%" cellpadding="0" cellspacing="0" style="margin-top:28px;">
          <tr>
            <td width="50%" valign="top" style="padding-right:20px;border-top:1px solid #EDE8E3;padding-top:20px;">
              <div style="font-size:9px;letter-spacing:3px;color:#9E8878;margin-bottom:12px;">DELIVERY ADDRESS</div>
              <div style="font-size:13px;color:#1C0A06;line-height:1.8;">
                $userName<br>
                ${if (address.isNotBlank()) "$address<br>" else ""}
                ${if (postalCode.isNotBlank() || city.isNotBlank()) "${postalCode} ${city}<br>" else ""}
                ${if (phone.isNotBlank()) "$phone" else ""}
              </div>
            </td>
            <td width="50%" valign="top" style="padding-left:20px;border-top:1px solid #EDE8E3;padding-top:20px;">
              <div style="font-size:9px;letter-spacing:3px;color:#9E8878;margin-bottom:12px;">PAYMENT</div>
              <div style="font-size:13px;color:#1C0A06;">$paymentLabel</div>
            </td>
          </tr>
        </table>

      </td></tr>

      <!-- FOOTER -->
      <tr><td style="background:#1C0A06;padding:24px 40px;text-align:center;border-radius:0 0 4px 4px;">
        <span style="font-size:11px;letter-spacing:3px;color:#F5EFE8;font-style:italic;">MAIA</span>
        <span style="color:#5A3A2A;margin:0 12px;">·</span>
        <span style="font-size:11px;letter-spacing:1px;color:#9E8878;">Thank you for shopping with us</span>
      </td></tr>

    </table>
  </td></tr>
</table>
</body>
</html>
            """.trimIndent()

            val multipart = MimeMultipart("alternative")
            val htmlPart = MimeBodyPart().apply {
                setContent(html, "text/html; charset=utf-8")
            }
            multipart.addBodyPart(htmlPart)

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(FROM_EMAIL, "MAIA"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Your MAIA Order — $orderRef"
                setContent(multipart)
            }

            Transport.send(message)
        } catch (ex: Exception) {
            // Email failure is non-critical
        }
    }
}
