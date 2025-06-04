package me.soldesk.katteproject_backend.service;


import me.soldesk.katteproject_backend.mapper.CsMapper;
import common.bean.cs.CsAnnounceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CsAnnounceService {

    @Autowired
    private CsMapper csMapper;

    //공지사항 작성
    public int addCsAnnounce(CsAnnounceBean csAnnounceBean) {
        return csMapper.addAnnounce(csAnnounceBean);
    }

    //공지 사항 조회 (목록 / 세부)
    public List<CsAnnounceBean> getAnnounce(Integer announce_id, Integer count, Integer offset) { //공지 ID를 받은 경우 공지 내역 상세를 리스트로.
        if (announce_id != null && announce_id > 0) {
            List<CsAnnounceBean> announceBean = csMapper.getAnnounceDetail(announce_id);
            if (announceBean != null && !announceBean.isEmpty()) {
                return announceBean;
            } else {
                throw new IllegalArgumentException("해당하는 공지사항을 찾지 못했습니다.");
            }
        }else if(count != null && offset != null) { //공지 ID를 받지 않은 경우에는 공지 내역 목록을 리스트로.
            List<CsAnnounceBean> announceList = csMapper.getAnnounce(count, offset);
            return announceList;
        } else {
            throw new IllegalArgumentException("공지사항이 존재하지 않습니다.");
        }
    }

    //공지사항 카테고리에 따른 리스트
    public List<CsAnnounceBean> getAnnounceByCategory(CsAnnounceBean.announce_category announce_category,
                                                      int count, int offset){
        return csMapper.getAnnounceByCategory(announce_category, count, offset);
    }


    //공지사항 수정
    public int updateAnnounce(CsAnnounceBean csAnnounceBean) {
        return csMapper.updateAnnounce(
                csAnnounceBean.getAnnounce_id(),
                csAnnounceBean.getAnnounce_category(),
                csAnnounceBean.getAnnounce_title(),
                csAnnounceBean.getAnnounce_content()
        );
    }


    //공지사항 삭제
    public int deleteCsAnnounce(CsAnnounceBean csAnnounceBean) {
        return csMapper.deleteAnnounce(
                csAnnounceBean.getAnnounce_id()
        );
    }

}
