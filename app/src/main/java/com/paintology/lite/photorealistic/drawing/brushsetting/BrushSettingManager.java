package com.paintology.lite.photorealistic.drawing.brushsetting;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

public class BrushSettingManager {

    private String TAG;
    private final int[] defaultOrder;
    public ArrayList<Integer> mBrushOrderList;
    public ArrayList<BrushSetting> mBrushSettingList;

    public BrushSettingManager(Context pContext) {
        mBrushSettingList = new ArrayList();
        mBrushOrderList = new ArrayList();
        int[] arrayOfInt = {112, 80, 81, 55, 272, 256, 64, 257, 96, 39, 56, 45, 46, 47, 48, 54, -1};
        defaultOrder = arrayOfInt;
        TAG = "BrushSetting";
        MyDbgLog(TAG, "restore");
        restoreSetting(pContext);
        restoreBrushOrder(pContext);
    }

    private void MyDbgLog(String pString1, String pString2) {
    }

    private void buildDefaultOrder() {
        String str = TAG;
        MyDbgLog(str, "build default order");
        int i = 0;
        while (defaultOrder[i] != -1) {
            int j = defaultOrder[i];
            Integer lInteger = new Integer(j);
            mBrushOrderList.add(lInteger);
            i += 1;
        }
    }

    private boolean notExist(int pInt) {
        Iterator lIterator = mBrushOrderList.iterator();

        while (lIterator.hasNext()) {
            if (((Integer) lIterator.next()).intValue() == pInt)
                return false;
        }

        return true;
    }

    private void sanityCheckOrderList() {
        int i = 0;
        while (defaultOrder[i] != -1) {
            int j = defaultOrder[i];
            if (notExist(j)) {
                int k = defaultOrder[i];
                Integer lInteger = new Integer(k);
                mBrushOrderList.add(lInteger);
//                Log.e("TAG", "sanityCheckOrderList Goto Add " + j + " " + k);
            } else {
                Log.e("TAG", "sanityCheckOrderList Goto Else " + j);
            }
            i += 1;
        }

    }

    private String writeOrderXml() {
        XmlSerializer lXmlSerializer1 = Xml.newSerializer();
        StringWriter lStringWriter = new StringWriter();
        try {
            lXmlSerializer1.setOutput(lStringWriter);
            lXmlSerializer1.startDocument("UTF-8", true);
            XmlSerializer lXmlSerializer2 = lXmlSerializer1.startTag("", "BrushSetting");
            String str1 = String.valueOf(mBrushOrderList.size());
            XmlSerializer lXmlSerializer3 = lXmlSerializer1.attribute("", "number", str1);
            Iterator lIterator = mBrushOrderList.iterator();
            {
                if (!lIterator.hasNext()) {
                    XmlSerializer lXmlSerializer4 = lXmlSerializer1.endTag("", "BrushSetting");
                    lXmlSerializer1.endDocument();
                    return lStringWriter.toString();
                }
                Integer lInteger = (Integer) lIterator.next();
                XmlSerializer lXmlSerializer5 = lXmlSerializer1.startTag("", "order");
                XmlSerializer lXmlSerializer6 = lXmlSerializer1.startTag("", "brush");
                String str2 = String.valueOf(lInteger.intValue());
                XmlSerializer lXmlSerializer7 = lXmlSerializer1.text(str2);
                XmlSerializer lXmlSerializer8 = lXmlSerializer1.endTag("", "brush");
                XmlSerializer lXmlSerializer9 = lXmlSerializer1.endTag("", "order");

                return lStringWriter.toString();
            }
        } catch (Exception lException) {
            return null;
        }
    }

    private String writeXml(ArrayList<BrushSetting> pArrayList) {
        XmlSerializer lXmlSerializer1 = Xml.newSerializer();
        StringWriter lStringWriter = new StringWriter();
        try {
            lXmlSerializer1.setOutput(lStringWriter);
            lXmlSerializer1.startDocument("UTF-8", true);
            XmlSerializer lXmlSerializer2 = lXmlSerializer1.startTag("", "BrushSetting");
            String str1 = String.valueOf(mBrushSettingList.size());
            XmlSerializer lXmlSerializer3 = lXmlSerializer1.attribute("", "number", str1);
            Iterator lIterator = mBrushSettingList.iterator();
//      while (true)
            {
                if (!lIterator.hasNext()) {
                    XmlSerializer lXmlSerializer4 = lXmlSerializer1.endTag("", "BrushSetting");
                    lXmlSerializer1.endDocument();
                    return lStringWriter.toString();
                }
                BrushSetting lBrushSetting = (BrushSetting) lIterator.next();
                XmlSerializer lXmlSerializer5 = lXmlSerializer1.startTag("", "Setting");
                XmlSerializer lXmlSerializer6 = lXmlSerializer1.startTag("", "BrushType");
                String str2 = String.valueOf(lBrushSetting.brushStyle);
                XmlSerializer lXmlSerializer7 = lXmlSerializer1.text(str2);
                XmlSerializer lXmlSerializer8 = lXmlSerializer1.endTag("", "BrushType");
                XmlSerializer lXmlSerializer9 = lXmlSerializer1.startTag("", "Size");
                String str3 = String.valueOf(lBrushSetting.size);
                XmlSerializer lXmlSerializer10 = lXmlSerializer1.text(str3);
                XmlSerializer lXmlSerializer11 = lXmlSerializer1.endTag("", "Size");
                XmlSerializer lXmlSerializer12 = lXmlSerializer1.startTag("", "Flow");
                String str4 = String.valueOf(lBrushSetting.flow);
                XmlSerializer lXmlSerializer13 = lXmlSerializer1.text(str4);
                XmlSerializer lXmlSerializer14 = lXmlSerializer1.endTag("", "Flow");
                XmlSerializer lXmlSerializer15 = lXmlSerializer1.startTag("", "Opacity");
                String str5 = String.valueOf(lBrushSetting.opacity);
                XmlSerializer lXmlSerializer16 = lXmlSerializer1.text(str5);
                XmlSerializer lXmlSerializer17 = lXmlSerializer1.endTag("", "Opacity");
                XmlSerializer lXmlSerializer18 = lXmlSerializer1.endTag("", "Setting");

                return lStringWriter.toString();
            }
        } catch (Exception lException) {
            return null;
        }
    }

    public BrushSetting addSetting(int pInt) {
        BrushSetting lBrushSetting = new BrushSetting();
        lBrushSetting.brushStyle = pInt;
        boolean bool = mBrushSettingList.add(lBrushSetting);
        return lBrushSetting;
    }

    public void archive(Context pContext) {
        String str = TAG;
        MyDbgLog(str, "archive");
        boolean bool1 = archiveSetting(pContext);
        boolean bool2 = archiveBrushOrder(pContext);
    }

    public boolean archiveBrushOrder(Context pContext) {
        int i = 0;
        try {
            FileOutputStream lFileOutputStream = pContext.openFileOutput("brushorder.xml", 0);
            OutputStreamWriter lOutputStreamWriter = new OutputStreamWriter(lFileOutputStream);
            String str = writeOrderXml();
            lOutputStreamWriter.write(str);
            lOutputStreamWriter.close();
            lFileOutputStream.close();
            i = 1;
            return true;
        } catch (FileNotFoundException lFileNotFoundException) {
            lFileNotFoundException.printStackTrace();
            return false;
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
            return false;
        }
    }

    public boolean archiveSetting(Context pContext) {
        int i = 0;
        try {
            FileOutputStream lFileOutputStream = pContext.openFileOutput("brushsetting.xml", 0);
            OutputStreamWriter lOutputStreamWriter = new OutputStreamWriter(lFileOutputStream);
            ArrayList lArrayList = mBrushSettingList;
            String str = writeXml(lArrayList);
            lOutputStreamWriter.write(str);
            lOutputStreamWriter.close();
            lFileOutputStream.close();
            return true;
        } catch (FileNotFoundException lFileNotFoundException) {
            lFileNotFoundException.printStackTrace();
            return false;
        } catch (IOException lIOException) {
            lIOException.printStackTrace();
            return false;
        }
    }

    void dump() {
        Iterator lIterator = mBrushSettingList.iterator();
        {
            if (!lIterator.hasNext())
                return;
            BrushSetting lBrushSetting = (BrushSetting) lIterator.next();
            String str1 = String.valueOf("");
            StringBuilder lStringBuilder1 = new StringBuilder(str1).append("type: ");
            int i = lBrushSetting.brushStyle;
            StringBuilder lStringBuilder2 = lStringBuilder1.append(i).append(" alpha: ");
            int j = lBrushSetting.opacity;
            StringBuilder lStringBuilder3 = lStringBuilder2.append(j).append(" size: ");
            float f = lBrushSetting.size;
            StringBuilder lStringBuilder4 = lStringBuilder3.append(f).append(" flow ");
        }
    }

    public int getBrushStyleAtOrder(int pInt) {
        try {
            return ((Integer) mBrushOrderList.get(pInt)).intValue();
        } catch (Exception e) {

        }
        return 0;
    }

    public BrushSetting getSetting(int pInt) {
        Iterator lIterator = mBrushSettingList.iterator();
        BrushSetting lBrushSetting;
//        Log.e("TAG", "setSelectedBrush called getSetting " + pInt + " mBrushSettingList size " + mBrushSettingList.size());
        while (lIterator.hasNext()) {
            lBrushSetting = (BrushSetting) lIterator.next();
            if (lBrushSetting.brushStyle == pInt) {
                Log.e("TAG", "setSelectedBrush lBrushSetting return");
                return lBrushSetting;
            }
        }
        return null;
    }

    public void moveBrushToTop(int pInt) {
        if (pInt == 112)
            return;

        Iterator lIterator = mBrushOrderList.iterator();

        while (lIterator.hasNext()) {
            Integer lInteger1 = (Integer) lIterator.next();
            if (lInteger1.intValue() == pInt) {
                mBrushOrderList.remove(lInteger1);
                Integer lInteger2 = new Integer(pInt);
                mBrushOrderList.add(1, lInteger2);
                break;
            }
        }
    }

    public void onDestroy() {
        mBrushSettingList.clear();
        mBrushSettingList = null;
        mBrushOrderList.clear();
        mBrushOrderList = null;
    }

    public void restoreBrushOrder(Context pContext) {
        DocumentBuilderFactory lDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            Context lContext = pContext;
            String str1 = "brushorder.xml";
            FileInputStream lFileInputStream = lContext.openFileInput(str1);
            Element lElement = lDocumentBuilderFactory.newDocumentBuilder().parse(lFileInputStream).getDocumentElement();
            String str2 = "order";
            NodeList lNodeList1 = lElement.getElementsByTagName(str2);

            for (int i = 0; i < lNodeList1.getLength(); i++) {
                NodeList lNodeList2 = lNodeList1.item(i).getChildNodes();

                for (int k = 0; k < lNodeList2.getLength(); k++) {
                    Node lNode = lNodeList2.item(k);
                    String str6 = lNode.getNodeName();
                    String str7 = lNode.getFirstChild().getNodeValue();
                    String str8 = "brush";
                    if (str6.equalsIgnoreCase(str8)) {
                        int n = Integer.parseInt(str7);
                        Integer lInteger = new Integer(n);
                        boolean bool = mBrushOrderList.add(lInteger);
                    }
                }
            }

            lFileInputStream.close();
            buildDefaultOrder();
        } catch (FileNotFoundException lFileNotFoundException) {
            MyDbgLog(TAG, "sanity check");
            sanityCheckOrderList();
        } catch (Exception lException) {
            lException.printStackTrace();
            MyDbgLog(TAG, "sanity check");
            sanityCheckOrderList();
        } finally {
            MyDbgLog(TAG, "sanity check");
            sanityCheckOrderList();
        }
    }

    public void restoreSetting(Context pContext) {
        DocumentBuilderFactory lDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            Context lContext = pContext;
            String str1 = "brushsetting.xml";
            FileInputStream lFileInputStream = lContext.openFileInput(str1);
            Element lElement = lDocumentBuilderFactory.newDocumentBuilder().parse(lFileInputStream).getDocumentElement();
            String str2 = "Setting";
            NodeList lNodeList1 = lElement.getElementsByTagName(str2);

            for (int i = 0; i < lNodeList1.getLength(); i++) {
                BrushSetting lBrushSetting = new BrushSetting();
                NodeList lNodeList2 = lNodeList1.item(i).getChildNodes();

                for (int k = 0; k < lNodeList2.getLength(); k++) {
                    Node lNode = lNodeList2.item(k);
                    String str3 = lNode.getNodeName();
                    String str4 = lNode.getFirstChild().getNodeValue();
                    lBrushSetting.setElement(str3, str4);
                }
                mBrushSettingList.add(lBrushSetting);
            }

            lFileInputStream.close();
            dump();

        } catch (Exception lException) {
            lException.printStackTrace();
        }
    }

    public void setBrushFlow(int pInt1, int pInt2) {
        BrushSetting lBrushSetting = getSetting(pInt1);
        if (lBrushSetting == null)
            lBrushSetting = addSetting(pInt1);
        lBrushSetting.flow = pInt2;
    }

    public void setBrushOpacity(int pInt1, int pInt2) {
        BrushSetting lBrushSetting = getSetting(pInt1);
        if (lBrushSetting == null)
            lBrushSetting = addSetting(pInt1);
        lBrushSetting.opacity = pInt2;
    }

    public void setBrushSize(int pInt, float pFloat) {
        String str1 = TAG;
        String str2 = "mgr brush change size" + pInt + " size:" + pFloat;
        MyDbgLog(str1, str2);
        BrushSetting lBrushSetting = getSetting(pInt);
        if (lBrushSetting == null)
            lBrushSetting = addSetting(pInt);
        lBrushSetting.size = pFloat;
    }
}
