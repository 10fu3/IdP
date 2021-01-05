package net.den3.IdP.Entity.Mail;

public interface IMailEntity {
    static IMailEntity getInstance() {
        return new MailEntity();
    }

    IMailEntity setTo(String toAddress);
    String getTo();
    IMailEntity setBody(String body);
    String getBody();
    IMailEntity setTitle(String title);
    String getTitle();
    IMailEntity setName(String name);
    String getName();
}
