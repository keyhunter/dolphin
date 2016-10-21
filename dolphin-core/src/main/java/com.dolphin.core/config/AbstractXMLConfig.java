package com.dolphin.rpc.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * XML配置项
 * @author jiujie
 * @version $Id: AbstractXMLConfig.java, v 0.1 2016年5月9日 下午4:58:51 jiujie Exp $
 */
public abstract class AbstractXMLConfig implements Config {

    private static Logger logger = Logger.getLogger(AbstractXMLConfig.class);

    private Document      document;

    private String        path;

    private URL           resource;

    public AbstractXMLConfig(String path) {
        try {
            this.path = ClassLoaderUtil.getAbsolutePath(path);
        } catch (MalformedURLException e) {
            logger.error("Config file not found.");
        }
        this.resource = ClassLoaderUtil.getClassLoader().getResource(path);
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(resource.getFile());
        } catch (DocumentException e) {
            logger.error("Config init failed.", e);
        }
    }

    /**
     * 取得xml中配置的int值，如果没有配置则默认返回0
     * @author jiujie
     * 2016年7月11日 下午3:44:16
     * @param path
     * @return
     */
    protected int getInt(String path) {
        String text = null;
        try {
            text = document.selectSingleNode(path).getText().trim();
            return Integer.valueOf(text);
        } catch (Exception e) {
            return 0;
        }
    }

    protected String getString(String path) {
        String text = null;
        try {
            text = document.selectSingleNode(path).getText().trim();
        } catch (Exception e) {
            logger.error("Config read failed.", e);
        }
        return text;
    }

    @SuppressWarnings("unchecked")
    protected List<String> getStrings(String path) {
        List<String> texts = new ArrayList<>();
        try {
            List<Element> elements = document.selectNodes(path);
            for (Element element : elements) {
                texts.add(element.getTextTrim());
            }
        } catch (Exception e) {
            logger.error("Config read failed.", e);
        }
        return texts;
    }

    public URL getResource() {
        return resource;
    }

    public String getPath() {
        return path;
    }

}
