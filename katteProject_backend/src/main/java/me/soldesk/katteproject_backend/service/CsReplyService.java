package me.soldesk.katteproject_backend.service;

import common.bean.cs.CsInquireCustomerBean;
import me.soldesk.katteproject_backend.mapper.CsMapper;
import common.bean.cs.CsReplyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CsReplyService {

    @Autowired
    public CsMapper csMapper;

    //1:1 문의 답변 작성
    public int addReply(CsReplyBean csReplyBean) {
        return csMapper.addCsReply(csReplyBean);
    }

    //1:1 문의 답변 조회 ( 상세 / 목록 합본 )
    public List<CsReplyBean> getCsReply(Integer reply_id, Integer inquire_id) {
        if (reply_id != null && reply_id > 0 && inquire_id != null && inquire_id > 0) {
            List<CsReplyBean> replyList = csMapper.getCsReplyDetail(reply_id, inquire_id);
            if (replyList == null || replyList.isEmpty()) {
                throw new NoSuchElementException("해당하는 문의 답변을 찾을 수 없습니다.");
            }
            return replyList;
        }
        return csMapper.getAllCsReply();
    }

    //문의 처리 상태에 따른 "문의" 리스트
    public List<CsInquireCustomerBean> getCsInquireByStatusAdmin(CsInquireCustomerBean.inquire_status inquire_status){
        return csMapper.getAllInquiresByStatusAdmin(inquire_status);
    }

    //1:1 문의 답변 수정
    public int updateReply(CsReplyBean csReplyBean) {
        return csMapper.updateReply(
                csReplyBean.getReply_id(),
                csReplyBean.getReply_title(),
                csReplyBean.getReply_content()
        );
    }



    //1:1 문의 답변 삭제
    public int deleteReply (CsReplyBean csReplyBean){
        return csMapper.deleteCsReply(csReplyBean.getReply_id());
    }

}