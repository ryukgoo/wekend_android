package com.entuition.wekend.view.main.campaign.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import com.entuition.wekend.data.source.product.ProductFilterOptions;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.product.enums.Category;
import com.entuition.wekend.data.source.product.enums.Concert;
import com.entuition.wekend.data.source.product.enums.Food;
import com.entuition.wekend.data.source.product.enums.IProductEnum;
import com.entuition.wekend.data.source.product.enums.Leisure;
import com.entuition.wekend.data.source.product.enums.ProductRegion;
import com.entuition.wekend.view.common.AbstractViewModel;

/**
 * Created by ryukgoo on 2017. 11. 8..
 */

public class OptionFilterViewModel extends AbstractViewModel {

    public static final String TAG = OptionFilterViewModel.class.getSimpleName();

    public final ObservableBoolean isOpen = new ObservableBoolean();
    public final ObservableBoolean sortLikeSelected = new ObservableBoolean();
    public final ObservableBoolean sortDateSelected = new ObservableBoolean();

    public final ObservableArrayList<IProductEnum> mainCategories = new ObservableArrayList<>();
    public final ObservableArrayList<IProductEnum> subCategories = new ObservableArrayList<>();
    public final ObservableArrayList<IProductEnum> regions = new ObservableArrayList<>();

    public final ObservableBoolean isSubEnable = new ObservableBoolean();
    public final ObservableBoolean isRegionEnable = new ObservableBoolean();

    private final ProductInfoDataSource productInfoDataSource;

    public ProductFilterOptions filterOptions = new ProductFilterOptions.Builder().build();

    public OptionFilterViewModel(Context context, ProductInfoDataSource productInfoDataSource) {
        super(context);
        this.productInfoDataSource = productInfoDataSource;

        isOpen.set(false);

        sortLikeSelected.set(false);
        sortDateSelected.set(false);
        isSubEnable.set(false);
        isRegionEnable.set(false);
    }

    @Override
    public void onCreate() {
        init();
    }

    @Override
    public void onResume() {
        restoreFilterOptions();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public boolean onBackPressed() {
        if (isOpen.get()) {
            close();
            return false;
        }
        return true;
    }

    public void toggle() {
        if (isOpen.get()) {
            isOpen.set(false);
        } else {
            isOpen.set(true);
        }
    }

    public void onSelectLikeSort() {
        sortLikeSelected.set(true);
        sortDateSelected.set(false);

        filterOptions.setSortType(ProductFilterOptions.ORDER_BY_LIKE);
    }

    public void onSelectDateSort() {
        sortDateSelected.set(true);
        sortLikeSelected.set(false);

        filterOptions.setSortType(ProductFilterOptions.ORDER_BY_DATE);
    }

    public void onSelectMainItem(int position) {

        IProductEnum item = mainCategories.get(position);
        filterOptions.setMainCategory(item);

        subCategories.clear();
        regions.clear();

        switch (position) {
            case 0: // all
                subCategories.addAll(Food.asList());
                regions.addAll(ProductRegion.asList());
                isSubEnable.set(false);
                isRegionEnable.set(false);
                break;
            case 1: // food
                subCategories.addAll(Food.asList());
                regions.addAll(ProductRegion.asList());
                isSubEnable.set(true);
                isRegionEnable.set(true);
                break;
            case 2: // concert
                subCategories.addAll(Concert.asList());
                regions.addAll(ProductRegion.asList());
                isSubEnable.set(true);
                isRegionEnable.set(true);
                break;
            case 3: // sport
                subCategories.addAll(Leisure.asList());
                regions.addAll(ProductRegion.asList());
                isSubEnable.set(true);
                isRegionEnable.set(true);
                break;
            default:
                subCategories.addAll(Food.asList());
                regions.addAll(ProductRegion.asList());
                isSubEnable.set(false);
                isRegionEnable.set(false);
                break;
        }
    }

    public void onSelectedSubItem(int position) {
        IProductEnum item = subCategories.get(position);
        filterOptions.setSubCategory(item);
    }

    public void onSelectedRegion(int position) {
        IProductEnum item = regions.get(position);
        filterOptions.setProductRegion(item);
    }

    public void reset() {
        mainCategories.clear();
        subCategories.clear();
        regions.clear();
        init();
    }

    public void close() {
        if (isOpen.get()) {
            isOpen.set(false);
        }
    }

    private void init() {
        if (filterOptions.getSortType() == ProductFilterOptions.ORDER_BY_DATE) {
            sortDateSelected.set(true);
        } else {
            sortLikeSelected.set(true);
        }

        mainCategories.addAll(Category.asList());
        subCategories.addAll(Food.asList());
        regions.addAll(ProductRegion.asList());
    }

    // TODO : restore filter options
    private void restoreFilterOptions() {
        ProductFilterOptions filterOptions = productInfoDataSource.getFilterOptions();
    }
}
