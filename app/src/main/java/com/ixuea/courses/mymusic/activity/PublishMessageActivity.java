package com.ixuea.courses.mymusic.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.ImageSelectAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Feed;
import com.ixuea.courses.mymusic.domain.event.PublishMessageEvent;
import com.ixuea.courses.mymusic.domain.param.FeedParam;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.OSSUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;
import com.ixuea.courses.mymusic.util.UUIDUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PublishMessageActivity extends BaseTitleActivity {

    private static final int REQUEST_SELECT_IMAGE = 10;
    private EditText et_message;
    private RecyclerView rv;
    private ImageSelectAdapter imageSelectAdapter;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_message);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        et_message = findViewById(R.id.et_message);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        rv.setLayoutManager(layoutManager);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        imageSelectAdapter = new ImageSelectAdapter(getActivity(),R.layout.item_select_iamge);
        imageSelectAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                Object data = imageSelectAdapter.getData(position);
                if (data instanceof LocalMedia){
                    //预览界面
                }else{
                    //选择图片界面
                    selectImage();
                }
            }
        });
        rv.setAdapter(imageSelectAdapter);

        setData(new ArrayList<Object>());
    }
    private void setData(ArrayList<Object> objects){
        if (objects.size()!=9){
            //选了9张图片，就不显示添加按钮
            objects.add(R.drawable.ic_add_grey);
        }
        imageSelectAdapter.setData(objects);
    }
    private void selectImage(){
        ArrayList<LocalMedia> selectedImage = getSelectedImages();

        //进入相册，以下是例子，用不到的api可以不写。
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(false)
                .imageFormat(PictureMimeType.JPEG)
                .isZoomAnim(true)//图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)//glide加载图片大小在0-1之间，如设置.glideOverride()无效
                .compress(true)
                .selectionMedia(selectedImage)
                .previewEggs(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }
    @NonNull
    private ArrayList<LocalMedia> getSelectedImages(){
        List<Object> datas = imageSelectAdapter.getDatas();
        ArrayList<LocalMedia> selectedImage = new ArrayList<>();
        for (Object o :datas) {
            if (o instanceof LocalMedia)
            selectedImage.add((LocalMedia)o);
        }
        return selectedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch(requestCode){
                case PictureConfig.CHOOSE_REQUEST:
                    //图片，视频，音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    ArrayList<Object> objects = new ArrayList<>();
                    objects.addAll(selectList);
                    setData(objects);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_message,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send){
            sendMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendMessage(){
        message = et_message.getText().toString().trim();
        if (StringUtils.isBlank(message)){
            ToastUtil.showSortToast(getActivity(),R.string.hint_message);
            return;
        }
        if (message.length()>140){
            ToastUtil.showSortToast(getActivity(),R.string.content_length_error);
            return;
        }
        ArrayList<LocalMedia> selectedImages = getSelectedImages();
        if (selectedImages.size()>0){
            //有图片，先上传图片
            uploadImage(selectedImages);
        }else{
            saveMessage(null);
        }
    }
    private void uploadImage(ArrayList<LocalMedia> selectedImages){
        final OSSClient oss = OSSUtil.getInstance(getActivity());
        new AsyncTask<List<LocalMedia>,Integer,List<String>>(){
            @Override
            protected List<String> doInBackground(List<LocalMedia>... params) {
                try{
                    ArrayList<String> results = new ArrayList<>();
                    for (Object o : params[0]) {
                        if (o instanceof LocalMedia){
                            /**
                             * 上传，OSS如果没有特殊需要建议不要分目录，如果一定要分目录
                             * 不要让目录名前面连续，例如时间戳倒过来，如果连续请求达到一定
                             * 量级会有性能影响。
                             * https://help.aliyun.com/document_detail/64945.html
                             */
                            String destFileName = UUIDUtil.getUUID()+".jpg";
                            PutObjectRequest put = new PutObjectRequest(Consts.OSS_BUCKET_NAME,destFileName,((LocalMedia)o).getCompressPath());
                            PutObjectResult putResult = oss.putObject(put);

                            results.add(destFileName);
                        }
                    }
                    return results;
                }catch(Exception e){
                    //服务异常
                   e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<String> data) {
                super.onPostExecute(data);
                if (data!=null&&data.size()>0){
                    saveMessage(data);
                }else{
                    ToastUtil.showSortToast(getActivity(),getString(R.string.upload_image_error));
                }
            }
        }.execute(selectedImages);
    }
    private void saveMessage(List<String> data){
        FeedParam feed = new FeedParam();
        feed.setContent(message);
        feed.setImages(data);
        Api.getInstance().createFeed(feed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Feed>>(getActivity()){
                    @Override
                    public void onSucceeded(DetailResponse<Feed> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }
    public void next(Feed feed){
        EventBus.getDefault().post(new PublishMessageEvent());
        finish();
    }
}
