package com.synapse.angrybikeman;
import android.webkit.JavascriptInterface;
public class Bridge {
  private final MainActivity a;
  public Bridge(MainActivity a){ this.a=a; }
  @JavascriptInterface public void speak(String t){ a.speakNow(t); }
  @JavascriptInterface public void stop(){ a.stopNow(); }
}
