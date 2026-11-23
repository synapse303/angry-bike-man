package com.synapse.angrybikeman;
import android.speech.tts.UtteranceProgressListener;
public class VoiceListener extends UtteranceProgressListener {
  private final MainActivity a;
  public VoiceListener(MainActivity a){ this.a=a; }
  @Override public void onStart(String id){}
  @Override public void onError(String id){}
  @Override public void onDone(String id){ if(id!=null&&id.startsWith("s")) a.deliver(id); }
}
