package me.soldesk.katteproject_backend.service;

import common.bean.content.ContentStyleBean;
import common.bean.search.SearchType;
import me.soldesk.katteproject_backend.mapper.SearchMapper;
import me.soldesk.katteproject_backend.test.ProductINFO_TEST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    private SearchMapper searchMapper;

    public List<?> searchByKeyword(SearchType type, String keyword) {
        keyword = "%" + keyword + "%";
        switch (type) {
            case PRODUCT:
                return searchMapper.searchProductByKeyword(keyword);
            case STYLE:
                return searchMapper.searchStyleByKeyword(keyword);
            default:
                throw new IllegalArgumentException("잘못된 검색 타입");
        }
    }
}
