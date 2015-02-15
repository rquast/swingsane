package com.swingsane.preferences;

import com.swingsane.preferences.model.ApplicationPreferences;
import com.swingsane.preferences.model.BooleanOption;
import com.swingsane.preferences.model.ButtonOption;
import com.swingsane.preferences.model.GroupOption;
import com.swingsane.preferences.model.IntegerOption;
import com.swingsane.preferences.model.Login;
import com.swingsane.preferences.model.OptionsOrderValuePair;
import com.swingsane.preferences.model.SaneServiceIdentity;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.preferences.model.StringOption;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public final class XStreamUtility {

  public static XStream getXStream() {
    final XStream xstream = loadXStream();
    processAnnotations(xstream);
    return xstream;
  }

  private static XStream loadXStream() {
    XStream xstream = new XStream(new DomDriver("UTF-8")) {
      @Override
      protected MapperWrapper wrapMapper(MapperWrapper next) {
        return new MapperWrapper(next) {
          @Override
          public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn,
              String fieldName) {
            if (definedIn == Object.class) {
              return false;
            }
            return super.shouldSerializeMember(definedIn, fieldName);
          }
        };
      }
    };
    xstream.setMode(XStream.NO_REFERENCES);
    return xstream;
  }

  private static void processAnnotations(XStream xstream) {
    xstream.processAnnotations(ApplicationPreferences.class);
    xstream.processAnnotations(Login.class);
    xstream.processAnnotations(SaneServiceIdentity.class);
    xstream.processAnnotations(Scanner.class);
    xstream.processAnnotations(BooleanOption.class);
    xstream.processAnnotations(IntegerOption.class);
    xstream.processAnnotations(StringOption.class);
    xstream.processAnnotations(ButtonOption.class);
    xstream.processAnnotations(GroupOption.class);
    xstream.processAnnotations(OptionsOrderValuePair.class);
  }

  private XStreamUtility() {
  }

}
