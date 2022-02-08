package com.datn.doffice.controller;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.defines.MailConstants;
import com.datn.doffice.dto.UserDTO;
import com.datn.doffice.entity.MailEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.service.MailService;
import com.datn.doffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;

@Api(path = "/api/test")
public class TestController extends ApiController {

    @Autowired
    private MailService mailService;

//    @GetMapping("/send-mail")
//    public ResponseEntity<?> testMail() {
//        MailEntity mail = new MailEntity();
//        mail.setMailTo("hatqptit@gmail.com");
//        mail.setMailSubject("123abvcác");
//        Map<String, String> mailDataContent = mailDataContent();
//        mailDataContent.put(MailConstants.MAIL_FULL_NAME_DATA, "Tran Quang Ha");
//        mailDataContent.put(MailConstants.MAIL_USER_DATA, "hatqptit");
//        mail.setMailDataContent(mailDataContent);
//        mailService.notifyForReceiver(mail);
//        return ok(ApiStatus.OK);
//    }
}
