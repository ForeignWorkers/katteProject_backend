package me.soldesk.katteproject_backend.scheduler;

import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.service.AdminService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminScheduler {

    private final AdminService adminService;

    //매일 새벽 3시 판매중 -> 만료된 상품 상태 갱신
    @Scheduled(cron = "0 0 3 * * *")
    public void updateExpiredSales() {
        adminService.updateExpiredSales();
    }

    //매일 새벽 3시 검수 실패 3일 후 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteInspectionFailedOld() {
        int count = adminService.deleteInspectionFailedOld();
        System.out.println("[검수 실패 자동 삭제] 삭제된 건수 = " + count);
    }

    // 매일 새벽 3시 판매 만료 3일 후 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldExpiredSales() {
        int deleted = adminService.deleteOldExpiredSales();
        System.out.println("[판매 만료 삭제] " + deleted + "건 삭제됨");
    }
}