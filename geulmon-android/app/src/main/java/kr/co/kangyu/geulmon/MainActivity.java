package kr.co.kangyu.geulmon;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.webkit.WebViewAssetLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 글몬 — 게임 본체(assets/www/index.html)를 담는 WebView 셸.
 * 카메라·위치 권한을 앱 차원에서 받아 WebView에 전달한다.
 */
public class MainActivity extends Activity {

    private static final int REQ_PERMS = 1001;
    private static final String START_URL = "https://appassets.androidplatform.net/assets/www/index.html";

    private WebView web;
    private PermissionRequest pendingMediaRequest;
    private GeolocationPermissions.Callback pendingGeoCallback;
    private String pendingGeoOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(Color.parseColor("#0f1519"));
        getWindow().setNavigationBarColor(Color.parseColor("#1a232b"));

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#0f1519"));
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);

        // 시스템 바(상태바·내비게이션바) 영역만큼 여백을 줘서 콘텐츠가 가려지지 않게 함 (Android 15 edge-to-edge 대응)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return WindowInsets.CONSUMED;
            });
        }

        setupWebView();
        requestNeededPermissions();
        web.loadUrl(START_URL);
    }

    private void setupWebView() {
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setGeolocationEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setTextZoom(100);
        web.setBackgroundColor(Color.parseColor("#0f1519"));
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true);

        // assets/ 를 https 오리진으로 서빙 → getUserMedia·위치·localStorage가 안정적으로 동작
        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 앱 내부 페이지만 WebView에서 열고, 그 외 링크는 막는다
                String host = request.getUrl().getHost();
                return host == null || !host.equals("appassets.androidplatform.net");
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (hasPerm(Manifest.permission.CAMERA)) {
                    grantMedia(request);
                } else {
                    pendingMediaRequest = request;
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_PERMS);
                }
            }
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (hasPerm(Manifest.permission.ACCESS_FINE_LOCATION) || hasPerm(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    callback.invoke(origin, true, false);
                } else {
                    pendingGeoCallback = callback;
                    pendingGeoOrigin = origin;
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_PERMS);
                }
            }
        });
    }

    private void grantMedia(PermissionRequest request) {
        List<String> granted = new ArrayList<>();
        for (String r : request.getResources()) {
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(r)) granted.add(r);
        }
        if (granted.isEmpty()) request.deny();
        else request.grant(granted.toArray(new String[0]));
    }

    private boolean hasPerm(String p) {
        return checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNeededPermissions() {
        List<String> need = new ArrayList<>();
        for (String p : new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}) {
            if (!hasPerm(p)) need.add(p);
        }
        if (!need.isEmpty()) requestPermissions(need.toArray(new String[0]), REQ_PERMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (pendingMediaRequest != null) {
            if (hasPerm(Manifest.permission.CAMERA)) grantMedia(pendingMediaRequest); else pendingMediaRequest.deny();
            pendingMediaRequest = null;
        }
        if (pendingGeoCallback != null) {
            boolean ok = hasPerm(Manifest.permission.ACCESS_FINE_LOCATION) || hasPerm(Manifest.permission.ACCESS_COARSE_LOCATION);
            pendingGeoCallback.invoke(pendingGeoOrigin, ok, false);
            pendingGeoCallback = null;
        }
    }

    @Override
    public void onBackPressed() {
        // 포획 화면이면 탐색으로, 시트가 열려 있으면 닫고, 그 외에는 앱 종료
        web.evaluateJavascript(
                "(function(){ try { return (typeof androidBack === 'function' && androidBack()) ? 'handled' : 'exit'; } catch (e) { return 'exit'; } })()",
                value -> { if (value == null || !value.contains("handled")) finish(); });
    }

    @Override
    protected void onPause() { super.onPause(); web.onPause(); }

    @Override
    protected void onResume() { super.onResume(); web.onResume(); }

    @Override
    protected void onDestroy() { web.destroy(); super.onDestroy(); }
}
