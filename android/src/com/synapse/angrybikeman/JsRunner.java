package com.synapse.angrybikeman;
import android.webkit.WebView;
public class JsRunner implements Runnable {
  private final WebView w; private final String js;
  public JsRunner(WebView w,String js){ this.w=w; this.js=js; }
  public void run(){ if(w!=null) w.evaluateJavascript(js,null); }
}
