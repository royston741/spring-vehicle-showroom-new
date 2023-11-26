package com.showroom.dto;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaData {
    private Integer id;
    private String filename;
    private byte[] attachment;
}
