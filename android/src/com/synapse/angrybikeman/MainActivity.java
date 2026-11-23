package com.synapse.angrybikeman;
import android.app.Activity;import android.os.Bundle;import android.view.*;import android.webkit.*;import android.speech.tts.TextToSpeech;import java.util.Locale;
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
  private WebView web; private TextToSpeech tts; private volatile boolean ready=false;
  @Override protected void onCreate(Bundle b){ super.onCreate(b);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);
    web=new WebView(this); setContentView(web);
    WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
    web.setWebChromeClient(new WebChromeClient()); web.addJavascriptInterface(new Bridge(this),"Android");
    web.loadUrl("file:///android_asset/index.html"); tts=new TextToSpeech(this,this); hideUi(); }
  public void onInit(int st){ if(st==TextToSpeech.SUCCESS){ try{tts.setLanguage(Locale.UK);}catch(Exception e){} tts.setPitch(0.7f); tts.setSpeechRate(1.25f); ready=true; } }
  public void speakNow(String t){ if(ready) tts.speak(t,TextToSpeech.QUEUE_FLUSH,null,"abm"); }
  public void stopNow(){ if(ready) tts.stop(); }
  @SuppressWarnings("deprecation") private void hideUi(){ getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); }
  @Override public void onWindowFocusChanged(boolean f){ super.onWindowFocusChanged(f); if(f) hideUi(); }
  @Override protected void onPause(){ web.evaluateJavascript("window.onAppPause&&onAppPause()",null); web.onPause(); super.onPause(); }
  @Override protected void onResume(){ super.onResume(); web.onResume(); web.evaluateJavascript("window.onAppResume&&onAppResume()",null); hideUi(); }
  @Override protected void onDestroy(){ if(tts!=null){tts.stop();tts.shutdown();} web.destroy(); super.onDestroy(); }
}
