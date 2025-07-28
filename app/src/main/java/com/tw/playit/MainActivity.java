package com.tw.playit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup navigation bar insets
        setupNavigationBarInsets();
        
        // Custom header is now included in layout - no toolbar setup needed
        setupHeaderButtons();

        // Setup WebView
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Add some bottom padding to YouTube content to avoid footer hiding
                injectBottomPaddingCSS();
            }
        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.loadUrl("https://www.youtube.com");

        // Handle back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    private void setupHeaderButtons() {
        // Setup Portfolio Button
        LinearLayout portfolioButton = findViewById(R.id.portfolio_button);
        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPortfolio();
            }
        });

        // Setup LinkedIn Button
        LinearLayout linkedinButton = findViewById(R.id.linkedin_button);
        linkedinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedIn();
            }
        });

        // Setup GitHub Button
        LinearLayout githubButton = findViewById(R.id.github_button);
        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGitHub();
            }
        });
    }

    private void openPortfolio() {
        String portfolioUrl = getString(R.string.portfolio_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(portfolioUrl));
        startActivity(intent);
    }

    private void openLinkedIn() {
        String linkedinUrl = getString(R.string.linkedin_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
        startActivity(intent);
    }

    private void openGitHub() {
        String githubUrl = getString(R.string.github_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    private void injectBottomPaddingCSS() {
        // Get navigation bar height dynamically
        int navBarHeight = getNavigationBarHeight();
        int paddingBottom = Math.max(navBarHeight + 20, 80); // At least 80px padding
        
        // Dynamic CSS injection based on actual navigation bar height
        String css = "javascript:(function() {" +
            "var style = document.createElement('style');" +
            "style.type = 'text/css';" +
            "style.innerHTML = '" +
                "body { padding-bottom: " + paddingBottom + "px !important; } " +
                "#player-container-outer { margin-bottom: " + navBarHeight + "px !important; } " +
                "ytd-mini-guide-renderer { bottom: " + navBarHeight + "px !important; } " +
                "#bottom-sheet { padding-bottom: " + paddingBottom + "px !important; } " +
                "ytm-app { padding-bottom: " + paddingBottom + "px !important; } " +
                ".mobile-topbar-header { position: sticky !important; top: 0 !important; z-index: 1000 !important; } " +
            "';" +
            "document.head.appendChild(style);" +
            "})()";
        
        webView.evaluateJavascript(css, null);
    }
    
    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 48; // Default fallback height in dp converted to px
    }

    private void setupNavigationBarInsets() {
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Get navigation bar height
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            
            // Apply margin to WebView to avoid overlap with navigation bar
            if (webView != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                params.bottomMargin = navigationBarHeight;
                webView.setLayoutParams(params);
            } else {
                // If WebView is not ready yet, schedule it for later
                rootView.post(() -> {
                    webView = findViewById(R.id.webView);
                    if (webView != null) {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                        params.bottomMargin = navigationBarHeight;
                        webView.setLayoutParams(params);
                    }
                });
            }
            
            return insets;
        });
    }
}
