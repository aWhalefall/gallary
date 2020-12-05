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

  dependencies { implementation 'com.github.aWhalefall:gallary:v1.0.1' }




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

        builder = new GalleryConfig.Builder()
              .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
              .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
              .provider("com.whalefail.gallary.fileProvider")   // provider (必填)
              .pathList(new ArrayList<String>())                 // 记录已选的图片
              .multiSelect(true, 1) // 是否多选   默认：false
              .crop(true)                      // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
              .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
              .isShowCamera(true)                     // 是否显示相机按钮  默认：false
              .filePath("/Gallery/Pictures")          // 图片存放路径
              .layoutStyle(0) // 0 显示多选样式 1 显示数字样式
              .setDisplayCol(3);  //默认显示列数
              .build();
    }

  /**
   * 默认选择样式
   */
    public void selectPhoto(View view) {
        GalleryPick.getInstance().setGalleryConfig(config).open(this);
    }

    /**
    * 设置数字选择样式，设置显示4列
    */
     public void selectPhoto1(View view) {
            builder.layoutStyle(1).setDisplayCol(4);
            GalleryPick.getInstance().setGalleryConfig(builder.build()).open(this);
        }
}



选中图片回调

    IHandlerCallBack iHandlerCallBack = new SimpleHandlerCallBack() {

        @Override
        public void onSuccess(List<String> photoList) {
//            text.setText("");
//            //返回选择图片路径  Android Q 以上包含Android   Q 返回的是 Uri
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < photoList.size(); i++) {
//                stringBuilder.append(photoList.get(i)).append("\n");
//            }
//            text.setText(stringBuilder.toString());
        }

        @Override
        public void onBoSuccess(List<PhotoInfo> photoList) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < photoList.size(); i++) {
                PhotoInfo photoInfo = photoList.get(i);
                stringBuilder.append("路径 =" + photoInfo.path + "\n width=" + photoInfo.with + " height=" + photoInfo.height).append("\n");
            }
            text.setText(stringBuilder.toString());
        }

    };



![1](https://user-images.githubusercontent.com/7346792/101244958-9d391600-3744-11eb-84ae-82041a9fead0.png)
![2](https://user-images.githubusercontent.com/7346792/101244963-a2966080-3744-11eb-8309-dc330ad1e6c4.jpeg)
![3](https://user-images.githubusercontent.com/7346792/101244965-a4602400-3744-11eb-8bf0-d8b95e3dd859.png)
![4](https://user-images.githubusercontent.com/7346792/101244966-a5915100-3744-11eb-849f-92ee11f53919.jpeg)
![5](https://user-images.githubusercontent.com/7346792/101244967-a75b1480-3744-11eb-92b2-7703f3737f41.jpeg)

