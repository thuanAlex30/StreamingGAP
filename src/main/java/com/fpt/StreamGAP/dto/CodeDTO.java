package com.fpt.StreamGAP.dto;

import com.fpt.StreamGAP.entity.VerifyCode;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CodeDTO {
    private int id;
    private String code;
    private int userId;

    public CodeDTO(VerifyCode verifyCode){
        if(!Objects.isNull(verifyCode)){
            this.id = verifyCode.getId();
            this.code = verifyCode.getCode();
            this.userId = verifyCode.getUser().getUser_id();
        }else{
            this.id = 0;
            this.code = "";
            this.userId = 0;
        }
    }
}
