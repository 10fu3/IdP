package net.den3.IdP.Entity.Mail;

public class MailEntity implements IMailEntity{
    private String title;
    private String body;
    private String toAddress;
    private String name;

    @Override
    public IMailEntity setTo(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    @Override
    public String getTo() {
        return this.toAddress;
    }

    @Override
    public IMailEntity setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public IMailEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public IMailEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
