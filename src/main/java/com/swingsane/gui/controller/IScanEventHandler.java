package com.swingsane.gui.controller;

import com.swingsane.business.scanning.ScanEvent;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface IScanEventHandler {

  void scanPerformed(ScanEvent scanEvent);

}
