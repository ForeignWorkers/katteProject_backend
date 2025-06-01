package me.soldesk.katteproject_backend.test;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserBanBean {
    private int id;//pk
    private int user_id;//정지된 유저의 ID
    private Date ban_start;//정지 시작일
}
