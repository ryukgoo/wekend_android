package com.entuition.wekend.model.data.product;

import com.entuition.wekend.model.data.product.enums.Category;
import com.entuition.wekend.model.data.product.enums.IProductEnum;

/**
 * Created by ryukgoo on 2016. 3. 31..
 */
public class ProductQueryOptions {

    public static final int ORDER_BY_DATE = 0;
    public static final int ORDER_BY_LIKE = 1;

    private final int orderType;
    private final IProductEnum mainCategory;
    private final IProductEnum subCategory;
    private final IProductEnum productRegion;

    public ProductQueryOptions(ProductQueryOptions.Builder builder) {
        this.orderType = builder.orderType;
        this.mainCategory = builder.mainCategory;
        this.subCategory = builder.subCategory;
        this.productRegion = builder.productRegion;
    }

    public int getOrderType() {
        return this.orderType;
    }

    public IProductEnum getMainCategory() {
        return this.mainCategory;
    }

    public IProductEnum getSubCategory() {
        return this.subCategory;
    }

    public IProductEnum getProductRegion() {
        return this.productRegion;
    }

    public boolean equals(ProductQueryOptions options) {

        return this.orderType == options.orderType &&
                this.mainCategory.getIdentifier() == options.mainCategory.getIdentifier() &&
                this.subCategory.getIdentifier() == options.subCategory.getIdentifier() &&
                this.productRegion.getIdentifier() == options.productRegion.getIdentifier();
    }

    public static class Builder {
        private int orderType;
        private IProductEnum mainCategory;
        private IProductEnum subCategory;
        private IProductEnum productRegion;

        public Builder() {
            this.orderType = ORDER_BY_LIKE;
            this.mainCategory = Category.NONE;
            this.subCategory = Category.NONE;
            this.productRegion = Category.NONE;
        }

        public ProductQueryOptions.Builder setOrderType(int orderType) {
            this.orderType = orderType;
            return this;
        }

        public ProductQueryOptions.Builder setMainCategory(IProductEnum mainCategory) {
            this.mainCategory = mainCategory;
            return this;
        }

        public ProductQueryOptions.Builder setSubCategory(IProductEnum subCategory) {
            this.subCategory = subCategory;
            return this;
        }

        public ProductQueryOptions.Builder setProductRegion(IProductEnum productRegion) {
            this.productRegion = productRegion;
            return this;
        }

        public ProductQueryOptions.Builder cloneFrom(ProductQueryOptions options) {
            this.orderType = options.orderType;
            this.mainCategory = options.mainCategory;
            this.subCategory = options.subCategory;
            this.productRegion = options.productRegion;
            return this;
        }

        public ProductQueryOptions build() {
            return new ProductQueryOptions(this);
        }
    }
}
