import org.simplejavamail.email._
import com.sun.mail.smtp.SMTPTransport
import com.sun.net.ssl.internal.ssl.Provider
import java.io.{BufferedReader, ByteArrayOutputStream, InputStreamReader,
                PrintStream}
import java.net.{URL, UnknownHostException}
import java.security.Security
import java.util.{Date, Properties}
import javax.mail.{Message, MessagingException, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}

object IpMailer {

  val url = new URL("http://icanhazip.com")

  Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider)
  val SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"

  val props = System.getProperties
  props.setProperty("mail.smtps.host", "smtp.gmail.com")
  props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY)
  props.setProperty("mail.smtp.socketFactory.fallback", "false")
  props.setProperty("mail.smtp.port", "465")
  props.setProperty("mail.smtp.socketFactory.port", "465")
  props.setProperty("mail.smtps.auth", "true")
  props.put("mail.smtps.quitwait", "false")

  val session = Session.getInstance(props, null)


  def getIpAddress: String = {
    val reader = new BufferedReader(new InputStreamReader(url.openStream))
    reader.readLine
  }


  def genError(except: Exception): String = {

    val snarkyMsg = "SYSTEM MALFUNCTION: OVERRIDE\n\n"
    val signature = "\n\nRESUME OPERATIVE,\nUNIT 376"

    val byteOut = new ByteArrayOutputStream
    val printOut = new PrintStream(byteOut)
    except.printStackTrace(printOut)
    printOut.close

    snarkyMsg + byteOut.toString + signature
  }


  def genMessage(data: String): String = data + "\n\nROGER ROGER,\nUNIT 376"


  def send(username: String, password: String)(message: String): Unit = {

    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(username + "@gmail.com"))
    msg.setRecipients(Message.RecipientType.TO, username + "@gmail.com")
    msg.setSubject("[Alert] New Server IP Address");
    msg.setText(message, "UTF-8")
    msg.setSentDate(new Date)

    val transport = session.getTransport("smtps")
    var sent = false

    while(!sent) {
      try {
        transport.connect("smtp.gmail.com", username, password)
        transport.sendMessage(msg, msg.getAllRecipients)
        transport.close()
        sent = true
      }
      catch {
        case me: MessagingException => Thread.sleep(10000)
      }
    }
  }


  def main(args: Array[String]) {

    val sendMsg = send(args(0), args(1))_
    var ip = ""

    while(true) {
      try {
        var newIp = getIpAddress
        if (newIp != ip) {
          ip = newIp
          sendMsg(genMessage(newIp))
        }
      } catch {
        case uhe: UnknownHostException => () // dns issues; try again later
        case e: Exception => sendMsg(genError(e))
      }
      Thread.sleep(300*1000) // five minutes
    }
  }

}

