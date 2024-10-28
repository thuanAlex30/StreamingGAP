package com.fpt.StreamGAP.dto;
import lombok.Data;

import java.util.Date;
        @Data
public class MessageDTO {
    private String content;
    private String sentBy;
    private Date sentAt;
}
