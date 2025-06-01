package me.soldesk.katteproject_backend.test;

import lombok.Data;

import java.util.Date;

@Data
public class UserRestrictionBean {
    private int id;
    private int user_id;
    private String restriction_type; //comment: 댓글 등록 제한, style: 스타일 등록 제한,sale: 판매 제한
    private Date start_date;
    private Date end_date;
    private int stop_days;
}
