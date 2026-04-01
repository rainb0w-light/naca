/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jlib.xml.Tag;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Utility class to load YAML configuration files and convert them to Tag objects.
 * This allows YAML to be used as an alternative to XML for NacaTrans configuration.
 *
 * @author Claude
 */
public class YamlConfigLoader
{
    /**
     * Load a YAML configuration file and convert it to a Tag object.
     *
     * @param csFilePath Path to the YAML configuration file
     * @return Tag object representing the configuration, or null if loading failed
     */
    public static Tag createFromFile(String csFilePath)
    {
        try (InputStream is = new FileInputStream(csFilePath))
        {
            return createFromStream(is);
        }
        catch (Exception e)
        {
            jlib.xml.LogTagError.log(e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load YAML configuration from an InputStream and convert it to a Tag object.
     *
     * @param is InputStream containing YAML content
     * @return Tag object representing the configuration, or null if loading failed
     */
    public static Tag createFromStream(InputStream is)
    {
        try
        {
            LoaderOptions loaderOptions = new LoaderOptions();
            SafeConstructor safeConstructor = new SafeConstructor(loaderOptions);
            Yaml yaml = new Yaml(safeConstructor);
            Object obj = yaml.load(is);

            if (obj instanceof Map<?, ?>)
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> yamlMap = (Map<String, Object>) obj;
                Tag tag = convertMapToTag(yamlMap);
                return tag;
            }

            return null;
        }
        catch (Exception e)
        {
            jlib.xml.LogTagError.log(e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a YAML Map to a Tag object.
     */
    @SuppressWarnings("unchecked")
    private static Tag convertMapToTag(Map<String, Object> yamlMap)
    {
        if (yamlMap.isEmpty())
            return null;

        // Get the root element name (first key)
        String rootName = yamlMap.keySet().iterator().next();
        Object rootValue = yamlMap.get(rootName);

        Tag rootTag = new Tag(rootName);

        if (rootValue instanceof Map<?, ?>)
        {
            addMapToTag(rootTag, (Map<String, Object>) rootValue);
        }
        else if (rootValue instanceof List<?>)
        {
            addListToTag(rootTag, (List<Object>) rootValue);
        }
        else if (rootValue != null)
        {
            rootTag.addVal("value", rootValue.toString());
        }

        return rootTag;
    }

    /**
     * Add map entries as attributes or child tags.
     */
    @SuppressWarnings("unchecked")
    private static void addMapToTag(Tag parentTag, Map<String, Object> map)
    {
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?>)
            {
                // Nested map - create child tag
                Tag childTag = parentTag.addTag(key);
                addMapToTag(childTag, (Map<String, Object>) value);
            }
            else if (value instanceof List<?>)
            {
                // List - create multiple child tags with same name
                for (Object item : (List<Object>) value)
                {
                    if (item instanceof Map<?, ?>)
                    {
                        Tag childTag = parentTag.addTag(key);
                        addMapToTag(childTag, (Map<String, Object>) item);
                    }
                    else
                    {
                        Tag childTag = parentTag.addTag(key);
                        childTag.addVal("value", item.toString());
                    }
                }
            }
            else if (value != null)
            {
                // Simple value - add as attribute
                parentTag.addVal(key, value.toString());
            }
        }
    }

    /**
     * Add list items as child tags.
     */
    @SuppressWarnings("unchecked")
    private static void addListToTag(Tag parentTag, List<Object> list)
    {
        for (Object item : list)
        {
            if (item instanceof Map<?, ?>)
            {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                if (itemMap.size() == 1)
                {
                    // Single-entry map - use key as tag name
                    String itemName = itemMap.keySet().iterator().next();
                    Object itemValue = itemMap.get(itemName);
                    Tag childTag = parentTag.addTag(itemName);

                    if (itemValue instanceof Map<?, ?>)
                    {
                        addMapToTag(childTag, (Map<String, Object>) itemValue);
                    }
                    else
                    {
                        childTag.addVal("value", itemValue.toString());
                    }
                }
                else
                {
                    // Multi-entry map - create generic child
                    Tag childTag = parentTag.addTag("item");
                    addMapToTag(childTag, itemMap);
                }
            }
        }
    }
}
