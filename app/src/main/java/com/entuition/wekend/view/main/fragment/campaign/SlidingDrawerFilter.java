package com.entuition.wekend.view.main.fragment.campaign;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.product.ProductQueryOptions;
import com.entuition.wekend.model.data.product.enums.Category;
import com.entuition.wekend.model.data.product.enums.Concert;
import com.entuition.wekend.model.data.product.enums.Food;
import com.entuition.wekend.model.data.product.enums.IProductEnum;
import com.entuition.wekend.model.data.product.enums.Leisure;
import com.entuition.wekend.model.data.product.enums.ProductRegion;
import com.entuition.wekend.view.util.CenterAlignedSpinnerAdapter;

/**
 * Created by ryukgoo on 2016. 7. 19..
 */
public class SlidingDrawerFilter {

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private SlidingDrawer slidingDrawerFilters;
    private TextView buttonLikeFilter;
    private TextView buttonRecentFilter;
    private AppCompatSpinner spinnerProductRegion;
    private AppCompatSpinner spinnerMainCategory;
    private AppCompatSpinner spinnerSubCategory;
    private Button buttonFilterSelect;

    private CenterAlignedSpinnerAdapter adapterRegion;
    private CenterAlignedSpinnerAdapter adapterMainCategory;
    private CenterAlignedSpinnerAdapter adapterSubCategoryFood;
    private CenterAlignedSpinnerAdapter adapterSubCategoryConcert;
    private CenterAlignedSpinnerAdapter adapterSubCategoryLeisure;

    private int orderType;
    private IProductEnum mainCategory;
    private IProductEnum subCategory;
    private IProductEnum region;

    private IFilterCallback filterCallback;

    public SlidingDrawerFilter(Context context, View rootView) {

        this.context = context;

        orderType = ProductQueryOptions.ORDER_BY_DATE;
        mainCategory = Category.NONE;
        subCategory = Category.NONE;
        region = Category.NONE;

        this.slidingDrawerFilters = (SlidingDrawer) rootView.findViewById(R.id.id_sliding_drawer_filters);

        this.buttonLikeFilter = (TextView) rootView.findViewById(R.id.id_button_sort_like);
        this.buttonRecentFilter = (TextView) rootView.findViewById(R.id.id_button_sort_recent);
        this.spinnerProductRegion = (AppCompatSpinner) rootView.findViewById(R.id.id_spinner_filter_region);
        this.spinnerMainCategory = (AppCompatSpinner) rootView.findViewById(R.id.id_spinner_filter_maincategory);
        this.spinnerSubCategory = (AppCompatSpinner) rootView.findViewById(R.id.id_spinner_filter_subcategory);
        this.buttonFilterSelect = (Button) rootView.findViewById(R.id.id_button_filter_ok);

        setAdapter();
    }

    private void setAdapter() {
        // setSpinners
        adapterMainCategory = new CenterAlignedSpinnerAdapter(context, R.layout.spinner_item,
                Category.asStringList(context), Category.asArray());

        adapterSubCategoryFood = new CenterAlignedSpinnerAdapter(context, R.layout.spinner_item,
                Food.asStringList(context), Food.asArray());

        adapterSubCategoryConcert = new CenterAlignedSpinnerAdapter(context, R.layout.spinner_item,
                Concert.asStringList(context), Concert.asArray());

        adapterSubCategoryLeisure = new CenterAlignedSpinnerAdapter(context, R.layout.spinner_item,
                Leisure.asStringList(context), Leisure.asArray());

        adapterRegion = new CenterAlignedSpinnerAdapter(context, R.layout.spinner_item,
                ProductRegion.asStringList(context), ProductRegion.asArray());

        spinnerMainCategory.setAdapter(adapterMainCategory);
        spinnerSubCategory.setAdapter(adapterSubCategoryFood);
        spinnerProductRegion.setAdapter(adapterRegion);
    }

    public void setListener(IFilterCallback callback) {
        filterCallback = callback;
        slidingDrawerFilters.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                filterCallback.onOpenDrawer();
                if (orderType == ProductQueryOptions.ORDER_BY_DATE) {
                    buttonRecentFilter.setSelected(true);
                    buttonLikeFilter.setSelected(false);
                } else {
                    buttonRecentFilter.setSelected(false);
                    buttonLikeFilter.setSelected(true);
                }
            }
        });

        slidingDrawerFilters.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                filterCallback.onCloseDrawer();
            }
        });

        buttonLikeFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                orderType = ProductQueryOptions.ORDER_BY_LIKE;
                buttonLikeFilter.setSelected(true);
                buttonRecentFilter.setSelected(false);
                return true;
            }
        });

        buttonRecentFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                orderType = ProductQueryOptions.ORDER_BY_DATE;
                buttonRecentFilter.setSelected(true);
                buttonLikeFilter.setSelected(false);
                return true;
            }
        });

        buttonFilterSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCallback.onStartClose();
                slidingDrawerFilters.animateClose();

                orderType = buttonLikeFilter.isSelected() ?
                        ProductQueryOptions.ORDER_BY_LIKE : ProductQueryOptions.ORDER_BY_DATE;

                mainCategory = (IProductEnum) spinnerMainCategory.getSelectedView().getTag();
                subCategory = (IProductEnum) spinnerSubCategory.getSelectedView().getTag();
                region = (IProductEnum) spinnerProductRegion.getSelectedView().getTag();

                filterCallback.onCompleteFilterSelect(orderType, mainCategory, subCategory, region);
            }
        });

        spinnerMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Category category = (Category) view.getTag();

                switch (category) {
                    case FOOD: // FOOD
                        spinnerSubCategory.setAdapter(adapterSubCategoryFood);
                        spinnerProductRegion.setAdapter(adapterRegion);
                        spinnerProductRegion.setEnabled(true);
                        spinnerSubCategory.setEnabled(true);
                        break;
                    case CONCERT: // CONCERT
                        spinnerSubCategory.setAdapter(adapterSubCategoryConcert);
                        spinnerProductRegion.setAdapter(adapterRegion);
                        spinnerProductRegion.setEnabled(true);
                        spinnerSubCategory.setEnabled(true);
                        break;
                    case LEISURE: // LEISURE
                        spinnerSubCategory.setAdapter(adapterSubCategoryLeisure);
                        spinnerProductRegion.setAdapter(adapterRegion);
                        spinnerProductRegion.setEnabled(true);
                        spinnerSubCategory.setEnabled(true);
                        break;
                    default: // NONE
                        spinnerSubCategory.setAdapter(adapterSubCategoryFood);
                        spinnerProductRegion.setAdapter(adapterRegion);
                        spinnerProductRegion.setEnabled(false);
                        spinnerSubCategory.setEnabled(false);
                        break;
                }

                if (orderType == ProductQueryOptions.ORDER_BY_DATE) {
                    buttonRecentFilter.setSelected(true);
                    buttonLikeFilter.setSelected(false);
                } else {
                    buttonRecentFilter.setSelected(false);
                    buttonLikeFilter.setSelected(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.d(TAG, "MAinCategory > onNothingSelected");

                if (orderType == ProductQueryOptions.ORDER_BY_DATE) {
                    buttonRecentFilter.setSelected(true);
                    buttonLikeFilter.setSelected(false);
                } else {
                    buttonRecentFilter.setSelected(false);
                    buttonLikeFilter.setSelected(true);
                }
            }
        });

        spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerProductRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean isOpen() {
        return slidingDrawerFilters.isOpened();
    }

    public void open() {
        resumeFilterPosition();
        filterCallback.onStartOpen();
        slidingDrawerFilters.animateOpen();
    }

    public void close() {
        filterCallback.onStartClose();
        slidingDrawerFilters.animateClose();
    }

    public void resumeFilterPosition() {

        Log.d(TAG, "resumeFilterPosition");
        if (orderType == ProductQueryOptions.ORDER_BY_LIKE) {
            buttonLikeFilter.setSelected(true);
            buttonRecentFilter.setSelected(false);
        } else {
            buttonLikeFilter.setSelected(false);
            buttonRecentFilter.setSelected(true);
        }

        spinnerMainCategory.setSelection(mainCategory.getIndex());
        spinnerSubCategory.setSelection(subCategory.getIndex());
        spinnerProductRegion.setSelection(region.getIndex());
    }

    public interface IFilterCallback {
        void onOpenDrawer();
        void onStartOpen();
        void onCloseDrawer();
        void onStartClose();

        void onCompleteFilterSelect(int order, IProductEnum mainCategory, IProductEnum subCategory, IProductEnum region);
    }
}
