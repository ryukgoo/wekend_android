package com.entuition.wekend.model.data.product;

import com.entuition.wekend.model.data.like.LikeDBItem;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
public interface IProductInfoDao {
    ProductInfo getProductInfo(int id);
    List<ProductInfo> loadProductList(ProductQueryOptions options, List<LikeDBItem> likeList);
    List<ProductInfo> getProductSubList();
    List<ProductInfo> getProductList();
    List<ProductInfo> searchProducts(String keyword, List<LikeDBItem> likeList);
}
