package me.soldesk.katteproject_backend.service;

import me.soldesk.katteproject_backend.mapper.CsMapper;
import common.bean.cs.CsStandardBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CsStandardService {

    @Autowired
    private CsMapper csMapper;

    //검수 기준 직접 작성
    public int addStandard(CsStandardBean csStandardBean) {
        return csMapper.addStandard(csStandardBean);
    }

    //검수 기준 목록 조회
    public List<CsStandardBean> getStandard(CsStandardBean.Standard_Category standard_category) {
        List<CsStandardBean> standardList = csMapper.getStandard(standard_category);
        return standardList;
    }

    //검수 기준 수정
    public int updateStandard(CsStandardBean csStandardBean) {
        return csMapper.updateStandard(
                csStandardBean.getStandard_id(),
                csStandardBean.getStandard_category(),
                csStandardBean.getStandard_content()
        );
    }

    //검수 기준 삭제
    public int deleteStandard(CsStandardBean csStandardBean) {
        return csMapper.deleteStandard(
                csStandardBean.getStandard_id()
        );
    }

}
