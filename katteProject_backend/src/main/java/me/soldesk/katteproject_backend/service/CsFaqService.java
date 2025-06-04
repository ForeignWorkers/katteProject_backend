package me.soldesk.katteproject_backend.service;

import common.bean.cs.CsFaqBean;
import me.soldesk.katteproject_backend.mapper.CsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CsFaqService

{
    @Autowired
    private CsMapper csMapper;

    //faq 직접 작성
    public int addFaq(CsFaqBean csFaqBean) {
        return csMapper.addFaq(csFaqBean);
    }

    //faq 목록 / 상세 조회
    public List<CsFaqBean> getFaqs(Integer faq_id) {
        if (faq_id != null && faq_id > 0) {
            List<CsFaqBean> faqBean = csMapper.getFaqDetail(faq_id);
            if(faqBean != null && !faqBean.isEmpty()){
                return faqBean;
            }else{
                throw new NoSuchElementException("자주 묻는 질문을 찾을 수 없습니다.");
            }
        }else{
            List<CsFaqBean> faqList = csMapper.getAllFaq();
            return faqList;
        }
    }

    //문의 조회 (처리 상태별로)
    public List<CsFaqBean> getFaqByCategory(CsFaqBean.faq_category faq_category){
        return csMapper.getFaqByCategory(faq_category);
    }

    //faq 수정
    public int updateFaq(CsFaqBean csFaqBean) {
        return csMapper.updateFaq(
                csFaqBean.getFaq_id(),
                csFaqBean.getFaq_category(),
                csFaqBean.getFaq_title(),
                csFaqBean.getFaq_content()
        );
    }

    //faq 삭제
    public int deleteFaq(CsFaqBean csFaqBean) {
        return csMapper.deleteFaq(
                csFaqBean.getFaq_id()
        );
    }


}
