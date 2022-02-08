package com.datn.doffice.entity;

import com.datn.doffice.enums.MailTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class MailEntity {
  private String mailTo;

  private String mailSubject;

  private String textContent;

  private Map<String, String> mailDataContent;

  private List<MultipartFile> attachments;

  private List<String> attachmentPaths;

  private MailTemplate template;

}
