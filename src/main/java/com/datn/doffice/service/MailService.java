package com.datn.doffice.service;

import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.LockInfoEntity;
import com.datn.doffice.entity.MailEntity;
import com.datn.doffice.entity.UserEntity;

public interface MailService {

  void notifyForReceiver(UserEntity receiver, DocumentEntity document);
  
  void notifyRename(UserEntity receiver, DocumentEntity oldDocument, String newName, String actor);
  
  void notifyUpdate(UserEntity receiver, DocumentEntity document, String actor);
  
  void notifyAdmin(UserEntity admin, String repoter, String time, String nameDocument, String actor);
  
  void notifyApprove(UserEntity user, String nameDocument);
  
  void notifyReject(UserEntity user, String nameDocument);

  void sendMail(MailEntity mailEntity);

}
