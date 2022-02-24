package dss.projeto.EmailManager;

import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmails {
    private final static String username = "testeEmpresaG25@outlook.pt"; // email user
    private final static String password = "bRP6gTJxsmrNEzT";
    private final static String sender = "testeEmpresaG25@outlook.pt"; // email
    private final static String host = "smtp-mail.outlook.com"; // email host
    private final static String port = "587"; // email port
    private final static Session session = createEmailSession();

    private static Properties emailProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        return props;
    }

    private static Session createEmailSession() {
        Properties properties = emailProperties();
        // Create session object passing properties and authenticator instance
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static Boolean sendEmailCode(String receiver, boolean orcamento, String nomeDoCliente, String descricao,
            double precoPecas, double tempoNecessario, double precototal) throws MessagingException {

        String type = orcamento ? "or&ccedil;amento " : "repara&ccedil;&atilde;o";

        // Create MimeMessage object
        MimeMessage message = new MimeMessage(session);

        // Set the Senders mail to From
        message.setFrom(new InternetAddress(sender));

        // Set the recipients email address
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

        // Subject of the email
        if (orcamento) {
            message.setSubject("O seu pedido de orçamento foi concluído!");
        } else {
            message.setSubject("A sua reparação foi concluída!");
        }

        // Body of the email

        String greeting;
        int hour = LocalDateTime.now().getHour();
        if (hour < 13 && hour > 5)
            greeting = "Bom dia Sr(a) " + nomeDoCliente;
        else if (hour > 12 && hour < 20)
            greeting = "Boa tarde Sr(a) " + nomeDoCliente;
        else
            greeting = "Boa noite Sr(a) " + nomeDoCliente;

        String code = makeEmail(orcamento, precoPecas, tempoNecessario, precototal);
        String textbody = "<h4>" + greeting
                + "!</h4><p>Vim por este meio informar que o seu pedido de " + type + "</b> encontra-se concluido,</p>"
                + "<p><b>Equipamento: </b>" + descricao + "</p><p>"
                + code
                + "</p><p>&nbsp;</p><h4>Cumprimentos,</h4><p>Grupo 25.</p>";

        message.setContent(textbody, "text/html");

        // Send email.
        Transport.send(message);
        System.out.println("Mail sent successfully");
        return true;
    }

    public static String makeEmail(boolean orcamento, double precoPecas, double tempoNecessario, double precototal) {
        StringBuilder message = new StringBuilder();
        if (orcamento) {
            message.append("<p><b>pre&ccedil;o de Pe&ccedil;as previsto:</b> ").append(precoPecas)
                    .append(" &euro;</p>");
            message.append("<p><b>Tempo Necessario previsto:</b> ").append(tempoNecessario).append("h</p>");
            message.append("<p><b>pre&ccedil;o total previsto:</b> ").append(precototal).append(" &euro;</p>");
            message.append("<p><b>Por favor responda a este email com a sua confirma&ccedil;&atilde;o.</b></p>");
        } else if (tempoNecessario != -1) {
            message.append("<p><b>pre&ccedil;o de Pe&ccedil;as final:</b> ").append(precoPecas).append(" &euro;</p>");
            message.append("<p><b>Tempo Necess&aacuterio final:</b> ").append(tempoNecessario).append("h</p>");
            message.append("<p><b>pre&ccedil;o total final:</b> ").append(precototal).append(" &euro;</p>");
            message.append("<p><b>Por favor desloque-se &agrave; loja para levantar o seu equipamento.</b></p>");
        } else {
            message.append("<p><b>Pre&ccedil;o da repara&ccedil;&atilde;o expresso:</b>").append(precoPecas)
                    .append(" &euro;</p>");
            message.append("<p><b>Por favor desloque-se &agrave; loja para levantar o seu equipamento.</b></p>");
        }
        return message.toString();
    }
}
