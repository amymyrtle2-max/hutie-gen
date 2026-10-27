package com.hutie.gen;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int REQ_PICK_APK = 1001;
    private static final int REQ_STORAGE = 1002;

    private LinearLayout cardInfo;
    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvVersion;
    private TextView tvSize;
    private EditText etTitle;
    private EditText etOpening;
    private EditText etIntro;
    private EditText etLink1;
    private EditText etLink2;

    private String appName;
    private String versionName;
    private String sizeText;
    private int appCategory = -1;
    private Bitmap iconBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardInfo = findViewById(R.id.cardInfo);
        ivIcon = findViewById(R.id.ivIcon);
        tvName = findViewById(R.id.tvName);
        tvVersion = findViewById(R.id.tvVersion);
        tvSize = findViewById(R.id.tvSize);
        etTitle = findViewById(R.id.etTitle);
        etOpening = findViewById(R.id.etOpening);
        etIntro = findViewById(R.id.etIntro);
        etLink1 = findViewById(R.id.etLink1);
        etLink2 = findViewById(R.id.etLink2);

        findViewById(R.id.btnPick).setOnClickListener(v -> pickApk());
        findViewById(R.id.btnRegen).setOnClickListener(v -> regenerate());
        findViewById(R.id.btnIcon).setOnClickListener(v -> exportIcon());
        findViewById(R.id.btnCopy).setOnClickListener(v -> copyPost());
        findViewById(R.id.btnShare).setOnClickListener(v -> sharePost());
    }

    /** 打开系统文件选择器挑选 APK */
    private void pickApk() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQ_PICK_APK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_APK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;
            try {
                String path = copyToCache(uri);
                parseApk(path);
            } catch (Exception e) {
                toast("解析失败：" + e.getMessage());
            }
        }
    }

    /** 把选中的文件复制到应用缓存目录，便于 PackageManager 解析 */
    private String copyToCache(Uri uri) throws Exception {
        File out = new File(getCacheDir(), "picked.apk");
        InputStream in = getContentResolver().openInputStream(uri);
        if (in == null) throw new Exception("无法读取文件");
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) fos.write(buf, 0, n);
        fos.close();
        in.close();
        return out.getAbsolutePath();
    }

    /** 解析 APK：应用名、版本号、包大小、图标、分类 */
    private void parseApk(String path) {
        PackageManager pm = getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
        if (pi == null) {
            toast("这不是有效的 APK 安装包");
            return;
        }
        ApplicationInfo ai = pi.applicationInfo;
        ai.sourceDir = path;
        ai.publicSourceDir = path;

        CharSequence label = ai.loadLabel(pm);
        appName = label == null ? "未知应用" : label.toString();
        versionName = pi.versionName == null ? "1.0" : pi.versionName;
        sizeText = formatSize(new File(path).length());
        appCategory = ai.category;

        Drawable icon = ai.loadIcon(pm);
        iconBitmap = drawableToBitmap(icon);
        ivIcon.setImageDrawable(icon);

        tvName.setText(appName);
        tvVersion.setText("版本：" + versionName);
        tvSize.setText("大小：" + sizeText);
        cardInfo.setVisibility(View.VISIBLE);

        regenerate();
        toast("解析成功，帖子已生成！");
    }

    /** 随机换一套标题/话术/介绍 */
    private void regenerate() {
        if (appName == null) {
            toast("请先选择 APK 安装包");
            return;
        }
        etTitle.setText(PostGenerator.randomTitle(appName, versionName));
        etOpening.setText(PostGenerator.randomOpening(appName, versionName, appCategory));
        etIntro.setText(PostGenerator.randomIntro(appName, versionName, sizeText, appCategory));
    }

    private String buildFullPost() {
        return PostGenerator.buildFullPost(
                etTitle.getText().toString().trim(),
                etOpening.getText().toString().trim(),
                appName, versionName, sizeText,
                etIntro.getText().toString().trim(),
                etLink1.getText().toString().trim(),
                etLink2.getText().toString().trim());
    }

    private void copyPost() {
        if (appName == null) {
            toast("请先选择 APK 安装包");
            return;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("post", buildFullPost()));
        toast("已复制完整帖子，去葫芦侠粘贴发布吧！");
    }

    private void sharePost() {
        if (appName == null) {
            toast("请先选择 APK 安装包");
            return;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, buildFullPost());
        startActivity(Intent.createChooser(i, "分享帖子"));
    }

    /** 导出应用图标到相册（Pictures/葫帖精灵） */
    private void exportIcon() {
        if (iconBitmap == null || appName == null) {
            toast("请先选择 APK 安装包");
            return;
        }
        if (Build.VERSION.SDK_INT <= 28
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
            return;
        }
        saveIcon();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveIcon();
        }
    }

    private void saveIcon() {
        String fileName = appName.replaceAll("[\\\\/:*?\"<>|]", "_") + "_icon.png";
        try {
            OutputStream os;
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/葫帖精灵");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new Exception("无法创建图片文件");
                os = getContentResolver().openOutputStream(uri);
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "葫帖精灵");
                if (!dir.exists()) dir.mkdirs();
                os = new FileOutputStream(new File(dir, fileName));
            }
            if (os == null) throw new Exception("无法写入文件");
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            toast("图标已保存到相册（Pictures/葫帖精灵）");
        } catch (Exception e) {
            toast("保存失败：" + e.getMessage());
        }
    }

    static String formatSize(long bytes) {
        double mb = bytes / 1024.0 / 1024.0;
        if (mb >= 1) return String.format(Locale.CHINA, "%.0fM", mb);
        return String.format(Locale.CHINA, "%.0fKB", bytes / 1024.0);
    }

    static Bitmap drawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable) return ((BitmapDrawable) d).getBitmap();
        int w = Math.max(1, d.getIntrinsicWidth());
        int h = Math.max(1, d.getIntrinsicHeight());
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, w, h);
        d.draw(c);
        return b;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
