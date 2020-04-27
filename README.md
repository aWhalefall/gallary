#兼容图片选择器


#### 1.支持拍照
#### 2.支持剪切
#### 3.兼容android Q +




快速使用Api

public class MainActivity extends AppCompatActivity {

    GalleryConfig config;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);

        Glide.init(this, new GlideBuilder());
        initListenter();
        config = new GalleryConfig.Builder()
                .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.whalefail.gallary.fileProvider")   // provider (必填)
                .pathList(new ArrayList<String>())                 // 记录已选的图片
                .multiSelect(true)
                // 是否多选   默认：false
                // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(3)                             // 配置多选时 的多选数量。    默认：9
                .crop(true)                      // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();
    }

    private void initListenter() {

    }

    public void selectPhoto(View view) {
        GalleryPick.getInstance().setGalleryConfig(config).open(this);
    }
}



选中图片回调

   IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(List<String> photoList) {
            text.setText("");
            //返回选择图片路径  Android Q 以上包含Android   Q 返回的是 Uri

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < photoList.size(); i++) {
                stringBuilder.append(photoList.get(i)).append("\n");
            }
            text.setText(stringBuilder.toString());
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError() {

        }
    };


[![](https://jitpack.io/v/aWhalefall/gallary.svg)](https://jitpack.io/#aWhalefall/gallary)

![image](https://github.com/aWhalefall/gallary/tree/master/app/src/main/res/mipmap-hdpi/1.png)
![image](https://github.com/aWhalefall/gallary/tree/master/app/src/main/res/mipmap-hdpi/2.png)
![image](https://github.com/aWhalefall/gallary/tree/master/app/src/main/res/mipmap-hdpi/3.png)
![image](https://github.com/aWhalefall/gallary/tree/master/app/src/main/res/mipmap-hdpi/4.png)