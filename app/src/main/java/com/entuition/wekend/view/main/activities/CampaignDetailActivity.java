package com.entuition.wekend.view.main.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.asynctask.AddLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.HasLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.IAddLikeProductCallback;
import com.entuition.wekend.model.data.like.asynctask.IHasLikeProductCallback;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.product.asynctask.ILoadUserInfoAndProductInfoCallback;
import com.entuition.wekend.model.data.product.asynctask.LoadUserInfoAndProductInfoTask;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.googleservice.googlemap.GetLocationTask;
import com.entuition.wekend.model.googleservice.googlemap.IGetLocationCallback;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.WekendActivity;
import com.entuition.wekend.view.util.AnimateFirstDisplayListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ryukgoo on 2016. 8. 4..
 */
public class CampaignDetailActivity extends WekendActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final String TAG = getClass().getSimpleName();

    private TextView txtTitleKor;
    private TextView txtTitleEng;
    private TextView txtSubTitle;
    private TextView txtDescription;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView buttonLikeProduct;
    private ProgressBar progressButtonLikeProduct;

    private MapView mapView;

    private ViewPager viewPager;
    private CampaignPagerAdapter viewPagerAdapter;
    private LinearLayout pageIndicator;
    private int dotsCount = 0;
    private ImageView[] dots;

    private DisplayImageOptions options;

    private String userId;
    private int productId;
    private UserInfo userInfo;
    private ProductInfo productInfo;

    private boolean hasAlreadyLike = false;

    // For Kakao
    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;

    // For Facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_campaign_detail);

        productId = getIntent().getIntExtra(Constants.PARAMETER_PRODUCT_ID, -1);
        userId = UserInfoDaoImpl.getInstance().getUserId(this);

        // Kakao
        try {
            kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());

        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        // Facebook
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "FAcebook callbackMAnger > onSuccess : " + result.toString());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook callbackManager > onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook callbackManager > error : " + error.getMessage());

                new AlertDialog.Builder(new ContextThemeWrapper(CampaignDetailActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.campaign_detail_share_failed_title))
                        .setMessage(getString(R.string.campaign_detail_share_failed_message))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            }
        });

        initView();
        loadProductInfo();
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_campaign_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.id_action_share);

        MenuItem shareForKakao = menu.findItem(R.id.id_action_share_kakao);
        shareForKakao.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                sendKakaoTalkLink();

                return false;
            }
        });

        MenuItem shareForFacebook = menu.findItem(R.id.id_action_share_facebook);
        shareForFacebook.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                sendFacebookLink();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick >>> !!!!!");

        switch (v.getId()) {
            case R.id.id_campaign_detail_phone_layout:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + productInfo.getTelephone().trim()));
                startActivity(callIntent);
                break;

//            case R.id.id_campaign_detail_map :
            case R.id.id_campaign_detail_address_layout :
                showGoogleMap();
                break;

            case R.id.id_progress_button_text:
                if (hasAlreadyLike) {
                    Intent intent = new Intent(CampaignDetailActivity.this, RecommendFriendListActivity.class);
                    intent.putExtra(Constants.PARAMETER_PRODUCT_ID, productInfo.getId());
                    startActivity(intent);

                } else {
                    LikeDBItem value = new LikeDBItem();
                    value.setUserId(userInfo.getUserId());
                    value.setProductId(productInfo.getId());
                    value.setNickname(userInfo.getNickname());
                    value.setGender(userInfo.getGender());
                    value.setProductTitle(productInfo.getTitleKor());
                    value.setProductDesc(productInfo.getSubTitle());

                    AddLikeProductTask task = new AddLikeProductTask();
                    task.setCallback(new AddLikeCallback());
                    task.execute(value);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_campaign_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtTitleKor = (TextView) findViewById(R.id.id_campaign_detail_title_kor);
        txtTitleEng = (TextView) findViewById(R.id.id_campaign_detail_title_eng);
        txtSubTitle = (TextView) findViewById(R.id.id_campaign_detail_subtitle);
        txtDescription = (TextView) findViewById(R.id.id_text_campaign_desc);
        txtPhone = (TextView) findViewById(R.id.id_text_campaign_phone);
        txtAddress = (TextView) findViewById(R.id.id_text_campaign_address);

        mapView = (MapView) findViewById(R.id.id_campaign_detail_map);
        mapView.setOnClickListener(this);

        progressButtonLikeProduct = (ProgressBar) findViewById(R.id.id_progress_button_progressbar);
        buttonLikeProduct = (TextView) findViewById(R.id.id_progress_button_text);
        buttonLikeProduct.setEnabled(false);

        buttonLikeProduct.setOnClickListener(this);
        findViewById(R.id.id_campaign_detail_phone_layout).setOnClickListener(this);
        findViewById(R.id.id_campaign_detail_address_layout).setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.id_campaign_detail_viewpager);
        viewPagerAdapter = new CampaignPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        pageIndicator = (LinearLayout) findViewById(R.id.id_campaign_detail_viewpager_indicator);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_default_logo_gray)
                .showImageForEmptyUri(R.drawable.img_default_logo_gray)
                .showImageOnFail(R.drawable.img_default_logo_gray)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void setViews() {

        txtTitleKor.setText(productInfo.getTitleKor());
        txtTitleEng.setText(productInfo.getTitleEng());
        txtSubTitle.setText(productInfo.getSubTitle());

        if (productInfo.getSubAddress() == null) {
            txtAddress.setText(productInfo.getAddress());
        } else {
            txtAddress.setText(productInfo.getAddress() + " " + productInfo.getSubAddress());
        }

        txtPhone.setText(productInfo.getTelephone());
        if ( appendExtraInfosToDescription() != null ) {
            txtDescription.setText(Html.fromHtml(appendExtraInfosToDescription()));
        }

        viewPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(this);

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0 ; i < dotsCount ; i ++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.drawable_indicator_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(7, 0, 7, 0);

            pageIndicator.addView(dots[i], params);
        }

        if (dots.length > 0) dots[0].setSelected(true);

        findViewById(R.id.id_campaign_detail_appbar).setVisibility(View.VISIBLE);
        findViewById(R.id.id_campaign_detail_description).setVisibility(View.VISIBLE);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                GetLocationTask task = new GetLocationTask(CampaignDetailActivity.this, new IGetLocationCallback() {
                    @Override
                    public void onSuccess(LatLng place) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, Constants.GOOGLE_MAP_ZOOM));
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(place));
                        marker.showInfoWindow();

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                showGoogleMap();
                            }
                        });

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                showGoogleMap();
                                return false;
                            }
                        });

                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                showGoogleMap();
                            }
                        });

                        googleMap.getUiSettings().setMapToolbarEnabled(false);
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(CampaignDetailActivity.this, "Failed to show map", Toast.LENGTH_SHORT).show();
                    }
                });
                task.execute(productInfo.getAddress());
            }
        });
    }

    private void loadProductInfo() {
        LikeDBItem item = new LikeDBItem();
        item.setUserId(userId);
        item.setProductId(productId);

        LoadUserInfoAndProductInfoTask task = new LoadUserInfoAndProductInfoTask();
        task.setCallback(new LoadUserInfoAndProductInfoCallback());
        task.execute(item);
    }

    private void sendKakaoTalkLink() {

        String imageName = ProductDaoImpl.getProductImageName(productInfo.getId(), 0);
        String imageUrl = S3Utils.getS3Url(Constants.PRODUCT_IMAGE_BUCKET_NAME, imageName);

        try {
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoTalkLinkMessageBuilder.addText(productInfo.getTitleKor());
            kakaoTalkLinkMessageBuilder.addImage(imageUrl, 300, 200);
            kakaoTalkLinkMessageBuilder.addAppButton(getString(R.string.app_name));
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            Log.e(TAG, "kakao Error : " + e.getMessage());
        }
    }

    private void sendFacebookLink() {

        String imageName = ProductDaoImpl.getProductImageName(productInfo.getId(), 0);
        String imageUrl = S3Utils.getS3Url(Constants.PRODUCT_IMAGE_BUCKET_NAME, imageName);

        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "article")
                .putString("og:title", productInfo.getTitleKor())
                .putString("og:description", productInfo.getDescription())
                .putString("og:image", imageUrl)
                .putString("og:url", "https://fb.me/673785809486815")
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("news.publishes")
                .putObject("article", object)
                .build();

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("article")
                .setAction(action)
                .build();

        shareDialog.show(content);
    }


    private void showGoogleMap() {
        Intent mapIntent = new Intent(this, GoogleMapsActivity.class);
        mapIntent.putExtra(Constants.PARAMETER_PRODUCT_TITLE, productInfo.getTitleKor());
        mapIntent.putExtra(Constants.PARAMETER_MAP_ADDRESS, productInfo.getAddress());
        startActivity(mapIntent);
    }

    private void checkAlreadyLikeProduct() {
        LikeDBItem value = new LikeDBItem();
        value.setUserId(userInfo.getUserId());
        value.setProductId(productInfo.getId());
        value.setGender(userInfo.getGender());
//        value.setProductTitle(productInfo.getTitleKor());
//        value.setProductDesc(productInfo.getSubTitle());
//        value.setEndpointArn(userInfo.getEndpointARN());

        HasLikeProductTask task = new HasLikeProductTask();
        task.setCallback(new HasLikeProductCallback());
        task.execute(value);
    }

    private void setLike(int count) {
        hasAlreadyLike = true;
        String buttonText = getString(R.string.campaign_detail_button_confirm_friend) + " : " + count;
        buttonLikeProduct.setText(buttonText);
    }

    private void setUnlike() {
        hasAlreadyLike = false;
        buttonLikeProduct.setText(getString(R.string.campaign_detail_button_like));
    }

    private String appendExtraInfosToDescription() {

        String newDescription = productInfo.getDescription();

        if (productInfo.getPrice() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + getString(R.string.campaign_detail_price) + " : " + productInfo.getPrice();
        }

        if (productInfo.getParking() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + getString(R.string.campaign_detail_parking) + " : " + productInfo.getParking();
        }

        if (productInfo.getOperationTime() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + getString(R.string.campaign_detail_operation_time) + " : " + productInfo.getOperationTime();
        }

        return newDescription;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0 ; i < dotsCount ; i ++) {
            dots[i].setSelected(false);
        }

        dots[position].setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class CampaignPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return (productInfo == null) ? 0 : productInfo.getImageCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.campaign_pager_item, container, false);
            container.addView(view);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);

            String imageName = ProductDaoImpl.getProductImageName(productInfo.getId(), position);
            String imageUrl = S3Utils.getS3Url(Constants.PRODUCT_IMAGE_BUCKET_NAME, imageName);
            ImageLoader.getInstance().displayImage(imageUrl, imageView, options, new AnimateFirstDisplayListener());

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class LoadUserInfoAndProductInfoCallback implements ILoadUserInfoAndProductInfoCallback {

        @Override
        public void onPrePared() {

        }

        @Override
        public void onSuccess(UserInfo user, ProductInfo product) {
            userInfo = user;
            productInfo = product;

            setViews();
            checkAlreadyLikeProduct();

            // TODO : setShareMessage
        }

        @Override
        public void onFailed() {

        }
    }

    private class HasLikeProductCallback implements IHasLikeProductCallback {

        @Override
        public void onPrepared() {
            progressButtonLikeProduct.setVisibility(View.VISIBLE);
            buttonLikeProduct.setEnabled(false);
        }

        @Override
        public void onSuccess(int count) {
            setLike(count);
            progressButtonLikeProduct.setVisibility(View.GONE);
            buttonLikeProduct.setEnabled(true);
        }

        @Override
        public void onFailed() {
            setUnlike();
            progressButtonLikeProduct.setVisibility(View.GONE);
            buttonLikeProduct.setEnabled(true);
        }
    }

    private class AddLikeCallback implements IAddLikeProductCallback {

        @Override
        public void onSuccess(int totalCount, int friendCount) {
            setLike(friendCount);
        }

        @Override
        public void onFailed() {
            setUnlike();
        }
    }
}
