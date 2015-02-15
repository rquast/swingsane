package com.swingsane.business.scanning;

import java.util.EventListener;

public interface ScanEventListener extends EventListener {

  void eventOccurred(ScanEvent sevt);

}
