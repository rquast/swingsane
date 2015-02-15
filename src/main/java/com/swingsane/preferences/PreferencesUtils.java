package com.swingsane.preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.swingsane.preferences.model.BooleanOption;
import com.swingsane.preferences.model.ButtonOption;
import com.swingsane.preferences.model.Constraints;
import com.swingsane.preferences.model.FixedOption;
import com.swingsane.preferences.model.GroupOption;
import com.swingsane.preferences.model.IntegerOption;
import com.swingsane.preferences.model.Login;
import com.swingsane.preferences.model.Option;
import com.swingsane.preferences.model.OptionsOrderValuePair;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.preferences.model.StringOption;
import com.thoughtworks.xstream.XStream;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public final class PreferencesUtils {

  private static Constraints copy(Constraints c1) {
    if (c1 == null) {
      return null;
    }
    Constraints c2 = new Constraints();
    c2.setFixedList(copyDoubleList(c1.getFixedList()));
    c2.setIntegerList(copyIntegerList(c1.getIntegerList()));
    c2.setMaximumFixed(copyDouble(c1.getMaximumFixed()));
    c2.setMaximumInteger(copyInteger(c1.getMaximumInteger()));
    c2.setMinimumFixed(copyDouble(c1.getMinimumFixed()));
    c2.setMinimumInteger(copyInteger(c1.getMinimumInteger()));
    c2.setQuantumFixed(copyDouble(c1.getQuantumFixed()));
    c2.setQuantumInteger(copyInteger(c1.getQuantumInteger()));
    c2.setStringList(copyStringList(c1.getStringList()));
    return c2;
  }

  public static HashMap<String, Login> copy(HashMap<String, Login> sl1) {
    HashMap<String, Login> sl2 = new HashMap<String, Login>();
    for (String resource : sl1.keySet()) {
      Login login = new Login();
      login.setUsername(sl1.get(resource).getUsername());
      login.setPassword(sl1.get(resource).getPassword());
      sl2.put(resource, login);
    }
    return sl2;
  }

  public static Option copy(Option o1) {

    Option o2 = null;

    OptionsOrderValuePair vp1 = o1.getOptionsOrderValuePair();
    OptionsOrderValuePair vp2 = new OptionsOrderValuePair();

    vp2.setActive(vp1.isActive());
    vp2.setSaneOptionType(vp1.getSaneOptionType());
    vp2.setKey(vp1.getKey());

    switch (vp1.getSaneOptionType()) {

    case STRING:
      StringOption so1 = (StringOption) o1;
      StringOption so2 = new StringOption();
      so2.setName(so1.getName());
      so2.setDescription(so1.getDescription());
      so2.setConstraints(copy(so1.getConstraints()));
      so2.setConstraintType(so1.getConstraintType());
      so2.setValue(so1.getValue());
      so2.setOptionsOrderValuePair(vp2);
      return so2;

    case INTEGER:
      IntegerOption io1 = (IntegerOption) o1;
      IntegerOption io2 = new IntegerOption();
      io2.setName(io1.getName());
      io2.setDescription(io1.getDescription());
      io2.setConstraints(copy(io1.getConstraints()));
      io2.setConstraintType(io1.getConstraintType());
      io2.setValue(io1.getValue());

      // cast to ArrayList - SaneOptions calls newArrayList
      io2.setValueList(copyIntegerList(io1.getValueList()));

      io2.setOptionsOrderValuePair(vp2);
      return io2;

    case BOOLEAN:
      BooleanOption bo1 = (BooleanOption) o1;
      BooleanOption bo2 = new BooleanOption();
      bo2.setName(bo1.getName());
      bo2.setDescription(bo1.getDescription());
      bo2.setConstraints(copy(bo1.getConstraints()));
      bo2.setConstraintType(bo1.getConstraintType());
      bo2.setValue(bo1.getValue());
      bo2.setOptionsOrderValuePair(vp2);
      return bo2;

    case FIXED:
      FixedOption fo1 = (FixedOption) o1;
      FixedOption fo2 = new FixedOption();
      fo2.setName(fo1.getName());
      fo2.setDescription(fo1.getDescription());
      fo2.setConstraints(copy(fo1.getConstraints()));
      fo2.setConstraintType(fo1.getConstraintType());
      fo2.setValue(fo1.getValue());

      // cast to ArrayList - SaneOptions calls newArrayList
      fo2.setValueList(copyDoubleList(fo1.getValueList()));

      fo2.setOptionsOrderValuePair(vp2);
      return fo2;

    case BUTTON:
      ButtonOption bto1 = (ButtonOption) o1;
      ButtonOption bto2 = new ButtonOption();
      copyBooleanOptions(bto1.getBooleanOptions(), bto2.getBooleanOptions());
      copyIntegerOptions(bto1.getIntegerOptions(), bto2.getIntegerOptions());
      copyStringOptions(bto1.getStringOptions(), bto2.getStringOptions());
      copyFixedOptions(bto1.getFixedOptions(), bto2.getFixedOptions());
      copyButtonOptions(bto1.getButtonOptions(), bto2.getButtonOptions());
      copyGroupOptions(bto1.getGroupOptions(), bto2.getGroupOptions());
      bto2.setName(bto1.getName());
      bto2.setDescription(bto1.getDescription());
      bto2.setOptionsOrderValuePair(vp2);
      bto2.setOptionOrdering(null); // TODO!!!!
      return bto2;

    case GROUP:
      GroupOption go1 = (GroupOption) o1;
      GroupOption go2 = new GroupOption();
      copyBooleanOptions(go1.getBooleanOptions(), go2.getBooleanOptions());
      copyIntegerOptions(go1.getIntegerOptions(), go2.getIntegerOptions());
      copyStringOptions(go1.getStringOptions(), go2.getStringOptions());
      copyFixedOptions(go1.getFixedOptions(), go2.getFixedOptions());
      copyGroupOptions(go1.getGroupOptions(), go2.getGroupOptions());
      copyGroupOptions(go1.getGroupOptions(), go2.getGroupOptions());
      go2.setName(go1.getName());
      go2.setDescription(go1.getDescription());
      go2.setOptionsOrderValuePair(vp2);
      go2.setOptionOrdering(null); // TODO!!!!
      return go2;

    default:
      break;

    }

    return o2;

  }

  public static Scanner copy(Scanner s1) {

    Scanner s2 = new Scanner();

    s2.setGuid(s1.getGuid());
    s2.setDescription(s1.getDescription());
    s2.setBatchPrefix(s1.getBatchPrefix());
    s2.setModel(s1.getModel());
    s2.setName(s1.getName());
    s2.setPagesToScan(s1.getPagesToScan());
    s2.setRemoteAddress(s1.getRemoteAddress());
    s2.setRemotePortNumber(s1.getRemotePortNumber());
    s2.setServiceName(s1.getServiceName());
    s2.setType(s1.getType());
    s2.setUsingCustomOptions(s1.isUsingCustomOptions());
    s2.setVendor(s1.getVendor());

    // deep copy the option maps
    copyBooleanOptions(s1.getBooleanOptions(), s2.getBooleanOptions());
    copyIntegerOptions(s1.getIntegerOptions(), s2.getIntegerOptions());
    copyFixedOptions(s1.getFixedOptions(), s2.getFixedOptions());
    copyStringOptions(s1.getStringOptions(), s2.getStringOptions());
    copyButtonOptions(s1.getButtonOptions(), s2.getButtonOptions());
    copyGroupOptions(s1.getGroupOptions(), s2.getGroupOptions());

    // reconnect the circular referencing
    copyOptionOrdering(s1.getOptionOrdering(), s2.getOptionOrdering(), s1.getBooleanOptions(),
        s1.getIntegerOptions(), s1.getFixedOptions(), s1.getStringOptions(), s1.getButtonOptions(),
        s1.getGroupOptions());

    return s2;

  }

  private static void copyBooleanOptions(HashMap<String, BooleanOption> bo1,
      HashMap<String, BooleanOption> bo2) {
    for (String key : bo1.keySet()) {
      bo2.put(key, (BooleanOption) copy(bo1.get(key)));
    }
  }

  private static void copyButtonOptions(HashMap<String, ButtonOption> bo1,
      HashMap<String, ButtonOption> bo2) {
    for (String key : bo1.keySet()) {
      bo2.put(key, (ButtonOption) copy(bo1.get(key)));
    }
  }

  private static Double copyDouble(Double d1) {
    if (d1 == null) {
      return null;
    }
    return new Double(d1.doubleValue());
  }

  public static ArrayList<Double> copyDoubleList(List<Double> l1) {
    if (l1 == null) {
      return null;
    }
    ArrayList<Double> l2 = new ArrayList<Double>(l1.size());
    for (Double i : l1) {
      l2.add(i.doubleValue());
    }
    return l2;
  }

  private static void copyFixedOptions(HashMap<String, FixedOption> fo1,
      HashMap<String, FixedOption> fo2) {
    for (String key : fo1.keySet()) {
      fo2.put(key, (FixedOption) copy(fo1.get(key)));
    }
  }

  private static void copyGroupOptions(HashMap<String, GroupOption> go1,
      HashMap<String, GroupOption> go2) {
    for (String key : go1.keySet()) {
      go2.put(key, (GroupOption) copy(go1.get(key)));
    }
  }

  private static Integer copyInteger(Integer i1) {
    if (i1 == null) {
      return null;
    }
    return new Integer(i1.intValue());
  }

  public static ArrayList<Integer> copyIntegerList(List<Integer> l1) {
    if (l1 == null) {
      return null;
    }
    ArrayList<Integer> l2 = new ArrayList<Integer>(l1.size());
    for (Integer i : l1) {
      l2.add(i.intValue());
    }
    return l2;
  }

  private static void copyIntegerOptions(HashMap<String, IntegerOption> io1,
      HashMap<String, IntegerOption> io2) {
    for (String key : io1.keySet()) {
      io2.put(key, (IntegerOption) copy(io1.get(key)));
    }
  }

  public static void copyOptionOrdering(ArrayList<OptionsOrderValuePair> oo1,
      ArrayList<OptionsOrderValuePair> oo2, HashMap<String, BooleanOption> booleanOptions,
      HashMap<String, IntegerOption> integerOptions, HashMap<String, FixedOption> fixedOptions,
      HashMap<String, StringOption> stringOptions, HashMap<String, ButtonOption> buttonOptions,
      HashMap<String, GroupOption> groupOptions) {

    for (OptionsOrderValuePair vp1 : oo1) {
      switch (vp1.getSaneOptionType()) {
      case STRING:
        oo2.add(stringOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      case INTEGER:
        oo2.add(integerOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      case FIXED:
        oo2.add(fixedOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      case BOOLEAN:
        oo2.add(booleanOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      case BUTTON:
        oo2.add(buttonOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      case GROUP:
        oo2.add(groupOptions.get(vp1.getKey()).getOptionsOrderValuePair());
        break;
      default:
        break;
      }
    }

  }

  private static ArrayList<String> copyStringList(List<String> l1) {
    if (l1 == null) {
      return null;
    }
    ArrayList<String> l2 = new ArrayList<String>(l1.size());
    for (String str : l1) {
      l2.add(str);
    }
    return l2;
  }

  private static void copyStringOptions(HashMap<String, StringOption> so1,
      HashMap<String, StringOption> so2) {
    for (String key : so1.keySet()) {
      so2.put(key, (StringOption) copy(so1.get(key)));
    }
  }

  public static void exportScannerXML(XStream xstream, Scanner scanner, File file)
      throws IOException {
    String rootNodeName = "swingsane";
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
        "UTF-8"));
    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    ObjectOutputStream oos;
    oos = xstream.createObjectOutputStream(out, rootNodeName);
    oos.writeObject(scanner);
    oos.close();
  }

  public static Scanner importScannerXML(XStream xstream, File file) throws IOException,
  ClassNotFoundException {
    ObjectInputStream s = null;
    FileInputStream fis = null;
    Scanner scanner = null;
    try {
      fis = new FileInputStream(file);
      s = xstream.createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
      scanner = ((Scanner) s.readObject());
      restoreCircularReferences(scanner);
      return scanner;
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (s != null) {
        s.close();
      }
      if (fis != null) {
        fis.close();
      }
    }
  }

  static void restoreCircularReferences(Scanner scanner) {
    ArrayList<OptionsOrderValuePair> ordering = new ArrayList<OptionsOrderValuePair>();
    for (OptionsOrderValuePair vp : scanner.getOptionOrdering()) {
      switch (vp.getSaneOptionType()) {
      case STRING:
        ordering.add(scanner.getStringOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      case INTEGER:
        ordering.add(scanner.getIntegerOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      case FIXED:
        ordering.add(scanner.getFixedOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      case BOOLEAN:
        ordering.add(scanner.getBooleanOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      case BUTTON:
        ordering.add(scanner.getButtonOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      case GROUP:
        ordering.add(scanner.getGroupOptions().get(vp.getKey()).getOptionsOrderValuePair());
        break;
      default:
        break;
      }
    }
    scanner.setOptionOrdering(ordering);
  }

  public static void update(HashMap<String, Login> sl1, HashMap<String, Login> sl2) {
    sl1.clear();
    sl1.putAll(sl2);
  }

  public static void update(Scanner s1, Scanner s2) {
    s1.setDescription(s2.getDescription());
    s1.setBatchPrefix(s2.getBatchPrefix());
    s1.setBooleanOptions(s2.getBooleanOptions());
    s1.setButtonOptions(s2.getButtonOptions());
    s1.setFixedOptions(s2.getFixedOptions());
    s1.setGroupOptions(s2.getGroupOptions());
    s1.setIntegerOptions(s2.getIntegerOptions());
    s1.setModel(s2.getModel());
    s1.setName(s2.getName());
    s1.setOptionOrdering(s2.getOptionOrdering());
    s1.setPagesToScan(s2.getPagesToScan());
    s1.setRemoteAddress(s2.getRemoteAddress());
    s1.setRemotePortNumber(s2.getRemotePortNumber());
    s1.setServiceName(s2.getServiceName());
    s1.setStringOptions(s2.getStringOptions());
    s1.setType(s2.getType());
    s1.setUsingCustomOptions(s2.isUsingCustomOptions());
    s1.setVendor(s2.getVendor());
  }

  private PreferencesUtils() {
  }

}
