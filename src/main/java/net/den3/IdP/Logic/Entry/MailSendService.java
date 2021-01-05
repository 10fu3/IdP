package net.den3.IdP.Logic.Entry;

import net.den3.IdP.Entity.Mail.IMailEntity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class MailSendService {
    private final String mailaddress;
    private final String password;
    private final String accountDescription;
    private final Properties props;

    public MailSendService(String mailaddress,String password,String accountDescription){
        this.mailaddress = mailaddress;
        this.password = password;
        this.accountDescription = accountDescription;
        this.props = new Properties();
        // SMTPサーバーの設定。ここではgooglemailのsmtpサーバーを設定。
        this.props.setProperty("mail.smtp.host", "smtp.gmail.com");

        // SSL用にポート番号を変更。
        this.props.setProperty("mail.smtp.port", "465");

        // タイムアウト設定
        this.props.setProperty("mail.smtp.connectiontimeout", "60000");
        this.props.setProperty("mail.smtp.timeout", "60000");

        // 認証
        this.props.setProperty("mail.smtp.auth", "true");

        // SSLを使用するとこはこの設定が必要。
        this.props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.setProperty("mail.smtp.socketFactory.fallback", "false");
        this.props.setProperty("mail.smtp.socketFactory.port", "465");
    }

    public void send(IMailEntity e, Runnable onSuccess, Runnable onFailed) {
        new Thread(()->{

            //propsに設定した情報を使用して、sessionの作成
            final Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailaddress, password);
                }
            });


            // ここからメッセージ内容の設定。上記で作成したsessionを引数に渡す。
            final MimeMessage message = new MimeMessage(session);

            try {
                final Address addrFrom = new InternetAddress(this.mailaddress, this.accountDescription, "UTF-8");
                message.setFrom(addrFrom);

                final Address addrTo = new InternetAddress(e.getTo(), e.getName(), "UTF-8");
                message.addRecipient(Message.RecipientType.TO, addrTo);

                // メールのSubject
                message.setSubject(e.getTitle(), "UTF-8");

                // メール本文。
                message.setText(e.getBody(),"UTF-8");

                // その他の付加情報。
                message.addHeader("X-Mailer", "blancoMail 0.1");

                message.setHeader("Content-Type", "text/html; charset=utf-8");
                message.setSentDate(new Date());
            } catch (MessagingException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
                onFailed.run();
            }

            // メール送信。
            try {
                Transport.send(message);
                onSuccess.run();
            } catch (MessagingException ex) {
                // 認証失敗
                ex.printStackTrace();
                onFailed.run();
            }// smtpサーバへの接続失敗
        }).start();
    }
}
