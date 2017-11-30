package com.entuition.wekend.data.source.product;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.product.enums.Category;
import com.entuition.wekend.data.source.product.enums.IProductEnum;
import com.entuition.wekend.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 3. 31..
 */
public class ProductFilterOptions {

    public static final String TAG = ProductFilterOptions.class.getSimpleName();

    public static final int ORDER_BY_DATE = 0;
    public static final int ORDER_BY_LIKE = 1;

    private static final String FILTER_KEY_MAIN_CATEGORY = ":category";
    private static final String FILTER_KEY_SUB_CATEGORY = ":subCategories";
    private static final String FILTER_KEY_PRODUCT_REGION = ":productRegion";
    private static final String FILTER_KEY_SEARCH_KEYWORD = ":keyword";

    private int sortType;
    private IProductEnum mainCategory;
    private IProductEnum subCategory;
    private IProductEnum productRegion;
    private String keyword;

    public ProductFilterOptions(ProductFilterOptions.Builder builder) {
        this.sortType = builder.orderType;
        this.mainCategory = builder.mainCategory;
        this.subCategory = builder.subCategory;
        this.productRegion = builder.productRegion;
        this.keyword = builder.keyword;
    }

    public int getSortType() {
        return this.sortType;
    }

    public void setSortType(int type) {
        this.sortType = type;
    }

    public IProductEnum getMainCategory() {
        return this.mainCategory;
    }

    public void setMainCategory(IProductEnum mainCategory) {
        this.mainCategory = mainCategory;
    }

    public IProductEnum getSubCategory() {
        return this.subCategory;
    }

    public void setSubCategory(IProductEnum subCategory) {
        this.subCategory = subCategory;
    }

    public IProductEnum getProductRegion() {
        return this.productRegion;
    }

    public void setProductRegion(IProductEnum region) {
        this.productRegion = region;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {

        Log.d(TAG, "setKeyword : " + keyword);

        this.keyword = keyword;
    }

    public boolean equals(ProductFilterOptions options) {

        return this.sortType == options.sortType &&
                this.mainCategory.getIdentifier() == options.mainCategory.getIdentifier() &&
                this.subCategory.getIdentifier() == options.subCategory.getIdentifier() &&
                this.productRegion.getIdentifier() == options.productRegion.getIdentifier();
    }

    public String getFilterExpress() {
        String expression = null;
        if (this.mainCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            expression = ProductInfo.Attribute.MAIN_CATEGORY + " = " + FILTER_KEY_MAIN_CATEGORY;
        }

        if (this.subCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            if (expression != null) {
                expression = expression + " and " + ProductInfo.Attribute.SUB_CATEGORY + " = " + FILTER_KEY_SUB_CATEGORY;
            } else {
                expression = ProductInfo.Attribute.SUB_CATEGORY + " = " + FILTER_KEY_SUB_CATEGORY;
            }
        }

        if (this.productRegion.getIdentifier() != Category.NONE.getIdentifier()) {
            if (expression != null) {
                expression = expression + " and " + ProductInfo.Attribute.PRODUCT_REGION + " = " + FILTER_KEY_PRODUCT_REGION;
            } else {
                expression = ProductInfo.Attribute.PRODUCT_REGION + " = " + FILTER_KEY_PRODUCT_REGION;
            }
        }

        if (!TextUtils.isNullorEmptyString(keyword)) {
            String keywordExpression = "contains(" + ProductInfo.Attribute.TITLE_KOR + ", " + FILTER_KEY_SEARCH_KEYWORD + ")"
                    + " or " + "contains(" + ProductInfo.Attribute.ADDRESS + ", " + FILTER_KEY_SEARCH_KEYWORD + ")"
                    + " or " + "contains(" + ProductInfo.Attribute.DESCRIPTION + ", " + FILTER_KEY_SEARCH_KEYWORD + ")";

            if (expression != null) {
                expression = expression + " and (" + keywordExpression + ")";
            } else {
                expression = keywordExpression;
            }
        }

        return expression;
    }

    public Map<String, AttributeValue> getAttributeValueMap() {
        Map<String, AttributeValue> valueMap = new HashMap<>();
        if (this.mainCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            String mainCategoryValue = String.valueOf(this.mainCategory.getIdentifier());
            valueMap.put(FILTER_KEY_MAIN_CATEGORY, new AttributeValue().withN(mainCategoryValue));
        }

        if (this.subCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            String subCategoryValue = String.valueOf(this.subCategory.getIdentifier());
            valueMap.put(FILTER_KEY_SUB_CATEGORY, new AttributeValue().withN(subCategoryValue));
        }

        if (this.productRegion.getIdentifier() != Category.NONE.getIdentifier()) {
            String productRegionValue = String.valueOf(this.productRegion.getIdentifier());
            valueMap.put(FILTER_KEY_PRODUCT_REGION, new AttributeValue().withN(productRegionValue));
        }

        if (!TextUtils.isNullorEmptyString(keyword)) {
            valueMap.put(FILTER_KEY_SEARCH_KEYWORD, new AttributeValue().withS(keyword));
        }

        return valueMap;
    }

    public String getIndexName() {
        if (this.sortType == ProductFilterOptions.ORDER_BY_LIKE) {
            return ProductInfo.Index.LIKE_COUNT;
        } else {
            return ProductInfo.Index.UPDATED_TIME;
        }
    }

    public String getTitle(Context context) {

        if (!TextUtils.isNullorEmptyString(keyword))
            return context.getString(R.string.campaign_keyword_title, keyword);

        String title = "";
        if (mainCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            title = context.getString(mainCategory.getResource());
        }
        if (subCategory.getIdentifier() != Category.NONE.getIdentifier()) {
            title += ", " + context.getString(subCategory.getResource());
        }
        if (productRegion.getIdentifier() != Category.NONE.getIdentifier()) {
            title += ", " + context.getString(productRegion.getResource());
        }

        if (TextUtils.isNullorEmptyString(title)) {
            return context.getString(R.string.title_campaign);
        }
        return title;
    }

    public static class Builder {
        private int orderType;
        private IProductEnum mainCategory;
        private IProductEnum subCategory;
        private IProductEnum productRegion;
        private String keyword;

        public Builder() {
            this.orderType = ORDER_BY_DATE;
            this.mainCategory = Category.NONE;
            this.subCategory = Category.NONE;
            this.productRegion = Category.NONE;
            this.keyword = null;
        }

        public ProductFilterOptions.Builder setOrderType(int orderType) {
            this.orderType = orderType;
            return this;
        }

        public ProductFilterOptions.Builder setMainCategory(IProductEnum mainCategory) {
            this.mainCategory = mainCategory;
            return this;
        }

        public ProductFilterOptions.Builder setSubCategory(IProductEnum subCategory) {
            this.subCategory = subCategory;
            return this;
        }

        public ProductFilterOptions.Builder setProductRegion(IProductEnum productRegion) {
            this.productRegion = productRegion;
            return this;
        }

        public ProductFilterOptions.Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public ProductFilterOptions.Builder cloneFrom(ProductFilterOptions options) {
            this.orderType = options.sortType;
            this.mainCategory = options.mainCategory;
            this.subCategory = options.subCategory;
            this.productRegion = options.productRegion;
            this.keyword = options.keyword;
            return this;
        }

        public ProductFilterOptions build() {
            return new ProductFilterOptions(this);
        }
    }
}
