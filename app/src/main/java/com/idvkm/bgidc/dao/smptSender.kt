package com.idvkm.bgidc.dao

import android.content.Context
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

class SmtpSender(
    private val context: Context,
    private val smtpUser: String,
    private val smtpPass: String
) {
    private fun createSession(): Session {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", SmtpConfigStore.SMTP_HOST)
            put("mail.smtp.port", SmtpConfigStore.SMTP_PORT)
            put("mail.smtp.connectiontimeout", "15000")
            put("mail.smtp.timeout", "15000")
            put("mail.smtp.ssl.trust", SmtpConfigStore.SMTP_HOST)
        }

        return Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(smtpUser, smtpPass)
            }
        })
    }

    private fun sendMime(message: MimeMessage) {
        Transport.send(message)
    }

    // Plantilla 1: Welcome

    suspend fun sendWelcomeEmail(from: String, to: String) = withContext(Dispatchers.IO) {
        val session = createSession()
        val msg = MimeMessage(session).apply {
            setFrom(InternetAddress(from))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            subject = "Welcome"

            setText("""
                Welcome to the Bulgarian Identity Conference 2025!

                We are very happy that you are attending the conference, and we hope you enjoy it.

                Thank you for your trust in us.
            """.trimIndent())
        }
        sendMime(msg)
    }

    // Plantilla 2: Warning (suspicious activity)

    suspend fun sendWarningEmail(from: String, to: String) = withContext(Dispatchers.IO) {
        val session = createSession()
        val msg = MimeMessage(session).apply {
            setFrom(InternetAddress(from))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            subject = "Warning"
            setText("""
                Warning

                Your account is in danger

                We've noticed suspicious activity on your account. Someone has tried to log in to your account several times without success.

                We recommend changing your password to ensure the security of your data.
            """.trimIndent())
        }
        sendMime(msg)
    }

    // Plantilla 3: Warning (suspicious activity)

    suspend fun sendChangePasswordEmail(from: String, to: String, cCode: String) = withContext(Dispatchers.IO) {
        val session = createSession()
        val msg = MimeMessage(session).apply {
            setFrom(InternetAddress(from))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            subject = "Password reset"
            setText("""
                Do not share this with anyone

                Here is your verification code in order to change the password:
                $cCode
                
            """.trimIndent())
        }
        sendMime(msg)
    }
}
