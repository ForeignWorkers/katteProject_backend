package me.soldesk.katteproject_backend.mapper;


import common.bean.cs.CsInquireCustomerBean;
import common.bean.cs.CsAnnounceBean;
import common.bean.cs.CsFaqBean;
import common.bean.cs.CsReplyBean;
import common.bean.cs.CsStandardBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CsMapper {

    //공지사항 --------------------------------------------------------------------------
    //공지사항 작성
    @Insert("INSERT INTO cs_announce(announce_category," +
            "announce_title, announce_content) " +
            "VALUES (#{announce_category}, #{announce_title}, #{announce_content})")
    @Options(useGeneratedKeys = true, keyProperty = "announce_id")
    int addAnnounce(CsAnnounceBean csAnnounceBean);


    //공지사항의 전체 갯수 취득
    @Select("SELECT COUNT(*) FROM cs_announce")
    int getAnnounceCount();

    //공지사항 전체 조회
    @Select("SELECT * FROM cs_announce order by announce_at desc, announce_id desc limit #{count} offset #{offset};")
    @Options(useGeneratedKeys = true, keyProperty = "announce_id")
    List<CsAnnounceBean> getAnnounce(int count, int offset);

    //공지사항 세부 조회
    @Select("SELECT * FROM cs_announce WHERE announce_id = #{announce_id};")
    List<CsAnnounceBean> getAnnounceDetail(int announce_id);

    //카테고리별 공지사항 조회 (접수 / 처리중 / 답변 완료)
    @Select("SELECT * FROM cs_announce WHERE announce_category = #{announce_category}" +
            "ORDER BY announce_at DESC, announce_id DESC limit #{count} offset #{offset}")
    List<CsAnnounceBean> getAnnounceByCategory(
            @Param("announce_category") CsAnnounceBean.Announce_Category announce_category,
            int count,
            int offset);

    // 공지사항 수정
    @Update("update cs_announce " +
            "set announce_category = #{announce_category}, " +
            "announce_title = #{announce_title}, announce_content = #{announce_content} " +
            "where announce_id = #{announce_id}")
    int updateAnnounce(
            @Param("announce_id") int announce_id,
            @Param("announce_category") CsAnnounceBean.Announce_Category announce_category,
            @Param("announce_title") String announce_title,
            @Param("announce_content") String announce_content
    );

    // 공지사항 삭제
    @Delete("delete from cs_announce where announce_id = #{announce_id}")
    int deleteAnnounce(
            @Param("announce_id") int announce_id
    );



    //자주 묻는 질문 ---------------------------------------------------
    //자주 묻는 질문 작성
    @Insert("INSERT INTO cs_support_faq (faq_category, faq_title, faq_content) " +
            "VALUES (#{faq_category}, #{faq_title}, #{faq_content})")
    @Options(useGeneratedKeys = true, keyProperty = "faq_id")
    int addFaq(CsFaqBean csFaqBean);

    // 모든 자주 묻는 질문 목록을 리스트로 받음
    @Select("SELECT * FROM cs_support_faq ORDER BY created_at DESC, faq_id DESC limit #{count} offset #{offset}")
    List<CsFaqBean> getAllFaq(int count, int offset);

    // faq_id를 통해 자주 묻는 질문 상세 출력
    @Select("SELECT * FROM cs_support_faq WHERE faq_id = #{faq_id}")
    List<CsFaqBean> getFaqDetail(
            @Param("faq_id") int faq_id);

    //자주 묻는 질문 카테고리별 공지사항 (접수 / 처리중 / 답변 완료)
    @Select("SELECT * FROM cs_support_faq WHERE faq_category = #{faq_category}" +
            "ORDER BY created_at DESC, faq_id DESC limit #{count} offset #{offset}")
    List<CsFaqBean> getFaqByCategory(
            @Param("faq_category") CsFaqBean.Faq_Category faq_category,
            int count,
            int offset);


    // 자주 묻는 질문 수정
    @Update("update cs_support_faq " +
            "set faq_category = #{faq_category}, " +
            "faq_title = #{faq_title}, faq_content = #{faq_content} " +
            "where faq_id = #{faq_id}")
    int updateFaq(
            @Param("faq_id") int announce_id,
            @Param("faq_category") CsFaqBean.Faq_Category faq_category,
            @Param("faq_title") String faq_title,
            @Param("faq_content") String faq_content
    );

    // 자주 묻는 질문 삭제
    @Delete("delete from cs_support_faq where faq_id = #{faq_id}")
    int deleteFaq(
            @Param("faq_id") int faq_id
    );


    // 고객측 1:1 문의 ---------------------------------------------------------------------
    // 1:1 문의 작성
    @Insert("INSERT INTO cs_inquire_customer(user_id, inquire_category," +
            "inquire_title, inquire_content) " +
            "VALUES (#{user_id}, #{inquire_category}, #{inquire_title}, #{inquire_content})")
    @Options(useGeneratedKeys = true, keyProperty = "inquire_id")
    int addCsInquireCustomer(CsInquireCustomerBean csinquirecustomerbean);

    // 1:1 문의 내역 전체 조회
    @Select("select * from cs_inquire_customer where user_id = #{user_id} order by inquire_at desc, inquire_id desc " +
            "limit #{count} offset #{offset}")
    List<CsInquireCustomerBean> getCsInquireCustomerByUserId(int user_id, int count, int offset);

    // 1:1 문의 내역 상세 조회
    @Select("select * from cs_inquire_customer where user_id = #{user_id} AND inquire_id = #{inquire_id}")
    List<CsInquireCustomerBean> getCsInquireCustomerByUserIdAndInquireId(
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id
    );

    //1:1 문의 리스트 (접수 / 처리중 / 답변 완료)
    @Select("SELECT * FROM cs_inquire_customer WHERE user_id = #{user_id} and inquire_status = #{inquire_status}" +
            " ORDER BY inquire_at DESC, inquire_id DESC limit #{count} offset #{offset}")
    List<CsInquireCustomerBean> getAllInquiresByStatusCustomer(
            @Param("user_id") int user_id,
            @Param("inquire_status") CsInquireCustomerBean.Inquire_Status inquire_status,
            int count,
            int offset);

    // 1:1 문의 수정
    @Update("update cs_inquire_customer " +
            "set inquire_category = #{inquire_category}, " +
            "inquire_title = #{inquire_title}, inquire_content = #{inquire_content} " +
            "where user_id = #{user_id} AND inquire_id = #{inquire_id}")
    int updateCsInquireCustomer(
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id,
            @Param("inquire_category") CsInquireCustomerBean.Inquire_Category inquire_category,
            @Param("inquire_title") String inquire_title,
            @Param("inquire_content") String inquire_content
    );

    // 1:1 문의 삭제
    @Delete("delete from cs_inquire_customer where user_id = #{user_id} and inquire_id = #{inquire_id}")
    int deleteCsInquireCustomerByUserIdAndInquireId(
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id
    );

    //1:1 문의 관리자 시점---------------------------------------------------------------------
    //1:1 답변 작성
    @Insert("INSERT INTO cs_reply_admin (inquire_id, reply_title, reply_content) " +
            "VALUES (#{inquire_id}, #{reply_title}, #{reply_content});")
    @Options(useGeneratedKeys = true, keyProperty = "reply_id")
    int addCsReply(CsReplyBean csReplyBean);

    //1:1 문의 답변 리스트
    @Select("select * from cs_reply_admin order by reply_at desc, reply_id desc limit #{count} offset #{offset};")
    List<CsReplyBean> getAllCsReply(int count, int offset);

    //1:1 문의 상태에 따른 리스트 (접수 / 처리중 / 답변 완료)
    @Select("SELECT * FROM cs_inquire_customer WHERE inquire_status = #{inquire_status}" +
            "ORDER BY inquire_at DESC, inquire_id DESC limit #{count} offset #{offset}")
    List<CsInquireCustomerBean> getAllInquiresByStatusAdmin(
            @Param("inquire_status") CsInquireCustomerBean.Inquire_Status inquire_status,
            int count,
            int offset
    );

    //1:1 문의 답변 상세 조회
    @Select("select * from cs_reply_admin where reply_id = #{reply_id} and inquire_id = #{inquire_id} " +
            " order by reply_at desc, reply_id desc;")
    List<CsReplyBean> getCsReplyDetail(
            @Param("reply_id") int reply_id,
            @Param("inquire_id")int inquire_id);

    //1:1 문의 답변 수정
    @Update("update cs_reply_admin set reply_title = #{reply_title}, reply_content = #{reply_content} " +
            "where reply_id = #{reply_id};")
    int updateReply(
            @Param("reply_id") int reply_id,
            @Param("reply_title") String reply_title,
            @Param("reply_content") String reply_content
    );

    //1:1 문의 답변 삭제
    @Delete("delete from cs_reply_admin where reply_id = #{reply_id};")
    int deleteCsReply(
            @Param("reply_id") int reply_id
    );

    //검수 기준 ---------------------------------------------------------------------------------------------------------
    //검수 기준 직접 작성
    @Insert("INSERT INTO cs_test_standard (standard_category, standard_content) " +
            "VALUES (#{standard_category}, #{standard_content})")
    @Options(useGeneratedKeys = true, keyProperty = "standard_id")
    int addStandard(CsStandardBean csStandardBean);

    // standard_category를 통해 검수 기준 상세 출력
    @Select("SELECT * FROM cs_test_standard WHERE standard_category = #{standard_category}")
    List<CsStandardBean> getStandard(
            @Param("standard_category") CsStandardBean.Standard_Category standard_category);

    // 검수 기준 질문 수정
    @Update("update cs_test_standard " +
            "set standard_category = #{standard_category}, " +
            "standard_content = #{standard_content} " +
            "where standard_id = #{standard_id}")
    int updateStandard(
            @Param("standard_id") int standard_id,
            @Param("standard_category") CsStandardBean.Standard_Category standard_category,
            @Param("standard_content") String standard_content
    );

    // 검수 기준 삭제
    @Delete("delete from cs_test_standard where standard_id = #{standard_id}")
    int deleteStandard(
            @Param("standard_id") int standard_id
    );


}
