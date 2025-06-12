package me.soldesk.katteproject_backend.typehandler;


import common.bean.product.ProductInfoBean;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(ProductInfoBean.Category.class)
public class CategoryTypeHandler extends BaseTypeHandler<ProductInfoBean.Category> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductInfoBean.Category parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDbValue());
    }

    @Override
    public ProductInfoBean.Category getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ProductInfoBean.Category.fromDbValue(rs.getString(columnName));
    }

    @Override
    public ProductInfoBean.Category getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ProductInfoBean.Category.fromDbValue(rs.getString(columnIndex));
    }

    @Override
    public ProductInfoBean.Category getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ProductInfoBean.Category.fromDbValue(cs.getString(columnIndex));
    }
}
