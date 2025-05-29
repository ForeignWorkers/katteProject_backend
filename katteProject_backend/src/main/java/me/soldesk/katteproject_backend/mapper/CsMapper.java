package me.soldesk.katteproject_backend.mapper;


import common.bean.cs.CsInquireCustomerBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CsMapper {

    // 고객측 1:1 문의
    // 1:1 문의 작성
    @Insert("INSERT INTO cs_inquire_customer(user_id, inquire_category," +
            "inquire_title, inquire_content) " +
            "VALUES (#{user_id}, #{inquire_category}, #{inquire_title}, #{inquire_content})")
    @Options(useGeneratedKeys = true, keyProperty = "inquire_id")
    int addCsInquireCustomer(CsInquireCustomerBean csinquirecustomerbean);

    // 1:1 문의 내역 조회
    @Select("select * from cs_inquire_customer where user_id = #{user_id} order by inquire_at desc, inquire_id desc")
    List<CsInquireCustomerBean> getCsInquireCustomerByUserId(int user_id);

    // 1:1 문의 내역 상세 조회
    @Select("select * from cs_inquire_customer where user_id = #{user_id} AND inquire_id = #{inquire_id}")
    List<CsInquireCustomerBean> getCsInquireCustomerByUserIdAndInquireId(
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id
    );

    // 1:1 문의 수정
    @Update("update cs_inquire_customer " +
            "set inquire_category = #{inquire_category}, " +
            "inquire_title = #{inquire_title}, inquire_content = #{inquire_content} " +
            "where user_id = #{user_id} AND inquire_id = #{inquire_id}")
    int updateCsInquireCustomer(
            //CsInquireCustomerBean csInquireCustomerBean
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id,
            @Param("inquire_category") CsInquireCustomerBean.inquire_category einquire_category,
            @Param("inquire_title") String inquire_title,
            @Param("inquire_content") String inquire_content
    );

    // 1:1 문의 삭제
    @Delete("delete from cs_inquire_customer where user_id = #{user_id} and inquire_id = #{inquire_id}")
    int deleteCsInquireCustomerByUserIdAndInquireId(
            @Param("user_id") int user_id,
            @Param("inquire_id") int inquire_id
    );
}
