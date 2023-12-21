package com.showroom.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private String emailTo;

//    @Value("${spring.mail.username}")
//    private String emailFrom;
//
//    @Value("#{'${spring.mail.cc}'.split(',')}")
//    private String[] ccMail;

    private String subject;
    private String htmlContentLink;
    private String textMessage;
    private List<FileMetaData> attachments=new ArrayList<>();
}
