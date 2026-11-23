package com.synapse.angrybikeman;
import android.app.Activity;import android.os.Bundle;import android.view.*;import android.webkit.*;
import android.speech.tts.TextToSpeech;import android.speech.tts.UtteranceProgressListener;import android.util.Base64;
import java.io.File;import java.io.FileInputStream;import java.util.ArrayList;import java.util.Locale;
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
  private WebView web; private TextToSpeech tts; private volatile boolean ready=false;
  private final ArrayList<String[]> pending=new ArrayList<String[]>();
  @Override protected void onCreate(Bundle b){ super.onCreate(b);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);
    web=new WebView(this); setContentView(web);
    WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
    web.setWebChromeClient(new WebChromeClient()); web.addJavascriptInterface(new Bridge(this),"Android");
    web.loadUrl("file:///android_asset/index.html"); tts=new TextToSpeech(this,this); hideUi(); }
  public void onInit(int st){
    if(st!=TextToSpeech.SUCCESS) return;
    try{ tts.setLanguage(Locale.UK); }catch(Exception e){}
    tts.setPitch(0.62f); tts.setSpeechRate(1.12f);  // deep and fast; the WebView adds the angry FX
    tts.setOnUtteranceProgressListener(new VoiceListener(this));
    ready=true;
    synchronized(pending){ for(String[] p:pending) synthNow(p[0],p[1]); pending.clear(); }
  }
  private File clipFile(String id){ return new File(getCacheDir(),"abm_"+id+".wav"); }
  public void synthNow(String id,String text){
    if(!id.matches("s[0-9]+")) return;
    if(!ready){ synchronized(pending){ pending.add(new String[]{id,text}); } return; }
    Bundle p=new Bundle(); p.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,id);
    tts.synthesizeToFile(text,p,clipFile(id),id);
  }
  void deliver(final String id){
    try{ File f=clipFile(id); byte[] buf=new byte[(int)f.length()]; FileInputStream in=new FileInputStream(f); int off=0,n; while(off<buf.length&&(n=in.read(buf,off,buf.length-off))>0) off+=n; in.close(); f.delete();
      final String js="window.onVoiceClip&&onVoiceClip('"+id+"','"+Base64.encodeToString(buf,Base64.NO_WRAP)+"')";
      runOnUiThread(new JsRunner(web,js));
    }catch(Exception e){}
  }
  public void speakNow(String t){ if(ready) tts.speak(t,TextToSpeech.QUEUE_FLUSH,null,"live"); }
  public void stopNow(){ if(ready) tts.stop(); }
  @SuppressWarnings("deprecation") private void hideUi(){ getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); }
  @Override public void onWindowFocusChanged(boolean f){ super.onWindowFocusChanged(f); if(f) hideUi(); }
  @Override protected void onPause(){ web.evaluateJavascript("window.onAppPause&&onAppPause()",null); web.onPause(); super.onPause(); }
  @Override protected void onResume(){ super.onResume(); web.onResume(); web.evaluateJavascript("window.onAppResume&&onAppResume()",null); hideUi(); }
  @Override protected void onDestroy(){ if(tts!=null){tts.stop();tts.shutdown();} web.destroy(); super.onDestroy(); }
}
