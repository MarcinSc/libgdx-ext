package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GLSLFragmentReader {
    private static Map<String, String> map = new HashMap<String, String>();

    private GLSLFragmentReader() {
    }

    public static String getFragment(String fragmentName, Map<String, String> replacements) {
        String fragment = readFragment(fragmentName);
        for (Map.Entry<String, String> replacement : replacements.entrySet())
            fragment = fragment.replace("#" + replacement.getKey() + "#", replacement.getValue());

        return fragment;
    }

    private static String readFragment(String fragmentName) {
        String result = map.get(fragmentName);
        if (result != null)
            return result;

        FileHandle fileHandle = Gdx.files.internal("glsl/fragment/" + fragmentName + ".glsl");
        String fragment = new String(fileHandle.readBytes(), Charset.forName("UTF-8"));
        map.put(fragmentName, fragment);

        return fragment;
    }

    public static String getFragment(String fragmentName) {
        return getFragment(fragmentName, Collections.<String, String>emptyMap());
    }
}
