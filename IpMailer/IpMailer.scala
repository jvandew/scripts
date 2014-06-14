import com.sun.mail.smtp.SMTPTransport
import java.io.{BufferedReader, InputStreamReader}
import java.net.URL
import java.security.Security
import java.util.{Date, Properties}
import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}

object IpMailer {

  val url = new URL("http://icanhazip.com")


  def getIpAddress: String = {
    val reader = new BufferedReader(new InputStreamReader(url.openStream))
    reader.readLine
  }

  def genError(except: Exception): String = {
    "Yo imma let it finish. But IpMailer had one of the worst failures of all time.\n\n" +
    except + "\n\nHumbly yours,\nJohnny IP"
  }


  def genMessage(data: String): String = data + "\n\nHumbly yours,\nJohnny IP"


  def send(message: String): Unit = {
    val username = "vandeweertj"
    val password = "qyoqgdyvknckbyhw"

    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider)
    val SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"

    // Get a Properties object
    val props = System.getProperties()
    props.setProperty("mail.smtps.host", "smtp.gmail.com")
    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY)
    props.setProperty("mail.smtp.socketFactory.fallback", "false")
    props.setProperty("mail.smtp.port", "465")
    props.setProperty("mail.smtp.socketFactory.port", "465")
    props.setProperty("mail.smtps.auth", "true")
    props.put("mail.smtps.quitwait", "false")

    val session = Session.getInstance(props, null)

    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(username + "@gmail.com"))
    msg.setRecipients(Message.RecipientType.TO, username + "@gmail.com")
    msg.setSubject("[Alert] New Server IP Address");
    msg.setText(message, "UTF-8")
    msg.setSentDate(new Date())

    val transport = session.getTransport("smtps")
    transport.connect("smtp.gmail.com", username, password)
    transport.sendMessage(msg, msg.getAllRecipients())
    transport.close()
  }


  def main(args: Array[String]) {

    var ip = ""

    while(true) {
      try {
        var newIp = getIpAddress
        if (newIp != ip) {
          ip = newIp
          send(genMessage(newIp))
        }
      } catch {
        case e: Exception => send(genError(e))
      }
      Thread.sleep(300*1000) // five minutes
    }
  }

}
