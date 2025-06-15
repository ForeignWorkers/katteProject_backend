package me.soldesk.katteproject_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import common.bean.cs.CsInquireCustomerBean;
import me.soldesk.katteproject_backend.mapper.CsMapper;

import java.util.List;

@Service
public class CsInquireCustomerService {

    @Autowired
    private CsMapper csMapper;

    //문의 작성
    public int addCsInquireCustomer(CsInquireCustomerBean csInquireCustomerBean) {
        return csMapper.addCsInquireCustomer(csInquireCustomerBean);
    }


    //문의 조회 ( 문의 내역 목록 / 상세 문의 내역 )
    public List<CsInquireCustomerBean> getCsInquire(int user_id, Integer inquire_id, Integer count, Integer offset) { //문의 ID를 받은 경우 상세 문의 내역을 리스트로.
        if (inquire_id != null && inquire_id > 0) {
            List<CsInquireCustomerBean> inquireBean = csMapper.getCsInquireCustomerByUserIdAndInquireId(user_id, inquire_id);
            if (inquireBean != null && !inquireBean.isEmpty()) {
                return inquireBean;
            } else {
                throw new IllegalArgumentException("찾으시는 문의 내역이 존재하지 않습니다.");
            }
        }else if(count != null && offset != null){ //문의 ID를 받지 않은 경우에는 문의 내역 목록을 리스트로.
            List<CsInquireCustomerBean> inquireList = csMapper.getCsInquireCustomerByUserId(user_id, count, offset);
            return inquireList;
        }else{
            throw new IllegalArgumentException("문의 내역이 존재하지 않습니다.");
        }
    }

    //문의 조회 (처리 상태별로)
    public List<CsInquireCustomerBean> getCsInquireByCategoryCustomer(int user_id,
                                                                      CsInquireCustomerBean.Inquire_Status inquire_status,
                                                                      int count, int offset){
        return csMapper.getAllInquiresByStatusCustomer(user_id, inquire_status, count, offset);
    }


    //문의 수정
    public int updateCsInquireCustomer(CsInquireCustomerBean csInquireCustomerBean) {
        return csMapper.updateCsInquireCustomer(
                csInquireCustomerBean.getUser_id(),
                csInquireCustomerBean.getInquire_id(),
                csInquireCustomerBean.getInquire_category(),
                csInquireCustomerBean.getInquire_title(),
                csInquireCustomerBean.getInquire_content()
        );
    }


    //문의 삭제
    public int deleteCsInquireCustomer(CsInquireCustomerBean csInquireCustomerBean) {
        return csMapper.deleteCsInquireCustomerByUserIdAndInquireId(
                csInquireCustomerBean.getUser_id(),
                csInquireCustomerBean.getInquire_id()
        );
    }

}
