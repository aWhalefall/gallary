#兼容图片选择器


#### 1.支持拍照
#### 2.支持剪切
#### 3.兼容android Q +



快速引用

1. dd it in your root build.gradle at the end of repositories

   allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency

  dependencies {
	        implementation 'com.github.aWhalefall:gallary:Tag'
	}




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

![拍照](https://user-images.githubusercontent.com/7346792/80484807-e9400300-898a-11ea-80b2-3311d25cbf24.png)
![照片集](https://user-images.githubusercontent.com/7346792/80484832-f3620180-898a-11ea-82b8-11db816b4f9a.png)
![相片列表](https://user-images.githubusercontent.com/7346792/80484836-f52bc500-898a-11ea-93a5-a0fda18353ed.png)
![选中效果](https://user-images.githubusercontent.com/7346792/80484841-f6f58880-898a-11ea-9049-c47b7cb892b3.png)
