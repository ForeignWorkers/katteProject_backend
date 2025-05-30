package me.soldesk.katteproject_backend.test;

import lombok.Data;

import java.util.Date;

@Data
public class ProductINFO_TEST {
    private int id;
    private int product_id;
    private int product_base_id;
    private String model_code;
    private category category;
    private String product_name;
    private String product_desc;
    private String brand_name;
    private Date release_date;
    private int release_price;

    enum category{
        shoes, clothing, bag, accessory, electronics, beauty, lifestyle, collectible, etc
    }
}
