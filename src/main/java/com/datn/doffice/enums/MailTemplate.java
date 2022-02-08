package com.datn.doffice.enums;

public enum MailTemplate {

  MAIL_TEST("mail_test.html", "mail.001"),
  MAIL_RENAME("mail_rename.html", "mail.002"),
  MAIL_UPDATE("mail_update.html", "mail.003"),
  MAIL_REPORT("mail_report.html", "mail.004"),
  MAIL_APPROVE("mail_approve.html", "mail.005"),
  MAIL_REJECT("mail_reject.html", "mail.006"),

  DEFAULT("", "");

  private String fileTemplateName;

  private String mailSubject;

  MailTemplate(String fileTemplateName, String mailSubject) {
    this.fileTemplateName = fileTemplateName;
    this.mailSubject = mailSubject;
  }

  public static MailTemplate fromName(String fileTemplateName) {
    for (MailTemplate type : MailTemplate.values()) {
      if (type.getFileTemplateName().equals(fileTemplateName)) {
        return type;
      }
    }
    return DEFAULT;
  }

  public String getFileTemplateName() {
    return fileTemplateName;
  }

  public String getMailSubject() {
    return mailSubject;
  }

}
