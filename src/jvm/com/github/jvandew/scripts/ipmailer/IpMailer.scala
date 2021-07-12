package com.github.jvandew.scripts.ipmailer

import java.io.{BufferedReader, ByteArrayOutputStream, InputStreamReader, PrintStream}
import java.net.{ConnectException, NoRouteToHostException, URL, UnknownHostException}
import java.util.Date
import org.simplejavamail.MailException
import org.simplejavamail.api.email.Email
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

object IpMailer {

  val ipUrl = new URL("http://icanhazip.com")
  val smtpHost = "smtp.gmail.com"
  val smtpPort = 465

  def genError(exception: Exception): String = {
    val snarkyMsg = "SYSTEM MALFUNCTION: OVERRIDE"
    val signature = "RESUME OPERATIVE,\nUNIT 376"

    val byteStream = new ByteArrayOutputStream
    val printStream = new PrintStream(byteStream)
    exception.printStackTrace(printStream)
    printStream.close()

    s"${snarkyMsg}\n\n${byteStream.toString}\n\n${signature}"
  }

  def genMessage(data: String): String = s"${data}\n\nROGER ROGER,\nUNIT 376"

  def getIpAddress(): String = {
    val reader = new BufferedReader(new InputStreamReader(ipUrl.openStream()))
    reader.readLine()
  }

  def newEmail(username: String, message: String): Email = {
    val gmailAddress = s"${username}@gmail.com"
    EmailBuilder
      .startingBlank
      .from(gmailAddress)
      .to(gmailAddress)
      .withPlainText(message)
      .withSubject("[Alert] New Server IP Address")
      .buildEmail()
  }

  def newMailer(username: String, password: String): Mailer = {
    MailerBuilder
      .withDebugLogging(true)
      .withSMTPServer(smtpHost, smtpPort, username, password)
      .withTransportStrategy(TransportStrategy.SMTPS)
      .buildMailer()
  }

  def send(mailer: Mailer, email: Email): Unit = {
    var sent = false

    while (!sent) {
      try {
        mailer.sendMail(email)
        sent = true
      }
      catch {
        case mailException: MailException => {
          mailException.printStackTrace()
          Thread.sleep(10000) // 10 seconds
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val Array(username, password) = args
    val mailer = newMailer(username, password)

    var ip = ""
    while (true) {
      try {
        var newIp = getIpAddress()
        if (newIp != ip) {
          ip = newIp
          val newIpEmail = newEmail(username, genMessage(newIp))
          send(mailer, newIpEmail)
        }
      } catch {
        case _: ConnectException | _: NoRouteToHostException | _: UnknownHostException => {
          // dns/connection issues; try again later
          println(s"${new Date} - connection error fetching IP address")
        }
        case other: Exception => {
          val errorEmail = newEmail(username, genError(other))
          send(mailer, errorEmail)
        }
      }
      Thread.sleep(600000) // 10 minutes
    }
  }
}
